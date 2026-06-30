package com.finax.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.finax.app.data.model.Lembrete
import com.finax.app.ui.theme.*
import com.finax.app.utils.*
import com.finax.app.viewmodel.AppUiState

@Composable
fun HomeScreen(
    uiState: AppUiState,
    onNavigateToNovaOS: () -> Unit,
    onNavigateToResumo: () -> Unit,
    onDeleteLembrete: (String) -> Unit
) {
    val context = LocalContext.current
    val ordensFiltradas = uiState.ordens.filter { os ->
        val (_, m, a) = parseDate(os.dataOrcamento) ?: return@filter false
        m - 1 == uiState.selectedMonth && a == uiState.selectedYear
    }

    val totalRecebido = ordensFiltradas.filter { it.status == "PAGO" }.sumOf { it.preco }
    val totalPendente = ordensFiltradas.filter { it.status == "PENDENTES" }.sumOf { it.preco }
    val recentActivities = ordensFiltradas.take(3)

    val today = todayStr()
    val lembretesDeHoje = uiState.lembretes.filter { it.data == today }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Monthly Summary Card
        Card(
            onClick = onNavigateToResumo,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "RESUMO MENSAL",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = IosSecondaryText,
                    letterSpacing = 1.sp,
                    modifier = Modifier.fillMaxWidth().wrapContentWidth(Alignment.CenterHorizontally)
                )
                Spacer(Modifier.height(12.dp))
                HorizontalDivider(color = Color(0xFFF2F2F7))
                Spacer(Modifier.height(12.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Recebido
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(
                                Brush.linearGradient(listOf(IosGreen.copy(alpha = 0.12f), IosGreen.copy(alpha = 0.05f))),
                                RoundedCornerShape(20.dp)
                            )
                            .border(1.dp, IosGreen.copy(alpha = 0.1f), RoundedCornerShape(20.dp))
                            .padding(16.dp)
                    ) {
                        Column {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("RECEBIDO", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = IosGreen, letterSpacing = 0.5.sp)
                                Icon(Icons.Default.CheckCircle, null, tint = IosGreen, modifier = Modifier.size(16.dp))
                            }
                            Spacer(Modifier.height(16.dp))
                            Text(formatCurrency(totalRecebido), fontSize = 17.sp, fontWeight = FontWeight.Bold, color = IosGreen)
                        }
                    }

                    // Pendente
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(
                                Brush.linearGradient(listOf(IosOrange.copy(alpha = 0.12f), IosOrange.copy(alpha = 0.05f))),
                                RoundedCornerShape(20.dp)
                            )
                            .border(1.dp, IosOrange.copy(alpha = 0.1f), RoundedCornerShape(20.dp))
                            .padding(16.dp)
                    ) {
                        Column {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("PENDENTE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = IosOrange, letterSpacing = 0.5.sp)
                                Icon(Icons.Default.AccessTime, null, tint = IosOrange, modifier = Modifier.size(16.dp))
                            }
                            Spacer(Modifier.height(16.dp))
                            Text(formatCurrency(totalPendente), fontSize = 17.sp, fontWeight = FontWeight.Bold, color = IosOrange)
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))
                HorizontalDivider(color = Color(0xFFF2F2F7))
                Spacer(Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Análise Detalhada", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF3C3C43))
                    Icon(Icons.Default.ChevronRight, null, tint = IosSecondaryText, modifier = Modifier.size(13.dp))
                }
            }
        }

        // New OS Button
        Button(
            onClick = onNavigateToNovaOS,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(containerColor = IosBlue),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
        ) {
            Icon(Icons.Default.AddCircle, null, modifier = Modifier.size(22.dp))
            Spacer(Modifier.width(8.dp))
            Text("NOVO ORÇAMENTO", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }

        // Today's Reminders
        if (lembretesDeHoje.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(
                                Modifier
                                    .size(28.dp)
                                    .background(Color(0xFFEFF6FF), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Notifications, null, tint = IosBlue, modifier = Modifier.size(14.dp))
                            }
                            Text("Compromissos de Hoje", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1C1C1E))
                        }
                        Text(
                            "${lembretesDeHoje.size} ${if (lembretesDeHoje.size == 1) "VISITA" else "VISITAS"}",
                            fontSize = 10.sp, fontWeight = FontWeight.Bold, color = IosSecondaryText
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    lembretesDeHoje.forEach { lembrete ->
                        LembreteItem(
                            lembrete = lembrete,
                            onDelete = { onDeleteLembrete(lembrete.id) },
                            onWhatsApp = {
                                val text = "Olá! Passando para confirmar nossa visita agendada: ${lembrete.descricao} hoje às ${lembrete.horario}."
                                openWhatsApp(context, lembrete.celular, text)
                            }
                        )
                        Spacer(Modifier.height(6.dp))
                    }
                }
            }
        }

        // Recent Activities
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "ATIVIDADES RECENTES",
                    fontSize = 11.sp, fontWeight = FontWeight.Bold, color = IosSecondaryText,
                    letterSpacing = 1.sp,
                    modifier = Modifier.fillMaxWidth().wrapContentWidth(Alignment.CenterHorizontally)
                )
                Spacer(Modifier.height(12.dp))
                HorizontalDivider(color = Color(0xFFF2F2F7))
                Spacer(Modifier.height(12.dp))

                if (recentActivities.isEmpty()) {
                    Text(
                        "Nenhuma atividade.", fontSize = 14.sp, color = IosSecondaryText,
                        modifier = Modifier.fillMaxWidth().wrapContentWidth(Alignment.CenterHorizontally).padding(16.dp)
                    )
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
    }
}

