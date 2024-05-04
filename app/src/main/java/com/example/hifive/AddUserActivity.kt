package com.example.hifive

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hifive.Models.User
import com.example.hifive.adapters.SearchAdapter
import com.example.hifive.utils.USER_NODE
import com.google.android.gms.nearby.connection.ConnectionInfo
import com.google.android.gms.nearby.connection.Payload
import com.google.android.gms.nearby.connection.Strategy
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.zxing.integration.android.IntentIntegrator
import com.squareup.picasso.Picasso


class AddUserActivity : ConnectionsActivity() {

    //Constant for connection API
    private val STRATEGY = Strategy.P2P_STAR
    //Only other activities with the same service id can communicate
    private val SERVICE_ID = "HiFive"
    //Identification of the user's endpoint for communication
    private var myName = "get user's name and put it here"

    private var mState = State.UNKNOWN

    private lateinit var adapter : SearchAdapter
    private var userList = ArrayList<User>()
    private lateinit var rv : RecyclerView

    private lateinit var addUserText : TextView
    private lateinit var userIDText : TextView
    private lateinit var connectedIDText : TextView

    private lateinit var UUID : String

    private lateinit var qrFrame : ImageView

    private lateinit var sendIdButton : Button

    private lateinit var scanQRButton : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_add_user)

        //DEBUG Temporary User ID Generation
        //myName = getRandomString(8)

        addUserText = findViewById(R.id.addUserDialog)
        userIDText = findViewById(R.id.debugUserID)

        connectedIDText = findViewById(R.id.debugConnectedID)
        qrFrame = findViewById(R.id.testQr)

        UUID = FirebaseAuth.getInstance().currentUser!!.uid
        myName = UUID

        Picasso.get().load("https://api.qrserver.com/v1/create-qr-code/?size=150x150&data="+myName).into(qrFrame)


        //Debug TextView (To remove)
        userIDText.text = "UUID: " + myName

        sendIdButton = findViewById(R.id.sendIDButton)
        sendIdButton.setOnClickListener{
            Log.d("Check UUID", UUID)
            send(Payload.fromBytes(UUID.toByteArray(Charsets.UTF_8)))

        }

        scanQRButton = findViewById(R.id.scanQRButton)
        scanQRButton.setOnClickListener {
            initQRCodeScanner()
        }

        adapter = SearchAdapter(applicationContext, userList)
        rv = findViewById(R.id.recyclerView)
        rv.layoutManager = LinearLayoutManager(applicationContext)
        rv.adapter = adapter
    }

    private fun initQRCodeScanner() {
        val integrator = IntentIntegrator(this)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
        integrator.setOrientationLocked(false)
        integrator.setPrompt("Scan a QR code")
        integrator.initiateScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                Toast.makeText(this, "Scan cancelled", Toast.LENGTH_LONG).show()
            } else {
                Firebase.firestore.collection(USER_NODE).get().addOnSuccessListener {

                    var tempList = ArrayList<User>()
                    userList.clear()
                    for (i in it.documents) {

                        if (i.id == Firebase.auth.currentUser!!.uid) {

                        } else {
                            Log.d("iterated user id", i.id)

                            if (result.contents == i.id) {
                                //Check if payload is ever caught
                                Log.d("debug query", "test query matched")
                                var user: User = i.toObject<User>()!!

                                tempList.add(user)
                            }
                        }

                    }

                    userList.addAll(tempList)
                    adapter.notifyDataSetChanged()
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun getRandomString(length: Int) : String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }

    override fun onStart(){
        super.onStart()

        setState(State.SEARCHING)
    }

    //Required Getters for Class implementation
    override fun getName(): String { return myName }
    override fun getServiceId(): String { return SERVICE_ID }
    override fun getStrategy(): Strategy { return STRATEGY }


    override fun onBackPressed() {
        disconnectFromAllEndpoints()
    }
    override fun onEndpointDiscovered(endpoint: Endpoint?) {
        stopDiscovering()
        connectToEndpoint(endpoint)
    }
    override fun onConnectionInitiated(endpoint: Endpoint?, connectionInfo: ConnectionInfo?) {
        acceptConnection(endpoint)
    }

    override fun onEndpointConnected(endpoint: Endpoint?) {
        Toast.makeText(this, "Connected to" + endpoint?.name, Toast.LENGTH_SHORT).show()
        setState(State.CONNECTED)
    }

    override fun onEndpointDisconnected(endpoint: Endpoint?) {
        Toast.makeText(this, "Disconnected", Toast.LENGTH_SHORT).show()
        setState(State.SEARCHING)
    }

    override fun onConnectionFailed(endpoint: Endpoint?) {
        if(mState == State.SEARCHING){
            startDiscovering()
        }
    }


    private fun setState(state : State){
        val oldState = mState
        mState = state
        onStateChanged(oldState, state)
    }
    //State Machine
    private fun onStateChanged(oldState : State, newState : State){
        //Change Nearby Connections behavior to new state
        when(newState){
            State.SEARCHING -> {
                addUserText.text = "Searching For Nearby Users..."
                disconnectFromAllEndpoints()
                startDiscovering()
                startAdvertising()
            }
            State.CONNECTED -> {
                addUserText.text = "Nearby User Found!"
                stopDiscovering()
                stopAdvertising()
            }
            State.UNKNOWN -> {
                stopAllEndpoints()
            }
        }

        //Update UI behavior
        when(oldState){
            State.SEARCHING -> {

            }
            State.CONNECTED -> {

            }
            State.UNKNOWN -> {

            }
        }
    }

    override fun onReceive(endpoint: Endpoint?, payload: Payload?) {
        if (payload!!.type == Payload.Type.BYTES) {
            connectedIDText.text = payload.asBytes()!!.toString(Charsets.UTF_8)
            //TODO: Parse Bytes Here And Display Follow RV for User
            Firebase.firestore.collection(USER_NODE).get().addOnSuccessListener {
                //Debug Statement
                //Log.d("firebase debug", "payload received, OnSuccess called")
                //Log.d("firebase debug", "Payload:" + payload.asBytes()!!.toString(Charsets.UTF_8))

                var tempList = ArrayList<User>()
                userList.clear()
                for (i in it.documents) {

                    if (i.id == Firebase.auth.currentUser!!.uid) {

                    } else {
                        Log.d("iterated user id", i.id)

                        if (connectedIDText.text == i.id) {
                            //Check if payload is ever caught
                            Log.d("debug query", "test query matched")
                            var user: User = i.toObject<User>()!!

                            tempList.add(user)
                        }
                    }

                }

                userList.addAll(tempList)
                adapter.notifyDataSetChanged()
            }
        }
    }



    //States to label UI state machine
    public enum class State {
        UNKNOWN,
        SEARCHING,
        CONNECTED
    }

}