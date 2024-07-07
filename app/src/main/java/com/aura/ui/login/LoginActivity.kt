package com.aura.ui.login



import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.aura.R
import com.aura.databinding.ActivityLoginBinding
import com.aura.ui.home.HomeActivity
import com.aura.ui.models.LoginRequest
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

  private lateinit var binding: ActivityLoginBinding
  private val loginViewModel: LoginViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    binding = ActivityLoginBinding.inflate(layoutInflater)
    setContentView(binding.root)

    val usernameEditText = binding.identifier
    val passwordEditText = binding.password
    val loginButton = binding.login
    val loadingProgressBar = binding.loading

    lifecycleScope.launch {
      loginViewModel.loginFormState.collect { loginState ->
        loginButton.isEnabled = loginState.isDataValid

        if (loginState.usernameError != null) {
          usernameEditText.error = loginState.usernameError
        }
        if (loginState.passwordError != null) {
          passwordEditText.error = loginState.passwordError
        }
      }
    }

    lifecycleScope.launch {
      loginViewModel.loginState.collect { state ->
        when (state) {
          is LoginViewModel.LoginState.Idle -> {
            loadingProgressBar.visibility = View.GONE
            loginButton.isEnabled = true
          }
          is LoginViewModel.LoginState.Loading -> {
            loadingProgressBar.visibility = View.VISIBLE
            loginButton.isEnabled = false
          }
          is LoginViewModel.LoginState.Success -> {
            loadingProgressBar.visibility = View.GONE
            loginButton.isEnabled = true
            updateUiWithUser(state.user)
            goToHomeActivity(state.user.displayName)
          }
          is LoginViewModel.LoginState.Error -> {
            loadingProgressBar.visibility = View.GONE
            loginButton.isEnabled = true
            showLoginFailed(state.message)
          }
        }
      }
    }

    usernameEditText.afterTextChanged {
      loginViewModel.loginDataChanged(
        usernameEditText.text.toString(),
        passwordEditText.text.toString()
      )
    }

    passwordEditText.afterTextChanged {
      loginViewModel.loginDataChanged(
        usernameEditText.text.toString(),
        passwordEditText.text.toString()
      )
    }

    loginButton.setOnClickListener {
      val username = usernameEditText.text.toString()
      val password = passwordEditText.text.toString()
      val loginRequest = LoginRequest(username, password)
      loginViewModel.login(loginRequest)
    }
  }

  private fun goToHomeActivity(userIdentifier: String) {
    val intent = Intent(this@LoginActivity, HomeActivity::class.java).apply {
      flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
    startActivity(intent)
    finish()
  }

  private fun updateUiWithUser(model: LoggedInUserView) {
    val welcome = getString(R.string.welcome)
    val displayName = model.displayName
    Toast.makeText(
      applicationContext,
      "$welcome $displayName",
      Toast.LENGTH_LONG
    ).show()
  }

  private fun showLoginFailed(errorString: String) {
    Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
  }

  fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
      override fun afterTextChanged(s: Editable?) {
        afterTextChanged.invoke(s.toString())
      }

      override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
      override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    })
  }
}
