package com.footprint.footprint.data.remote.auth

class SocialUserData {
    lateinit var userId: String
    lateinit var username: String
    lateinit var email: String

    fun SocialUserData(userId: String, username: String, email: String){
        this.userId = userId
        this.username = username
        this.email = email
    }
}