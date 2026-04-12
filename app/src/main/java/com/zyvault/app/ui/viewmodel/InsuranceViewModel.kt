package com.zyvault.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class InsurancePolicy(
    val id: String = "",
    val userId: String = "",
    val type: String = "", // House, Health, Vehicle
    val provider: String = "",
    val policyNumber: String = "",
    val coverageAmount: Double = 0.0,
    val expiryDate: String = "",
    val premium: Double = 0.0,
    val status: String = "Active"
)

data class InsuranceUiState(
    val policies: List<InsurancePolicy> = emptyList(),
    val isLoading: Boolean = false
)

class InsuranceViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val _uiState = MutableStateFlow(InsuranceUiState())
    val uiState: StateFlow<InsuranceUiState> = _uiState.asStateFlow()

    init {
        fetchPolicies()
    }

    fun fetchPolicies() {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val snapshot = firestore.collection("insurance_policies")
                    .whereEqualTo("userId", userId)
                    .get()
                    .await()
                
                val policies = snapshot.toObjects(InsurancePolicy::class.java)
                _uiState.value = InsuranceUiState(policies = policies, isLoading = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }
}
