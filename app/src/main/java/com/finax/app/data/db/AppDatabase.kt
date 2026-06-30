package com.finax.app.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.finax.app.data.model.Lembrete
import com.finax.app.data.model.OrdemServico

@Database(
    entities = [OrdemServico::class, Lembrete::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun ordemServicoDao(): OrdemServicoDao
    abstract fun lembreteDao(): LembreteDao
}
