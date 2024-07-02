package com.aura.ui.transfer

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.aura.databinding.ActivityTransferBinding
import com.aura.ui.models.TransferRequest
import com.aura.ui.utils.RetrofitInstance
import kotlinx.coroutines.launch

class TransferActivity : AppCompatActivity() {

  private lateinit var binding: ActivityTransferBinding
  private val transferViewModel: TransferViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityTransferBinding.inflate(layoutInflater)
    setContentView(binding.root)

    val recipientEditText = binding.recipient
    val amountEditText = binding.amount
    val senderEditText = binding.sender

    val transferButton = binding.transfer
    val loadingProgressBar = binding.loading

    transferViewModel.transferFormState.observe(this@TransferActivity, Observer {
      val transferState = it ?: return@Observer

      transferButton.isEnabled = transferState.isDataValid

      if (transferState.recipientError != null) {
        recipientEditText.error = transferState.recipientError
      }
      if (transferState.amountError != null) {
        amountEditText.error = transferState.amountError
      }
    })

    recipientEditText.afterTextChanged {
      transferViewModel.transferDataChanged(
        recipientEditText.text.toString(),
        amountEditText.text.toString(),
        senderEditText.text.toString()
      )
    }

    amountEditText.afterTextChanged {
      transferViewModel.transferDataChanged(
        recipientEditText.text.toString(),
        amountEditText.text.toString(),
        senderEditText.text.toString()
      )
    }

    transferButton.setOnClickListener {
      loadingProgressBar.visibility = View.VISIBLE

      val recipient = recipientEditText.text.toString()
      val amount = amountEditText.text.toString().toDouble()
      val sender = "1234" // Assuming sender ID is 1234 for this example

      val request = TransferRequest(sender, recipient, amount)

      Log.d("TransferActivity", "Request: $request")

      lifecycleScope.launch {
        try {
          val response = RetrofitInstance.api.transfer(request)
          loadingProgressBar.visibility = View.GONE

          if (response.result) {
            Toast.makeText(this@TransferActivity, "Transfer successful", Toast.LENGTH_LONG).show()
            setResult(Activity.RESULT_OK)
            finish()
          } else {
            Log.e("TransferActivity", "Transfer failed: $response")
            Toast.makeText(this@TransferActivity, "Transfer failed", Toast.LENGTH_LONG).show()
          }
        } catch (e: Exception) {
          loadingProgressBar.visibility = View.GONE
          Log.e("TransferActivity", "Error during transfer", e)
          Toast.makeText(this@TransferActivity, "Network error: ${e.message}", Toast.LENGTH_LONG)
            .show()
        }
      }
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
}
