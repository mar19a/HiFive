package com.example.hifive

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // Set the content view to the main activity layout

        window.statusBarColor = Color.TRANSPARENT // Make the status bar transparent

        // Delay the execution of the next block of code using a handler
        Handler(Looper.getMainLooper()).postDelayed({
            // Check if there is a currently logged-in user
            if (FirebaseAuth.getInstance().currentUser == null)
                startActivity(Intent(this, SignUpActivity::class.java))  // If no user, go to SignUpActivity
            else
                startActivity(Intent(this, HomeActivity::class.java)) // If user exists, go to HomeActivity
            finish() // Finish this activity
        }, 3000)

    }
}