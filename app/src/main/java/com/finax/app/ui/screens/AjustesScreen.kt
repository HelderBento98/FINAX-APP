package com.finax.app.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.finax.app.data.model.Lembrete
import com.finax.app.data.model.OrdemServico
import com.finax.app.data.model.UserProfile
import com.finax.app.ui.theme.*
import com.finax.app.utils.todayStr
import com.finax.app.viewmodel.AppUiState
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter

@Composable
fun AjustesScreen(
    uiState: AppUiState,
    onUpdateProfile: (UserProfile) -> Unit,
    onImportData: (List<OrdemServico>, List<Lembrete>, UserProfile) -> Unit
) {
    val context = LocalContext.current
    var showPlanoDialog by remember { mutableStateOf(false) }
    var showLogoMenu by remember { mutableStateOf(false) }
    var showPrivacidadeDialog by remember { mutableStateOf(false) }
    var editando by remember { mutableStateOf(false) }

    val profile = uiState.userProfile

    var nomeEmpresa by remember(profile.nomeEmpresa) { mutableStateOf(profile.nomeEmpresa) }
    var telefone by remember(profile.telefone) { mutableStateOf(profile.telefone) }
    var dataCriacao by remember(profile.dataCriacao) { mutableStateOf(profile.dataCriacao) }
    var cnpj by remember(profile.cnpj) { mutableStateOf(profile.cnpj) }
    var email by remember(profile.email) { mutableStateOf(profile.email) }
    var chavePix by remember(profile.chavePix) { mutableStateOf(profile.chavePix) }

    val exportCsvLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("text/csv")
    ) { uri ->
        uri?.let { exportCsv(context, it, uiState.ordens) }
    }

    val exportBackupLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        uri?.let { exportBackup(context, it, uiState) }
    }

    val importBackupLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let { importBackup(context, it, onImportData) }
    }

    val logoPickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            context.contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            onUpdateProfile(profile.copy(logo = it.toString()))
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Company Info Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "INFORMAÇÕES",
                        fontSize = 11.sp, fontWeight = FontWeight.Bold, color = IosSecondaryText,
                        letterSpacing = 1.sp
                    )
                    if (editando) {
                        Button(
                            onClick = {
                                onUpdateProfile(
                                    profile.copy(
                                        nomeEmpresa = nomeEmpresa,
                                        telefone = telefone,
                                        dataCriacao = dataCriacao,
                                        cnpj = cnpj,
                                        email = email,
                                        chavePix = chavePix
                                    )
                                )
                                editando = false
                                Toast.makeText(context, "Informações salvas!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.height(36.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            shape = RoundedCornerShape(50),
                            colors = ButtonDefaults.buttonColors(containerColor = IosBlue)
                        ) {
                            Icon(Icons.Default.Check, null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Salvar", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        OutlinedButton(
                            onClick = { editando = true },
                            modifier = Modifier.height(36.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            shape = RoundedCornerShape(50),
                            border = BorderStroke(1.dp, IosBlue),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = IosBlue)
                        ) {
                            Icon(Icons.Default.Edit, null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Editar", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
                HorizontalDivider(color = Color(0xFFF2F2F7))
                Spacer(Modifier.height(16.dp))

                SettingsField("NOME DA EMPRESA", nomeEmpresa, { nomeEmpresa = it }, enabled = editando)
                SettingsField("TELEFONE", telefone, { telefone = it }, enabled = editando)
                SettingsField("DATA CRIAÇÃO", dataCriacao, { dataCriacao = it }, enabled = editando)
                SettingsField("CNPJ", cnpj, { cnpj = it }, enabled = editando)
                SettingsField("E-MAIL", email, { email = it }, enabled = editando)
                SettingsField("CHAVE PIX", chavePix, { chavePix = it }, enabled = editando, isLast = true)

                if (!editando) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Toque em \"Editar\" para alterar suas informações.",
                        fontSize = 11.sp, color = IosSecondaryText, textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Actions Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column {
                ActionRow(
                    icon = Icons.Default.Star,
                    iconBg = Color(0xFFF3E8FF),
                    iconTint = Color(0xFF9333EA),
                    label = "Meu Plano",
                    showDivider = true,
                    onClick = { showPlanoDialog = true }
                )
                ActionRow(
                    icon = Icons.Default.Image,
                    iconBg = Color(0xFFDBEAFE),
                    iconTint = Color(0xFF2563EB),
                    label = "Logotipo da Empresa",
                    showDivider = true,
                    onClick = { showLogoMenu = true }
                )
                ActionRow(
                    icon = Icons.Default.Shield,
                    iconBg = Color(0xFFDCFCE7),
                    iconTint = Color(0xFF16A34A),
                    label = "Segurança e Privacidade",
                    showDivider = true,
                    onClick = { showPrivacidadeDialog = true }
                )
                ActionRow(
                    icon = Icons.Default.TableChart,
                    iconBg = Color(0xFFD1FAE5),
                    iconTint = Color(0xFF059669),
                    label = "Exportar Planilha",
                    showDivider = true,
                    onClick = {
                        exportCsvLauncher.launch("Finax_Ordens_${todayStr().replace("/", "_")}.csv")
                    }
                )
                ActionRow(
                    icon = Icons.Default.Download,
                    iconBg = Color(0xFFE0E7FF),
                    iconTint = Color(0xFF4338CA),
                    label = "Exportar Backup",
                    showDivider = true,
                    onClick = {
                        exportBackupLauncher.launch("Finax_Backup_${todayStr().replace("/", "_")}.json")
                    }
                )
                ActionRow(
                    icon = Icons.Default.Upload,
                    iconBg = Color(0xFFCCFBF1),
                    iconTint = Color(0xFF0D9488),
                    label = "Importar Backup",
                    showDivider = false,
                    onClick = {
                        importBackupLauncher.launch(arrayOf("application/json"))
                    }
                )
            }
        }
    }

    if (showPlanoDialog) {
        PlanoDialog(
            currentPlan = profile.planoAtivo,
            onSelectPlan = { plan -> onUpdateProfile(profile.copy(planoAtivo = plan)) },
            onDismiss = { showPlanoDialog = false }
        )
    }

    if (showLogoMenu) {
        LogoBottomSheet(
            onUpload = {
                showLogoMenu = false
                logoPickerLauncher.launch("image/*")
            },
            onDelete = {
                showLogoMenu = false
                onUpdateProfile(profile.copy(logo = ""))
            },
            onDismiss = { showLogoMenu = false }
        )
    }

    if (showPrivacidadeDialog) {
        PrivacidadeDialog(onDismiss = { showPrivacidadeDialog = false })
    }
}

@Composable
private fun SettingsField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean = true,
    isLast: Boolean = false
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = if (isLast) 2.dp else 18.dp)
    ) {
        Text(label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = IosSecondaryText, letterSpacing = 0.5.sp)
        Spacer(Modifier.height(6.dp))
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            enabled = enabled,
            singleLine = true,
            textStyle = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color(0xFF1C1C1E)),
            cursorBrush = SolidColor(IosBlue),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        HorizontalDivider(
            color = if (enabled) IosBlue else Color(0xFFE5E5EA),
            thickness = if (enabled) 1.5.dp else 1.dp
        )
    }
}

