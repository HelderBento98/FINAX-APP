package com.finax.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
    Surface(
        color = Color.White,
        shadowElevation = 16.dp,
        tonalElevation = 0.dp,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
                .padding(top = 12.dp, bottom = 26.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            navItems.forEach { item ->
                val active = isNavActive(item.route, currentRoute)
                val interaction = remember { MutableInteractionSource() }
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(if (active) IosBlue.copy(alpha = 0.12f) else Color.Transparent)
                        .clickable(
                            interactionSource = interaction,
                            indication = null
                        ) { onNavigate(item.route) }
                        .padding(horizontal = if (active) 16.dp else 14.dp, vertical = 9.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        tint = if (active) IosBlue else Color(0xFF9A9AA0),
                        modifier = Modifier.size(22.dp)
                    )
                    AnimatedVisibility(
                        visible = active,
                        enter = fadeIn() + expandHorizontally(),
                        exit = fadeOut() + shrinkHorizontally()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = item.label,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = IosBlue
                            )
                        }
                    }
                }
            }
        }
    }
}
