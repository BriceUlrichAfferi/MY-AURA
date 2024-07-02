package com.aura.ui.transfer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TransferViewModel : ViewModel() {

    private val _transferFormState = MutableLiveData<TransferFormState>()
    val transferFormState: LiveData<TransferFormState> = _transferFormState

    fun transferDataChanged(recipient: String, amount: String, toString: String) {
        if (!isRecipientValid(recipient)) {
            _transferFormState.value = TransferFormState(recipientError = "Invalid recipient")
        } else if (!isAmountValid(amount)) {
            _transferFormState.value = TransferFormState(amountError = "Invalid amount")
        } else {
            _transferFormState.value = TransferFormState(isDataValid = true)
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
