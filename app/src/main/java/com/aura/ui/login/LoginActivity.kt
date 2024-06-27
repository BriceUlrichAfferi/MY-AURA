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
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.aura.R
import com.aura.databinding.ActivityLoginBinding
import com.aura.ui.DataStore.UserPreferences
import com.aura.ui.home.HomeActivity
import com.aura.ui.models.LoginRequest
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

    // Check for stored user identifier
    lifecycleScope.launch {
      val userIdentifier = UserPreferences.getUserIdentifier(this@LoginActivity).first()
      if (!userIdentifier.isNullOrEmpty()) {
        usernameEditText.setText(userIdentifier)
      }
    }

    loginViewModel.loginFormState.observe(this@LoginActivity, Observer {
      val loginState = it ?: return@Observer

      // Disable login button unless both username / password is valid
      loginButton.isEnabled = loginState.isDataValid

      if (loginState.usernameError != null) {
        usernameEditText.error = loginState.usernameError
      }
      if (loginState.passwordError != null) {
        passwordEditText.error = loginState.passwordError
      }
    })

    loginViewModel.loginState.observe(this@LoginActivity, Observer { state ->
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
    })

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
      loginViewModel.login(loginRequest, this)
    }
  }

  private fun goToHomeActivity(userIdentifier: String) {
    val intent = Intent(this@LoginActivity, HomeActivity::class.java).apply {
      flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
    intent.putExtra("USER_IDENTIFIER", userIdentifier)
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
