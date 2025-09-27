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
    private val repository: AccountRepository
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
        if (index in _state.value.filteredAccounts.indices) {
            _state.value = _state.value.copy(currentAccountIndex = index)
        }
    }

    fun nextAccount() {
        val nextIndex = (_state.value.currentAccountIndex + 1) % _state.value.filteredAccounts.size
        switchAccount(nextIndex)
    }

    fun previousAccount() {
        val prevIndex = (_state.value.currentAccountIndex - 1).takeIf { it >= 0 }
            ?: (_state.value.filteredAccounts.size - 1)
        switchAccount(prevIndex)
    }

    // UPDATED: Now accepts Base64 string instead of file path
    private fun updateProfileImage(base64Image: String) {
        viewModelScope.launch {
            val currentAccount = _state.value.currentAccount
            if (currentAccount != null) {
                try {
                    println("DEBUG: 🎯 Starting profile image update for account: ${currentAccount.id}")

                    // Try to update repository, but continue even if it fails
                    try {
                        repository.updateProfileImage(currentAccount.id, base64Image)
                        println("DEBUG: ✅ Repository update completed")
                    } catch (e: Exception) {
                        println("DEBUG: ⚠️ Repository update failed: ${e.message}. Continuing with UI update...")
                        // Don't return - continue with local state update
                    }

                    // Always update local state for immediate UI feedback
                    val updatedAccounts = allAccounts.map { account ->
                        if (account.id == currentAccount.id) {
                            account.copy(profileImageUri = base64Image)
                        } else {
                            account
                        }
                    }

                    val updatedFilteredAccounts = _state.value.filteredAccounts.map { account ->
                        if (account.id == currentAccount.id) {
                            account.copy(profileImageUri = base64Image)
                        } else {
                            account
                        }
                    }

                    allAccounts = updatedAccounts
                    _state.value = _state.value.copy(
                        accounts = updatedAccounts,
                        filteredAccounts = updatedFilteredAccounts,
                        isLoading = false,
                        error = null
                    )

                    println("DEBUG: ✅ UI state updated successfully")
                    println("DEBUG: ✅ New profileImageUri length: ${_state.value.currentAccount?.profileImageUri?.length}")

                } catch (e: Exception) {
                    println("DEBUG: ❌ Error in updateProfileImage: ${e.message}")
                    _state.value = _state.value.copy(
                        error = "Failed to update profile image: ${e.message}",
                        isLoading = false
                    )
                }
            } else {
                println("DEBUG: ❌ No current account found")
            }
        }
    }

    // UPDATED: Simplified using new ImageUtils.processSelectedImage()
    fun handleImageSelection(context: Context, uri: Uri) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                println("DEBUG: Starting image processing for URI: $uri")

                // Use the new simplified utility function
                val base64Image = ImageUtils.processSelectedImage(context, uri)

                if (base64Image != null) {
                    println("DEBUG: ✅ Image processing successful, Base64 length: ${base64Image.length}")
                    println("DEBUG: ✅ First 50 chars: ${base64Image.take(50)}...")
                    updateProfileImage(base64Image)
                    //println("DEBUG: Image processing successful, Base64 length: ${base64Image.length}")


                    // Optional: Clean up old profile images
                    ImageUtils.cleanupOldProfileImages(context, 3)
                } else {
                    println("DEBUG: ❌ Image processing returned null")
                    throw Exception("Failed to process image or image too large (max 1MB)")
                }

            } catch (e: Exception) {
                val errorMsg = "Failed to process image: ${e.message}"
                println("DEBUG: $errorMsg")
                _state.value = _state.value.copy(
                    error = errorMsg,
                    isLoading = false
                )
            }
        }
    }

    // NEW: Alternative method if you want to keep file-based storage as backup
    fun handleImageSelectionAsFile(context: Context, uri: Uri) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                println("DEBUG: Starting file-based image processing for URI: $uri")

                // Convert URI to byte array first
                val byteArray = ImageUtils.uriToByteArray(context, uri)
                if (byteArray != null) {
                    // Compress the image
                    val compressedImage = ImageUtils.compressImage(byteArray)

                    // Save to file
                    val filePath = ImageUtils.saveImageToFile(context, compressedImage)

                    if (ImageUtils.doesFileExist(filePath)) {
                        println("DEBUG: File created successfully: $filePath")
                        updateProfileImage(filePath) // This will still work if your Account uses file paths
                    } else {
                        throw Exception("Failed to create image file")
                    }
                } else {
                    throw Exception("Failed to read image data from URI")
                }

            } catch (e: Exception) {
                val errorMsg = "Failed to process image as file: ${e.message}"
                println("DEBUG: $errorMsg")
                _state.value = _state.value.copy(
                    error = errorMsg,
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
) {
    val currentAccount: Account? get() = filteredAccounts.getOrNull(currentAccountIndex)
    val totalAccounts: Int get() = filteredAccounts.size
    val isSearchActive: Boolean get() = filteredAccounts != accounts
}
