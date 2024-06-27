package com.aura.ui.utils



import com.aura.ui.models.LoginRequest
import com.aura.ui.models.LoginResponse
import com.aura.ui.models.TransfertRequest
import com.aura.ui.models.TransfertResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("transfer")
    suspend fun transfer(@Body transferRequest: TransfertRequest): TransfertResponse
}



