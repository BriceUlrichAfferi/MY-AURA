package com.aura.ui.login


import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aura.ui.models.LoginRequest
import com.aura.ui.utils.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: LoginRepository = LoginRepository()) : ViewModel() {

    private val _loginFormState = MutableStateFlow(LoginFormState())
    val loginFormState: StateFlow<LoginFormState> = _loginFormState

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState



    //Processes each result from the login request
    fun login(loginRequest: LoginRequest) {
        _loginState.value = LoginState.Loading
        viewModelScope.launch {
            repository.login(loginRequest)
                .onEach { result ->
                    if (result.isSuccess && result.getOrDefault(false)) {
                        val loggedInUser = LoggedInUserView(loginRequest.id)
                        _loginState.value = LoginState.Success(loggedInUser)
                    } else {
                        _loginState.value = LoginState.Error("Login failed: Invalid Login details")
                    }
                }
                .catch { e ->
                    _loginState.value = LoginState.Error(e.message ?: "Network error")
                }
                .collect()
        }
    }

    fun loginDataChanged(username: String, password: String) {
        if (username.isNotBlank() && !isUserNameValid(username)) {
            _loginFormState.value = LoginFormState(usernameError = "Invalid username")
        } else if (password.isNotBlank() && !isPasswordValid(password)) {
            _loginFormState.value = LoginFormState(passwordError = "Invalid password")
        } else {
            _loginFormState.value = LoginFormState(isDataValid = username.isNotBlank() && password.isNotBlank())
        }
    }
    private fun isUserNameValid(username: String): Boolean {
        return username.isNotBlank() && username.length >= 3
    }

    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }



    sealed class LoginState {
        object Idle : LoginState()
        object Loading : LoginState()
        data class Success(val user: LoggedInUserView) : LoginState()
        data class Error(val message: String) : LoginState()
    }
}

data class LoginFormState(
    val usernameError: String? = null,
    val passwordError: String? = null,
    val isDataValid: Boolean = false
)

data class LoggedInUserView(
    val displayName: String
)
