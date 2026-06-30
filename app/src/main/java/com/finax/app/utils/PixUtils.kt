package com.finax.app.utils

import java.text.Normalizer

fun generatePixPayload(
    key: String,
    amount: Double,
    merchantName: String = "FINAX",
    merchantCity: String = "SAO PAULO",
    txId: String = "***"
): String {
    val cleanKey = key.trim()

    fun f(id: String, value: String): String {
        val len = value.length.toString().padStart(2, '0')
        return "$id$len$value"
    }

    val gui = f("00", "br.gov.pix")
    val keyField = f("01", cleanKey)
    val merchantAccountInfo = f("26", gui + keyField)

    var payload = "000201"
    payload += merchantAccountInfo
    payload += f("52", "0000")
    payload += f("53", "986")

    if (amount > 0) {
        payload += f("54", String.format("%.2f", amount))
    }

    payload += f("58", "BR")

    fun cleanStr(s: String, maxLen: Int): String {
        return Normalizer.normalize(s, Normalizer.Form.NFD)
            .replace(Regex("[\\u0300-\\u036f]"), "")
            .replace(Regex("[^a-zA-Z0-9 ]"), "")
            .uppercase()
            .take(maxLen)
    }

    val cleanName = cleanStr(merchantName, 25).ifEmpty { "FINAX" }
    val cleanCity = cleanStr(merchantCity, 15).ifEmpty { "SAO PAULO" }

    payload += f("59", cleanName)
    payload += f("60", cleanCity)

    val cleanTxId = cleanStr(txId, 25).replace(" ", "").ifEmpty { "***" }
    val txIdField = f("05", cleanTxId)
    payload += f("62", txIdField)

    payload += "6304"

    val checksum = crc16(payload)
    return payload + checksum
}

private fun crc16(data: String): String {
    var crc = 0xFFFF
    val polynomial = 0x1021

    for (char in data) {
        val charCode = char.code
        for (bit in 0..7) {
            val bitOn = ((charCode shr (7 - bit)) and 1) == 1
            val crcOn = ((crc shr 15) and 1) == 1
            crc = crc shl 1
            if (bitOn != crcOn) crc = crc xor polynomial
        }
    }

    crc = crc and 0xFFFF
    return crc.toString(16).uppercase().padStart(4, '0')
}
