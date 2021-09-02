package com.example.moviequiz.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import com.example.moviequiz.R
import com.example.moviequiz.repository.FirebaseRepository
import com.example.moviequiz.views.LoginActivity
import com.example.moviequiz.views.MainActivity
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class FeedFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_feed, container, false)

        val btnSair = rootView.findViewById<Button>(R.id.btn_sair)

        return rootView
    }
}