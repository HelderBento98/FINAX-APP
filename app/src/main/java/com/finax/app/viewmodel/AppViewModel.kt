package com.finax.app.viewmodel

import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.android.billingclient.api.ProductDetails
import com.finax.app.FinaxApp
import com.finax.app.data.db.AppDatabase
import com.finax.app.data.model.Lembrete
import com.finax.app.data.model.OrdemServico
import com.finax.app.data.model.UserProfile
import com.finax.app.data.preferences.UserPreferences
import com.finax.app.data.repository.AppRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.UUID

const val TRIAL_DAYS = 30

data class AppUiState(
    val ordens: List<OrdemServico> = emptyList(),
    val lembretes: List<Lembrete> = emptyList(),
    val userProfile: UserProfile = UserProfile(),
    val selectedMonth: Int = Calendar.getInstance().get(Calendar.MONTH),
    val selectedYear: Int = Calendar.getInstance().get(Calendar.YEAR)
)

/** Whether the user may use the app: either inside the free trial or subscribed. */
data class GateState(
    val loading: Boolean = true,
    val hasAccess: Boolean = false,
    val isSubscribed: Boolean = false,
    val trialActive: Boolean = false,
    val trialDaysLeft: Int = 0
)

class AppViewModel(application: Application) : AndroidViewModel(application) {

    private val database = Room.databaseBuilder(
        application,
        AppDatabase::class.java,
        "finax_database"
    ).build()

    val repository = AppRepository(
        database.ordemServicoDao(),
        database.lembreteDao(),
        UserPreferences(application)
    )

    private val billingManager = (application as FinaxApp).billingManager

    private val _selectedMonth = MutableStateFlow(Calendar.getInstance().get(Calendar.MONTH))
    private val _selectedYear = MutableStateFlow(Calendar.getInstance().get(Calendar.YEAR))

    init {
        viewModelScope.launch { repository.ensureTrialStarted() }
        billingManager.queryPurchases()
    }

    /** Subscription products available for purchase (prices come from Google Play). */
    val products: StateFlow<List<ProductDetails>> = billingManager.products

    /** Access gate combining the 30-day free trial with the Play subscription state. */
    val gateState: StateFlow<GateState> = combine(
        repository.trialStartFlow,
        billingManager.isSubscribed
    ) { trialStart, subscribed ->
        val daysUsed = if (trialStart <= 0L) 0
        else ((System.currentTimeMillis() - trialStart) / 86_400_000L).toInt()
        val daysLeft = (TRIAL_DAYS - daysUsed).coerceAtLeast(0)
        val trialActive = trialStart <= 0L || daysLeft > 0
        GateState(
            loading = false,
            isSubscribed = subscribed,
            trialActive = trialActive,
            trialDaysLeft = daysLeft,
            hasAccess = subscribed || trialActive
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = GateState()
    )

    fun subscribe(activity: Activity, productDetails: ProductDetails) {
        billingManager.launchPurchase(activity, productDetails)
    }

    fun refreshPurchases() {
        billingManager.queryPurchases()
    }

    val uiState: StateFlow<AppUiState> = combine(
        repository.ordensFlow,
        repository.lembretesFlow,
        repository.userProfileFlow,
        _selectedMonth,
        _selectedYear
    ) { ordens, lembretes, userProfile, month, year ->
        AppUiState(
            ordens = ordens,
            lembretes = lembretes,
            userProfile = userProfile,
            selectedMonth = month,
            selectedYear = year
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AppUiState()
    )

    fun setGlobalDate(month: Int, year: Int) {
        _selectedMonth.value = month
        _selectedYear.value = year
    }

    fun addOS(
        cliente: String,
        servico: String,
        preco: Double,
        formaPagamento: String,
        contato: String,
        dataOrcamento: String,
        validadeOrcamento: String,
        onComplete: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            val allOrdens = repository.getAllOrdens()
            val parts = dataOrcamento.split("/")
            val month = parts.getOrNull(1)?.toIntOrNull()?.minus(1)
            val year = parts.getOrNull(2)?.toIntOrNull()

            val ordensDoMes = allOrdens.filter { os ->
                val p = os.dataOrcamento.split("/")
                val m = p.getOrNull(1)?.toIntOrNull()?.minus(1)
                val a = p.getOrNull(2)?.toIntOrNull()
                m == month && a == year
            }

            val maxNum = ordensDoMes.maxOfOrNull { os ->
                Regex("\\d+").find(os.id)?.value?.toIntOrNull() ?: 0
            } ?: 0

            val newId = "Nº${(maxNum + 1).toString().padStart(2, '0')}"

            val newOS = OrdemServico(
                id = newId,
                cliente = cliente,
                servico = servico,
                preco = preco,
                formaPagamento = formaPagamento,
                contato = contato,
                dataOrcamento = dataOrcamento,
                validadeOrcamento = validadeOrcamento,
                status = "AGUARDANDO INICIO"
            )

            repository.addOrdem(newOS)
            onComplete(newId)
        }
    }

    fun updateOSStatus(id: String, newStatus: String) {
        viewModelScope.launch { repository.updateOSStatus(id, newStatus) }
    }

    fun deleteOS(id: String) {
        viewModelScope.launch { repository.deleteOS(id) }
    }

    fun addLembrete(descricao: String, horario: String, data: String, celular: String = "") {
        viewModelScope.launch {
            repository.addLembrete(
                Lembrete(
                    id = UUID.randomUUID().toString(),
                    descricao = descricao,
                    horario = horario,
                    data = data,
                    celular = celular,
                    notificado = false
                )
            )
        }
    }

    fun deleteLembrete(id: String) {
        viewModelScope.launch { repository.deleteLembrete(id) }
    }

    fun markLembreteAsNotified(id: String) {
        viewModelScope.launch { repository.markLembreteAsNotified(id) }
    }

    fun updateUserProfile(profile: UserProfile) {
        viewModelScope.launch { repository.updateUserProfile(profile) }
    }

    fun importData(ordens: List<OrdemServico>, lembretes: List<Lembrete>, profile: UserProfile) {
        viewModelScope.launch { repository.importData(ordens, lembretes, profile) }
    }
}
