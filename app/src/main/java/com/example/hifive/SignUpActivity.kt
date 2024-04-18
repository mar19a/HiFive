package com.example.hifive

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.example.hifive.Models.User
import com.example.hifive.databinding.ActivitySignUpBinding
import com.example.hifive.utils.USER_NODE
import com.example.hifive.utils.USER_PROFILE_FOLDER
import com.example.hifive.utils.uploadImage
import com.squareup.picasso.Picasso


class SignUpActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivitySignUpBinding.inflate(layoutInflater)
    }

    private var user: User? = null

    private val launcher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            uploadImage(uri, USER_PROFILE_FOLDER) { url ->
                url?.let {
                    user?.image = it
                    binding.profileImage.setImageURI(uri)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val text = "<font color=#FF000000>Already have an Account</font> <font color=#1E88E5>Login ?</font>"
        binding.login.text = Html.fromHtml(text)

        user = User()

        if (intent.hasExtra("MODE") && intent.getIntExtra("MODE", -1) == 1) {
            binding.signUpBtn.text = "Update Profile"
            Firebase.auth.currentUser?.uid?.let { userId ->
                Firebase.firestore.collection(USER_NODE).document(userId).get()
                    .addOnSuccessListener { documentSnapshot ->
                        val fetchedUser = documentSnapshot.toObject<User>()
                        if (fetchedUser != null) {
                            user = fetchedUser
                            user?.apply {
                                if (!image.isNullOrEmpty()) {
                                    Picasso.get().load(image).into(binding.profileImage)
                                }
                                binding.name.editText?.setText(name)
                                binding.email.editText?.setText(email)
                                binding.password.editText?.setText(password)
                            }
                        } else {
                            Toast.makeText(this, "Failed to fetch user data.", Toast.LENGTH_LONG).show()
                        }
                    }
            }
        }


        binding.signUpBtn.setOnClickListener {
            if (intent.getIntExtra("MODE", -1) == 1) {
                user?.let { currentUser ->
                    Firebase.firestore.collection(USER_NODE)
                        .document(Firebase.auth.currentUser?.uid ?: return@setOnClickListener)
                        .set(currentUser)
                        .addOnSuccessListener {
                            startActivity(Intent(this, HomeActivity::class.java))
                            finish()
                        }
                }
            } else {
                val name = binding.name.editText?.text.toString()
                val email = binding.email.editText?.text.toString()
                val password = binding.password.editText?.text.toString()
                if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(this, "Please fill all information", Toast.LENGTH_SHORT).show()
                } else {
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { result ->
                            if (result.isSuccessful) {
                                val newUser = User(name, email, password)
                                Firebase.firestore.collection(USER_NODE)
                                    .document(Firebase.auth.currentUser?.uid ?: return@addOnCompleteListener)
                                    .set(newUser)
                                    .addOnSuccessListener {
                                        startActivity(Intent(this, HomeActivity::class.java))
                                        finish()
                                    }
                            } else {
                                Toast.makeText(this, result.exception?.localizedMessage, Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            }
        }

        binding.addImage.setOnClickListener {
            launcher.launch("image/*")
        }

        binding.login.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}
