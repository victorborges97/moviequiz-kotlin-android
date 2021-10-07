package com.example.moviequiz.repository

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.moviequiz.R
import com.example.moviequiz.models.Post
import com.example.moviequiz.models.Wishes
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import java.util.*

class FirebaseRepository {
    private var db = FirebaseFirestore.getInstance()
    private val mAuth = FirebaseAuth.getInstance()
    private val TAG = "FIREBASE_CLASSE"
    private var googleSignClient : GoogleSignInClient? = null
    var user: FirebaseUser? = FirebaseAuth.getInstance().currentUser;

    fun requestSignInOptions(c: Context): GoogleSignInClient? {
        db = FirebaseFirestore.getInstance()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(c.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignClient = GoogleSignIn.getClient(c, gso)
        return googleSignClient
    }

    fun getLists(): Query {
        db = FirebaseFirestore.getInstance()

        return db
            .collection("posts")
            .orderBy("create_at", Query.Direction.ASCENDING)
    }

    fun deletePost(id: String) {
        db = FirebaseFirestore.getInstance()

        db.collection("posts")
            .document(id)
            .delete()
    }

    fun addPost(post: Post) {
        db = FirebaseFirestore.getInstance()

        db.collection("posts")
            .add(post.toMap())
            .addOnSuccessListener {
                Log.d("CREATE_FIREBASE", "OnSuccess Created id: ${it.id}")
                return@addOnSuccessListener
            }
            .addOnFailureListener {
                    e -> Log.w("CREATE_FIREBASE", "OnFailure Update: ", e)
                return@addOnFailureListener
            }
    }

    fun votarPost(id: String, voto: Wishes) {
        db = FirebaseFirestore.getInstance()

        db.collection("posts")
            .document(id)
            .update(
                mapOf(
                    "wishes" to FieldValue.arrayUnion(voto.toMap()),
                )
            )
            .addOnSuccessListener {
                Log.d("CREATE_FIREBASE", "OnSuccess Created Wishe: $id")
                return@addOnSuccessListener
            }
            .addOnFailureListener {
                    e -> Log.w("CREATE_FIREBASE", "OnFailure Update: ", e)
                return@addOnFailureListener
            }
    }

}