package com.example.hifive

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.example.hifive.Models.User
import com.example.hifive.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    // Lazy initialization of the binding variable to inflate the layout only when needed
    private val binding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root) // Set the content view to the inflated layout
        // Set an onClickListener on the login button
        binding.loginBtn.setOnClickListener {
            // Check if email or password fields are empty
            if (binding.email.editText?.text.toString().equals("") or
                binding.pass.editText?.text.toString().equals("")
            ) {
                // Show a toast if either field is empty
                Toast.makeText(
                    this@LoginActivity,
                    getString(R.string.incomplete_form_message),
                    Toast.LENGTH_SHORT
                ).show()

            } else {
                // Create a user object with email and password from the input fields
                var user = User(
                    binding.email.editText?.text.toString(),
                    binding.pass.editText?.text.toString()
                )
                // Try to sign in with Firebase Authentication
                Firebase.auth.signInWithEmailAndPassword(user.email!!, user.password!!)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            // If login is successful, navigate to HomeActivity
                                startActivity(Intent(this@LoginActivity,HomeActivity::class.java))
                            finish() // Finish this activity
                        } else {
                            // If login fails, show a toast with the error message
                            Toast.makeText(
                                this@LoginActivity,
                                it.exception?.localizedMessage,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            }

        }
        // Set an onClickListener on the create account button to navigate to SignUpActivity
        binding.createAccountBtn.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }
}