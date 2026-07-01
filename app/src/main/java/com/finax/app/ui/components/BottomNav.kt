package com.finax.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.finax.app.ui.theme.IosBlue

data class NavItem(val route: String, val label: String, val icon: ImageVector)

val navItems = listOf(
    NavItem("home", "Início", Icons.Default.Home),
    NavItem("historico", "Histórico", Icons.Default.List),
    NavItem("calendario", "Calendário", Icons.Default.CalendarToday),
    NavItem("ajustes", "Ajustes", Icons.Default.Settings)
)

fun isNavActive(itemRoute: String, currentRoute: String?): Boolean {
    if (currentRoute == null) return false
    return when (itemRoute) {
        "home" -> currentRoute == "home" || currentRoute == "nova_os"
        "historico" -> currentRoute == "historico" || currentRoute.startsWith("lista_os") || currentRoute.startsWith("detalhes_os")
        else -> currentRoute == itemRoute
    }
}

@Composable
fun BottomNav(currentRoute: String?, onNavigate: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White.copy(alpha = 0.85f))
            .border(width = 0.5.dp, color = Color(0xFFE5E5EA), shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .padding(horizontal = 16.dp)
            .padding(top = 12.dp, bottom = 24.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        navItems.forEach { item ->
            val active = isNavActive(item.route, currentRoute)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onNavigate(item.route) }
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.label,
                    tint = if (active) IosBlue else Color(0xFF8E8E93),
                    modifier = Modifier.size(23.dp)
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = item.label,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (active) IosBlue else Color(0xFF8E8E93)
                )
                if (active) {
                    Spacer(Modifier.height(2.dp))
                    Box(
                        Modifier
                            .size(4.dp)
                            .clip(CircleShape)
                            .background(IosBlue)
                    )
                }
            }
        }
    }
}
