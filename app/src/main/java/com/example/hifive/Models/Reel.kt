package com.example.hifive.Models

class Reel {
    var reelUrl:String=""
    var caption:String=""
    var profileLink:String?=null
    var eventAddr:String=""
    var eventType:String=""
    var eventLoc:String=""

    constructor()
    constructor(reelUrl: String,caption:String) {
        this.reelUrl = reelUrl
        this.caption=caption
    }

    constructor(reelUrl: String, caption: String, profileLink: String) {
        this.reelUrl = reelUrl
        this.caption = caption
        this.profileLink = profileLink
    }

    constructor(reelUrl: String, caption: String, profileLink: String, addr: String, loc: String, etype: String) {
        this.reelUrl = reelUrl
        this.caption = caption
        this.profileLink = profileLink
        this.eventAddr = addr
        this.eventLoc = loc
        this.eventType = etype
    }


}