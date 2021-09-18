package com.example.moviequiz.models

import com.google.firebase.Timestamp

data class User (
    var idUser: String = "",
    var name: String = "",
    var nick: String = "",
    var email: String = "",
    var bio: String = "",
    var url: String = "",
    var create_at: Timestamp? = null,
) {

    fun updateUser() {

    }


}