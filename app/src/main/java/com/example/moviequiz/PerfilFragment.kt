package com.example.moviequiz

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.squareup.picasso.Picasso

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class PerfilFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_perfil, container, false)

        val user: FirebaseUser = FirebaseAuth.getInstance().currentUser
        val imageViewUser = rootView.findViewById<ImageView>(R.id.iv_image_user_current)
        val tvName = rootView.findViewById<TextView>(R.id.tv_nomeUser)
        val tvEmail = rootView.findViewById<TextView>(R.id.tv_emailUser)
        val tvBio = rootView.findViewById<TextView>(R.id.tv_bioUser)

        Picasso.get().load(user.photoUrl).fit().centerCrop().into(imageViewUser);
        tvName.text = user.displayName
        tvEmail.text = user.email


        return rootView
    }


    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PerfilFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}