@Composable
private fun ActionRow(
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    label: String,
    showDivider: Boolean,
    onClick: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier.size(32.dp).background(iconBg, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = iconTint, modifier = Modifier.size(16.dp))
            }
            Text(label, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color(0xFF1C1C1E), modifier = Modifier.weight(1f))
            Icon(Icons.Default.ChevronRight, null, tint = IosSecondaryText, modifier = Modifier.size(18.dp))
        }
        if (showDivider) HorizontalDivider(color = Color(0xFFF2F2F7), modifier = Modifier.padding(start = 60.dp))
    }
}

@Composable
private fun PlanoDialog(
    currentPlan: String,
    onSelectPlan: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var showConfirmCancel by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = { onDismiss(); showConfirmCancel = false },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column {
                Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.TopEnd) {
                    IconButton(onClick = { onDismiss(); showConfirmCancel = false }) {
                        Box(Modifier.size(32.dp).background(Color(0xFFF2F2F7), CircleShape), Alignment.Center) {
                            Icon(Icons.Default.Close, null, tint = IosSecondaryText, modifier = Modifier.size(18.dp))
                        }
                    }
                }

                if (!showConfirmCancel) {
                    Column(
                        modifier = Modifier.padding(horizontal = 20.dp).padding(bottom = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        PlanOption(
                            title = "Mensal",
                            subtitle = "Assinatura cobrada mensalmente",
                            price = "R$ 4,90",
                            period = "/mês",
                            isActive = currentPlan == "mensal",
                            onClick = { onSelectPlan("mensal") }
                        )
                        PlanOption(
                            title = "Anual",
                            subtitle = "Economize 15% pagando o ano",
                            price = "R$ 49,90",
                            period = "/ano",
                            isActive = currentPlan == "anual",
                            onClick = { onSelectPlan("anual") }
                        )
                    }

                    if (currentPlan == "mensal" || currentPlan == "anual") {
                        HorizontalDivider(color = Color(0xFFF2F2F7))
                        Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                            TextButton(onClick = { showConfirmCancel = true }) {
                                Text("Cancelar assinatura atual", color = IosRed, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                            }
                        }
                    }
                } else {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("Cancelar assinatura?", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1C1C1E))
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Tem certeza que deseja cancelar sua assinatura atual? Você perderá o acesso aos benefícios exclusivos.",
                            fontSize = 14.sp, color = IosSecondaryText
                        )
                        Spacer(Modifier.height(20.dp))
                        Button(
                            onClick = { onSelectPlan(""); showConfirmCancel = false },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = IosRed)
                        ) { Text("Sim, quero cancelar", fontWeight = FontWeight.SemiBold) }
                        Spacer(Modifier.height(8.dp))
                        Button(
                            onClick = { showConfirmCancel = false },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF2F2F7))
                        ) { Text("Voltar", fontWeight = FontWeight.SemiBold, color = Color(0xFF1C1C1E)) }
                    }
                }
            }
        }
    }
}

