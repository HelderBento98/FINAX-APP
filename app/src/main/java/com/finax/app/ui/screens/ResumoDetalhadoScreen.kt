package com.finax.app.ui.screens

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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.finax.app.ui.components.SubScreenHeader
import com.finax.app.ui.theme.*
import com.finax.app.utils.*
import com.finax.app.viewmodel.AppUiState

@Composable
fun ResumoDetalhadoScreen(
    uiState: AppUiState,
    onBack: () -> Unit
) {
    val ordensFiltradas = uiState.ordens.filter { os ->
        val (_, m, a) = parseDate(os.dataOrcamento) ?: return@filter false
        m - 1 == uiState.selectedMonth && a == uiState.selectedYear
    }

    val ordensPagas = ordensFiltradas.filter { it.status == "PAGO" }
    val ordensPendentes = ordensFiltradas.filter { it.status == "PENDENTES" }

    val totalRecebido = ordensPagas.sumOf { it.preco }
    val totalPendente = ordensPendentes.sumOf { it.preco }
    val ticketMedio = if (ordensPagas.isNotEmpty()) totalRecebido / ordensPagas.size else 0.0
    val totalDecididas = ordensPagas.size + ordensPendentes.size
    val taxaConversao = if (totalDecididas > 0) (ordensPagas.size.toDouble() / totalDecididas) * 100 else 0.0

    val intervals = listOf(
        "01-05" to (1..5), "06-10" to (6..10), "11-15" to (11..15),
        "16-20" to (16..20), "21-25" to (21..25), "26-31" to (26..31)
    )

    val intervalData = intervals.map { (label, range) ->
        val total = range.sumOf { day ->
            val dayStr = "${day.toString().padStart(2, '0')}/${(uiState.selectedMonth + 1).toString().padStart(2, '0')}/${uiState.selectedYear}"
            uiState.ordens.filter { it.status == "PAGO" && it.dataOrcamento == dayStr }.sumOf { it.preco }
        }
        label to total
    }
    val maxTotal = maxOf(intervalData.maxOfOrNull { it.second } ?: 0.0, 500.0)

    val formasPagamento = ordensPagas.groupBy { it.formaPagamento.ifEmpty { "Outro" } }
        .mapValues { it.value.sumOf { os -> os.preco } }
        .entries.sortedByDescending { it.value }

    val rankingServicos = ordensPagas.groupBy { os ->
        val s = os.servico.ifEmpty { "Não Especificado" }
        if (s.length > 32) s.take(32) + "..." else s
    }.mapValues { (_, list) ->
        list.sumOf { it.preco } to list.size
    }.entries.sortedByDescending { it.value.first }.take(4)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Header
        SubScreenHeader(
            caption = "ANÁLISE FINANCEIRA",
            title = "${MESES.getOrElse(uiState.selectedMonth) { "" }} de ${uiState.selectedYear}",
            onBack = onBack
        )

        // Metric Cards Grid
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            MetricCard(
                modifier = Modifier.weight(1f),
                label = "Total Recebido",
                value = formatCurrency(totalRecebido),
                subtitle = "${ordensPagas.size} ${if (ordensPagas.size == 1) "ordem paga" else "ordens pagas"}",
                color = IosGreen,
                icon = Icons.Default.CheckCircle
            )
            MetricCard(
                modifier = Modifier.weight(1f),
                label = "A Receber",
                value = formatCurrency(totalPendente),
                subtitle = "${ordensPendentes.size} ${if (ordensPendentes.size == 1) "pendente" else "pendentes"}",
                color = IosOrange,
                icon = Icons.Default.AccessTime
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            MetricCard(
                modifier = Modifier.weight(1f),
                label = "Ticket Médio",
                value = formatCurrency(ticketMedio),
                subtitle = "Valor médio por OS",
                color = IosBlue,
                icon = Icons.Default.AttachMoney
            )
            MetricCard(
                modifier = Modifier.weight(1f),
                label = "Conversão",
                value = "${taxaConversao.toInt()}%",
                subtitle = "Aprovação de serviços",
                color = Color(0xFF9333EA),
                icon = Icons.Default.Percent
            )
        }

        // Bar Chart
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(Modifier.size(28.dp).background(IosBlue.copy(alpha = 0.1f), CircleShape), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.TrendingUp, null, tint = IosBlue, modifier = Modifier.size(14.dp))
                    }
                    Text("Faturamento por Período", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1C1C1E))
                }
                Spacer(Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth().height(96.dp),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    intervalData.forEach { (label, total) ->
                        val heightFraction = if (maxTotal > 0) (total / maxTotal).toFloat().coerceIn(0.04f, 1f) else 0.04f
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Bottom
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight(heightFraction)
                                    .background(
                                        if (total > 0) Brush.verticalGradient(listOf(Color(0xFF3B82F6), IosBlue))
                                        else Brush.verticalGradient(listOf(Color(0xFFF0F0F0), Color(0xFFE5E5EA))),
                                        RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)
                                    )
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(label, fontSize = 8.sp, fontWeight = FontWeight.Bold, color = IosSecondaryText)
                        }
                    }
                }
            }
        }

        // Payment methods
        if (formasPagamento.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(Modifier.size(28.dp).background(Color(0xFFD1FAE5), CircleShape), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.CreditCard, null, tint = Color(0xFF059669), modifier = Modifier.size(14.dp))
                        }
                        Text("Faturamento por Forma de Pagamento", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1C1C1E))
                    }
                    Spacer(Modifier.height(14.dp))

                    formasPagamento.forEach { (forma, total) ->
                        val pct = if (totalRecebido > 0) (total / totalRecebido * 100).toFloat() else 0f
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(forma, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1C1C1E))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text("${pct.toInt()}%", fontSize = 11.sp, color = IosSecondaryText)
                                    Text(formatCurrency(total), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1C1C1E))
                                }
                            }
                            LinearProgressIndicator(
                                progress = { pct / 100f },
                                modifier = Modifier.fillMaxWidth().height(6.dp),
                                color = Color(0xFF10B981),
                                trackColor = Color(0xFFF0F0F0)
                            )
                        }
                        Spacer(Modifier.height(10.dp))
                    }
                }
            }
        }

        // Ranking
        if (rankingServicos.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(Modifier.size(28.dp).background(Color(0xFFF3E8FF), CircleShape), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.EmojiEvents, null, tint = Color(0xFF9333EA), modifier = Modifier.size(14.dp))
                        }
                        Text("Serviços mais Rentáveis", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1C1C1E))
                    }
                    Spacer(Modifier.height(14.dp))

                    rankingServicos.forEachIndexed { idx, (servico, data) ->
                        val (total, qtd) = data
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF9F9F9), RoundedCornerShape(12.dp))
                                .border(1.dp, Color(0xFFF0F0F0), RoundedCornerShape(12.dp))
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    Modifier.size(24.dp).background(Color(0xFFF3E8FF), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("${idx + 1}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF9333EA))
                                }
                                Column {
                                    Text(servico, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1C1C1E), maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    Text("$qtd ${if (qtd == 1) "realização" else "realizações"}", fontSize = 10.sp, color = IosSecondaryText)
                                }
                            }
                            Text(formatCurrency(total), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1C1C1E))
                        }
                        if (idx < rankingServicos.size - 1) Spacer(Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun MetricCard(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    subtitle: String,
    color: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Box(
        modifier = modifier
            .background(
                Brush.linearGradient(listOf(color.copy(alpha = 0.12f), color.copy(alpha = 0.04f))),
                RoundedCornerShape(20.dp)
            )
            .border(1.dp, color.copy(alpha = 0.1f), RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        Column {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(label, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = color, letterSpacing = 0.3.sp, modifier = Modifier.weight(1f))
                Icon(icon, null, tint = color, modifier = Modifier.size(16.dp))
            }
            Spacer(Modifier.height(12.dp))
            Text(value, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1C1C1E))
            Text(subtitle, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = IosSecondaryText)
        }
    }
}
