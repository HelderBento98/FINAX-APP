package com.finax.app.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.finax.app.data.model.OrdemServico
import com.finax.app.data.model.UserProfile
import com.finax.app.ui.theme.*
import com.finax.app.utils.*

@Composable
fun DetalhesOSScreen(
    os: OrdemServico,
    userProfile: UserProfile,
    onBack: () -> Unit,
    onUpdateStatus: (String) -> Unit,
    onDelete: () -> Unit,
    onNavigateToAjustes: () -> Unit
) {
    val context = LocalContext.current
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var showWarrantyOptions by remember { mutableStateOf(false) }
    var showPixModal by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .padding(bottom = 16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.size(36.dp).background(Color(0xFFF2F2F7), RoundedCornerShape(50))
                    ) {
                        Icon(Icons.Default.ArrowBack, null, tint = IosBlue, modifier = Modifier.size(18.dp))
                    }
                    Text("Detalhes do Serviço", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1C1C1E))
                    Spacer(Modifier.size(36.dp))
                }

                Spacer(Modifier.height(16.dp))
                Text("Pedido #${os.id}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = IosSecondaryText, letterSpacing = 0.8.sp)
                Spacer(Modifier.height(16.dp))

                // Details
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    DetailField("CLIENTE", os.cliente)
                    DetailField("SERVIÇO", os.servico)
                    DetailField("VALOR", formatCurrency(os.preco), valueColor = IosBlue)
                    DetailField("PAGAMENTO", os.formaPagamento)
                    DetailField("CONTATO", os.contato)
                    DetailField("DATA", os.dataOrcamento)
                    if (os.validadeOrcamento.isNotEmpty()) {
                        DetailField("VALIDADE ORÇAMENTO", os.validadeOrcamento)
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Action Buttons
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    when (os.status) {
                        "AGUARDANDO INICIO" -> {
                            Button(
                                onClick = { onUpdateStatus("PAGO") },
                                modifier = Modifier.fillMaxWidth().height(56.dp),
                                shape = RoundedCornerShape(20.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = IosGreen),
                                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                            ) {
                                Text("Marcar como Pago", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                            }
                            Button(
                                onClick = { onUpdateStatus("PENDENTES") },
                                modifier = Modifier.fillMaxWidth().height(56.dp),
                                shape = RoundedCornerShape(20.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = IosOrange),
                                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                            ) {
                                Text("Mover para Pendente", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                        "PENDENTES" -> {
                            Button(
                                onClick = { onUpdateStatus("PAGO") },
                                modifier = Modifier.fillMaxWidth().height(56.dp),
                                shape = RoundedCornerShape(20.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = IosGreen),
                                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                            ) {
                                Text("Pagamento Recebido", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                            }
                            Button(
                                onClick = { showPixModal = true },
                                modifier = Modifier.fillMaxWidth().height(56.dp),
                                shape = RoundedCornerShape(20.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = IosBlue),
                                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                            ) {
                                Icon(Icons.Default.QrCode, null, modifier = Modifier.size(20.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("Gerar Cobrança Pix", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                            }
                            OutlinedButton(
                                onClick = {
                                    val companyName = userProfile.nomeEmpresa.ifEmpty { "Sua Empresa" }
                                    val companyPhone = userProfile.telefone
                                    val msg = "Olá, ${os.cliente}! 😊\n\nPassando apenas para lembrar sobre o pagamento referente ao serviço realizado:\n\n🛠️ Serviço: ${os.servico}\n📅 Data: ${os.dataOrcamento}\n💰 Valor: ${formatCurrency(os.preco)}\n\nFicamos à disposição.\n\nAtenciosamente,\n$companyName${if (companyPhone.isNotEmpty()) "\n📞 $companyPhone" else ""}"
                                    openWhatsApp(context, os.contato, msg)
                                },
                                modifier = Modifier.fillMaxWidth().height(52.dp),
                                shape = RoundedCornerShape(20.dp),
                                border = BorderStroke(1.dp, IosBorder)
                            ) {
                                Text("Cobrar via WhatsApp", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1C1C1E))
                            }
                        }
                        "PAGO" -> {
                            if (showWarrantyOptions) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFFF9F9F9), RoundedCornerShape(20.dp))
                                        .border(1.dp, IosBorder, RoundedCornerShape(20.dp))
                                        .padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Text("Selecione o prazo de garantia:", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1C1C1E), modifier = Modifier.fillMaxWidth().wrapContentWidth(Alignment.CenterHorizontally))
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        listOf(30, 60, 90).forEach { days ->
                                            OutlinedButton(
                                                onClick = {
                                                    PdfUtils.generateGarantia(context, os, days, userProfile)
                                                    val msg = "Olá *${os.cliente}*, estou te enviando o certificado de garantia do seu serviço (${os.servico}). O prazo de garantia é de *$days dias*!"
                                                    openWhatsApp(context, os.contato, msg)
                                                    showWarrantyOptions = false
                                                },
                                                modifier = Modifier.weight(1f),
                                                shape = RoundedCornerShape(16.dp),
                                                border = BorderStroke(1.dp, IosBorder)
                                            ) {
                                                Text("$days Dias", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = IosBlue)
                                            }
                                        }
                                    }
                                    TextButton(onClick = { showWarrantyOptions = false }, modifier = Modifier.fillMaxWidth()) {
                                        Text("Cancelar", color = IosSecondaryText)
                                    }
                                }
                            } else {
                                OutlinedButton(
                                    onClick = { showWarrantyOptions = true },
                                    modifier = Modifier.fillMaxWidth().height(52.dp),
                                    shape = RoundedCornerShape(20.dp),
                                    border = BorderStroke(1.dp, IosBorder)
                                ) {
                                    Text("Gerar Garantia", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1C1C1E))
                                }
                            }
                        }
                    }

                    // Delete Button
                    HorizontalDivider(modifier = Modifier.padding(top = 4.dp), color = Color(0xFFF2F2F7))

                    if (showDeleteConfirm) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFFFF1F0), RoundedCornerShape(20.dp))
                                .border(1.dp, IosRed.copy(alpha = 0.2f), RoundedCornerShape(20.dp))
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("Tem certeza que deseja excluir esta ordem?", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF7F1D1D), modifier = Modifier.fillMaxWidth().wrapContentWidth(Alignment.CenterHorizontally))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedButton(
                                    onClick = { showDeleteConfirm = false },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(16.dp),
                                    border = BorderStroke(1.dp, IosRed.copy(alpha = 0.3f))
                                ) { Text("Cancelar", color = IosRed) }
                                Button(
                                    onClick = onDelete,
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = IosRed)
                                ) { Text("Sim, Excluir") }
                            }
                        }
                    } else {
                        TextButton(
                            onClick = { showDeleteConfirm = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(IosRed.copy(alpha = 0.05f), RoundedCornerShape(20.dp))
                                .border(1.dp, IosRed.copy(alpha = 0.15f), RoundedCornerShape(20.dp))
                        ) {
                            Text("Excluir Ordem de Serviço", color = IosRed, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }

    if (showPixModal) {
        PixModal(
            os = os,
            userProfile = userProfile,
            onClose = { showPixModal = false },
            onNavigateToAjustes = { showPixModal = false; onNavigateToAjustes() }
        )
    }
}

@Composable
fun DetailField(label: String, value: String, valueColor: Color = Color(0xFF1C1C1E)) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    ) {
        Text(label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = IosSecondaryText, letterSpacing = 0.5.sp)
        Spacer(Modifier.height(4.dp))
        Text(value, fontSize = 15.sp, fontWeight = FontWeight.Medium, color = valueColor)
        Spacer(Modifier.height(8.dp))
        HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 0.8.dp)
    }
}

