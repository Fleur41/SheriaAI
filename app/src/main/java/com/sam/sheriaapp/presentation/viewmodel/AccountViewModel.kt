package com.sam.sheriaapp.presentation.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sam.sheriaapp.ImageUtils
import com.sam.sheriaapp.domain.model.Account
import com.sam.sheriaapp.domain.model.SampleAccounts
import com.sam.sheriaapp.domain.repository.AccountRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val repository: AccountRepository,

) : ViewModel() {
    private val _state = MutableStateFlow(AccountState())
    val state = _state.asStateFlow()

    private var allAccounts = emptyList<Account>()
    init {
        loadAccounts()
    }
    private fun loadAccounts() {
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = _state.value.copy(isLoading = true)

            try {
                val accounts = listOf(SampleAccounts.lucasBennett, SampleAccounts.ethanCarter)
                allAccounts = accounts
                _state.value = AccountState(
                    accounts = accounts,
                    filteredAccounts = accounts,
                    currentAccountIndex = 0,
                    isLoading = false
                )
            } catch (e: Exception) {
                _state.value = AccountState(
                    isLoading = false,
                    error = e.message ?: "Failed to load accounts"
                )
            }
        }
    }
//    private fun loadAccounts() {
//        viewModelScope.launch(Dispatchers.IO) {
//            _state.value = _state.value.copy(isLoading = true)
//
//            try {
//                val accounts = listOf(SampleAccounts.lucasBennett, SampleAccounts.ethanCarter)
//
//                _state.value = AccountState(
//                    accounts = accounts,
//                    currentAccountIndex = 0,
//                    isLoading = false
//                )
//            } catch (e: Exception) {
//                _state.value = AccountState(
//                    isLoading = false,
//                    error = e.message ?: "Failed to load accounts"
//                )
//            }
//        }
//    }
    fun searchAccounts(query: String) {
        if (query.isEmpty()) {
            _state.value = _state.value.copy(
                filteredAccounts = allAccounts,
                currentAccountIndex = 0
            )
        } else {
            val filtered = allAccounts.filter { account ->
                account.name.contains(query, ignoreCase = true) ||
                        account.email.contains(query, ignoreCase = true) ||
                        account.phone.contains(query, ignoreCase = true) ||
                        account.address.contains(query, ignoreCase = true)
            }
            _state.value = _state.value.copy(
                filteredAccounts = filtered,
                currentAccountIndex = if (filtered.isNotEmpty()) 0 else -1
            )
        }
    }

    fun clearSearch() {
        _state.value = _state.value.copy(
            filteredAccounts = allAccounts,
            currentAccountIndex = 0
        )
    }

    fun switchAccount(index: Int) {
        if (index in _state.value.accounts.indices) {
            _state.value = _state.value.copy(currentAccountIndex = index)
        }
    }

    fun nextAccount() {
        val nextIndex = (_state.value.currentAccountIndex + 1) % _state.value.accounts.size
        switchAccount(nextIndex)
    }

    fun previousAccount() {
        val prevIndex = (_state.value.currentAccountIndex - 1).takeIf { it >= 0 }
            ?: (_state.value.accounts.size - 1)
        switchAccount(prevIndex)
    }

    fun updateProfileImage(imageUri: String){
        viewModelScope.launch {
            val currentAccount = _state.value.currentAccount
            if (currentAccount != null){
                try {// Update repository
                    repository.updateProfileImage(currentAccount.id, imageUri)

                    // Update both allAccounts and filteredAccounts
                    val updatedAccounts = allAccounts.map { account ->
                        if (account.id == currentAccount.id) {
                            account.copy(profileImageUri = imageUri)
                        } else {
                            account
                        }
                    }

                    val updatedFilteredAccounts = _state.value.filteredAccounts.map { account ->
                        if (account.id == currentAccount.id) {
                            account.copy(profileImageUri = imageUri)
                        } else {
                            account
                        }
                    }

                    allAccounts = updatedAccounts
                    _state.value = _state.value.copy(
                        accounts = updatedAccounts,
                        filteredAccounts = updatedFilteredAccounts,
                        //currentAccountIndex = updatedAccounts.indexOf(currentAccount)
                    )
                } catch (e: Exception) {
                    _state.value = _state.value.copy(error = e.message ?: "Failed to update profile image")
                }
            }
        }

    }
//    fun updateProfileImage(imageUri: String) {
//        viewModelScope.launch {
////            println("DEBUG: Updating profile with: $imageUri")
//            val currentAccount = _state.value.currentAccount
//            if (currentAccount != null) {
//                try {
//                    repository.updateProfileImage(currentAccount.id, imageUri)
//                    val updatedAccounts = _state.value.accounts.map { account ->
//                        if (account.id == currentAccount.id) {
//                            println("DEBUG: Account updated with new image")
//                            account.copy(profileImageUri = imageUri)
//                        } else {
//                            account
//                        }
//                    }
//                    _state.value = _state.value.copy(accounts = updatedAccounts)
//                    println("DEBUG: State updated successfully")
//                } catch (e: Exception) {
//                    println("DEBUG: Error updating profile: ${e.message}")
//                    _state.value = _state.value.copy(error = e.message ?: "Failed to update profile image")
//                }
//            }
//        }
//    }




    // Update this function to handle URI instead of String
    fun handleImageSelection(context: Context, uri: Uri) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null) //Added this line
            try {
                val filePath = ImageUtils.copyImageToAppStorage(context, uri)
                updateProfileImage(filePath)
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = "Failed to process image: ${e.message}",
                    isLoading = false
                )
            }
        }
    }

}

data class AccountState(
    val accounts: List<Account> = emptyList(),
    val filteredAccounts: List<Account> = emptyList(),
    val currentAccountIndex: Int = 0,
    val isLoading: Boolean = true,
    val error: String? = null
){
    val currentAccount: Account? get() = filteredAccounts.getOrNull(currentAccountIndex)
//    val currentAccount: Account? get() = accounts.getOrNull(currentAccountIndex)
    val totalAccounts: Int get() = filteredAccounts.size
    //val totalAccounts: Int get() = accounts.size
    val isSearchActive: Boolean get() = filteredAccounts != accounts
}