@Composable
private fun PlanOption(
    title: String,
    subtitle: String,
    price: String,
    period: String,
    isActive: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (isActive) IosBlue else Color(0xFFF0F0F0)
    val bgColor = if (isActive) IosBlue.copy(alpha = 0.04f) else Color.White

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(bgColor)
            .border(2.dp, borderColor, RoundedCornerShape(20.dp))
            .clickable { onClick() }
    ) {
        if (isActive) {
            Box(
                modifier = Modifier.align(Alignment.TopEnd)
                    .background(IosBlue, RoundedCornerShape(topEnd = 18.dp, bottomStart = 12.dp))
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text("Plano Ativo", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White, letterSpacing = 0.5.sp)
            }
        }
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1C1C1E))
                Box(
                    modifier = Modifier.size(22.dp).background(
                        if (isActive) IosBlue else Color.White,
                        CircleShape
                    ).border(2.dp, if (isActive) IosBlue else Color(0xFFD1D1D6), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (isActive) Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(14.dp))
                }
            }
            Spacer(Modifier.height(4.dp))
            Text(subtitle, fontSize = 14.sp, color = IosSecondaryText)
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(price, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1C1C1E))
                Text(period, fontSize = 14.sp, color = IosSecondaryText, modifier = Modifier.padding(bottom = 2.dp))
            }
        }
    }
}

