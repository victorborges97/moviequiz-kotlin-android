package com.example.moviequiz.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.moviequiz.R
import com.example.moviequiz.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class PerfilFragment() : Fragment() {
    private lateinit var db: FirebaseFirestore;
    private lateinit var auth: FirebaseAuth;
    private lateinit var user: User;

    private lateinit var imageViewUser: ImageView;
    private lateinit var tvName: TextView;
    private lateinit var tvEmail: TextView;
    private lateinit var tvBio: TextView;

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_perfil, container, false)
        imageViewUser = rootView.findViewById(R.id.iv_image_user_current)
        tvName = rootView.findViewById(R.id.tv_nomeUser)
        tvEmail = rootView.findViewById(R.id.tv_emailUser)
        tvBio = rootView.findViewById(R.id.tv_bioUser)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        auth.uid?.let {
            getUser(it)
        }

        return rootView
    }

    fun getUser(id: String) {
        db.collection("users")
            .document(id.toString())
            .get()
            .addOnSuccessListener {
                user = it.toObject(User::class.java)!!
                Log.d("PERFIL", user.name);
                visibleInfor(user)
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