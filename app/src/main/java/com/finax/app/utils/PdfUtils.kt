package com.finax.app.utils

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import com.finax.app.data.model.OrdemServico
import com.finax.app.data.model.UserProfile
import java.io.File
import java.io.FileOutputStream

object PdfUtils {

    private fun sharePdf(context: Context, file: File) {
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Compartilhar PDF"))
    }

    fun generateOrcamento(
        context: Context,
        cliente: String,
        servico: String,
        preco: String,
        formaPagamento: String,
        validadeOrcamento: String,
        dataOrcamento: String,
        userProfile: UserProfile
    ) {
        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = document.startPage(pageInfo)
        val canvas = page.canvas

        val titlePaint = Paint().apply {
            color = Color.BLACK
            textSize = 24f
            isFakeBoldText = true
        }
        val bodyPaint = Paint().apply {
            color = Color.DKGRAY
            textSize = 14f
        }
        val boldPaint = Paint().apply {
            color = Color.BLACK
            textSize = 14f
            isFakeBoldText = true
        }
        val linePaint = Paint().apply {
            color = Color.LTGRAY
            strokeWidth = 1f
        }

        val companyName = userProfile.nomeEmpresa.ifEmpty { "Sua Empresa" }
        val companyPhone = userProfile.telefone

        var y = 60f

        canvas.drawText("ORÇAMENTO DE SERVIÇO", 297f, y, titlePaint.apply { textAlign = Paint.Align.CENTER })
        y += 10f
        canvas.drawLine(40f, y, 555f, y, linePaint)
        y += 30f

        canvas.drawText("Olá, $cliente!", 40f, y, bodyPaint)
        y += 20f
        canvas.drawText("Segue o orçamento solicitado:", 40f, y, bodyPaint)
        y += 30f

        canvas.drawText("Descrição do Serviço:", 40f, y, boldPaint)
        y += 20f
        canvas.drawText(servico, 40f, y, bodyPaint)
        y += 30f

        canvas.drawText("Valor do Serviço:", 40f, y, boldPaint)
        y += 20f
        canvas.drawText("R$ $preco", 40f, y, bodyPaint.apply { color = Color.rgb(0, 122, 255) })
        bodyPaint.color = Color.DKGRAY
        y += 30f

        canvas.drawText("Data do Orçamento:", 40f, y, boldPaint)
        y += 20f
        canvas.drawText(dataOrcamento, 40f, y, bodyPaint)
        y += 30f

        canvas.drawText("Observações:", 40f, y, boldPaint)
        y += 20f
        canvas.drawText("• Forma de pagamento: $formaPagamento", 50f, y, bodyPaint)
        y += 20f
        canvas.drawText("• Validade deste orçamento: $validadeOrcamento", 50f, y, bodyPaint)
        y += 40f

        canvas.drawLine(40f, y, 555f, y, linePaint)
        y += 20f

        canvas.drawText("Agradecemos pela oportunidade. Ficamos à disposição.", 40f, y, bodyPaint)
        y += 30f
        canvas.drawText("Atenciosamente,", 40f, y, bodyPaint)
        y += 20f
        canvas.drawText(companyName, 40f, y, boldPaint)
        if (companyPhone.isNotEmpty()) {
            y += 20f
            canvas.drawText("Telefone: $companyPhone", 40f, y, bodyPaint)
        }

        document.finishPage(page)

        val file = File(context.cacheDir, "Orcamento_${cliente.replace(" ", "_")}.pdf")
        document.writeTo(FileOutputStream(file))
        document.close()

        sharePdf(context, file)
    }

