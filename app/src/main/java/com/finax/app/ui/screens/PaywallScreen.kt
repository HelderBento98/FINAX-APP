package com.finax.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.billingclient.api.ProductDetails
import com.finax.app.billing.BillingManager
import com.finax.app.billing.formattedPrice
import com.finax.app.ui.components.GradientButton
import com.finax.app.ui.theme.*

@Composable
fun PaywallScreen(
    products: List<ProductDetails>,
    trialDaysLeft: Int,
    onSubscribe: (ProductDetails) -> Unit,
    onRestore: () -> Unit
) {
    val mensal = products.firstOrNull { it.productId == BillingManager.PRODUTO_MENSAL }
    val anual = products.firstOrNull { it.productId == BillingManager.PRODUTO_ANUAL }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(IosBackground)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
            .padding(top = 48.dp, bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(72.dp).background(IosBlue.copy(alpha = 0.12f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Lock, null, tint = IosBlue, modifier = Modifier.size(34.dp))
        }

        Spacer(Modifier.height(20.dp))

        Text(
            if (trialDaysLeft > 0) "Seu período grátis está acabando" else "Seu período grátis terminou",
            fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1C1C1E),
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(8.dp))

        Text(
            if (trialDaysLeft > 0)
                "Você ainda tem $trialDaysLeft dia(s). Assine um plano para continuar usando o Finax sem interrupções."
            else
                "Para continuar gerenciando suas ordens de serviço, escolha um plano abaixo.",
            fontSize = 14.sp, color = IosSecondaryText, textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )

        Spacer(Modifier.height(24.dp))

        // Benefícios
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                BenefitRow("Ordens de serviço ilimitadas")
                BenefitRow("Orçamentos e garantias em PDF")
                BenefitRow("Cobrança via PIX com QR Code")
                BenefitRow("Lembretes e relatórios financeiros")
                BenefitRow("Backup dos seus dados")
            }
        }

        Spacer(Modifier.height(24.dp))

        PlanCard(
            titulo = "Anual",
            descricao = "Melhor custo-benefício • economize 15%",
            preco = anual?.formattedPrice()?.ifEmpty { "R$ 49,90" } ?: "R$ 49,90",
            periodo = "/ano",
            destaque = true,
            habilitado = anual != null,
            onClick = { anual?.let(onSubscribe) }
        )

        Spacer(Modifier.height(12.dp))

        PlanCard(
            titulo = "Mensal",
            descricao = "Assinatura cobrada mensalmente",
            preco = mensal?.formattedPrice()?.ifEmpty { "R$ 4,90" } ?: "R$ 4,90",
            periodo = "/mês",
            destaque = false,
            habilitado = mensal != null,
            onClick = { mensal?.let(onSubscribe) }
        )

        Spacer(Modifier.height(20.dp))

        if (products.isEmpty()) {
            Text(
                "Não foi possível carregar os planos. Verifique sua conexão e se você está conectado à Google Play.",
                fontSize = 12.sp, color = IosSecondaryText, textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(8.dp))
        }

        TextButton(onClick = onRestore) {
            Text("Restaurar compra / Atualizar", color = IosBlue, fontWeight = FontWeight.SemiBold)
        }

        Spacer(Modifier.height(8.dp))

        Text(
            "A assinatura é cobrada pela sua conta Google Play e renova automaticamente. " +
                "Você pode cancelar quando quiser na Play Store.",
            fontSize = 11.sp, color = IosSecondaryText, textAlign = TextAlign.Center, lineHeight = 16.sp
        )
    }
}

@Composable
private fun BenefitRow(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Icon(Icons.Default.CheckCircle, null, tint = IosGreen, modifier = Modifier.size(20.dp))
        Text(text, fontSize = 14.sp, color = Color(0xFF1C1C1E))
    }
}

@Composable
private fun PlanCard(
    titulo: String,
    descricao: String,
    preco: String,
    periodo: String,
    destaque: Boolean,
    habilitado: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (destaque) IosBlue else Color(0xFFE5E5EA)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(if (destaque) 2.dp else 1.dp, borderColor, RoundedCornerShape(20.dp))
            .clickable(enabled = habilitado, onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (destaque) IosBlue.copy(alpha = 0.04f) else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            if (destaque) {
                Text(
                    "MAIS POPULAR",
                    fontSize = 10.sp, fontWeight = FontWeight.Bold, color = IosBlue, letterSpacing = 1.sp
                )
                Spacer(Modifier.height(6.dp))
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(titulo, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1C1C1E))
                    Text(descricao, fontSize = 13.sp, color = IosSecondaryText)
                }
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(preco, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1C1C1E))
                    Text(periodo, fontSize = 13.sp, color = IosSecondaryText, modifier = Modifier.padding(bottom = 2.dp))
                }
            }
            Spacer(Modifier.height(16.dp))
            if (destaque) {
                GradientButton(
                    text = "Assinar $titulo",
                    onClick = onClick,
                    enabled = habilitado,
                    height = 50.dp,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                Button(
                    onClick = onClick,
                    enabled = habilitado,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1C1C1E))
                ) {
                    Text("Assinar $titulo", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}
