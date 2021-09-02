package com.example.moviequiz.views

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.moviequiz.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class RegisterActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth

    private lateinit var et_nome: TextInputEditText
    private lateinit var et_nick: TextInputEditText
    private lateinit var et_email: TextInputEditText
    private lateinit var et_senha: TextInputEditText
    private lateinit var et_confir: TextInputEditText

    private lateinit var progressBar: ProgressBar
    private lateinit var btn_registrar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val linearLayout: ConstraintLayout = findViewById(R.id.bg_linear_register)
        aplicarLinearGradiente(linearLayout)

        mAuth = FirebaseAuth.getInstance()

        et_nome = findViewById(R.id.et_nome)
        et_nick = findViewById(R.id.et_nick)
        et_email = findViewById(R.id.et_email)
        et_senha = findViewById(R.id.et_senha)
        et_confir = findViewById(R.id.et_confir)

        progressBar = findViewById(R.id.progressBar_register)
        btn_registrar = findViewById(R.id.btn_registrar)

        btn_registrar.setOnClickListener {
            val mNome = et_nome.text.toString().trim()
            val mNick = et_nick.text.toString().trim()
            val mEmail = et_email.text.toString().trim()
            val mPassword = et_senha.text.toString().trim()
            val mCPassword = et_confir.text.toString().trim()

            val data = hashMapOf(
                "name" to mNome,
                "nick" to mNick,
                "email" to mEmail,
                "bio" to "",
                "url" to "",
                "create_at" to FieldValue.serverTimestamp(),
            )

            if (confirmacaoEt(mEmail, mPassword, mCPassword, mNome, mNick)) return@setOnClickListener

            progressBar.visibility = View.VISIBLE

            mAuth.createUserWithEmailAndPassword(mEmail, mPassword).addOnCompleteListener{ task ->
                if (task.isSuccessful) {

                    FirebaseFirestore
                        .getInstance()
                        .collection("users")
                        .document(task.result.user?.uid.toString())
                        .set(data)
                        .addOnSuccessListener {
                            startActivity(Intent(applicationContext, LoginActivity::class.java))
                            progressBar.visibility = View.INVISIBLE
                            finish()
                        }.addOnFailureListener {
                            progressBar.visibility = View.INVISIBLE
                            Log.w("REGISTER","Error ! " + Objects.requireNonNull(task.exception?.message))
                        }

                } else {
                    progressBar.visibility = View.INVISIBLE
                    Log.w("REGISTER","Error ! " + Objects.requireNonNull(task.exception?.message))
                }
            }

        }

    }

    private fun confirmacaoEt(
        mEmail: String,
        mPassword: String,
        mCPassword: String,
        mNome: String,
        mNick: String
    ): Boolean {
        if (mNome.isEmpty()) {
            et_email.error = "Nome is Required."
            return true
        }
        if (mNick.isEmpty()) {
            et_email.error = "Nickname is Required."
            return true
        }
        if (mEmail.isEmpty()) {
            et_email.error = "Email is Required."
            return true
        }
        if (mPassword.isEmpty()) {
            et_senha.error = "Password is Required."
            return true
        }
        if (mPassword.length < 6) {
            et_senha.error = "Password Must be >= 6 Characters"
            return true
        }
        if (mPassword != mCPassword) {
            et_senha.error = "Passwords are not the same"
            et_confir.error = "Passwords are not the same"
            return true
        }
        return false
    }


    private fun aplicarLinearGradiente(linearLayout: ConstraintLayout) {
        val gradientDrawable = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            intArrayOf(
                Color.parseColor("#FF5A5A"),
                Color.parseColor("#F58E43")
            )
        )

        gradientDrawable.cornerRadius = 0f;

        linearLayout.background = gradientDrawable;
    }
}