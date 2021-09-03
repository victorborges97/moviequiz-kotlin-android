package com.example.moviequiz.views

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.moviequiz.R
import com.example.moviequiz.Uteis.Uteis.snack
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*


class LoginActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    var googleSignClient : GoogleSignInClient? = null
    val RC_SIGN_IN = 1000

    private lateinit var input_text_email_login: TextInputEditText
    private lateinit var input_text_password_login: TextInputEditText
    private lateinit var progressBar_login: ProgressBar
    private lateinit var btn_login: AppCompatButton
    private lateinit var btn_google: SignInButton

    private lateinit var itView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mAuth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignClient = GoogleSignIn.getClient(this, gso)

        val linearLayout: ConstraintLayout = findViewById(R.id.bg_linear)
        aplicarLinearGradiente(linearLayout)

        input_text_email_login = findViewById(R.id.input_text_email_login)
        input_text_password_login = findViewById(R.id.input_text_password_login)
        progressBar_login = findViewById(R.id.progressBar_login)
        btn_login = findViewById(R.id.btn_login)
        btn_google = findViewById(R.id.btn_google)

        val registre = findViewById<TextView>(R.id.btn_register)

        btn_google.setOnClickListener {
            changedUiLogin(true)
            signInGoogle()
        }

        btn_login.setOnClickListener {
            itView = it
            loginEmail()
        }

        registre.setOnClickListener {
            val intent = Intent(applicationContext, RegisterActivity::class.java)
            startActivity(intent)
        }

        input_text_password_login.setOnEditorActionListener{ v, actionId, event ->
            var handled = false
            if (EditorInfo.IME_ACTION_DONE == actionId || EditorInfo.IME_ACTION_UNSPECIFIED == actionId) {
                loginEmail()
                handled = true
            }
            handled
        }

    }

    private fun loginEmail() {
        val mEmail = input_text_email_login.text.toString().trim()
        val mPassword = input_text_password_login.text.toString().trim()

        if (confirmation(mEmail, mPassword)) return

        changedUiLogin(true)
        mAuth.signInWithEmailAndPassword(mEmail, mPassword).addOnCompleteListener{ task ->
            if (task.isSuccessful) {
                gotoProfile()
                progressBar_login.visibility = View.INVISIBLE
            } else {
                changedUiLogin(false)
                // Retorno de erro para o usuario
                snack(itView, "Algo deu errado no login"+ (task.exception?.message) +"")
            }
        }
    }

    private fun changedUiLogin(el: Boolean) {
        blocksFields(el)
        progressBar_login.visibility = if(el) View.VISIBLE else View.INVISIBLE
        btn_login.hint = if(el) "Entrando..." else "Entrar"
    }

    private fun blocksFields(b: Boolean) {
        btn_login.isEnabled = !b
        input_text_password_login.isEnabled = !b
        input_text_email_login.isEnabled = !b
    }

    private fun confirmation(mEmail: String, mPassword: String): Boolean {
        if (mEmail.isEmpty()) {
            input_text_email_login.error = "Email is Required."
            return true
        }
        if (mPassword.isEmpty()) {
            input_text_password_login.error = "Password is Required."
            return true
        }
        if (mPassword.length < 6) {
            input_text_password_login.error = "Password Must be >= 6 Characters"
            return true
        }
        return false
    }


    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser: FirebaseUser? = mAuth.getCurrentUser()
        updateUI(currentUser)
    }

    private fun gotoProfile() {
        val intent = Intent(applicationContext, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun gotoProfileSign(user: FirebaseUser?) {
        var nome = "${user?.displayName}"
        var nick = ""
        var email = "${user?.email}"
        var url = "${user?.photoUrl}"
        var bio = ""

        val data = hashMapOf<String, Any>(
            "name" to nome,
            "nick" to nick,
            "email" to email,
            "bio" to bio,
            "url" to url,
            "create_at" to FieldValue.serverTimestamp(),
        )
        FirebaseFirestore
            .getInstance()
            .collection("users")
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener {
                if(it.documents.isEmpty()){
                    FirebaseFirestore
                        .getInstance()
                        .collection("users")
                        .document(user?.uid.toString())
                        .set(data)
                        .addOnSuccessListener {
                            val intent = Intent(applicationContext, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }.addOnFailureListener {
                            Log.w("REGISTER","Error ! " + Objects.requireNonNull(it.message))
                            snack(itView, "Error ! " + Objects.requireNonNull(it.message)+"")
                        }
                } else {
                    val intent = Intent(applicationContext, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                changedUiLogin(false)
            }
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

    private fun updateUI(currentUser: FirebaseUser?) {
        if(currentUser != null){
            Log.i("LOGIN", currentUser.getEmail().toString());
            gotoProfile();
        }
    }

    private fun signInGoogle() {
        val signInIntent = googleSignClient?.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount?) {
        val credencial = GoogleAuthProvider.getCredential(acct?.idToken, null)
        FirebaseAuth.getInstance().signInWithCredential(credencial).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user: FirebaseUser? = task.result.user
                gotoProfileSign(user)

            } else {
                snack(itView, "Algo deu errado no login"+ (task.exception?.message) +"")
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == RC_SIGN_IN){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val account = task.getResult(ApiException::class.java)
            firebaseAuthWithGoogle(account)
        }
    }

}


