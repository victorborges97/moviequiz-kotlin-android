package com.example.moviequiz.models

import com.example.moviequiz.repository.FirebaseRepository
import com.google.firebase.Timestamp

data class Post(
    var idPost: String = "",
    var title: String = "",
    var userName: String = "",
    var userPhoto: String = "",
    var userId: String = "",
    var movies: List<MovieChoice> = listOf(),
    var wishes: List<Map<String, String>> = listOf(),
    var create_at: Timestamp? = Timestamp.now(),
    var usersChecked: List<String> = listOf(),
) {

    fun deletePost() {
        if(this.idPost !== ""){
            FirebaseRepository().deletePost(this.idPost)
        }
    }

    fun toMap(): Map<String, Any?> {
        return mapOf(
            "title" to this.title,
            "userName" to this.userName,
            "userPhoto" to this.userPhoto,
            "userId" to this.userId,
            "movies" to this.movies,
            "wishes" to this.wishes,
            "create_at" to this.create_at,
            "usersChecked" to this.usersChecked,
        )
    }

}