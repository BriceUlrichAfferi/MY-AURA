package com.aura.ui.login

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aura.ui.DataStore.UserPreferences
import com.aura.ui.models.LoginRequest
import com.aura.ui.utils.RetrofitInstance
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    sealed class LoginState {
        object Idle : LoginState()
        object Loading : LoginState()
        data class Success(val user: LoggedInUserView) : LoginState()
        data class Error(val message: String) : LoginState()
    }

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _loginState = MutableLiveData<LoginState>(LoginState.Idle)
    val loginState: LiveData<LoginState> = _loginState

    fun login(loginRequest: LoginRequest, context: Context) {
        _loginState.value = LoginState.Loading
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.login(loginRequest)
                if (response.granted) {
                    val loggedInUser = LoggedInUserView(loginRequest.id)
                    _loginState.value = LoginState.Success(loggedInUser)
                    UserPreferences.saveUserIdentifier(context, loginRequest.id)
                } else {
                    _loginState.value = LoginState.Error("Login failed")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _loginState.value = LoginState.Error("Network error")
            }
        }
    }

    fun loginDataChanged(username: String, password: String) {
        if (username.isNotBlank() && !isUserNameValid(username)) {
            _loginForm.value = LoginFormState(usernameError = "Invalid username")
        } else if (password.isNotBlank() && !isPasswordValid(password)) {
            _loginForm.value = LoginFormState(passwordError = "Invalid password")
        } else {
            _loginForm.value = LoginFormState(isDataValid = username.isNotBlank() && password.isNotBlank())
        }
    }

    private fun isUserNameValid(username: String): Boolean {
        return username.isNotBlank()
    }

    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }
}

data class LoginFormState(
    val usernameError: String? = null,
    val passwordError: String? = null,
    val isDataValid: Boolean = false
)

data class LoginResult(
    val success: LoggedInUserView? = null,
    val error: String? = null
)

data class LoggedInUserView(
    val displayName: String
)
