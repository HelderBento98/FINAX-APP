package com.finax.app.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
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
import com.finax.app.data.model.UserProfile
import com.finax.app.ui.components.GradientButton
import com.finax.app.ui.components.SubScreenHeader
import com.finax.app.ui.theme.*
import com.finax.app.utils.PdfUtils
import com.finax.app.utils.todayStr

@Composable
fun NovaOSScreen(
    userProfile: UserProfile,
    onBack: () -> Unit,
    onFinalizar: (String, String, Double, String, String, String, String) -> Unit
) {
    val context = LocalContext.current

    var cliente by remember { mutableStateOf("") }
    var servico by remember { mutableStateOf("") }
    var preco by remember { mutableStateOf("") }
    var formaPagamento by remember { mutableStateOf("") }
    var contato by remember { mutableStateOf("") }
    var dataOrcamento by remember { mutableStateOf(todayStr()) }
    var validadeOrcamento by remember { mutableStateOf("A combinar") }

    var showError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .padding(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SubScreenHeader(
            caption = "NOVA",
            title = "ORDEM DE SERVIÇO",
            onBack = onBack,
            trailing = {
                TextButton(onClick = {
                    cliente = ""; servico = ""; preco = ""; formaPagamento = ""; contato = ""
                    dataOrcamento = todayStr(); validadeOrcamento = "A combinar"
                }) {
                    Text("Limpar", color = IosRed, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                }
            }
        )
        Card(
            modifier = Modifier.fillMaxWidth().weight(1f),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                Text(
                    "DADOS DA ORDEM",
                    fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = IosSecondaryText,
                    letterSpacing = 0.8.sp
                )
                Spacer(Modifier.height(12.dp))

                // Form fields
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OSInputField("NOME CLIENTE:", cliente, { cliente = it })
                    OSInputField("SERVIÇO:", servico, { servico = it })
                    OSInputField("PREÇO:", preco, { preco = it }, KeyboardType.Decimal)
                    OSInputField("FORMA DE PAGAMENTO:", formaPagamento, { formaPagamento = it })
                    OSInputField("CONTATO:", contato, { contato = it }, KeyboardType.Phone)
                    OSInputField("DATA ORÇAMENTO:", dataOrcamento, { dataOrcamento = it })
                    OSInputField("VALIDADE ORÇAMENTO:", validadeOrcamento, { validadeOrcamento = it })
                }

                if (showError) {
                    Text(
                        "Preencha os campos obrigatórios: Cliente, Serviço e Preço.",
                        color = IosRed, fontSize = 12.sp,
                        modifier = Modifier.padding(vertical = 6.dp)
                    )
                }

                Spacer(Modifier.height(16.dp))

                // Action buttons
                GradientButton(
                    text = "Finalizar OS",
                    onClick = {
                        if (cliente.isEmpty() || servico.isEmpty() || preco.isEmpty()) {
                            showError = true
                        } else {
                            val precoNum = preco.replace(",", ".").replace(Regex("[^0-9.]"), "").toDoubleOrNull() ?: 0.0
                            onFinalizar(cliente, servico, precoNum, formaPagamento, contato, dataOrcamento, validadeOrcamento)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(10.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(
                        onClick = {
                            if (cliente.isEmpty() || servico.isEmpty() || preco.isEmpty()) {
                                showError = true; return@OutlinedButton
                            }
                            PdfUtils.generateOrcamento(
                                context, cliente, servico, preco, formaPagamento, validadeOrcamento, dataOrcamento, userProfile
                            )
                        },
                        modifier = Modifier.weight(1f).height(52.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF1C1C1E)),
                        border = BorderStroke(1.dp, IosBorder)
                    ) {
                        Text("PDF", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    }

                    Button(
                        onClick = {
                            val companyName = userProfile.nomeEmpresa.ifEmpty { "Sua Empresa" }
                            val companyPhone = userProfile.telefone
                            val msg = buildString {
                                appendLine("📋 ORÇAMENTO DE SERVIÇO")
                                appendLine()
                                appendLine("Olá, ${cliente.ifEmpty { "Cliente" }}!")
                                appendLine()
                                appendLine("Segue o orçamento solicitado:")
                                appendLine()
                                appendLine("🔹 Descrição do Serviço:")
                                appendLine(servico.ifEmpty { "-" })
                                appendLine()
                                appendLine("💰 Valor do Serviço:")
                                appendLine("R$ ${preco.ifEmpty { "0,00" }}")
                                appendLine()
                                appendLine("📅 Data do Orçamento:")
                                appendLine(dataOrcamento)
                                appendLine()
                                appendLine("✅ Observações:")
                                appendLine("• Forma de pagamento: ${formaPagamento.ifEmpty { "-" }}")
                                appendLine("• Validade deste orçamento: $validadeOrcamento")
                                appendLine()
                                appendLine("Atenciosamente,")
                                append(companyName)
                                if (companyPhone.isNotEmpty()) append("\n📞 $companyPhone")
                            }
                            openWhatsApp(context, contato, msg)
                        },
                        modifier = Modifier.weight(1f).height(52.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = IosGreen)
                    ) {
                        Text("WhatsApp", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

@Composable
fun OSInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(BorderStroke(0.dp, Color.Transparent))
    ) {
        Text(label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = IosSecondaryText, letterSpacing = 0.8.sp)
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = androidx.compose.ui.text.TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1C1C1E)
            ),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            modifier = Modifier.fillMaxWidth().padding(top = 4.dp, bottom = 4.dp),
            singleLine = false
        )
        HorizontalDivider(color = Color(0xFFE5E5EA), thickness = 0.8.dp)
    }
}