    fun generateExtrato(
        context: Context,
        ordens: List<OrdemServico>,
        mes: String,
        ano: Int,
        userProfile: UserProfile
    ) {
        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = document.startPage(pageInfo)
        val canvas = page.canvas

        val titlePaint = Paint().apply {
            color = Color.BLACK
            textSize = 20f
            isFakeBoldText = true
        }
        val headerPaint = Paint().apply {
            color = Color.WHITE
            textSize = 12f
            isFakeBoldText = true
        }
        val bodyPaint = Paint().apply {
            color = Color.DKGRAY
            textSize = 11f
        }
        val boldPaint = Paint().apply {
            color = Color.BLACK
            textSize = 11f
            isFakeBoldText = true
        }
        val bgPaint = Paint().apply { color = Color.rgb(245, 245, 245) }
        val greenPaint = Paint().apply { color = Color.rgb(34, 197, 94) }
        val linePaint = Paint().apply {
            color = Color.LTGRAY
            strokeWidth = 0.5f
        }

        val companyName = userProfile.nomeEmpresa.ifEmpty { "Sua Empresa" }
        val companyPhone = userProfile.telefone
        val totalRecebido = ordens.sumOf { it.preco }

        var y = 40f

        canvas.drawText("EXTRATO DE SERVIÇOS", 40f, y, titlePaint)
        y += 20f
        canvas.drawText(companyName, 40f, y, bodyPaint)
        if (companyPhone.isNotEmpty()) canvas.drawText(" • $companyPhone", 40f + titlePaint.measureText(companyName), y, bodyPaint)
        y += 10f
        canvas.drawLine(40f, y, 555f, y, linePaint)
        y += 25f

        // Summary cards
        val cardW = 160f
        val cardH = 50f
        val card1X = 40f
        val card2X = 215f
        val card3X = 390f

        canvas.drawRoundRect(RectF(card1X, y, card1X + cardW, y + cardH), 6f, 6f, bgPaint)
        canvas.drawRoundRect(RectF(card2X, y, card2X + cardW, y + cardH), 6f, 6f, bgPaint)
        canvas.drawRoundRect(RectF(card3X, y, card3X + cardW, y + cardH), 6f, 6f, bgPaint)

        val smallPaint = Paint().apply { color = Color.GRAY; textSize = 9f }
        canvas.drawText("Valor Total", card1X + 10f, y + 16f, smallPaint)
        canvas.drawText("Qtd. Serviços", card2X + 10f, y + 16f, smallPaint)
        canvas.drawText("Período", card3X + 10f, y + 16f, smallPaint)

        canvas.drawText(formatCurrency(totalRecebido), card1X + 10f, y + 38f, boldPaint)
        canvas.drawText("${ordens.size}", card2X + 10f, y + 38f, boldPaint)
        canvas.drawText("$mes/$ano", card3X + 10f, y + 38f, boldPaint)

        y += cardH + 25f

        // Table header
        val tableHeaderPaint = Paint().apply { color = Color.rgb(50, 50, 50) }
        canvas.drawRect(40f, y - 14f, 555f, y + 6f, bgPaint)
        canvas.drawText("Data", 45f, y, tableHeaderPaint.apply { isFakeBoldText = true; textSize = 10f })
        canvas.drawText("Cliente", 120f, y, tableHeaderPaint)
        canvas.drawText("Serviço", 250f, y, tableHeaderPaint)
        canvas.drawText("Valor", 495f, y, tableHeaderPaint.apply { textAlign = Paint.Align.RIGHT })
        y += 10f
        canvas.drawLine(40f, y, 555f, y, linePaint)
        y += 14f

        // Table rows
        ordens.forEachIndexed { idx, os ->
            if (y > 780f) return@forEachIndexed // skip if out of page
            if (idx % 2 == 0) {
                canvas.drawRect(40f, y - 10f, 555f, y + 8f, Paint().apply { color = Color.rgb(248, 248, 248) })
            }
            val rowPaint = Paint().apply { color = Color.rgb(80, 80, 80); textSize = 10f }
            val valorPaint = Paint().apply {
                color = Color.rgb(30, 30, 30); textSize = 10f; isFakeBoldText = true
                textAlign = Paint.Align.RIGHT
            }
            canvas.drawText(os.dataOrcamento, 45f, y, rowPaint)
            canvas.drawText(os.cliente.take(16), 120f, y, rowPaint)
            canvas.drawText(os.servico.take(20), 250f, y, rowPaint)
            canvas.drawText(formatCurrency(os.preco), 555f, y, valorPaint)
            y += 18f
        }

        canvas.drawLine(40f, y, 555f, y, linePaint)
        y += 20f

        // Total footer
        canvas.drawRoundRect(RectF(380f, y - 14f, 555f, y + 6f), 4f, 4f, greenPaint)
        canvas.drawText("TOTAL GERAL:", 390f, y, headerPaint.apply { textSize = 10f })
        canvas.drawText(formatCurrency(totalRecebido), 550f, y, headerPaint.apply { textAlign = Paint.Align.RIGHT })

        // Footer
        val footerPaint = Paint().apply { color = Color.LTGRAY; textSize = 8f }
        val emissao = "Emitido em: ${java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale("pt", "BR")).format(java.util.Date())}"
        canvas.drawText(emissao, 40f, 820f, footerPaint)
        canvas.drawText("$companyName${if (companyPhone.isNotEmpty()) " • $companyPhone" else ""}", 555f, 820f, footerPaint.apply { textAlign = Paint.Align.RIGHT })

        document.finishPage(page)

        val file = File(context.cacheDir, "Extrato_${mes}_$ano.pdf")
        document.writeTo(FileOutputStream(file))
        document.close()

        sharePdf(context, file)
    }