@Composable
fun PixModal(
    os: OrdemServico,
    userProfile: UserProfile,
    onClose: () -> Unit,
    onNavigateToAjustes: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    var copied by remember { mutableStateOf(false) }

    val hasPixKey = userProfile.chavePix.isNotBlank()
    val pixCode = if (hasPixKey) generatePixPayload(userProfile.chavePix, os.preco, userProfile.nomeEmpresa.ifEmpty { "FINAX" }) else ""
    val qrUrl = if (hasPixKey) "https://api.qrserver.com/v1/create-qr-code/?size=180x180&data=${android.net.Uri.encode(pixCode)}" else ""

    Dialog(onDismissRequest = onClose) {
        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("COBRANÇA PIX", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = IosSecondaryText, letterSpacing = 1.sp)
                    IconButton(onClick = onClose, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, null, tint = IosSecondaryText)
                    }
                }

                Spacer(Modifier.height(16.dp))

                if (!hasPixKey) {
                    Icon(Icons.Default.Warning, null, tint = Color(0xFFF59E0B), modifier = Modifier.size(48.dp))
                    Spacer(Modifier.height(12.dp))
                    Text("Chave Pix não cadastrada", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1C1C1E))
                    Spacer(Modifier.height(8.dp))
                    Text("Cadastre sua Chave Pix nas configurações para gerar cobranças automáticas.", fontSize = 13.sp, color = IosSecondaryText, modifier = Modifier.padding(horizontal = 8.dp))
                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = onNavigateToAjustes,
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = IosBlue)
                    ) { Text("Cadastrar Chave Pix") }
                } else {
                    AsyncImage(
                        model = qrUrl,
                        contentDescription = "QR Code Pix",
                        modifier = Modifier
                            .size(160.dp)
                            .background(Color(0xFFF9F9F9), RoundedCornerShape(16.dp))
                            .padding(8.dp)
                    )

                    Spacer(Modifier.height(12.dp))
                    Text("Valor a Receber", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = IosSecondaryText, letterSpacing = 0.5.sp)
                    Text(formatCurrency(os.preco), fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = IosGreen)
                    Text("Chave: ${userProfile.chavePix}", fontSize = 12.sp, color = IosSecondaryText)

                    Spacer(Modifier.height(12.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF9F9F9), RoundedCornerShape(16.dp))
                            .border(1.dp, IosBorder, RoundedCornerShape(16.dp))
                            .padding(12.dp)
                    ) {
                        Column {
                            Text("Pix Copia e Cola", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = IosSecondaryText, letterSpacing = 0.5.sp)
                            Spacer(Modifier.height(4.dp))
                            Text(pixCode, fontSize = 11.sp, color = Color(0xFF3C3C43), maxLines = 3)
                        }
                        IconButton(
                            onClick = {
                                clipboardManager.setText(AnnotatedString(pixCode))
                                copied = true
                            },
                            modifier = Modifier.align(Alignment.BottomEnd).size(32.dp).background(Color.White, RoundedCornerShape(8.dp)).border(1.dp, IosBorder, RoundedCornerShape(8.dp))
                        ) {
                            Icon(if (copied) Icons.Default.Check else Icons.Default.ContentCopy, null, tint = if (copied) IosGreen else IosSecondaryText, modifier = Modifier.size(14.dp))
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    Button(
                        onClick = {
                            val price = String.format("%.2f", os.preco).replace(".", ",")
                            val msg = "Olá, ${os.cliente}! 😊\n\nPara o pagamento do serviço *${os.servico}* no valor de *R$ $price*, use a chave Pix ou o código abaixo:\n\n🔑 *Chave Pix:* ${userProfile.chavePix}\n\n📋 *Pix Copia e Cola:*\n$pixCode\n\nMuito obrigado!"
                            openWhatsApp(context, os.contato, msg)
                        },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366))
                    ) {
                        Icon(Icons.Default.Send, null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Enviar por WhatsApp", fontWeight = FontWeight.Bold)
                    }

                    Spacer(Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = onClose,
                        modifier = Modifier.fillMaxWidth().height(44.dp),
                        shape = RoundedCornerShape(18.dp),
                        border = BorderStroke(1.dp, IosBorder)
                    ) { Text("Voltar", color = Color(0xFF1C1C1E)) }
                }
            }
        }
    }
}