@Composable
private fun LogoBottomSheet(onUpload: () -> Unit, onDelete: () -> Unit, onDismiss: () -> Unit) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
            Box(Modifier.fillMaxSize().clickable { onDismiss() })
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f))
                ) {
                    Column {
                        Box(
                            modifier = Modifier.fillMaxWidth().clickable { onUpload() }.padding(vertical = 14.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Fazer upload", fontSize = 17.sp, color = IosBlue)
                        }
                        HorizontalDivider(color = Color(0xFFE5E5E5))
                        Box(
                            modifier = Modifier.fillMaxWidth().clickable { onDelete() }.padding(vertical = 14.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Excluir foto", fontSize = 17.sp, color = IosRed)
                        }
                    }
                }
                Card(
                    modifier = Modifier.fillMaxWidth().clickable { onDismiss() },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f))
                ) {
                    Box(modifier = Modifier.fillMaxWidth().padding(vertical = 14.dp), contentAlignment = Alignment.Center) {
                        Text("Cancelar", fontSize = 17.sp, fontWeight = FontWeight.SemiBold, color = IosBlue)
                    }
                }
            }
        }
    }
}

@Composable
private fun PrivacidadeDialog(onDismiss: () -> Unit) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Política de Privacidade", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1C1C1E))
                    TextButton(onClick = onDismiss) {
                        Text("Fechar", color = IosBlue, fontWeight = FontWeight.SemiBold, fontSize = 17.sp)
                    }
                }
                HorizontalDivider(color = Color(0xFFF2F2F7))
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp, vertical = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    PrivText("A FINAX – Gerenciador Empresarial valoriza a privacidade e a proteção dos dados de seus usuários. Esta Política de Privacidade descreve como coletamos, utilizamos, armazenamos, protegemos e compartilhamos informações quando você utiliza nossos serviços.")
                    PrivText("Ao criar uma conta ou utilizar o FINAX, você declara que leu, compreendeu e concorda com os termos desta Política.")
                    PrivSection("1. SOBRE O FINAX", "O FINAX é uma plataforma de gestão empresarial destinada a auxiliar empresas, profissionais autônomos e prestadores de serviços na administração de clientes, orçamentos, ordens de serviço, pagamentos, recebimentos, agenda, relatórios e demais atividades relacionadas à gestão de negócios.")
                    PrivSection("2. DADOS COLETADOS", "Coletamos dados de cadastro (nome, e-mail, telefone, empresa, CPF/CNPJ), dados inseridos pelo usuário (clientes, ordens de serviço, orçamentos, informações financeiras) e dados coletados automaticamente (IP, dispositivo, sistema operacional, data de acesso).")
                    PrivSection("3. FINALIDADE DO USO DOS DADOS", "Os dados são usados para permitir o acesso ao sistema, gerenciar contas, armazenar informações empresariais, gerar relatórios, melhorar funcionalidades, garantir segurança, realizar suporte técnico, cumprir obrigações legais e prevenir fraudes.")
                    PrivSection("4. DADOS DE CLIENTES", "O FINAX permite cadastrar informações de seus próprios clientes. O usuário é legítimo responsável pela coleta e uso dessas informações, comprometendo-se a observar a LGPD.")
                    PrivSection("5. COMPARTILHAMENTO DE DADOS", "O FINAX não vende ou comercializa dados pessoais. Informações podem ser compartilhadas apenas por obrigação legal, determinação judicial, com prestadores de serviços necessários ao funcionamento, ou para proteção de direitos.")
                    PrivSection("6. SEGURANÇA", "Empregamos medidas técnicas e administrativas incluindo criptografia, controle de acesso, autenticação, monitoramento, backups periódicos e atualizações de segurança.")
                    PrivSection("7. RETENÇÃO DOS DADOS", "Os dados permanecerão armazenados enquanto necessários para prestação dos serviços ou cumprimento de obrigações legais.")
                    PrivSection("8. DIREITOS DOS USUÁRIOS", "O usuário pode solicitar confirmação de tratamento, acesso aos dados, correção, atualização, exclusão, revogação de consentimentos e informações sobre compartilhamento.")
                    PrivSection("9. RESPONSABILIDADES DO USUÁRIO", "O usuário compromete-se a fornecer informações verdadeiras, manter confidencialidade da senha, não compartilhar credenciais, utilizar a plataforma de forma lícita e ter autorização legal para inserir dados de terceiros.")
                    PrivSection("10. CONTATO", "Para dúvidas, solicitações ou questões relacionadas à privacidade e proteção de dados, entre em contato pelos canais oficiais disponibilizados pelo FINAX.")
                    Spacer(Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
private fun PrivSection(title: String, body: String) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1C1C1E))
        Text(body, fontSize = 14.sp, color = Color(0xFF4A4A4A), lineHeight = 20.sp)
    }
}

