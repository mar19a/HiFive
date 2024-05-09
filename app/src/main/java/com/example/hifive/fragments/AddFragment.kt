package com.example.hifive.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import com.example.hifive.MapsViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.example.hifive.Post.PostActivity
import com.example.hifive.Post.ReelsActivity
import com.example.hifive.databinding.FragmentAddBinding
// Defines a fragment for adding new posts, utilizing a BottomSheetDialogFragment for its UI.
class AddFragment : BottomSheetDialogFragment() {
    // Uses a ViewModel to interact with map data.
    private val mapsVM: MapsViewModel by activityViewModels()

    // Lateinit allows us to safely initialize non-null types after declaration.
    private lateinit var binding: FragmentAddBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState) // Always call the super method first in onCreate.

    }
    // Inflates the fragment's view and sets up the UI interactions.
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflates the fragment's view and sets up the UI interactions.
        binding = FragmentAddBinding.inflate(inflater, container, false)
        // Set up a click listener on the 'post' button.
        binding.post.setOnClickListener {
            // Inflates the fragment's view and sets up the UI interactions.
            val intent = Intent(requireContext(), PostActivity::class.java)
            // Log the current location fetched from the ViewModel.
            Log.d("AddFragment", mapsVM.getMyLocation().toString())
            // Pass the current location as an extra in the intent.
            intent.putExtra("location", "${mapsVM.getMyLocation()?.latitude},${mapsVM.getMyLocation()?.longitude}")
            Log.d("AddFragment", "${mapsVM.getMyLocation()?.latitude},${mapsVM.getMyLocation()?.longitude}")
            // Start the activity and finish the current one.
            activity?.startActivity(intent)
            activity?.finish()
        }

        return binding.root // Return the inflated view.
    }

    companion object {

    }
}