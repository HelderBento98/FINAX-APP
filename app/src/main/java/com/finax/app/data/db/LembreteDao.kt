package com.finax.app.data.db

import androidx.room.*
import com.finax.app.data.model.Lembrete
import kotlinx.coroutines.flow.Flow

@Dao
interface LembreteDao {

    @Query("SELECT * FROM lembretes")
    fun getAllFlow(): Flow<List<Lembrete>>

    @Query("SELECT * FROM lembretes")
    suspend fun getAll(): List<Lembrete>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(lembrete: Lembrete)

    @Query("DELETE FROM lembretes WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("UPDATE lembretes SET notificado = 1 WHERE id = :id")
    suspend fun markAsNotified(id: String)

    @Query("DELETE FROM lembretes")
    suspend fun deleteAll()
}
