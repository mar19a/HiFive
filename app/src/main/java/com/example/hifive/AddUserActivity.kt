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


class AddUserActivity : ConnectionsActivity() {

    //Constant for connection API
    private val STRATEGY = Strategy.P2P_STAR
    //Only other activities with the same service id can communicate
    private val SERVICE_ID = "HiFive"
    //Identification of the user's endpoint for communication
    private val myName = "get user's name and put it here"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_user)
    }

    //Required Getters for Class implementation
    override fun getName(): String { return myName }
    override fun getServiceId(): String { return SERVICE_ID }
    override fun getStrategy(): Strategy { return STRATEGY }


    //State Machine
    private fun onStateChanged(oldState : State, newState : State){
        //Change Nearby Connections behavior to new state
        when(newState){
            State.SEARCHING -> {
                disconnectFromAllEndpoints()
                startDiscovering()
                startAdvertising()
            }
            State.CONNECTED -> {
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



    //States to label UI state machine
    public enum class State {
        UNKNOWN,
        SEARCHING,
        CONNECTED
    }

}