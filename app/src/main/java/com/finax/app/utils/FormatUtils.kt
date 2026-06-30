package com.finax.app.utils

import java.text.NumberFormat
import java.util.Locale

fun formatCurrency(value: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
    return format.format(value)
}

fun parseDate(dateStr: String): Triple<Int, Int, Int>? {
    val parts = dateStr.split("/")
    if (parts.size < 3) return null
    val day = parts[0].toIntOrNull() ?: return null
    val month = parts[1].toIntOrNull() ?: return null
    val year = parts[2].toIntOrNull() ?: return null
    return Triple(day, month, year)
}

fun todayStr(): String {
    val cal = java.util.Calendar.getInstance()
    val d = cal.get(java.util.Calendar.DAY_OF_MONTH).toString().padStart(2, '0')
    val m = (cal.get(java.util.Calendar.MONTH) + 1).toString().padStart(2, '0')
    val y = cal.get(java.util.Calendar.YEAR)
    return "$d/$m/$y"
}

fun todayDisplayStr(): String {
    val cal = java.util.Calendar.getInstance()
    val locale = Locale("pt", "BR")
    val dayOfWeek = java.text.SimpleDateFormat("EEE", locale).format(cal.time).replaceFirstChar { it.uppercase() }
    val day = cal.get(java.util.Calendar.DAY_OF_MONTH)
    val month = java.text.SimpleDateFormat("MMMM", locale).format(cal.time).replaceFirstChar { it.uppercase() }
    return "$dayOfWeek, $day de $month"
}

val MESES = listOf(
    "Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho",
    "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"
)

val MESES_UPPER = MESES.map { it.uppercase() }
