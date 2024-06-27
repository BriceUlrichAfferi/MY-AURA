package com.aura.ui.transfer

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.aura.databinding.ActivityTransferBinding

class TransferActivity : AppCompatActivity() {

  private lateinit var binding: ActivityTransferBinding
  private val transfertViewModel: TransferViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    binding = ActivityTransferBinding.inflate(layoutInflater)
    setContentView(binding.root)

    val recipientEditText = binding.recipient
    val amountEditText = binding.amount
    val transferButton = binding.transfer
    val loadingProgressBar = binding.loading

    transfertViewModel.transferFormState.observe(this@TransferActivity, Observer {
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
      transfertViewModel.transferDataChanged(
        recipientEditText.text.toString(),
        amountEditText.text.toString()
      )
    }

    amountEditText.afterTextChanged {
      transfertViewModel.transferDataChanged(
        recipientEditText.text.toString(),
        amountEditText.text.toString()
      )
    }

    transferButton.setOnClickListener {
      loadingProgressBar.visibility = View.VISIBLE

      loadingProgressBar.visibility = View.GONE
      setResult(Activity.RESULT_OK)
      finish()
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