    fun generateGarantia(
        context: Context,
        os: OrdemServico,
        days: Int,
        userProfile: UserProfile
    ) {
        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = document.startPage(pageInfo)
        val canvas = page.canvas

        val titlePaint = Paint().apply {
            color = Color.BLACK
            textSize = 22f
            isFakeBoldText = true
            textAlign = Paint.Align.CENTER
        }
        val subtitlePaint = Paint().apply {
            color = Color.DKGRAY
            textSize = 16f
            textAlign = Paint.Align.CENTER
        }
        val bodyPaint = Paint().apply { color = Color.DKGRAY; textSize = 13f }
        val boldPaint = Paint().apply { color = Color.BLACK; textSize = 13f; isFakeBoldText = true }
        val linePaint = Paint().apply { color = Color.LTGRAY; strokeWidth = 1f }

        val companyName = userProfile.nomeEmpresa.ifEmpty { "Minha Empresa" }

        var y = 80f
        canvas.drawText("Certificado de Garantia", 297f, y, titlePaint)
        y += 25f
        canvas.drawText(companyName, 297f, y, subtitlePaint)
        y += 15f
        canvas.drawLine(40f, y, 555f, y, linePaint)
        y += 40f

        canvas.drawText("Cliente:", 40f, y, boldPaint)
        canvas.drawText(os.cliente, 160f, y, bodyPaint)
        y += 25f

        canvas.drawText("Serviço Realizado:", 40f, y, boldPaint)
        y += 20f
        canvas.drawText(os.servico, 40f, y, bodyPaint)
        y += 25f

        canvas.drawText("Data do Serviço:", 40f, y, boldPaint)
        canvas.drawText(os.dataOrcamento, 160f, y, bodyPaint)
        y += 25f

        canvas.drawText("Valor Original:", 40f, y, boldPaint)
        canvas.drawText(formatCurrency(os.preco), 160f, y, bodyPaint)
        y += 40f

        canvas.drawLine(40f, y, 555f, y, linePaint)
        y += 25f

        canvas.drawText("Prazo de Garantia: $days dias", 40f, y, boldPaint.apply { textSize = 15f })
        y += 25f

        val validUntil = run {
            val parts = os.dataOrcamento.split("/")
            if (parts.size >= 3) {
                val cal = java.util.Calendar.getInstance()
                cal.set(
                    parts[2].toIntOrNull() ?: 2024,
                    (parts[1].toIntOrNull() ?: 1) - 1,
                    parts[0].toIntOrNull() ?: 1
                )
                cal.add(java.util.Calendar.DAY_OF_MONTH, days)
                "${cal.get(java.util.Calendar.DAY_OF_MONTH).toString().padStart(2, '0')}/" +
                        "${(cal.get(java.util.Calendar.MONTH) + 1).toString().padStart(2, '0')}/" +
                        "${cal.get(java.util.Calendar.YEAR)}"
            } else "-"
        }

        canvas.drawText("Válido até: $validUntil", 40f, y, bodyPaint.apply { textSize = 14f })
        y += 60f

        val footerPaint = Paint().apply {
            color = Color.GRAY
            textSize = 10f
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("Este documento certifica que o serviço descrito acima possui garantia técnica", 297f, y, footerPaint)
        y += 15f
        canvas.drawText("pelo período estipulado, oferecida por $companyName.", 297f, y, footerPaint)

        document.finishPage(page)

        val file = File(context.cacheDir, "Garantia_${os.id}.pdf")
        document.writeTo(FileOutputStream(file))
        document.close()

        sharePdf(context, file)
    }
}
