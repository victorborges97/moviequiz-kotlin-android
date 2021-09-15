package com.example.moviequiz.repository

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.moviequiz.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import java.util.*

class FirebaseRepository {
    private var db = FirebaseFirestore.getInstance()
    private val mAuth = FirebaseAuth.getInstance()
    private val TAG = "FIREBASE_CLASSE"
    private var googleSignClient : GoogleSignInClient? = null

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

}