@Composable
private fun PrivText(text: String) {
    Text(text, fontSize = 14.sp, color = Color(0xFF4A4A4A), lineHeight = 20.sp)
}

private fun exportCsv(context: Context, uri: Uri, ordens: List<OrdemServico>) {
    try {
        context.contentResolver.openOutputStream(uri)?.use { stream ->
            val writer = OutputStreamWriter(stream, Charsets.UTF_8)
            writer.write("﻿")
            writer.write("ID;Cliente;Servico;Preco (R\$);Forma de Pagamento;Contato;Data;Status\n")
            ordens.forEach { os ->
                val cliente = os.cliente.replace("\"", "\"\"")
                val servico = os.servico.replace("\"", "\"\"")
                writer.write("${os.id};\"$cliente\";\"$servico\";${os.preco.toString().replace(".", ",")};${os.formaPagamento};${os.contato};${os.dataOrcamento};${os.status}\n")
            }
            writer.flush()
        }
        Toast.makeText(context, "Planilha exportada com sucesso!", Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {
        Toast.makeText(context, "Erro ao exportar planilha.", Toast.LENGTH_SHORT).show()
    }
}

private fun exportBackup(context: Context, uri: Uri, uiState: AppUiState) {
    try {
        val backup = mapOf(
            "version" to "1.0",
            "ordens" to uiState.ordens,
            "lembretes" to uiState.lembretes,
            "userProfile" to uiState.userProfile
        )
        context.contentResolver.openOutputStream(uri)?.use { stream ->
            stream.write(Gson().toJson(backup).toByteArray())
        }
        Toast.makeText(context, "Backup exportado com sucesso!", Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {
        Toast.makeText(context, "Erro ao exportar backup.", Toast.LENGTH_SHORT).show()
    }
}

private fun importBackup(context: Context, uri: Uri, onImport: (List<OrdemServico>, List<Lembrete>, UserProfile) -> Unit) {
    try {
        val text = context.contentResolver.openInputStream(uri)?.use { stream ->
            BufferedReader(InputStreamReader(stream)).readText()
        } ?: return
        val map = Gson().fromJson<Map<String, Any>>(text, object : TypeToken<Map<String, Any>>() {}.type)
        val gson = Gson()
        val ordensJson = gson.toJson(map["ordens"])
        val lembretesJson = gson.toJson(map["lembretes"])
        val profileJson = gson.toJson(map["userProfile"])
        val ordens = gson.fromJson<List<OrdemServico>>(ordensJson, object : TypeToken<List<OrdemServico>>() {}.type) ?: emptyList()
        val lembretes = gson.fromJson<List<Lembrete>>(lembretesJson, object : TypeToken<List<Lembrete>>() {}.type) ?: emptyList()
        val profile = gson.fromJson(profileJson, UserProfile::class.java) ?: UserProfile()
        onImport(ordens, lembretes, profile)
        Toast.makeText(context, "Backup importado com sucesso!", Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {
        Toast.makeText(context, "Formato de backup inválido.", Toast.LENGTH_SHORT).show()
    }
}
