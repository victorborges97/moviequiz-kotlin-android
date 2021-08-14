package com.example.moviequiz

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatImageButton
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.snackbar.Snackbar

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val linearLayout: ConstraintLayout = findViewById(R.id.bg_linear)
        aplicarLinearGradiente(linearLayout)


        val ib_login: AppCompatImageButton = findViewById(R.id.ib_login)
        ib_login.setOnClickListener {
            loginForGoogle(it)
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

    private fun loginForGoogle(view: View) {
        alert(
            this,
            "Alert que muda background.",
            "Você deseja modificar o background para cor azul?",
            {
                snack(view,"Sim")
            },
            {
                snack(view,"Não")
            },
            )
    }

    private fun alert(
        context: Context,
        title: String,
        content: String,
        yes: (()->Unit)? = null,
        not: (()->Unit)? = null,
        cancel: (()->Unit)? = null,
    ) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
        builder.setMessage(content)
        if(yes !== null) {
            builder.setPositiveButton("Sim") { dialog, which ->
                yes.invoke()
            }
        }
        if(not !== null) {
            builder.setNegativeButton("Não") { dialog, which ->
                not.invoke()
            }
        }
        if(cancel !== null) {
            builder.setNeutralButton("Cancelar") { _, _ ->
                cancel.invoke()
            }
        }

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    fun snack(view: View, message: String, duration: Int = Snackbar.LENGTH_LONG) {
        Snackbar.make(view, message, duration).show()
    }
}


