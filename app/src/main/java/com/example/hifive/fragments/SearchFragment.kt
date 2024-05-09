package com.example.hifive.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hifive.AddUserActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.example.hifive.Models.User
import com.example.hifive.adapters.SearchAdapter
import com.example.hifive.databinding.FragmentSearchBinding
import com.example.hifive.utils.FOLLOW
import com.example.hifive.utils.USER_NODE
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso


class SearchFragment : Fragment() {
    lateinit var binding: FragmentSearchBinding
    lateinit var adapter: SearchAdapter
    var userList = ArrayList<User>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState) // Standard onCreate method in fragments

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        // Setup recycler view with a linear layout manager
        binding.rv.layoutManager = LinearLayoutManager(requireContext())
        // Initialize the adapter with the context and user list
        adapter = SearchAdapter(requireContext(), userList)
        binding.rv.adapter = adapter  // Set the adapter for the recycler view

        //Handler For Add Users Button
        binding.addUsersButton.setOnClickListener {
            val intent = Intent(context, AddUserActivity::class.java)
            intent.putExtra("LOGGED_IN_USER", FirebaseAuth.getInstance().currentUser!!.uid)
            startActivity(intent) // Start AddUserActivity on button click
        }
        //Only show users you are already following
        Firebase.firestore.collection(Firebase.auth.currentUser!!.uid + FOLLOW).get()
            .addOnSuccessListener {
                var tempList=ArrayList<User>()
                userList.clear()
                for(i in it.documents){
                    var user:User=i.toObject<User>()!! // Add user to the temporary list
                    tempList.add(user) // Add user to the temporary list
                }
                userList.addAll(tempList)  // Update the main user list
                adapter.notifyDataSetChanged() // Notify the adapter of data change

            }

        binding.searchButton.setOnClickListener {
            var text=binding.searchView.text.toString() // Get the text from the search view

            Firebase.firestore.collection(USER_NODE).whereEqualTo("name",text).get().addOnSuccessListener {


                    var tempList = ArrayList<User>()
                    userList.clear()
                if (it.isEmpty){
                    // Handle empty result set

                }else{
                    for (i in it.documents) {
                        if (i.id.toString().equals(Firebase.auth.currentUser!!.uid.toString())){
                            // Skip the current user
                        }else{
                            var user: User = i.toObject<User>()!!  // Deserialize document to User object

                            tempList.add(user) // Add user to the temporary list
                        }

                    }

                    userList.addAll(tempList) // Update the main user list
                    adapter.notifyDataSetChanged()  // Notify the adapter of data change
                }



                }

        }

        return binding.root // Return the root view of the fragment
    }

    companion object {

    }
}