@Composable
fun LembreteItem(lembrete: Lembrete, onDelete: () -> Unit, onWhatsApp: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF9F9F9), RoundedCornerShape(12.dp))
            .border(1.dp, Color(0xFFF0F0F0), RoundedCornerShape(12.dp))
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .background(Color(0xFFEDE9FE), RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp, vertical = 6.dp)
        ) {
            Text(lembrete.horario, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF7C3AED))
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(lembrete.descricao, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1C1C1E), maxLines = 1, overflow = TextOverflow.Ellipsis)
            if (lembrete.celular.isNotEmpty()) {
                Text(lembrete.celular, fontSize = 10.sp, color = IosSecondaryText)
            }
        }

        if (lembrete.celular.isNotEmpty()) {
            IconButton(
                onClick = onWhatsApp,
                modifier = Modifier
                    .size(28.dp)
                    .background(Color(0xFFDCFCE7), CircleShape)
            ) {
                Icon(Icons.Default.Chat, null, tint = Color(0xFF16A34A), modifier = Modifier.size(14.dp))
            }
        }

        IconButton(
            onClick = onDelete,
            modifier = Modifier
                .size(28.dp)
                .background(Color.Transparent, CircleShape)
        ) {
            Icon(Icons.Default.Delete, null, tint = IosRed, modifier = Modifier.size(14.dp))
        }
    }
}

@Composable
fun StatusBadge(status: String) {
    val (color, label, bg) = when (status) {
        "PAGO" -> Triple(IosGreen, "PAGO", IosGreen.copy(alpha = 0.1f))
        "PENDENTES" -> Triple(IosOrange, "PENDENTE", IosOrange.copy(alpha = 0.1f))
        "AGUARDANDO INICIO" -> Triple(IosBlue, "AGENDADO", IosBlue.copy(alpha = 0.1f))
        else -> Triple(IosSecondaryText, status, IosSecondaryText.copy(alpha = 0.1f))
    }
    Text(
        text = label,
        fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = color,
        modifier = Modifier
            .background(bg, RoundedCornerShape(50))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    )
}

fun openWhatsApp(context: android.content.Context, phone: String, text: String) {
    val clean = phone.replace(Regex("\\D"), "")
    val withCountry = if (clean.length == 10 || clean.length == 11) "55$clean" else clean
    val url = if (withCountry.isNotEmpty()) {
        "https://api.whatsapp.com/send?phone=$withCountry&text=${android.net.Uri.encode(text)}"
    } else {
        "https://wa.me/?text=${android.net.Uri.encode(text)}"
    }
    val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(url))
    context.startActivity(intent)
}
