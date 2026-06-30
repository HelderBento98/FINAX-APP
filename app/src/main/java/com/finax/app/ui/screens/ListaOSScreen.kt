package com.finax.app.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.finax.app.data.model.OrdemServico
import com.finax.app.ui.theme.*
import com.finax.app.utils.*
import com.finax.app.viewmodel.AppUiState

@Composable
fun ListaOSScreen(
    uiState: AppUiState,
    statusFilter: String?,
    onBack: () -> Unit,
    onOSClick: (String) -> Unit
) {
    val context = LocalContext.current
    var searchTerm by remember { mutableStateOf("") }
    var sortBy by remember { mutableStateOf("recent") }

    val ordensFiltradas = uiState.ordens.filter { os ->
        val (_, m, a) = parseDate(os.dataOrcamento) ?: return@filter false
        m - 1 == uiState.selectedMonth && a == uiState.selectedYear
    }.let { list ->
        if (statusFilter != null) list.filter { it.status == statusFilter } else list
    }.filter { os ->
        val term = searchTerm.lowercase()
        os.cliente.lowercase().contains(term) ||
                os.servico.lowercase().contains(term) ||
                os.id.lowercase().contains(term)
    }.let { list ->
        when (sortBy) {
            "value_desc" -> list.sortedByDescending { it.preco }
            "value_asc" -> list.sortedBy { it.preco }
            else -> list.sortedByDescending { dateMillis(it.dataOrcamento) }
        }
    }

    val (headerColor, title) = when (statusFilter) {
        "AGUARDANDO INICIO" -> Pair(IosBlue, "AGENDADOS")
        "PAGO" -> Pair(IosGreen, "PAGOS")
        "PENDENTES" -> Pair(IosOrange, "PENDENTES")
        else -> Pair(Color(0xFF1C1C1E), "TODOS")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .padding(bottom = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Header
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.size(36.dp).background(Color(0xFFF2F2F7), RoundedCornerShape(50))
                    ) {
                        Icon(Icons.Default.ArrowBack, null, tint = IosSecondaryText, modifier = Modifier.size(16.dp))
                    }
                    Column {
                        Text("SERVIÇOS", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = IosSecondaryText, letterSpacing = 0.5.sp)
                        Text(title, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = headerColor)
                    }
                }
                Text(ordensFiltradas.size.toString(), fontSize = 42.sp, fontWeight = FontWeight.Light, color = headerColor)
            }
        }

        // Search and Sort
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = searchTerm,
                    onValueChange = { searchTerm = it },
                    placeholder = { Text("Pesquisar por cliente, serviço...", fontSize = 13.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, null, tint = IosSecondaryText) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = IosBorder,
                        focusedBorderColor = IosBlue,
                        unfocusedContainerColor = Color(0xFFF9F9F9),
                        focusedContainerColor = Color.White
                    ),
                    singleLine = true
                )

                HorizontalDivider(color = Color(0xFFF2F2F7))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SortChip("Recentes", sortBy == "recent") { sortBy = "recent" }
                    SortChip("Maior Valor", sortBy == "value_desc") { sortBy = "value_desc" }
                    SortChip("Menor Valor", sortBy == "value_asc") { sortBy = "value_asc" }
                }
            }
        }

        // List
        Card(
            modifier = Modifier.fillMaxWidth().weight(1f),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Serviços Listados", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = IosSecondaryText)
                    Text(
                        "${ordensFiltradas.size} REGISTROS",
                        fontSize = 10.sp, fontWeight = FontWeight.Bold, color = IosSecondaryText,
                        modifier = Modifier.background(Color(0xFFF2F2F7), RoundedCornerShape(50)).padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }

                Spacer(Modifier.height(12.dp))

                if (ordensFiltradas.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.FilterList, null, tint = Color(0xFFD1D1D6), modifier = Modifier.size(32.dp))
                            Spacer(Modifier.height(8.dp))
                            Text("Nenhum serviço encontrado.", fontSize = 14.sp, color = IosSecondaryText)
                        }
                    }
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(ordensFiltradas, key = { it.id }) { os ->
                            OSListItem(os = os, onClick = { onOSClick(os.id) })
                        }

                        if (statusFilter == "PAGO") {
                            item {
                                Spacer(Modifier.height(8.dp))
                                OutlinedButton(
                                    onClick = {
                                        PdfUtils.generateExtrato(
                                            context,
                                            ordensFiltradas,
                                            MESES_UPPER.getOrNull(uiState.selectedMonth) ?: "",
                                            uiState.selectedYear,
                                            uiState.userProfile
                                        )
                                    },
                                    modifier = Modifier.fillMaxWidth().height(52.dp),
                                    shape = RoundedCornerShape(20.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF1C1C1E)),
                                    border = BorderStroke(1.dp, IosBorder)
                                ) {
                                    Text("+ EXTRATO ATUAL", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RowScope.SortChip(label: String, selected: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
        modifier = Modifier.weight(1f),
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = IosBlue.copy(alpha = 0.1f),
            selectedLabelColor = IosBlue
        )
    )
}

@Composable
fun OSListItem(os: OrdemServico, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF9F9F9), RoundedCornerShape(16.dp))
            .border(1.dp, Color(0xFFF0F0F0), RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 10.dp)) {
            Text(os.cliente, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1C1C1E), maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(os.servico, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = IosSecondaryText, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Spacer(Modifier.height(2.dp))
            Text(
                "${os.dataOrcamento} • ${formatCurrency(os.preco)}",
                fontSize = 10.sp, fontWeight = FontWeight.Bold, color = IosSecondaryText
            )
        }
        StatusBadge(status = os.status)
    }
}

private fun dateMillis(dateStr: String): Long {
    val parts = dateStr.split("/")
    if (parts.size < 3) return 0L
    return try {
        val cal = java.util.Calendar.getInstance()
        cal.set(parts[2].toInt(), parts[1].toInt() - 1, parts[0].toInt())
        cal.timeInMillis
    } catch (e: Exception) { 0L }
}
