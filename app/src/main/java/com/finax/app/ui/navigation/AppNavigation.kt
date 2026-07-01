package com.finax.app.ui.navigation

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.finax.app.ui.components.BottomNav
import com.finax.app.ui.components.TopBar
import com.finax.app.ui.screens.*
import com.finax.app.ui.theme.AppBgGradient
import com.finax.app.viewmodel.AppViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val viewModel: AppViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val gate by viewModel.gateState.collectAsStateWithLifecycle()
    val products by viewModel.products.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Access gate: when the free trial has ended and there is no active
    // subscription, block the app behind the paywall.
    if (!gate.loading && !gate.hasAccess) {
        PaywallScreen(
            products = products,
            trialDaysLeft = gate.trialDaysLeft,
            onSubscribe = { pd -> (context as? Activity)?.let { viewModel.subscribe(it, pd) } },
            onRestore = { viewModel.refreshPurchases() }
        )
        return
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBgGradient)
    ) {
        TopBar(userProfile = uiState.userProfile)

        Box(modifier = Modifier.weight(1f)) {
            NavHost(
                navController = navController,
                startDestination = "home",
                modifier = Modifier.fillMaxSize()
            ) {
                composable("home") {
                    HomeScreen(
                        uiState = uiState,
                        onNavigateToNovaOS = { navController.navigate("nova_os") },
                        onNavigateToResumo = { navController.navigate("resumo_detalhado") },
                        onDeleteLembrete = { viewModel.deleteLembrete(it) }
                    )
                }

                composable("nova_os") {
                    NovaOSScreen(
                        userProfile = uiState.userProfile,
                        onBack = { navController.popBackStack() },
                        onFinalizar = { cliente, servico, preco, pagamento, contato, data, validade ->
                            viewModel.addOS(cliente, servico, preco, pagamento, contato, data, validade) {
                                navController.navigate("lista_os/AGUARDANDO INICIO") {
                                    popUpTo("home")
                                }
                            }
                        }
                    )
                }

                composable("historico") {
                    HistoricoScreen(
                        uiState = uiState,
                        onNavigateToLista = { status ->
                            navController.navigate("lista_os/$status")
                        }
                    )
                }

                composable(
                    route = "lista_os/{statusFilter}",
                    arguments = listOf(navArgument("statusFilter") { type = NavType.StringType })
                ) { backStackEntry ->
                    val statusFilter = backStackEntry.arguments?.getString("statusFilter")
                    ListaOSScreen(
                        uiState = uiState,
                        statusFilter = statusFilter,
                        onBack = { navController.popBackStack() },
                        onOSClick = { osId ->
                            navController.navigate("detalhes_os/$osId")
                        }
                    )
                }

                composable(
                    route = "detalhes_os/{osId}",
                    arguments = listOf(navArgument("osId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val osId = backStackEntry.arguments?.getString("osId") ?: ""
                    val os = uiState.ordens.find { it.id == osId }
                    if (os != null) {
                        DetalhesOSScreen(
                            os = os,
                            userProfile = uiState.userProfile,
                            onBack = { navController.popBackStack() },
                            onUpdateStatus = { newStatus ->
                                viewModel.updateOSStatus(osId, newStatus)
                                navController.navigate("lista_os/$newStatus") {
                                    popUpTo("historico")
                                }
                            },
                            onDelete = {
                                viewModel.deleteOS(osId)
                                navController.popBackStack()
                            },
                            onNavigateToAjustes = { navController.navigate("ajustes") }
                        )
                    }
                }

                composable("calendario") {
                    CalendarioScreen(
                        uiState = uiState,
                        onSetGlobalDate = { month, year -> viewModel.setGlobalDate(month, year) },
                        onAddLembrete = { descricao, horario, data, celular ->
                            viewModel.addLembrete(descricao, horario, data, celular)
                        },
                        onDeleteLembrete = { viewModel.deleteLembrete(it) }
                    )
                }

                composable("ajustes") {
                    AjustesScreen(
                        uiState = uiState,
                        onUpdateProfile = { viewModel.updateUserProfile(it) },
                        onImportData = { ordens, lembretes, profile ->
                            viewModel.importData(ordens, lembretes, profile)
                        }
                    )
                }

                composable("resumo_detalhado") {
                    ResumoDetalhadoScreen(
                        uiState = uiState,
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }

        BottomNav(
            currentRoute = currentRoute,
            onNavigate = { route ->
                // Tapping a tab always lands on that tab's root screen (never a
                // deep sub-screen like a filtered list), so the user can always
                // get back to the main pages.
                navController.navigate(route) {
                    popUpTo(navController.graph.startDestinationId) { saveState = false }
                    launchSingleTop = true
                    restoreState = false
                }
            }
        )
    }
}
