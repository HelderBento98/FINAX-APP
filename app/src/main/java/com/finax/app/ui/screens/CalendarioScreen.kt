package com.finax.app.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.finax.app.data.model.Lembrete
import com.finax.app.ui.theme.*
import com.finax.app.utils.*
import com.finax.app.viewmodel.AppUiState
import java.util.Calendar

private val ANOS = (2024..2040).toList()
private val DIAS_SEMANA = listOf("D", "S", "T", "Q", "Q", "S", "S")

@Composable
fun CalendarioScreen(
    uiState: AppUiState,
    onSetGlobalDate: (Int, Int) -> Unit,
    onAddLembrete: (String, String, String, String) -> Unit,
    onDeleteLembrete: (String) -> Unit
) {
    val context = LocalContext.current
    val hoje = Calendar.getInstance()

    var selectedDay by remember(uiState.selectedMonth, uiState.selectedYear) {
        mutableStateOf(
            if (hoje.get(Calendar.MONTH) == uiState.selectedMonth && hoje.get(Calendar.YEAR) == uiState.selectedYear)
                hoje.get(Calendar.DAY_OF_MONTH)
            else 1
        )
    }

    var showMesMenu by remember { mutableStateOf(false) }
    var showAnoMenu by remember { mutableStateOf(false) }
    var showForm by remember { mutableStateOf(false) }
    var descricao by remember { mutableStateOf("") }
    var horario by remember { mutableStateOf("12:00") }
    var celular by remember { mutableStateOf("") }

    val firstDayOfMonth = Calendar.getInstance().apply { set(uiState.selectedYear, uiState.selectedMonth, 1) }
    val daysInMonth = firstDayOfMonth.getActualMaximum(Calendar.DAY_OF_MONTH)
    val startDay = firstDayOfMonth.get(Calendar.DAY_OF_WEEK) - 1

    val selectedDateStr = "${selectedDay.toString().padStart(2, '0')}/${(uiState.selectedMonth + 1).toString().padStart(2, '0')}/${uiState.selectedYear}"
    val selectedLembretes = uiState.lembretes.filter { it.data == selectedDateStr }
    val isCurrentMonthYear = hoje.get(Calendar.MONTH) == uiState.selectedMonth && hoje.get(Calendar.YEAR) == uiState.selectedYear

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Month/Year Selectors
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            // Year
            Box(modifier = Modifier.weight(1f)) {
                Card(
                    onClick = { showAnoMenu = !showAnoMenu; showMesMenu = false },
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(uiState.selectedYear.toString(), fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1C1C1E))
                        Icon(if (showAnoMenu) Icons.Default.ExpandLess else Icons.Default.ExpandMore, null, tint = IosSecondaryText)
                    }
                }
                DropdownMenu(expanded = showAnoMenu, onDismissRequest = { showAnoMenu = false }) {
                    ANOS.forEach { ano ->
                        DropdownMenuItem(
                            text = { Text(ano.toString(), fontWeight = FontWeight.Medium) },
                            onClick = { onSetGlobalDate(uiState.selectedMonth, ano); showAnoMenu = false }
                        )
                    }
                }
            }

            // Month
            Box(modifier = Modifier.weight(2f)) {
                Card(
                    onClick = { showMesMenu = !showMesMenu; showAnoMenu = false },
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(MESES.getOrElse(uiState.selectedMonth) { "" }.uppercase(), fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1C1C1E))
                        Icon(if (showMesMenu) Icons.Default.ExpandLess else Icons.Default.ExpandMore, null, tint = IosSecondaryText)
                    }
                }
                DropdownMenu(expanded = showMesMenu, onDismissRequest = { showMesMenu = false }) {
                    MESES.forEachIndexed { idx, mes ->
                        DropdownMenuItem(
                            text = { Text(mes, fontWeight = FontWeight.Medium) },
                            onClick = { onSetGlobalDate(idx, uiState.selectedYear); showMesMenu = false }
                        )
                    }
                }
            }
        }

        // Calendar Grid
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                // Day headers
                Row(modifier = Modifier.fillMaxWidth()) {
                    DIAS_SEMANA.forEach { d ->
                        Text(
                            d, fontSize = 11.sp, fontWeight = FontWeight.Bold,
                            color = if (d == "D" || d == "S") IosSecondaryText.copy(alpha = 0.6f) else IosSecondaryText,
                            modifier = Modifier.weight(1f).wrapContentWidth(Alignment.CenterHorizontally)
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                // Days grid
                val totalCells = startDay + daysInMonth
                val rows = (totalCells + 6) / 7
                for (row in 0 until rows) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        for (col in 0..6) {
                            val cellIdx = row * 7 + col
                            val dayNum = cellIdx - startDay + 1
                            if (dayNum < 1 || dayNum > daysInMonth) {
                                Box(modifier = Modifier.weight(1f).aspectRatio(1f))
                            } else {
                                val isToday = isCurrentMonthYear && dayNum == hoje.get(Calendar.DAY_OF_MONTH)
                                val isSelected = dayNum == selectedDay
                                val isWeekend = col == 0 || col == 6

                                val dayDateStr = "${dayNum.toString().padStart(2, '0')}/${(uiState.selectedMonth + 1).toString().padStart(2, '0')}/${uiState.selectedYear}"
                                val hasPago = uiState.ordens.any { os ->
                                    os.status == "PAGO" && os.dataOrcamento == dayDateStr
                                }
                                val hasLembrete = uiState.lembretes.any { it.data == dayDateStr }

                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { selectedDay = dayNum; showForm = false },
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .background(
                                                when {
                                                    isToday -> IosBlue
                                                    isSelected -> IosBlue.copy(alpha = 0.1f)
                                                    else -> Color.Transparent
                                                },
                                                CircleShape
                                            )
                                            .border(
                                                if (isSelected && !isToday) BorderStroke(1.dp, IosBlue.copy(alpha = 0.3f)) else BorderStroke(0.dp, Color.Transparent),
                                                CircleShape
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            dayNum.toString(),
                                            fontSize = 14.sp,
                                            fontWeight = if (isToday || isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = when {
                                                isToday -> Color.White
                                                isSelected -> IosBlue
                                                isWeekend -> IosSecondaryText
                                                else -> Color(0xFF3C3C43)
                                            }
                                        )
                                    }
                                    Row(horizontalArrangement = Arrangement.spacedBy(2.dp), modifier = Modifier.height(8.dp)) {
                                        if (hasPago) Box(Modifier.size(4.dp).background(if (isToday) Color.White.copy(alpha = 0.6f) else IosGreen, CircleShape))
                                        if (hasLembrete) Box(Modifier.size(4.dp).background(if (isToday) Color.White else Color(0xFF9333EA), CircleShape))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Reminders for selected day
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Box(Modifier.size(32.dp).background(Color(0xFFF3E8FF), CircleShape), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Notifications, null, tint = Color(0xFF9333EA), modifier = Modifier.size(16.dp))
                        }
                        Column {
                            Text("Compromissos", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1C1C1E))
                            Text("${selectedDay.toString().padStart(2, '0')} DE ${(MESES.getOrElse(uiState.selectedMonth) { "" }).uppercase()}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = IosSecondaryText, letterSpacing = 0.5.sp)
                        }
                    }
                    if (!showForm) {
                        TextButton(
                            onClick = { showForm = true },
                            modifier = Modifier.background(IosBlue.copy(alpha = 0.1f), RoundedCornerShape(50))
                        ) {
                            Icon(Icons.Default.Add, null, tint = IosBlue, modifier = Modifier.size(14.dp))
                            Text("NOVO", color = IosBlue, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                if (showForm) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF9F9F9), RoundedCornerShape(16.dp))
                            .border(1.dp, IosBorder, RoundedCornerShape(16.dp))
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Column {
                            Text("DESCRIÇÃO / VISITA", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = IosSecondaryText, letterSpacing = 0.5.sp)
                            Spacer(Modifier.height(4.dp))
                            OutlinedTextField(
                                value = descricao,
                                onValueChange = { descricao = it },
                                placeholder = { Text("Ex: Visita ao cliente João", fontSize = 13.sp) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = IosBorder, focusedBorderColor = IosBlue)
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("HORÁRIO", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = IosSecondaryText, letterSpacing = 0.5.sp)
                                Spacer(Modifier.height(4.dp))
                                OutlinedTextField(
                                    value = horario,
                                    onValueChange = { horario = it },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(14.dp),
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = IosBorder, focusedBorderColor = IosBlue)
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text("CELULAR (OPCIONAL)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = IosSecondaryText, letterSpacing = 0.5.sp)
                                Spacer(Modifier.height(4.dp))
                                OutlinedTextField(
                                    value = celular,
                                    onValueChange = { celular = it },
                                    placeholder = { Text("11999999999", fontSize = 12.sp) },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(14.dp),
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                    colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = IosBorder, focusedBorderColor = IosBlue)
                                )
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = { showForm = false; descricao = ""; celular = "" },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(16.dp),
                                border = BorderStroke(1.dp, IosBorder)
                            ) { Text("CANCELAR", fontSize = 11.sp, color = IosSecondaryText) }
                            Button(
                                onClick = {
                                    if (descricao.isNotBlank()) {
                                        onAddLembrete(descricao.trim(), horario, selectedDateStr, celular.trim())
                                        descricao = ""; horario = "12:00"; celular = ""; showForm = false
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = IosBlue)
                            ) { Text("SALVAR", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                        }
                    }
                } else {
                    if (selectedLembretes.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF9F9F9), RoundedCornerShape(16.dp))
                                .border(1.dp, IosBorder.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Sem compromissos agendados", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = IosSecondaryText)
                        }
                    } else {
                        selectedLembretes.forEach { lembrete ->
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
        }

        // Generate Extract Button
        Button(
            onClick = {
                val ordensDoMes = uiState.ordens.filter { os ->
                    if (os.status != "PAGO") return@filter false
                    val (_, m, a) = parseDate(os.dataOrcamento) ?: return@filter false
                    m - 1 == uiState.selectedMonth && a == uiState.selectedYear
                }.sortedBy { os -> parseDate(os.dataOrcamento)?.first ?: 0 }
                PdfUtils.generateExtrato(
                    context,
                    ordensDoMes,
                    MESES_UPPER.getOrElse(uiState.selectedMonth) { "" },
                    uiState.selectedYear,
                    uiState.userProfile
                )
            },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(containerColor = IosBlue),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
        ) {
            Icon(Icons.Default.Description, null, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            Text("GERAR EXTRATO", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}
