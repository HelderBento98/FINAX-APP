package com.finax.app.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.finax.app.ui.theme.*
import com.finax.app.utils.*
import com.finax.app.viewmodel.AppUiState

@Composable
fun HistoricoScreen(
    uiState: AppUiState,
    onNavigateToLista: (String) -> Unit
) {
    val ordensFiltradas = uiState.ordens.filter { os ->
        val (_, m, a) = parseDate(os.dataOrcamento) ?: return@filter false
        m - 1 == uiState.selectedMonth && a == uiState.selectedYear
    }

    val countAgendados = ordensFiltradas.count { it.status == "AGUARDANDO INICIO" }
    val countPagos = ordensFiltradas.count { it.status == "PAGO" }
    val countPendentes = ordensFiltradas.count { it.status == "PENDENTES" }
    val recentActivities = ordensFiltradas.take(2)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Atividades Recentes
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    "ATIVIDADES RECENTES",
                    fontSize = 11.sp, fontWeight = FontWeight.Bold, color = IosSecondaryText, letterSpacing = 1.sp,
                    modifier = Modifier.fillMaxWidth().wrapContentWidth(Alignment.CenterHorizontally)
                )
                Spacer(Modifier.height(12.dp))
                HorizontalDivider(color = Color(0xFFF2F2F7))
                Spacer(Modifier.height(12.dp))

                if (recentActivities.isEmpty()) {
                    Text("Nenhuma atividade.", fontSize = 14.sp, color = IosSecondaryText,
                        modifier = Modifier.fillMaxWidth().wrapContentWidth(Alignment.CenterHorizontally).padding(16.dp))
                } else {
                    recentActivities.forEach { os ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF9F9F9), RoundedCornerShape(16.dp))
                                .border(1.dp, Color(0xFFF0F0F0), RoundedCornerShape(16.dp))
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(os.servico.ifEmpty { "Ordem ${os.id}" }, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1C1C1E), maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Text(formatCurrency(os.preco), fontSize = 13.sp, color = IosSecondaryText, fontWeight = FontWeight.Medium)
                            }
                            StatusBadge(status = os.status)
                        }
                        Spacer(Modifier.height(8.dp))
                    }
                }
            }
        }

        // Status Count Cards
        StatusCountCard(
            label = "AGENDADOS",
            count = countAgendados,
            color = IosBlue,
            onClick = { onNavigateToLista("AGUARDANDO INICIO") }
        )

        StatusCountCard(
            label = "PAGOS",
            count = countPagos,
            color = IosGreen,
            onClick = { onNavigateToLista("PAGO") }
        )

        StatusCountCard(
            label = "PENDENTES",
            count = countPendentes,
            color = IosOrange,
            onClick = { onNavigateToLista("PENDENTES") }
        )
    }
}

@Composable
fun StatusCountCard(label: String, count: Int, color: Color, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("SERVIÇOS", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = IosSecondaryText, letterSpacing = 0.5.sp)
                Text(label, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = color)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(48.dp)
                        .background(Color(0xFFF0F0F0))
                )
                Spacer(Modifier.width(20.dp))
                Text(count.toString(), fontSize = 48.sp, fontWeight = FontWeight.Light, color = color)
            }
        }
    }
}
