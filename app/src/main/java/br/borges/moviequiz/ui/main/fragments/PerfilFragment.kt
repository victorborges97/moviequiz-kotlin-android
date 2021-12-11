package br.borges.moviequiz.ui.main.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import br.borges.moviequiz.R
import br.borges.moviequiz.models.User
import br.borges.moviequiz.repository.FirebaseRepository
import br.borges.moviequiz.ui.main.adapter.PerfilAdapter
import br.borges.moviequiz.ui.main.view_models.ViewModelFeed
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_perfil.view.*
import android.view.MenuInflater
import br.borges.moviequiz.ui.about.AboutActivity
import br.borges.moviequiz.ui.login.LoginActivity
import com.google.android.gms.auth.api.signin.GoogleSignInClient


class PerfilFragment : Fragment() {
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var user: User

    private lateinit var firestoreRepository: FirebaseRepository

    private var googleSignClient : GoogleSignInClient? = null

    private lateinit var imageViewUser: ImageView
    private lateinit var tvName: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvBio: TextView

    private lateinit var rootView: View

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_menu, menu)
        super.onCreateOptionsMenu(menu!!, inflater)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_sair -> {
                logout()
                true
            }
            R.id.menu_sobre -> {
                goToSobre()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        rootView = inflater.inflate(R.layout.fragment_perfil, container, false)
        setHasOptionsMenu(true);
        initialComponents()
        getUser()
        val viewModel = ViewModelProvider(this)[ViewModelFeed::class.java]

        rootView.rvPerfilPostsUser.apply {
            layoutManager = GridLayoutManager(rootView.context, 2)
            setHasFixedSize(true)
        }

        viewModel.postsUser.observe(viewLifecycleOwner, {
            val recyclerViewState = rootView.rvPerfilPostsUser.layoutManager?.onSaveInstanceState()
            rootView.rvPerfilPostsUser.adapter = PerfilAdapter(it)
            rootView.rvPerfilPostsUser.adapter?.notifyDataSetChanged()
            rootView.rvPerfilPostsUser.layoutManager?.onRestoreInstanceState(recyclerViewState)
        })

        return rootView
    }

    override fun onStart() {
        super.onStart()
        googleSignClient = firestoreRepository.requestSignInOptions(rootView.context)
    }

    private fun initialComponents() {
        firestoreRepository = FirebaseRepository()
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        imageViewUser = rootView.findViewById(R.id.ivPerfilUser)
        tvName = rootView.findViewById(R.id.tvNomePerfilUser)
        tvEmail = rootView.findViewById(R.id.tvEmailPerfilUser)
        tvBio = rootView.findViewById(R.id.tvBioPerfilUser)

    }

    private fun getUser() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if(currentUser != null) {
            try {
                db.collection("users")
                    .document(currentUser.uid)
                    .get()
                    .addOnSuccessListener {
                        val userObject = it.toObject(User::class.java)
                        if (userObject != null) {
                            userObject.idUser = it.id;
                            user = userObject
                            visibleInfor(userObject)
                        }
                    }
            } catch (e: Exception) {
                Log.e("ERROR PERFIL", "${e.message}")
            }
        }
    }

    private fun visibleInfor(user: User) {
        if(user.url != ""){
            Picasso.get().load(user.url).fit()
                .centerCrop()
                .placeholder(R.drawable.ic_user)
                .into(imageViewUser)
        } else {
            Picasso.get().load(R.drawable.ic_user).fit()
                .centerCrop()
                .into(imageViewUser)
        }
        tvName.text = user.name
        tvEmail.text = user.email
    }

    private fun logout() {
        googleSignClient?.signOut()?.addOnCompleteListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(rootView.context, LoginActivity::class.java)
            activity?.startActivity(intent)
            activity?.finish()
        }
    }

    private fun goToSobre() {
        activity?.let{
            val intent = Intent (it, AboutActivity::class.java)
            it.startActivity(intent)
        }
    }
}