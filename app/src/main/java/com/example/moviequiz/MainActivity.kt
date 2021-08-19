package com.example.moviequiz

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue

class MainActivity : AppCompatActivity() {
    private lateinit var db: FirebaseFirestore
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mudarFragment("Feed", FeedFragment())

        db = FirebaseFirestore.getInstance()

        val user: MutableMap<String, Any> = HashMap()
        user["name"] = "Jardim Sandler"
        user["photo"] = ""
        user["create_at"] = FieldValue.serverTimestamp()

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

    private fun goProfile(app: AppCompatActivity) {
//        val intent = Intent(applicationContext, ??????????::class.java)
//        startActivity(intent)
    }

    private fun logout() {
//        googleSignClient?.signOut()?.addOnCompleteListener(this) { }
        FirebaseAuth.getInstance().signOut();
        val intent = Intent(applicationContext, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }


    fun addUser(user: MutableMap<String, Any>) {
        // Add a new document with a generated ID
        db.collection("users")
            .add(user)
            .addOnSuccessListener { documentReference ->
                Log.d(
                    TAG,
                    "DocumentSnapshot added with ID: " + documentReference.id
                )
            }
            .addOnFailureListener { e -> Log.w(TAG, "Error adding document", e) }
    }

    fun getUsers() {
        db.collection("users")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result!!) {
                        Log.d(TAG, document.id + " => " + document.data)
                    }
                } else {
                    Log.w(TAG, "Error getting documents.", task.exception)
                }
            }
    }

}