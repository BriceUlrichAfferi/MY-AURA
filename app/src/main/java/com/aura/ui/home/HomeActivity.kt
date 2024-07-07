package com.aura.ui.home


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
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

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    binding = ActivityHomeBinding.inflate(layoutInflater)
    setContentView(binding.root)


    val balance = binding.balance
    val transfer = binding.transfer

    balance.text = "2654,54€"

    transfer.setOnClickListener {
      startActivity(Intent(this@HomeActivity, TransferActivity::class.java))
    }
  }

  override fun onCreateOptionsMenu(menu: Menu?): Boolean {
    menuInflater.inflate(R.menu.home_menu, menu)
    return true
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    return when (item.itemId) {
      R.id.disconnect -> {
        AlertDialog.Builder(this)
          .setTitle("Exit Confirmation")
          .setMessage("Are you sure you want to exit?")
          .setPositiveButton("Yes") { dialog, _ ->
            dialog.dismiss()
            super.onBackPressed()
          }
          .setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
          }
          .show()
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
        super.onBackPressed()
      }
      .setNegativeButton("No") { dialog, _ ->
        dialog.dismiss()
      }
      .show()
  }
}
