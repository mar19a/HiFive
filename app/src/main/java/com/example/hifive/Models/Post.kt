package com.example.hifive.Models

import com.google.android.gms.maps.model.LatLng

class Post {
    var postUrl:String=""
    var caption:String=""
    var uid:String=""
    var time:String=""
    var eventAddr:String=""
    var eventType:String=""
    var eventLoc:String=""



    constructor()

    constructor(postUrl: String,caption:String) {
        this.postUrl = postUrl
        this.caption=caption
    }

    constructor(postUrl: String, caption: String, uid: String, time: String) {
        this.postUrl = postUrl
        this.caption = caption
        this.uid = uid
        this.time = time
    }

    constructor(postUrl: String, caption: String, uid: String, time: String, addr: String, loc: String, etype: String) {
        this.postUrl = postUrl
        this.caption = caption
        this.uid = uid
        this.time = time
        this.eventAddr = addr
        this.eventLoc = loc
        this.eventType = etype
    }


}