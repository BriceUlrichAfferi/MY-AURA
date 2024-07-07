package com.aura
import android.content.Context
import com.aura.ui.login.LoggedInUserView
import com.aura.ui.login.LoginRepository
import com.aura.ui.login.LoginViewModel
import com.aura.ui.models.LoginRequest
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

@ExperimentalCoroutinesApi
class LoginViewModelTest {

    private val testDispatcher = TestCoroutineDispatcher()

    @Mock
    lateinit var mockRepository: LoginRepository

    @Mock
    lateinit var mockContext: Context

    private lateinit var viewModel: LoginViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        viewModel = LoginViewModel(mockRepository)

        // Ensure mockContext returns itself for applicationContext
        Mockito.`when`(mockContext.applicationContext).thenReturn(mockContext)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain() // reset the main dispatcher to the original Main dispatcher
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun `login success`() = runBlockingTest {
        // Given
        val loginRequest = LoginRequest("username", "password")
        val loggedInUser = LoggedInUserView("username")
        val resultFlow = MutableStateFlow(Result.success(true))
        Mockito.`when`(mockRepository.login(loginRequest)).thenReturn(resultFlow)

        // When
        viewModel.login(loginRequest, mockContext)

        // Give some time for the state to update
        advanceUntilIdle()

        // Then
        val loginState = viewModel.loginState.value
        println("Current login state: $loginState")

        assert(loginState is LoginViewModel.LoginState.Success) { "Expected LoginState.Success but was $loginState" }
        assert((loginState as LoginViewModel.LoginState.Success).user == loggedInUser) {
            "Expected user to be $loggedInUser but was ${loginState.user}"
        }
    }

    @Test
    fun `login failure`() = runBlockingTest {
        // Given
        val loginRequest = LoginRequest("username", "password")
        val resultFlow = MutableStateFlow(Result.failure<Boolean>(Exception("Login failed")))
        Mockito.`when`(mockRepository.login(loginRequest)).thenReturn(resultFlow)

        // When
        viewModel.login(loginRequest, mockContext)

        // Give some time for the state to update
        advanceUntilIdle()

        // Then
        val loginState = viewModel.loginState.value
        println("Current login state: $loginState")

        assert(loginState is LoginViewModel.LoginState.Error) { "Expected LoginState.Error but was $loginState" }
    }

    @Test
    fun `valid login data`() {
        // Given
        val validUsername = "validUsername"
        val validPassword = "validPassword123"

        // When
        viewModel.loginDataChanged(validUsername, validPassword)

        // Then
        val loginFormState = viewModel.loginFormState.value
        println("Current login form state: $loginFormState")

        assert(loginFormState.isDataValid) { "Expected isDataValid to be true but was false" }
    }

    @Test
    fun `invalid username`() = runBlockingTest {
        // Given
        val invalidUsername = "ab"  // Invalid because it's less than 3 characters
        val password = "password123"

        // When
        viewModel.loginDataChanged(invalidUsername, password)

        // Then
        val loginFormState = viewModel.loginFormState.value
        println("Current login form state: $loginFormState")

        assert(loginFormState.usernameError == "Invalid username") {
            "Expected usernameError to be set but was ${loginFormState.usernameError}"
        }
        assert(!loginFormState.isDataValid) {
            "Expected isDataValid to be false but was ${loginFormState.isDataValid}"
        }
    }




    @Test
    fun `invalid password`() {
        // Given
        val validUsername = "validUsername"
        val invalidPassword = "123"

        // When
        viewModel.loginDataChanged(validUsername, invalidPassword)

        // Then
        val loginFormState = viewModel.loginFormState.value
        println("Current login form state: $loginFormState")

        assert(loginFormState.passwordError != null) { "Expected passwordError to be set but was null" }
        assert(!loginFormState.isDataValid) { "Expected isDataValid to be false but was true" }
    }



    @Test
    fun `network error`() = runBlockingTest {
        // Given
        val loginRequest = LoginRequest("username", "password")
        val resultFlow = MutableStateFlow(Result.failure<Boolean>(Exception("Network error")))
        Mockito.`when`(mockRepository.login(loginRequest)).thenReturn(resultFlow)

        // When
        viewModel.login(loginRequest, mockContext)

        // Give some time for the state to update
        advanceUntilIdle()

        // Then
        val loginState = viewModel.loginState.value
        println("Current login state: $loginState")

        assert(loginState is LoginViewModel.LoginState.Error) { "Expected LoginState.Error but was $loginState" }
        assert((loginState as LoginViewModel.LoginState.Error).message == "Login failed") {
            "Expected error message to be 'Login failed' but was ${loginState.message}"
        }
    }

}