package com.example.hifive.Post


import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.widget.TimePicker
import android.widget.Toast
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
import java.util.Locale


class PostActivity : AppCompatActivity() {

    private var addressData: String? = null

    private lateinit var addr: String

    private var locationData: String? = null

    private lateinit var loc: String

    private var edate = ""

    private var etime = ""

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

    private val launcher2 =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

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
                binding.location.hint = getString(R.string.choose_location)
                loc_enabled = false
                binding.postButton.isEnabled = false
            }
            //Log.d("lnch2", result.data?.getStringExtra("latlong").toString())
            //Log.d("lnch2", loc)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val value = intent.getStringExtra("location").toString()
        Log.d("PostActivity", value)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            startActivity(Intent(this@PostActivity, HomeActivity::class.java))
            finish()
        }

        binding.selectImage.setOnClickListener {
            launcher1.launch("image/*")
        }

        binding.location.setOnClickListener {
            val intent = Intent(this@PostActivity, MapsActivity::class.java)
            intent.putExtra("location", value)

            launcher2.launch(intent)
        }

        binding.date.setOnClickListener {
            // Get the current date
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            // Create a DatePickerDialog and show it when the button is clicked
            val datePickerDialog = DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDay ->
                    // Handle the selected date
                    val selectedDate = "${selectedMonth + 1}/${selectedDay}/$selectedYear"

                    binding.date.text = selectedDate
                    edate = selectedDate
                    Log.d("PostActivity", "date:${edate}")
                },
                year,
                month,
                day
            )
            datePickerDialog.show()
        }

        binding.time.setOnClickListener {
            // Get the current time
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            // Create a TimePickerDialog and show it when the button is clicked
            val timePickerDialog = TimePickerDialog(
                this,
                TimePickerDialog.OnTimeSetListener { _: TimePicker, selectedHour: Int, selectedMinute: Int ->
                    // Handle the selected time
                    // You can do something with the selectedTime here, such as displaying it in a TextView
                    binding.time.text = format12HourTime(selectedHour, selectedMinute)
                    etime = format24HourTime(selectedHour, selectedMinute)
                    Log.d("PostActivity", "24hr time:${etime}")
                },
                hour,
                minute,
                false // Set to true to use 24-hour format, false for 12-hour format
            )
            timePickerDialog.show()
        }

//        binding.event.setOnClickListener {
//            launcher2.launch(Intent(this@PostActivity, MapsActivity::class.java))
//        }

        binding.cancelButton.setOnClickListener {
            startActivity(Intent(this@PostActivity, HomeActivity::class.java))
            finish()
        }

        binding.postButton.setOnClickListener {
            // Ensure imageUrl is not null before creating the post object
            if (imageUrl != null) {
                // Create a new document reference with a unique ID
                val newPostRef = Firebase.firestore.collection("posts").document()

                val post = Post(
                    postId = newPostRef.id, // Use the generated document ID
                    title = binding.title.editText?.text.toString(),
                    caption = binding.caption.editText?.text.toString(),
                    uid = Firebase.auth.currentUser!!.uid,
                    time = System.currentTimeMillis().toString(),
                    postUrl = imageUrl!!, // Assign the uploaded image URL to the post
                    addr = addr,
                    loc = loc,
                    edate = edate,
                    etime = etime,
                    etype = binding.event.editText?.text.toString()
                )

                // Set the post object to the new document
                newPostRef.set(post).addOnSuccessListener {
                    Firebase.firestore.collection(Firebase.auth.currentUser!!.uid).document(newPostRef.id)
                        .set(post)
                        .addOnSuccessListener {
                            startActivity(Intent(this@PostActivity, HomeActivity::class.java))
                            finish()
                        }
                }.addOnFailureListener {
                    // Handle case where the Firestore operation fails
                    Toast.makeText(this,
                        getString(R.string.failed_to_create_post_please_try_again), Toast.LENGTH_SHORT).show()
                }
            } else {
                // Handle case where image URL is null
                Toast.makeText(this,
                    getString(R.string.please_upload_an_image_first), Toast.LENGTH_SHORT).show()
            }
        }


    }

    private fun format12HourTime(hourOfDay: Int, minute: Int): String {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
        calendar.set(Calendar.MINUTE, minute)
        val format = if (calendar.get(Calendar.AM_PM) == Calendar.AM) "AM" else "PM"
        val hour = if (hourOfDay % 12 == 0) 12 else hourOfDay % 12
        return String.format("%02d:%02d %s", hour, minute, format)
    }

    private fun format24HourTime(hour: Int, minute: Int): String {
        return String.format("%02d:%02d", hour, minute)
    }
    

}