package com.finax.app.data.db

import androidx.room.*
import com.finax.app.data.model.OrdemServico
import kotlinx.coroutines.flow.Flow

@Dao
interface OrdemServicoDao {

    @Query("SELECT * FROM ordens_servico ORDER BY dataOrcamento DESC")
    fun getAllFlow(): Flow<List<OrdemServico>>

    @Query("SELECT * FROM ordens_servico")
    suspend fun getAll(): List<OrdemServico>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(os: OrdemServico)

    @Update
    suspend fun update(os: OrdemServico)

    @Query("DELETE FROM ordens_servico WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM ordens_servico")
    suspend fun deleteAll()
}
