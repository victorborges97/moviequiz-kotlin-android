package com.example.moviequiz.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.moviequiz.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.squareup.picasso.Picasso

class PerfilFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_perfil, container, false)

        val user: FirebaseUser? = FirebaseAuth.getInstance().currentUser
        val imageViewUser = rootView.findViewById<ImageView>(R.id.iv_image_user_current)
        val tvName = rootView.findViewById<TextView>(R.id.tv_nomeUser)
        val tvEmail = rootView.findViewById<TextView>(R.id.tv_emailUser)
        val tvBio = rootView.findViewById<TextView>(R.id.tv_bioUser)

        Picasso.get().load(user?.photoUrl).fit().centerCrop().into(imageViewUser);
        tvName.text = user?.displayName
        tvEmail.text = user?.email


        return rootView
    }
}