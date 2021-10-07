package com.example.moviequiz.Uteis

import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.util.rangeTo
import com.example.moviequiz.R
import com.example.moviequiz.models.Post
import com.google.android.material.snackbar.Snackbar
import java.lang.Exception
import java.nio.charset.Charset
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

        fun next(): String {
            return UUID.randomUUID().toString().replace("-", "").uppercase(Locale.getDefault());
        }

        fun generatedUid(): String {
            val u = UUID.randomUUID()
            return toIDString(u.mostSignificantBits) + toIDString(u.leastSignificantBits)
        }

        private fun toIDString(i: Long): String {
            var i = i
            val buf = CharArray(32)
            val z = 64 // 1 << 6;
            var cp = 32
            val b = (z - 1).toLong()
            do {
                buf[--cp] = DIGITS66[(i and b).toInt()]
                i = i ushr 6
            } while (i != 0L)
            return String(buf, cp, 32 - cp)
        }

        // array de 64+2 digitos
        private val DIGITS66 = charArrayOf(
            '0',
            '1',
            '2',
            '3',
            '4',
            '5',
            '6',
            '7',
            '8',
            '9',
            'a',
            'b',
            'c',
            'd',
            'e',
            'f',
            'g',
            'h',
            'i',
            'j',
            'k',
            'l',
            'm',
            'n',
            'o',
            'p',
            'q',
            'r',
            's',
            't',
            'u',
            'v',
            'w',
            'x',
            'y',
            'z',
            'A',
            'B',
            'C',
            'D',
            'E',
            'F',
            'G',
            'H',
            'I',
            'J',
            'K',
            'L',
            'M',
            'N',
            'O',
            'P',
            'Q',
            'R',
            'S',
            'T',
            'U',
            'V',
            'W',
            'X',
            'Y',
            'Z',
            '-',
            '.',
            '_',
            '~'
        )

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