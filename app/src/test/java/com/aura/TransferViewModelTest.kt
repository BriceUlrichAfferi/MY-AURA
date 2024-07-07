package com.aura


import com.aura.ui.models.TransferRequest
import com.aura.ui.transfer.TransferRepository
import com.aura.ui.transfer.TransferViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

@ExperimentalCoroutinesApi
class TransferViewModelTest {

    private val testDispatcher = TestCoroutineDispatcher()

    @Mock
    lateinit var mockRepository: TransferRepository

    private lateinit var viewModel: TransferViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        viewModel = TransferViewModel(mockRepository)
    }

    @After
    fun tearDown() {
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun `initiate transfer success`() = testDispatcher.runBlockingTest {
        // Given
        val senderId = "senderId"
        val recipientId = "recipientId"
        val amount = 100.0
        val transferRequest = TransferRequest(senderId, recipientId, amount)
        val resultFlow = MutableStateFlow(true)
        Mockito.`when`(mockRepository.initiateTransfer(transferRequest)).thenReturn(resultFlow)

        // When
        viewModel.initiateTransfer(senderId, recipientId, amount)

        // Then
        val result = viewModel.transferResult.first()
        assert(result == true) { "Expected transfer result to be true, but was $result" }
    }


    @Test
    fun `initiate transfer failure`() = testDispatcher.runBlockingTest {
        // Given
        val senderId = "senderId"
        val recipientId = "recipientId"
        val amount = 100.0
        val transferRequest = TransferRequest(senderId, recipientId, amount)
        val resultFlow = MutableStateFlow(false)
        Mockito.`when`(mockRepository.initiateTransfer(transferRequest)).thenReturn(resultFlow)

        // When
        viewModel.initiateTransfer(senderId, recipientId, amount)

        // Then
        advanceUntilIdle() // Ensure all coroutines are idle

        // Collect the result from transferResult
        val result = viewModel.transferResult.first()

        // Assert the result
        assert(result == false) { "Expected transfer result to be false, but was $result" }
    }



    @Test
    fun `transfer data validation`() {
        // Given
        val validRecipient = "recipient"
        val validAmount = "100"

        // When
        viewModel.transferDataChanged(validRecipient, validAmount)

        // Then
        val transferFormState = viewModel.transferFormState.value
        assert(transferFormState.isDataValid)
    }

    @Test
    fun `invalid recipient`() {
        // Given
        val invalidRecipient = ""
        val validAmount = "100"

        // When
        viewModel.transferDataChanged(invalidRecipient, validAmount)

        // Then
        val transferFormState = viewModel.transferFormState.value
        assert(transferFormState.recipientError == "Invalid recipient")
        assert(!transferFormState.isDataValid)
    }

    @Test
    fun `invalid amount`() {
        // Given
        val validRecipient = "recipient"
        val invalidAmount = "abc"

        // When
        viewModel.transferDataChanged(validRecipient, invalidAmount)

        // Then
        val transferFormState = viewModel.transferFormState.value
        assert(transferFormState.amountError == "Invalid amount")
        assert(!transferFormState.isDataValid)
    }
}
