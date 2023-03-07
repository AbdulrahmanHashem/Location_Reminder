package com.example.location_reminder.authentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.location_reminder.databinding.ActivityAuthenticationBinding
import com.example.location_reminder.location_reminders.RemindersActivity
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class AuthenticationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthenticationBinding

    val registeryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthenticationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        title = "Login"

        Firebase.auth.addAuthStateListener { auth ->
            if (auth.currentUser != null) {
                startActivity(Intent(this.applicationContext, RemindersActivity::class.java))
                finish()
            } else {
                Toast.makeText(
                        this,
                        "Please Login to Use the App",
                        Toast.LENGTH_SHORT).show()
            }
        }

        binding.login.setOnClickListener {
            launchAuthScreen()
        }
        // TODO: a bonus is to customize the sign in flow to look nice using :
        //https://github.com/firebase/FirebaseUI-Android/blob/master/auth/README.md#custom-layout
    }

    private fun launchAuthScreen() {
        val options = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build())

        registeryLauncher.launch(AuthUI
            .getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(options)
            .build()
        )
    }
}