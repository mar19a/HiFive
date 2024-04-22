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

class AddFragment : BottomSheetDialogFragment() {

    private val mapsVM: MapsViewModel by activityViewModels()

    private lateinit var binding: FragmentAddBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAddBinding.inflate(inflater, container, false)

        binding.post.setOnClickListener {
            val intent = Intent(requireContext(), PostActivity::class.java)
            Log.d("AddFragment", mapsVM.getLocation().toString())
            intent.putExtra("location", "${mapsVM.getLocation()?.latitude},${mapsVM.getLocation()?.longitude}")
            activity?.startActivity(intent)
            activity?.finish()
        }
        binding.reel.setOnClickListener {
            activity?.startActivity(Intent(requireContext(),ReelsActivity::class.java))
        }

        return binding.root
    }

    companion object {

    }
}