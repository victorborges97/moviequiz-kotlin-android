package br.borges.moviequiz.models

import android.util.Log
import br.borges.moviequiz.repository.FirebaseRepository
import com.google.firebase.Timestamp

data class Post(
    var idPost: String = "",
    var title: String = "",
    var userName: String = "",
    var userPhoto: String = "",
    var userId: String = "",
    var movies: List<MovieChoice> = listOf(),
    var wishes: List<Wishes> = listOf(),
    var create_at: Timestamp? = Timestamp.now(),
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
        )
    }

    fun votar(post: Post, movie: MovieChoice) {
        val uid = FirebaseRepository().user?.uid
        if(uid != null && uid != ""){
            Log.i("POST", "ID: $uid")
            Log.i("POST", "POST CLASS: $idPost")
            Log.i("POST", "POST: ${post.toString()}")

            if(uid == userId) {
                return
            }

            val isExist = wishes.find {
                it.userId == uid
            }
            Log.i("POST", "isExist: ${isExist.toString()}")
            if(isExist != null) {
                return
            }

            FirebaseRepository().votarPost(post.idPost, Wishes(idMovie = movie.idMovie, userId = uid))

        }
    }

    override fun toString(): String {
        return "\nPost(idPost='$idPost', title='$title', userName='$userName', userId='$userId', wishes=$wishes)"
    }


}