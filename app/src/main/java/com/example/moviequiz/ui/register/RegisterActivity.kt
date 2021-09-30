package com.example.moviequiz.ui.register

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.moviequiz.R
import com.example.moviequiz.Uteis.Uteis
import com.example.moviequiz.ui.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_register.*
import java.util.*

class RegisterActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    private val PICK_PHOTO_CODE = 1234
    private var photoUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val linearLayout: ConstraintLayout = findViewById(R.id.bg_linear_register)
        toApplyLinearGradiente(linearLayout)

        mAuth = FirebaseAuth.getInstance()

        imageView.setOnClickListener {
            getImageGalery()
        }

        bRegister.setOnClickListener { viewBtnRegister ->
            startRegisterFirebaseEmailPassword(viewBtnRegister)
        }

    }

    private fun startRegisterFirebaseEmailPassword(viewBtnRegister: View) {
        val tNome = etNome.text.toString().trim()
        val tNick = etNick.text.toString().trim()
        val tEmail = etEmail.text.toString().trim()
        val tPassword = etSenha.text.toString().trim()
        val tCPassword = etConfirm.text.toString().trim()

        val data = hashMapOf(
            "name" to tNome,
            "nick" to tNick,
            "email" to tEmail,
            "bio" to "",
            "url" to "",
            "create_at" to FieldValue.serverTimestamp(),
        )

        if (confirmTextInput(tEmail, tPassword, tCPassword, tNome, tNick)) return

        progressBarRegister.visibility = View.VISIBLE

        mAuth.createUserWithEmailAndPassword(tEmail, tPassword).addOnCompleteListener { task ->
            if (task.isSuccessful) {

                saveImageUser(task.result.user?.uid.toString())

    //                    FirebaseFirestore
    //                        .getInstance()
    //                        .collection("users")
    //                        .document(task.result.user?.uid.toString())
    //                        .set(data)
    //                        .addOnSuccessListener {
    //
    //                            gotoLogin()
    //                        }.addOnFailureListener { errorSaveUser ->
    //                            progressBarRegister.visibility = View.INVISIBLE
    //                            Uteis.snack(
    //                                viewBtnRegister,
    //                                "Error ! " + Objects.requireNonNull(errorSaveUser.message) + ""
    //                            )
    //                        }

            } else {
                progressBarRegister.visibility = View.INVISIBLE
                Uteis.snack(
                    viewBtnRegister,
                    "Error ! " + Objects.requireNonNull(task.exception?.message) + ""
                )
            }
        }
    }

    private fun getImageGalery() {
        val imagePickerIntent = Intent(Intent.ACTION_GET_CONTENT)
        imagePickerIntent.type = "image/*"
        if(imagePickerIntent.resolveActivity(packageManager) != null){
            startActivityForResult(imagePickerIntent, PICK_PHOTO_CODE)
        }
    }

    private fun saveImageUser(id: String) {
        val tNome = etNome.text.toString().trim()
        val tNick = etNick.text.toString().trim()
        val tEmail = etEmail.text.toString().trim()
        val photoUploadUri = photoUri as Uri
        val ref = FirebaseStorage.getInstance().reference.child("images/profile/$id.png")


        if(photoUri != null) {
            val uploadTask = ref.putFile(photoUploadUri)

            uploadTask
                .continueWithTask { photoTask ->
                    if (!photoTask.isSuccessful)
                    {
                        photoTask.exception?.let {
                            Log.i("ERROR UPLOAD PHOTO", "Error upload ${it.message.toString()}")
                            Toast.makeText(this, "Failed to save image", Toast.LENGTH_SHORT).show()
                        }
                    }

                    Log.i("SUCCESS UPLOAD PHOTO", "uploaded bytes ${photoTask.result?.bytesTransferred}")
                    ref.downloadUrl
                }
                .addOnCompleteListener { downloadUrlTask ->

                    if(downloadUrlTask.isSuccessful)
                    {
                        val data = hashMapOf(
                            "name" to tNome,
                            "nick" to tNick,
                            "email" to tEmail,
                            "bio" to "",
                            "url" to downloadUrlTask.result.toString(),
                            "create_at" to FieldValue.serverTimestamp(),
                        )

                        FirebaseFirestore
                            .getInstance()
                            .collection("users")
                            .document("$id")
                            .set(data)
                            .addOnSuccessListener {
                                etNome.text?.clear()
                                etNick.text?.clear()
                                etEmail.text?.clear()
                                etSenha.text?.clear()
                                etConfirm.text?.clear()
                                imageView.setImageResource(0)
                                gotoLogin()
                            }
                            .addOnFailureListener { errorPhoto ->
                                Log.e("REGISTER", "Expeption during Firebase operations ${errorPhoto.message.toString()}")
                                Toast.makeText(this, "Failed to save post", Toast.LENGTH_SHORT).show()
                            }
                    }
                    else
                    {
                        Toast.makeText(this, "Error upload ${downloadUrlTask.exception?.message.toString()}", Toast.LENGTH_SHORT).show()
                    }

                }
        } else {
            val data = hashMapOf(
                "name" to tNome,
                "nick" to tNick,
                "email" to tEmail,
                "bio" to "",
                "url" to "",
                "create_at" to FieldValue.serverTimestamp(),
            )

            FirebaseFirestore
                .getInstance()
                .collection("users")
                .document("$id")
                .set(data)
                .addOnSuccessListener {
                    etNome.text?.clear()
                    etNick.text?.clear()
                    etEmail.text?.clear()
                    etSenha.text?.clear()
                    etConfirm.text?.clear()

                    gotoLogin()
                }
                .addOnFailureListener { errorSaveUser ->
                    progressBarRegister.visibility = View.INVISIBLE
                    Uteis.snack(
                        bRegister,
                        "Error ! " + Objects.requireNonNull(errorSaveUser.message) + ""
                    )
                }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == PICK_PHOTO_CODE){
            photoUri = data?.data
            Log.i("GETIMAGE", "photoUri $photoUri")
            imageView.setImageURI(photoUri)
//            Picasso.get().load(photoUri).centerCrop().into(imageView)
        } else {
            Toast.makeText(this, "Image picker action canceled", Toast.LENGTH_SHORT).show()
        }
    }

    private fun gotoLogin() {
        val intent = Intent(applicationContext, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun confirmTextInput(
        tEmail: String,
        tPassword: String,
        tCPassword: String,
        tNome: String,
        tNick: String
    ): Boolean {
        if (tNome.isEmpty()) {
            etEmail.error = "Nome is Required."
            return true
        }
        if (tNick.isEmpty()) {
            etEmail.error = "Nickname is Required."
            return true
        }
        if (tEmail.isEmpty()) {
            etEmail.error = "Email is Required."
            return true
        }
        if (tPassword.isEmpty()) {
            etSenha.error = "Password is Required."
            return true
        }

        if (tPassword.length > 6) {
            etSenha.error = "Password Must be >= 6 Characters"
            return true
        }
        if (tPassword != tCPassword) {
            etSenha.error = "Passwords are not the same"
            etConfirm.error = "Passwords are not the same"
            return true
        }
        return false
    }

    private fun toApplyLinearGradiente(linearLayout: ConstraintLayout) {
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