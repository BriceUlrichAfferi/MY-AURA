package com.aura.ui.transfer

import android.app.Activity
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

    val transferButton = binding.transfer
    val loadingProgressBar = binding.loading

    lifecycleScope.launch {
      transferViewModel.transferFormState.collect { transferState ->
        transferButton.isEnabled = transferState.isDataValid

        if (transferState.recipientError != null) {
          recipientEditText.error = transferState.recipientError
        }
        if (transferState.amountError != null) {
          amountEditText.error = transferState.amountError
        }
      }
    }

    lifecycleScope.launch {
      transferViewModel.transferResult.collect { result ->
        result?.let {
          loadingProgressBar.visibility = View.GONE
          if (it) {
            Toast.makeText(this@TransferActivity, "Transfer successful", Toast.LENGTH_LONG).show()
            setResult(Activity.RESULT_OK)
            finish()
          } else {
            Toast.makeText(this@TransferActivity, "Transfer failed: Check Receiver and Amount", Toast.LENGTH_LONG).show()
          }
        }
      }
    }

    recipientEditText.afterTextChanged {
      transferViewModel.transferDataChanged(
        recipientEditText.text.toString(),
        amountEditText.text.toString()
      )
    }

    amountEditText.afterTextChanged {
      transferViewModel.transferDataChanged(
        recipientEditText.text.toString(),
        amountEditText.text.toString()
      )
    }

    transferButton.setOnClickListener {
      loadingProgressBar.visibility = View.VISIBLE

      val recipient = recipientEditText.text.toString()
      val amount = amountEditText.text.toString().toDouble()
      val sender = "1234" // Assuming sender ID is 1234

      transferViewModel.initiateTransfer(sender, recipient, amount)
    }
  }

  private fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
      override fun afterTextChanged(s: Editable?) {
        afterTextChanged.invoke(s.toString())
      }

      override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
      override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    })
  }
}