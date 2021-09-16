package com.example.moviequiz.views

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.moviequiz.fragments.PerfilFragment
import com.example.moviequiz.R
import com.example.moviequiz.fragments.FeedFragment
import com.example.moviequiz.models.User
import com.example.moviequiz.repository.FirebaseRepository
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_perfil.*

class MainActivity : AppCompatActivity() {
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private var googleSignClient : GoogleSignInClient? = null
    private lateinit var firestoreRepository: FirebaseRepository
    private lateinit var user: User;
    private val TAG = "MAIN"

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener {it ->
        when(it.itemId) {
            R.id.feed -> {
                mudarFragment("Feed", false, FeedFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.perfil -> {
                mudarFragment("Perfil", true, PerfilFragment())
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
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        googleSignClient = firestoreRepository.requestSignInOptions(this)
        mudarFragment("Feed", false, FeedFragment())

        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottom_navigation)
//        bottomNavigation.background = ColorDrawable(resources.getColor(R.color.appPrimary))
        bottomNavigation.background = null;
        bottomNavigation.menu.getItem(1).isEnabled = false
        bottomNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

    }

    private fun mudarFragment(title: String, isTitle: Boolean = true, frag: Fragment) {
        if(isTitle){
            supportActionBar?.title = title
            setLogo(isTitle)
        } else {
            setLogo(isTitle)
            supportActionBar?.title = null;
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
            FirebaseAuth.getInstance().signOut();
            val intent = Intent(applicationContext, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

}