package com.example.hifive.Models

import com.google.android.gms.maps.model.LatLng

class Post {
    var postId: String = ""
    var title:String=""
    var postUrl:String=""
    var caption:String=""
    var uid:String=""
    var time:String=""
    var eventAddr:String=""
    var eventType:String=""
    var eventLoc:String=""
    var eventDate:String=""
    var eventTime:String=""
    var isLiked: Boolean = false


    constructor()

    constructor(postId: String, postUrl: String, caption: String) {
        this.postId = postId
        this.postUrl = postUrl
        this.caption = caption
    }

    constructor(postId: String, postUrl: String, caption: String, uid: String, time: String) {
        this.postId = postId
        this.postUrl = postUrl
        this.caption = caption
        this.uid = uid
        this.time = time
    }

    constructor(postId: String, postUrl: String, caption: String, uid: String, time: String, addr: String, loc: String) {
        this.postId = postId
        this.postUrl = postUrl
        this.caption = caption
        this.uid = uid
        this.time = time
        this.eventAddr = addr
        this.eventLoc = loc
    }

    constructor(postId: String, title: String, postUrl: String, caption: String, uid: String, time: String, addr: String, loc: String, etype: String) {
        this.postId = postId
        this.title = title
        this.postUrl = postUrl
        this.caption = caption
        this.uid = uid
        this.time = time
        this.eventAddr = addr
        this.eventLoc = loc
        this.eventType = etype
    }
}