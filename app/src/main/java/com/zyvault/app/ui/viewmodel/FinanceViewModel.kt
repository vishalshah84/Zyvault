package com.zyvault.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.plaid.link.result.LinkSuccess
import com.zyvault.app.data.api.ExchangeTokenRequest
import com.zyvault.app.data.api.LinkTokenRequest
import com.zyvault.app.data.api.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class BankAccount(
    val id: String = "",
    val userId: String = "",
    val name: String = "",
    val balance: Double = 0.0,
    val initial: String = "",
    val colorHex: String = "#1A73E8",
    val billsCovered: Int = 0
)

data class CreditCard(
    val id: String = "",
    val userId: String = "",
    val name: String = "",
    val lastFour: String = "",
    val balance: Double = 0.0,
    val limit: Int = 0,
    val dueText: String = "",
    val dueSoon: Boolean = false
)

data class FinanceUiState(
    val bankAccounts: List<BankAccount> = emptyList(),
    val creditCards: List<CreditCard> = emptyList(),
    val isLoading: Boolean = false,
    val totalBalance: Double = 0.0,
    val linkToken: String? = null
)

class FinanceViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val _uiState = MutableStateFlow(FinanceUiState())
    val uiState: StateFlow<FinanceUiState> = _uiState.asStateFlow()

    init {
        fetchFinanceData()
        // preparePlaidLink() // Plaid implementation commented out for now
    }

    /* 
    private fun preparePlaidLink() {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                // In production, this fetches a real token from your backend
                val response = RetrofitClient.plaidApi.createLinkToken(LinkTokenRequest(userId))
                _uiState.value = _uiState.value.copy(linkToken = response.linkToken)
                android.util.Log.d("PlaidLog", "Link Token fetched successfully: ${response.linkToken}")
            } catch (e: Exception) {
                android.util.Log.e("PlaidLog", "Failed to fetch Link Token: ${e.message}")
                // For development/demo, you can manually set a sandbox token here if you have one
            }
        }
    }

    fun onPlaidSuccess(success: LinkSuccess) {
        val userId = auth.currentUser?.uid ?: return
        val publicToken = success.publicToken
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                // 1. Exchange public token via backend
                RetrofitClient.plaidApi.exchangePublicToken(ExchangeTokenRequest(publicToken, userId))
                
                // 2. Refresh data from Firestore (backend should have populated it)
                fetchFinanceData()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }
    */

    fun fetchFinanceData() {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val banks = firestore.collection("bank_accounts")
                    .whereEqualTo("userId", userId)
                    .get()
                    .await()
                    .toObjects(BankAccount::class.java)

                val cards = firestore.collection("credit_cards")
                    .whereEqualTo("userId", userId)
                    .get()
                    .await()
                    .toObjects(CreditCard::class.java)

                val total = banks.sumOf { it.balance }

                _uiState.value = _uiState.value.copy(
                    bankAccounts = banks,
                    creditCards = cards,
                    totalBalance = total,
                    isLoading = false
                )

                firestore.collection("users").document(userId).update(
                    mapOf(
                        "totalSaved" to total,
                        "bankAccountCount" to banks.size
                    )
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    fun resetFinanceData() {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val banks = firestore.collection("bank_accounts").whereEqualTo("userId", userId).get().await()
                banks.documents.forEach { it.reference.delete().await() }

                val cards = firestore.collection("credit_cards").whereEqualTo("userId", userId).get().await()
                cards.documents.forEach { it.reference.delete().await() }

                firestore.collection("users").document(userId).update(
                    mapOf("totalSaved" to 0.0, "bankAccountCount" to 0)
                ).await()

                fetchFinanceData()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }
}
