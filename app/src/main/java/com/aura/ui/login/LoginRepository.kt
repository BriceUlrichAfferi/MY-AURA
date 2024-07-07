package com.aura.ui.login



import com.aura.ui.models.LoginRequest
import com.aura.ui.utils.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.lang.Exception

class LoginRepository {

    fun login(loginRequest: LoginRequest): Flow<Result<Boolean>> = flow {
        try {
            val response = RetrofitInstance.api.login(loginRequest)
            emit(Result.success(response.granted))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)
}
