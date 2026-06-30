package com.finax.app.data.repository

import com.finax.app.data.db.LembreteDao
import com.finax.app.data.db.OrdemServicoDao
import com.finax.app.data.model.Lembrete
import com.finax.app.data.model.OrdemServico
import com.finax.app.data.model.UserProfile
import com.finax.app.data.preferences.UserPreferences
import kotlinx.coroutines.flow.Flow

class AppRepository(
    private val ordemServicoDao: OrdemServicoDao,
    private val lembreteDao: LembreteDao,
    private val userPreferences: UserPreferences
) {

    val ordensFlow: Flow<List<OrdemServico>> = ordemServicoDao.getAllFlow()
    val lembretesFlow: Flow<List<Lembrete>> = lembreteDao.getAllFlow()
    val userProfileFlow: Flow<UserProfile> = userPreferences.userProfileFlow

    suspend fun addOrdem(os: OrdemServico) = ordemServicoDao.insert(os)

    suspend fun getAllOrdens(): List<OrdemServico> = ordemServicoDao.getAll()

    suspend fun updateOSStatus(id: String, newStatus: String) {
        val all = ordemServicoDao.getAll()
        val os = all.find { it.id == id } ?: return
        ordemServicoDao.update(os.copy(status = newStatus))
    }

    suspend fun deleteOS(id: String) = ordemServicoDao.deleteById(id)

    suspend fun addLembrete(lembrete: Lembrete) = lembreteDao.insert(lembrete)

    suspend fun deleteLembrete(id: String) = lembreteDao.deleteById(id)

    suspend fun markLembreteAsNotified(id: String) = lembreteDao.markAsNotified(id)

    suspend fun updateUserProfile(profile: UserProfile) = userPreferences.updateUserProfile(profile)

    suspend fun getAllLembretes(): List<Lembrete> = lembreteDao.getAll()

    suspend fun importData(ordens: List<OrdemServico>, lembretes: List<Lembrete>, profile: UserProfile) {
        ordemServicoDao.deleteAll()
        lembreteDao.deleteAll()
        ordens.forEach { ordemServicoDao.insert(it) }
        lembretes.forEach { lembreteDao.insert(it) }
        userPreferences.updateUserProfile(profile)
    }
}
