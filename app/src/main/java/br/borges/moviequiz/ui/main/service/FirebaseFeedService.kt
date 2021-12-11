package br.borges.moviequiz.ui.main.service

import android.util.Log
import br.borges.moviequiz.models.Post
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

object FirebaseFeedService {

    private const val TAG = "FirebaseProfileService"
    suspend fun getPostData(postId: String): Post? {
        val db = FirebaseFirestore.getInstance()
        return try {
            db.collection("posts")
                .document(postId).get().await().toObject(Post::class.java)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting user details", e)
//            FirebaseCrashlytics.getInstance().log("Error getting user details")
//            FirebaseCrashlytics.getInstance().setCustomKey("user id", xpertSlug)
//            FirebaseCrashlytics.getInstance().recordException(e)
            null
        }
    }

    @ExperimentalCoroutinesApi
    fun getPosts(): Flow<List<Post>?> {
        val db = FirebaseFirestore.getInstance()
        return callbackFlow {
            val listenerRegistration = db.collection("posts")
                .orderBy("create_at", Query.Direction.DESCENDING)
                .addSnapshotListener { querySnapshot: QuerySnapshot?, firebaseFirestoreException: FirebaseFirestoreException? ->
                    if (firebaseFirestoreException != null) {
                        cancel(message = "Error fetching posts",
                            cause = firebaseFirestoreException)
                        return@addSnapshotListener
                    }
                    val map = querySnapshot?.documents?.mapNotNull {
                        val e = it.toObject(Post::class.java)
                        e?.idPost = it.id
                        e
                    }
                    offer(map)
                }
            awaitClose {
                Log.d(TAG, "Cancelling posts listener")
                listenerRegistration.remove()
            }
        }
    }

}