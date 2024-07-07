package com.aura.ui.utils



import com.aura.ui.models.LoginRequest
import com.aura.ui.models.LoginResponse
import com.aura.ui.models.TransferRequest
import com.aura.ui.models.TransferResponse

import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("transfer")
    suspend fun transfer(@Body transferRequest: TransferRequest): TransferResponse

}



