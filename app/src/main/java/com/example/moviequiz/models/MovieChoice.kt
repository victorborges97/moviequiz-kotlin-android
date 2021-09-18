package com.example.moviequiz.models

data class MovieChoice (
    var idMovie: String = "",
    var title: String = "",
    var photo: String = "",
) {
    fun toMap(): Map<String, String> {
        return mapOf("title" to this.title, "photo" to this.photo, "idMovie" to this.idMovie, )
    }
}