package com.rotibook

import android.app.Application
import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import java.util.Date


class ClientDetailViewModel(
    private val repository: RotiTrackerRepository,
    private val clientId: Int
) : ViewModel() {

    private val _client = MutableStateFlow<Client?>(null)
    val client: StateFlow<Client?> = _client

    private val _purchases = MutableStateFlow<List<RotiPurchase>>(emptyList())
    val purchases: StateFlow<List<RotiPurchase>> = _purchases

    private val _currentLanguage = MutableStateFlow(Translations.Language.ENGLISH)
    val currentLanguage: StateFlow<Translations.Language> = _currentLanguage

    init {
        viewModelScope.launch {
            repository.getClientById(clientId).collect { client ->
                _client.value = client
            }
        }
        viewModelScope.launch {
            repository.getPurchasesForClient(clientId).collect { purchases ->
                _purchases.value = purchases
            }
        }
    }

    fun addPurchase(quantity: Int) {
        viewModelScope.launch {
            repository.insertPurchase(RotiPurchase(clientId = clientId, quantity = quantity, date = Date()))
        }
    }

    fun updatePurchase(purchase: RotiPurchase) {
        viewModelScope.launch {
            repository.updatePurchase(purchase)
        }
    }

    fun deletePurchase(purchase: RotiPurchase) {
        viewModelScope.launch {
            repository.deletePurchase(purchase)
        }
    }

    fun toggleLanguage() {
        _currentLanguage.value = if (_currentLanguage.value == Translations.Language.ENGLISH) {
            Translations.Language.URDU
        } else {
            Translations.Language.ENGLISH
        }
    }

    class Factory(
        private val repository: RotiTrackerRepository,
        private val clientId: Int
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ClientDetailViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ClientDetailViewModel(repository, clientId) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

class ClientListViewModel(private val repository: RotiTrackerRepository) : ViewModel() {

    private val _clients = MutableStateFlow<List<Client>>(emptyList())
    private val _searchQuery = MutableStateFlow("")
    private val _currentLanguage = MutableStateFlow(Translations.Language.ENGLISH)
    val searchQuery: StateFlow<String> = _searchQuery
    val currentLanguage: StateFlow<Translations.Language> = _currentLanguage

    val clients: StateFlow<List<Client>> = combine(_clients, searchQuery) { clients, query ->
        if (query.isEmpty()) {
            clients
        } else {
            clients.filter { it.name.contains(query, ignoreCase = true) || it.phoneNumber.contains(query) }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            repository.allClients.collect { clients ->
                _clients.value = clients
            }
        }
    }

    fun addClient(name: String, phoneNumber: String) {
        viewModelScope.launch {
            repository.insertClient(Client(name = name, phoneNumber = phoneNumber))
        }
    }

    fun updateClient(client: Client) {
        viewModelScope.launch {
            repository.updateClient(client)
        }
    }

    fun deleteClient(client: Client) {
        viewModelScope.launch {
            repository.deleteClient(client)
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggleLanguage() {
        _currentLanguage.value = if (_currentLanguage.value == Translations.Language.ENGLISH) {
            Translations.Language.URDU
        } else {
            Translations.Language.ENGLISH
        }
    }

    class Factory(private val repository: RotiTrackerRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ClientListViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ClientListViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

class DataExportImportViewModel(
    application: Application,
    private val repository: RotiTrackerRepository
) : AndroidViewModel(application) {

    private val _exportStatus = MutableStateFlow<ExportImportStatus>(ExportImportStatus.Idle)
    val exportStatus: StateFlow<ExportImportStatus> = _exportStatus

    private val _importStatus = MutableStateFlow<ExportImportStatus>(ExportImportStatus.Idle)
    val importStatus: StateFlow<ExportImportStatus> = _importStatus

    fun exportData(uri: Uri) {
        viewModelScope.launch {
            _exportStatus.value = ExportImportStatus.InProgress
            try {
                val clients = repository.allClients.first()
                val purchases = repository.getAllPurchases()
                DataExportImport.exportData(getApplication(), uri, clients, purchases)
                _exportStatus.value = ExportImportStatus.Success
            } catch (e: Exception) {
                _exportStatus.value = ExportImportStatus.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun importData(uri: Uri) {
        viewModelScope.launch {
            _importStatus.value = ExportImportStatus.InProgress
            try {
                val (clients, purchases) = DataExportImport.importData(getApplication(), uri)
                repository.importClients(clients)
                repository.importPurchases(purchases)
                _importStatus.value = ExportImportStatus.Success
            } catch (e: Exception) {
                _importStatus.value = ExportImportStatus.Error(e.message ?: "Unknown error")
            }
        }
    }

    sealed class ExportImportStatus {
        object Idle : ExportImportStatus()
        object InProgress : ExportImportStatus()
        object Success : ExportImportStatus()
        data class Error(val message: String) : ExportImportStatus()
    }
}




class RotiTrackerRepository(
    private val clientDao: ClientDao,
    private val rotiPurchaseDao: RotiPurchaseDao
) {
    val allClients: Flow<List<Client>> = clientDao.getAllClients()

    fun getClientById(clientId: Int): Flow<Client> = clientDao.getClientById(clientId)

    suspend fun insertClient(client: Client) {
        clientDao.insertClient(client)
    }

    suspend fun updateClient(client: Client) {
        clientDao.updateClient(client)
    }

    suspend fun deleteClient(client: Client) {
        clientDao.deleteClient(client)
    }

    fun getPurchasesForClient(clientId: Int): Flow<List<RotiPurchase>> {
        return rotiPurchaseDao.getPurchasesForClient(clientId)
    }

    suspend fun insertPurchase(purchase: RotiPurchase) {
        rotiPurchaseDao.insertPurchase(purchase)
    }

    suspend fun updatePurchase(purchase: RotiPurchase) {
        rotiPurchaseDao.updatePurchase(purchase)
    }

    suspend fun deletePurchase(purchase: RotiPurchase) {
        rotiPurchaseDao.deletePurchase(purchase)
    }

    suspend fun importClients(clients: List<Client>) {
        clients.forEach { client ->
            clientDao.insertClient(client)
        }
    }

    suspend fun importPurchases(purchases: List<RotiPurchase>) {
        purchases.forEach { purchase ->
            rotiPurchaseDao.insertPurchase(purchase)
        }
    }

    suspend fun getAllPurchases(): List<RotiPurchase> {
        return rotiPurchaseDao.getAllPurchases()
    }
}

