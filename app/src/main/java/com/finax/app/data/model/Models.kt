package com.finax.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ordens_servico")
data class OrdemServico(
    @PrimaryKey val id: String,
    val cliente: String,
    val servico: String,
    val preco: Double,
    val formaPagamento: String,
    val contato: String,
    val dataOrcamento: String,
    val validadeOrcamento: String = "A combinar",
    val status: String // "PAGO" | "PENDENTES" | "AGUARDANDO INICIO"
)

@Entity(tableName = "lembretes")
data class Lembrete(
    @PrimaryKey val id: String,
    val descricao: String,
    val horario: String,
    val data: String,
    val celular: String = "",
    val notificado: Boolean = false
)

data class UserProfile(
    val nomeEmpresa: String = "",
    val telefone: String = "",
    val dataCriacao: String = "",
    val cnpj: String = "",
    val email: String = "",
    val logo: String = "",
    val planoAtivo: String = "",
    val chavePix: String = ""
)

object StatusOS {
    const val PAGO = "PAGO"
    const val PENDENTES = "PENDENTES"
    const val AGUARDANDO_INICIO = "AGUARDANDO INICIO"
}
