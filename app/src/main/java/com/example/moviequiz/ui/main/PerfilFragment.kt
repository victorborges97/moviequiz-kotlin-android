package com.example.moviequiz.ui.main

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.example.moviequiz.R
import com.example.moviequiz.models.User
import com.example.moviequiz.repository.FirebaseRepository
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class PerfilFragment() : Fragment() {
    private lateinit var db: FirebaseFirestore;
    private lateinit var auth: FirebaseAuth;
    private lateinit var user: User;

    private var googleSignClient : GoogleSignInClient? = null
    private lateinit var firestoreRepository: FirebaseRepository

    private lateinit var imageViewUser: ImageView;
    private lateinit var tvName: TextView;
    private lateinit var tvEmail: TextView;
    private lateinit var tvBio: TextView;
    private lateinit var mbSair: Button;

    private lateinit var rootView: View;

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_perfil, container, false)
        initialComponents()
        getUser()


        mbSair.setOnClickListener {
        };

        return rootView
    }

    private fun initialComponents() {
        firestoreRepository = FirebaseRepository()
        googleSignClient = firestoreRepository.requestSignInOptions(rootView.context)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        mbSair = rootView.findViewById<Button>(R.id.mbSair)
        imageViewUser = rootView.findViewById(R.id.ivPerfilUser)
        tvName = rootView.findViewById(R.id.tvNomePerfilUser)
        tvEmail = rootView.findViewById(R.id.tvEmailPerfilUser)
        tvBio = rootView.findViewById(R.id.tvBioPerfilUser)
    }

    fun getUser() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        Log.d("PERFIL", currentUser.toString());
        if(currentUser != null) {
            try {
                db.collection("users")
                    .document(currentUser.uid)
                    .get()
                    .addOnSuccessListener {
                        val userObject = it.toObject(User::class.java)
                        Log.d("PERFIL", userObject.toString());
                        if (userObject != null) {
                            user = userObject
                            Log.d("PERFIL", user.name);
                            visibleInfor(userObject)
                        }
                    }
            } catch (e: Exception) {
                Log.e("ERROR PERFIL", "${e.message}");
            }
        }
    }

    private fun visibleInfor(user: User) {
        if(user.url != ""){
            Picasso.get().load(user.url).fit()
                .centerCrop()
                .placeholder(R.drawable.ic_user)
                .into(imageViewUser);
        } else {
            Picasso.get().load(R.drawable.ic_user).fit()
                .centerCrop()
                .into(imageViewUser);
        }
        tvName.text = user.name
        tvEmail.text = user.email
    }
}