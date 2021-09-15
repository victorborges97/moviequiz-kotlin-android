package com.example.moviequiz.models

import com.google.firebase.Timestamp

class Post {
    var idPost: String = "";
    val title: String = "";
    val userName: String = "";
    val userPhoto: String = "";
    val userId: String = "";
    val movies: List<MovieChoice> = listOf();
    val wishes: List<Map<String, String>> = listOf();
    val create_at: Timestamp? = null;
    val usersChecked: List<String> = listOf();
}