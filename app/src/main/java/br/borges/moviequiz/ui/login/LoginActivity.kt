package br.borges.moviequiz.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import br.borges.moviequiz.R
import br.borges.moviequiz.Uteis.Uteis
import br.borges.moviequiz.repository.FirebaseRepository
import br.borges.moviequiz.ui.main.MainActivity
import br.borges.moviequiz.ui.register.RegisterActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_login.*
import java.util.*

class LoginActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    private var googleSignClient : GoogleSignInClient? = null
    private lateinit var firestoreRepository: FirebaseRepository
    private val RC_SIGN_IN = 1000

    private lateinit var itView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        initalComponents()

    }

    private fun initalComponents() {
        mAuth = FirebaseAuth.getInstance()
        firestoreRepository = FirebaseRepository()
        googleSignClient = firestoreRepository.requestSignInOptions(this)

        bGoogle.setOnClickListener {
            changedUiLogin(true)
            signInGoogle()
        }

        bLogin.setOnClickListener {
            itView = it
            loginWithEmail()
        }

        tvRegister.setOnClickListener {
            val intent = Intent(applicationContext, RegisterActivity::class.java)
            startActivity(intent)
        }

        itPasswordLogin.setOnEditorActionListener { _, actionId, _ ->
            var handled = false
            if (EditorInfo.IME_ACTION_DONE == actionId || EditorInfo.IME_ACTION_UNSPECIFIED == actionId) {
                loginWithEmail()
                handled = true
            }
            handled
        }
    }

    private fun loginWithEmail() {
        val sEmail = itEmailLogin.text.toString().trim()
        val sPassword = itPasswordLogin.text.toString().trim()

        if (confirmation(sEmail, sPassword)) return

        changedUiLogin(true)

        mAuth.signInWithEmailAndPassword(sEmail, sPassword).addOnCompleteListener{ task ->
            if (task.isSuccessful) {
                gotoMain()
                progressBarLogin.visibility = View.INVISIBLE
            } else {
                changedUiLogin(false)
                // Error return to user
                Uteis.snack(itView, "N??o foi poss??vel realizar o login"+ (task.exception?.message) +"")
            }
        }
    }

    private fun changedUiLogin(el: Boolean) {
        blocksFields(el)
        progressBarLogin.visibility = if(el) View.VISIBLE else View.INVISIBLE
        bLogin.hint = if(el) "Entrando..." else "Entrar"
    }

    private fun blocksFields(b: Boolean) {
        bLogin.isEnabled = !b
        itPasswordLogin.isEnabled = !b
        itEmailLogin.isEnabled = !b
    }

    private fun confirmation(mEmail: String, mPassword: String): Boolean {
        if (mEmail.isEmpty()) {
            itEmailLogin.error = "Email is Required."
            return true
        }
        if (mPassword.isEmpty()) {
            itPasswordLogin.error = "Password is Required."
            return true
        }
        if (mPassword.length < 6) {
            itPasswordLogin.error = "Password Must be >= 6 Characters"
            return true
        }
        return false
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser: FirebaseUser? = mAuth.currentUser
        updateUI(currentUser)
    }

    private fun gotoMain() {
        val intent = Intent(applicationContext, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun gotoProfileSignIn(user: FirebaseUser?) {
        val nome = "${user?.displayName}"
        val nick = ""
        val email = "${user?.email}"
        val url = "${user?.photoUrl}"
        val bio = ""

        val data = hashMapOf(
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
            .addOnSuccessListener { success ->
                if(success.documents.isEmpty()){
                    FirebaseFirestore
                        .getInstance()
                        .collection("users")
                        .document(user?.uid.toString())
                        .set(data)
                        .addOnSuccessListener {
                            gotoMain()
                        }.addOnFailureListener { error ->
                            Uteis.snack(itView, "Error ! " + Objects.requireNonNull(error.message)+"")
                        }
                } else {
                    if(success.documents.first().id == user?.uid){
                        gotoMain()
                    } else {
                        FirebaseFirestore
                            .getInstance()
                            .collection("users")
                            .document(user?.uid.toString())
                            .set(data)
                            .addOnSuccessListener {
                                FirebaseFirestore
                                    .getInstance()
                                    .collection("users")
                                    .document(success.documents[0].id)
                                    .delete()

                                gotoMain()
                            }.addOnFailureListener { error ->
                                Uteis.snack(itView, "Error ! " + Objects.requireNonNull(error.message)+"")
                            }
                    }

                }
                changedUiLogin(false)
            }
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        if(currentUser != null){
            Log.i("LOGIN", currentUser.email.toString())
            gotoMain()
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
                gotoProfileSignIn(user)

            } else {
                Uteis.snack(itView, "Algo deu errado no login"+ (task.exception?.message) +"")
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


