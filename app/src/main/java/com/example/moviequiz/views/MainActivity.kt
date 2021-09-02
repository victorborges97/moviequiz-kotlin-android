package com.example.moviequiz.views

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.moviequiz.fragments.PerfilFragment
import com.example.moviequiz.R
import com.example.moviequiz.fragments.FeedFragment
import com.example.moviequiz.repository.FirebaseRepository
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {
    private lateinit var db: FirebaseFirestore
    private var googleSignClient : GoogleSignInClient? = null
    private lateinit var firestoreRepository: FirebaseRepository
    private val TAG = "MAIN"

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener {it ->
        when(it.itemId) {
            R.id.feed -> {
                mudarFragment("Feed", FeedFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.perfil -> {
                mudarFragment("Perfil", PerfilFragment())
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
        firestoreRepository = FirebaseRepository()
        googleSignClient = firestoreRepository.requestSignInOptions(this)
        mudarFragment("Feed", FeedFragment())

        db = FirebaseFirestore.getInstance()

        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigation.background = ColorDrawable(resources.getColor(R.color.appPrimary))
        bottomNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

    }

    private fun mudarFragment(title: String, frag: Fragment) {
        supportActionBar?.title = title
        changeFragment(frag)
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
            FirebaseAuth.getInstance().signOut();
            val intent = Intent(applicationContext, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

}