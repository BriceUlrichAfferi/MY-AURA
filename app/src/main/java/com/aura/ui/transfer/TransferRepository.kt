package com.aura.ui.transfer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.aura.ui.models.TransferRequest
import com.aura.ui.utils.RetrofitInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class TransferRepository {

    fun initiateTransfer(transferRequest: TransferRequest): Flow<Boolean> = flow {
        try {
            val response = RetrofitInstance.api.transfer(transferRequest)
            emit(response.result)
        } catch (e: Exception) {
            e.printStackTrace()
            emit(false)
        }
    }.flowOn(Dispatchers.IO)
}
