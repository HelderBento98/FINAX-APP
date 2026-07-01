package com.finax.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import com.finax.app.ui.theme.BrandGradient
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
        // Brand: gradient "F" mark + FINAX wordmark + date
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .shadow(6.dp, RoundedCornerShape(12.dp), spotColor = Color(0xFF5E5CE6))
                    .clip(RoundedCornerShape(12.dp))
                    .background(BrandGradient),
                contentAlignment = Alignment.Center
            ) {
                Text("F", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Black)
            }
            Column {
                Text(
                    "FINAX",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = TextPrimary,
                    letterSpacing = 1.sp
                )
                Text(
                    todayDisplayStr(),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = IosSecondaryText
                )
            }
        }

        // Company name + avatar
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (userProfile.nomeEmpresa.isNotEmpty()) {
                Text(
                    text = userProfile.nomeEmpresa,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.widthIn(max = 120.dp)
                )
            }
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .shadow(4.dp, CircleShape)
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
                    Icon(Icons.Default.Person, contentDescription = null, tint = IosSecondaryText, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}
