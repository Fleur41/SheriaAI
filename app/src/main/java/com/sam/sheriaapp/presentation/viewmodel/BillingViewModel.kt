package com.sam.sheriaapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sam.sheriaapp.domain.model.Billing
import com.sam.sheriaapp.domain.repository.BillingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.Dispatcher

@HiltViewModel
class BillingViewModel @Inject constructor(
    private val billingRepository: BillingRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(BillingUiState(isLoading = true))
    val uiState: StateFlow<BillingUiState> get() = _uiState.asStateFlow()

    init {
        loadBillingData()
    }
    private fun loadBillingData(){
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val currentPlan = billingRepository.getCurrentPlan()
                val billingHistory = billingRepository.getBillingHistory()
                val paymentMethod = billingRepository.getPaymentMethod()

                _uiState.value = _uiState.value.copy(
                    currentPlan = currentPlan,
                    billingHistory = billingHistory,
                    paymentMethod = paymentMethod,
                    isLoading = false,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Failed to load billing data",
                    isLoading = false
                )
            }

        }
    }

}

data class BillingUiState(
    val currentPlan: Billing? = null,
    val billingHistory: List<Billing> = emptyList(),
    val paymentMethod: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)
