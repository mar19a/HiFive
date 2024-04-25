package com.example.hifive

import com.google.android.gms.nearby.connection.Strategy
import android.Manifest
import android.animation.Animator
import android.os.Bundle
import android.os.Build
import com.google.android.gms.nearby.connection.Payload
import com.google.android.gms.nearby.connection.ConnectionInfo
import kotlin.random.Random
import android.widget.Toast
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.hifive.Models.User
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import com.example.hifive.adapters.SearchAdapter


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

    private lateinit var addUserText : TextView
    private lateinit var userIDText : TextView
    private lateinit var connectedIDText : TextView

    private lateinit var UUID : String

    private lateinit var qrFrame : ImageView

    private lateinit var sendIdButton : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_user)

        //DEBUG Temporary User ID Generation
        myName = getRandomString(8)
        addUserText = findViewById(R.id.addUserDialog)
        userIDText = findViewById(R.id.debugUserID)
        userIDText.text = "ID: " + myName
        connectedIDText = findViewById(R.id.debugConnectedID)
        qrFrame = findViewById(R.id.testQr)
        Picasso.get().load("https://api.qrserver.com/v1/create-qr-code/?size=150x150&data="+myName).into(qrFrame)

        sendIdButton = findViewById(R.id.sendIDButton)
        sendIdButton.setOnClickListener{
            send(Payload.fromBytes(myName.toByteArray(Charsets.UTF_8)))
        }

        adapter = SearchAdapter(applicationContext, userList)
        UUID = FirebaseAuth.getInstance().currentUser!!.uid

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
        if (payload!!.type == Payload.Type.BYTES){
            connectedIDText.text = payload.asBytes().toString()
            //TODO: Parse Bytes Here And Display Follow RV for User
        }
    }




    //States to label UI state machine
    public enum class State {
        UNKNOWN,
        SEARCHING,
        CONNECTED
    }

}