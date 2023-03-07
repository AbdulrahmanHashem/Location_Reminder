package com.example.location_reminder.location_reminders

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.location_reminder.authentication.AuthenticationActivity
import com.example.location_reminder.databinding.ActivityRemindersBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class RemindersActivity : AppCompatActivity() {

    lateinit var binding: ActivityRemindersBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRemindersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Firebase.auth.addAuthStateListener { auth ->
            if (auth.currentUser == null) {
                startActivity(Intent(this.applicationContext, AuthenticationActivity::class.java))
                finish()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        NavigationUI.setupActionBarWithNavController(
            this,
            findNavController(binding.navHostFragment.id)
        )
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(binding.navHostFragment.id).navigateUp()
    }
}