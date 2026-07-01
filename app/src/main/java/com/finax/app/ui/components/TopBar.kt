package com.finax.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.finax.app.data.model.UserProfile
import com.finax.app.ui.theme.IosBlue
import com.finax.app.ui.theme.IosSecondaryText
import com.finax.app.ui.theme.TextPrimary
import com.finax.app.utils.todayDisplayStr

@Composable
fun TopBar(userProfile: UserProfile) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(top = 18.dp, bottom = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Date chip
        Row(
            modifier = Modifier
                .shadow(3.dp, RoundedCornerShape(50), spotColor = Color(0x22000000))
                .background(Color.White, RoundedCornerShape(50))
                .border(1.dp, Color(0xFFEDEDF0), RoundedCornerShape(50))
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(Icons.Default.CalendarToday, contentDescription = null, tint = IosBlue, modifier = Modifier.size(13.dp))
            Text(
                text = todayDisplayStr(),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF3C3C43)
            )
        }

        // User info + avatar
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "Bem-vindo(a)",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = IosSecondaryText,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = userProfile.nomeEmpresa.ifEmpty { "Sua Empresa" },
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.widthIn(max = 140.dp)
                )
            }

            Box(
                modifier = Modifier
                    .size(44.dp)
                    .shadow(5.dp, CircleShape, spotColor = Color(0x33000000))
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(1.dp, Color(0xFFE5E5EA), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (userProfile.logo.isNotEmpty()) {
                    AsyncImage(
                        model = userProfile.logo,
                        contentDescription = "Logo",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(Icons.Default.Person, contentDescription = null, tint = IosSecondaryText, modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}
