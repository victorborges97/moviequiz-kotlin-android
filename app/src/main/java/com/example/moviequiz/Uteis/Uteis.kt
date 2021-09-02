package com.example.moviequiz.Uteis

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.moviequiz.R
import com.google.android.material.snackbar.Snackbar

object Uteis {
    fun configAppBar(context: AppCompatActivity, name: String? = null) {
        context.supportActionBar?.setBackgroundDrawable(ColorDrawable(context.resources.getColor(R.color.appPrimary)))

        if(name != null) {
            context.supportActionBar?.title = name
        }
    }

    fun alert(
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