package com.example.hifive.Post


import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts

import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.example.hifive.HomeActivity
import com.example.hifive.MapsActivity
import com.example.hifive.Models.Post
import com.example.hifive.R

import com.example.hifive.databinding.ActivityPostBinding
import com.example.hifive.utils.POST
import com.example.hifive.utils.POST_FOLDER
import com.example.hifive.utils.uploadImage
import com.google.android.gms.maps.MapFragment
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng


class PostActivity : AppCompatActivity() {

    private var addressData: String? = null

    private lateinit var addr: String

    private var locationData: String? = null

    private lateinit var loc: String

    private var etype = "Other"

    private var loc_enabled = false

    private var image_enabled = false


    private val binding by lazy {
        ActivityPostBinding.inflate(layoutInflater)
    }
    private var imageUrl: String? = null

    private val launcher1 = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            uploadImage(uri, POST_FOLDER) { url ->
                if (url != null) {
                    binding.selectImage.setImageURI(uri)
                    imageUrl = url
                    image_enabled = true
                    if (loc_enabled) {
                        binding.postButton.isEnabled = true
                    }
                }

            }
        }

    }

    private val launcher2 = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

        //Log.d("lnch2", result.data?.getStringExtra("address").toString())
        addressData = result.data?.getStringExtra("address")//toString()
        locationData = result.data?.getStringExtra("latlong")//.toString()
        //Log.d("lnch2", addr)
        if (addressData != null && locationData != null) {
            addr = result.data?.getStringExtra("address").toString()
            loc = result.data?.getStringExtra("latlong").toString()
            binding.location.text = addr
            loc_enabled = true
            if (image_enabled) {
                binding.postButton.isEnabled = true
            }
        } else {
            binding.location.text = ""
            binding.location.hint = "Choose Location"
            loc_enabled = false
            binding.postButton.isEnabled = false
        }
        //Log.d("lnch2", result.data?.getStringExtra("latlong").toString())
        //Log.d("lnch2", loc)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setSupportActionBar(binding.materialToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        binding.materialToolbar.setNavigationOnClickListener {
            startActivity(Intent(this@PostActivity, HomeActivity::class.java))
            finish()
        }

        binding.selectImage.setOnClickListener {
            launcher1.launch("image/*")
        }

        binding.location.setOnClickListener {
            launcher2.launch(Intent(this@PostActivity, MapsActivity::class.java))
        }

//        binding.event.setOnClickListener {
//            launcher2.launch(Intent(this@PostActivity, MapsActivity::class.java))
//        }

        binding.cancelButton.setOnClickListener {
            startActivity(Intent(this@PostActivity, HomeActivity::class.java))
            finish()
        }

        binding.postButton.setOnClickListener {

            val post: Post = Post(
                postUrl = imageUrl!!,
                caption = binding.caption.editText?.text.toString(),
                uid = Firebase.auth.currentUser!!.uid,
                time = System.currentTimeMillis().toString(),
                addr = addr,
                loc = loc,
                etype = etype
            )

            Firebase.firestore.collection(POST).document().set(post).addOnSuccessListener {
                Firebase.firestore.collection(Firebase.auth.currentUser!!.uid).document()
                    .set(post)
                    .addOnSuccessListener {
                        startActivity(Intent(this@PostActivity, HomeActivity::class.java))
                        finish()
                    }

            }


        }
    }

}

