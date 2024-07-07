package com.aura.ui.transfer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aura.ui.login.LoginRepository
import com.aura.ui.models.TransferRequest
import com.aura.ui.transfer.TransferRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class TransferViewModel(private val repository: TransferRepository= TransferRepository()) : ViewModel() {

    private val _transferFormState = MutableStateFlow(TransferFormState())
    val transferFormState: StateFlow<TransferFormState> = _transferFormState

    private val _transferResult = MutableStateFlow<Boolean?>(null)
    val transferResult: StateFlow<Boolean?> = _transferResult

    fun transferDataChanged(recipient: String, amount: String) {
        if (!isRecipientValid(recipient)) {
            _transferFormState.value = TransferFormState(recipientError = "Invalid recipient")
        } else if (!isAmountValid(amount)) {
            _transferFormState.value = TransferFormState(amountError = "Invalid amount")
        } else {
            _transferFormState.value = TransferFormState(isDataValid = true)
        }
    }

    fun initiateTransfer(senderId: String, recipientId: String, amount: Double) {
        val transferRequest = TransferRequest(senderId, recipientId, amount)
        viewModelScope.launch {
            repository.initiateTransfer(transferRequest)
                .catch { e ->
                    e.printStackTrace()
                    _transferResult.value = false
                }
                .collect { result ->
                    _transferResult.value = result
                }
        }
    }

    private fun isRecipientValid(recipient: String): Boolean {
        return recipient.isNotBlank()
    }

    private fun isAmountValid(amount: String): Boolean {
        return amount.isNotBlank() && amount.toDoubleOrNull() != null && amount.toDouble() > 0
    }
}

data class TransferFormState(
    val recipientError: String? = null,
    val amountError: String? = null,
    val isDataValid: Boolean = false
)
