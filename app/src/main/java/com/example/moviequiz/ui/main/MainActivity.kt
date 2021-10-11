package com.example.moviequiz.ui.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.moviequiz.R
import com.example.moviequiz.models.User
import com.example.moviequiz.repository.FirebaseRepository
import com.example.moviequiz.ui.login.LoginActivity
import com.example.moviequiz.ui.create.CreatePostActivity
import com.example.moviequiz.ui.main.fragments.FeedFragment
import com.example.moviequiz.ui.main.fragments.PerfilFragment
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.bottom_navigation_coordinator.*
import kotlinx.android.synthetic.main.fragment_perfil.*

class MainActivity : AppCompatActivity() {
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private var googleSignClient : GoogleSignInClient? = null
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var firestoreRepository: FirebaseRepository
    private lateinit var user: User
    private val TAG = "MAIN"

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener {
        when(it.itemId) {
            R.id.feed -> {
                mudarFragment("Feed", false, FeedFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.perfil -> {
                mudarFragment("Perfil", false, PerfilFragment())
                return@OnNavigationItemSelectedListener true
            }
        }
        return@OnNavigationItemSelectedListener false
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_sair -> {
                logout()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initalComponents()
        getUser()

//        bottomNavigation.background = ColorDrawable(resources.getColor(R.color.appPrimary))
        bottomNavigation.background = null
        bottomNavigation.menu.getItem(1).isEnabled = false
        bottomNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        val launchActivityCreate = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if(result.resultCode == Activity.RESULT_OK){
                Log.d(TAG, "VOLTOU")
            }
        }

        fabCoord.setOnClickListener {
            val intent = Intent(applicationContext, CreatePostActivity::class.java)
            launchActivityCreate.launch(intent)
        }

    }

    override fun onStart() {
        super.onStart()
        googleSignClient = firestoreRepository.requestSignInOptions(this)
    }

    private fun initalComponents() {
        bottomNavigation = findViewById(R.id.bottom_navigation)
        firestoreRepository = FirebaseRepository()
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        mudarFragment("Feed", false, FeedFragment())
    }

    private fun mudarFragment(title: String, isTitle: Boolean = true, frag: Fragment) {
        if(isTitle){
            supportActionBar?.title = title
            setLogo(isTitle)
        } else {
            setLogo(isTitle)
            supportActionBar?.title = null
        }
        changeFragment(frag)
    }

    private fun setLogo(isTitle: Boolean) {
        this.supportActionBar?.setLogo(R.drawable.logo)
        this.supportActionBar?.setDisplayUseLogoEnabled(!isTitle)
        this.supportActionBar?.setDisplayShowHomeEnabled(!isTitle)
    }

    private fun changeFragment(targetFragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.main_fragment, targetFragment, "fragment")
            .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .commit()
    }

    private fun logout() {
        googleSignClient?.signOut()?.addOnCompleteListener(this) {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(applicationContext, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
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
                        userObject?.idUser = it.id
                        if (userObject != null) {
                            user = userObject
                        }
                    }
            } catch (e: Exception) {
                Log.e("ERROR PERFIL", "${e.message}")
            }
        }
    }

}