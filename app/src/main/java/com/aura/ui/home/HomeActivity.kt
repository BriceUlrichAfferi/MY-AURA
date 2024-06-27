package com.aura.ui.home

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.aura.R
import com.aura.databinding.ActivityHomeBinding
import com.aura.ui.login.LoginActivity
import com.aura.ui.transfer.TransferActivity

/**
 * The home activity for the app.
 */
class HomeActivity : AppCompatActivity() {

  /**
   * The binding for the home layout.
   */
  private lateinit var binding: ActivityHomeBinding

  /**
   * A callback for the result of starting the TransferActivity.
   */
  private val startTransferActivityForResult =
    registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
      //TODO
    }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    binding = ActivityHomeBinding.inflate(layoutInflater)
    setContentView(binding.root)

    val userIdentifier = intent.getStringExtra("USER_IDENTIFIER")
    Toast.makeText(this, "User Identifier: $userIdentifier", Toast.LENGTH_LONG).show()

    val balance = binding.balance
    val transfer = binding.transfer

    balance.text = "2654,54€"

    transfer.setOnClickListener {
      startTransferActivityForResult.launch(Intent(this@HomeActivity, TransferActivity::class.java))
    }
  }

  override fun onCreateOptionsMenu(menu: Menu?): Boolean {
    menuInflater.inflate(R.menu.home_menu, menu)
    return true
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    return when (item.itemId) {
      R.id.disconnect -> {
        //startActivity(Intent(this@HomeActivity, LoginActivity::class.java))
        //moveTaskToBack(true) // This moves the task containing the HomeActivity to the back of the activity stack, effectively exiting the app

        AlertDialog.Builder(this)
            .setTitle("Exit Confirmation")
            .setMessage("Are you sure you want to exit?")
            .setPositiveButton("Yes") { dialog, _ ->
              dialog.dismiss()
              super.onBackPressed()  // Call super.onBackPressed() only if the user confirms the exit
            }
            .setNegativeButton("No") { dialog, _ ->
              dialog.dismiss()
            }
            .show()

        //finish()
        true
      }
      else -> super.onOptionsItemSelected(item)
    }
  }

  @SuppressLint("MissingSuperCall")
  override fun onBackPressed() {
    showExitConfirmationDialog()
  }

  private fun showExitConfirmationDialog() {
    AlertDialog.Builder(this)
      .setTitle("Exit Confirmation")
      .setMessage("Are you sure you want to exit?")
      .setPositiveButton("Yes") { dialog, _ ->
        dialog.dismiss()
        super.onBackPressed()  // Call super.onBackPressed() only if the user confirms the exit
      }
      .setNegativeButton("No") { dialog, _ ->
        dialog.dismiss()
      }
      .show()
  }
}
