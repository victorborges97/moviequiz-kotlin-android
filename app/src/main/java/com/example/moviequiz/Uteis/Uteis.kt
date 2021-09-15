package com.example.moviequiz.Uteis

import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.moviequiz.R
import com.example.moviequiz.models.Post
import com.google.android.material.snackbar.Snackbar
import java.text.NumberFormat
import java.util.*

class Uteis {
    companion object {
        fun configAppBar(context: AppCompatActivity, name: String? = null) {
            context.supportActionBar?.setBackgroundDrawable(ColorDrawable(context.resources.getColor(R.color.appPrimary)))

            if(name != null) {
                context.supportActionBar?.title = name
            }
        }

        fun generatedUid(): String {
            val uuid = UUID.randomUUID()
            return uuid.toString();
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

        fun sendShared(c: Context, texto: String) {
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, texto)
                type = "text/plain"
            }

            val shareIntent = Intent.createChooser(sendIntent, null)
            c.startActivity(shareIntent)
        }

        fun templateShared(currentItem: Post, itens: String, total: Double): String {
            val meuLocal = Locale("pt", "BR")
            val z: NumberFormat = NumberFormat.getCurrencyInstance(meuLocal)
            // Retirei o total -->  "\n\nTotal do carrinho: ${z.format(total)}"
            return "Post: ${currentItem.title}"
        }

        fun multiply(x: Double, y: Double) = x * y

        fun findIndex(arr: MutableList<Post>?, t: String): Int {
            if (arr == null) {
                return -1
            }
            var idx = -1
            for (i in arr.indices) {
                if (arr[i].idPost == t) {
                    idx = i
                }
            }
            return idx
        }
    }
}