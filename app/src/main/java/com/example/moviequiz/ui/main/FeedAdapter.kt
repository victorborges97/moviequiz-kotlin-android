package com.example.moviequiz.ui.main

import android.content.Context
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Space
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.moviequiz.R
import com.example.moviequiz.models.MovieChoice
import com.example.moviequiz.models.Post
import com.example.moviequiz.repository.FirebaseRepository
import com.google.firebase.Timestamp
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.card_post.view.*
import kotlinx.android.synthetic.main.row_movie_card.view.*
import java.lang.Exception
import java.util.*

class FeedAdapter (
    private val listData: MutableList<Post>,
    val contextMain: Context,
) : RecyclerView.Adapter<FeedAdapter.MainViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.card_post,
            parent, false
        )

        return MainViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val currentItem = listData[position]

        holder.bind(currentItem, position)
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    inner class MainViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(currentItem: Post, position: Int) {
            with(currentItem){
                itemView.tvTitleMovie.text = "${this.title}"
                itemView.tvNameUser.text = "${this.userName}"

                if(this.userPhoto != ""){
                    Picasso.get().load("${this.userPhoto}").fit()
                        .centerCrop()
                        .into(itemView.ivPhotoUser);
                } else {
                    Picasso.get().load(R.drawable.ic_user).fit()
                        .centerCrop()
                        .into(itemView.ivPhotoUser);
                }

                itemView.contentSurvey.removeAllViews();
                setMoviesData(this.movies, this)
            }
        }

        private fun Post.setMoviesData(movies: List<MovieChoice>, post: Post) {
            var count = 0;
            movies.map {
                // Parte dos filmes
                val view = LayoutInflater.from(this@MainViewHolder.itemView.context)
                    .inflate(R.layout.row_movie_card, null, false);

                val uid = FirebaseRepository().user?.uid
                val filme: MovieChoice = it;

                if(uid != post.userId){
                    view.setOnClickListener {
                        post.votar(post, filme)
                    }
                }
                val isVoto = post.wishes.filter { wishe ->
                    wishe.idMovie == filme.idMovie
                }

                Log.i("TESTE", "${isVoto.toString()}")

                var quantVotos: Int = 0

                if(isVoto.isNotEmpty()) {
                    quantVotos = isVoto.size
                }

                view.tvRowCountMovie.text = "${getLetra(count)}"
                view.tvRowTitle.text = "${it.title}"
                view.tvWishesotals.text = "${quantVotos} Votos -"
                view.tvPercentage.text = "0%"

                try {
                    Picasso.get().load(it.photo).fit()
                        .centerCrop()
                        .into(view.ivPhotoMovie);
                } catch (e: Exception) {
                    Log.d("ERROR", "ERROR CARREGAR FOTO ${e.message}")
                }

                if(uid != post.userId) {
                    val attrs = intArrayOf(R.attr.selectableItemBackground)
                    val typedArray = view.context.obtainStyledAttributes(attrs)
                    val selectableItemBackground = typedArray.getResourceId(0, 0)

                    view.isClickable = true
                    view.isFocusable = true
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        view.foreground = view.context.getDrawable(selectableItemBackground)
                    }
                }

                this@MainViewHolder.itemView.contentSurvey.addView(view)
                val space = Space(view.context)
                space.minimumHeight = 20
                this@MainViewHolder.itemView.contentSurvey.addView(space)
                count += 1
            }
        }
    }

    private fun getLetra(count: Int): String {
        return when (count) {
            0 -> "A"
            1 -> "B"
            2 -> "C"
            3 -> "D"
            else -> {
                "E"
            }
        }
    }

    fun TextView.getShortDate(ts: Timestamp?) {
        if(ts != null) {
            val calendar = Calendar.getInstance(Locale.getDefault())
            calendar.timeInMillis = ts.toDate().time
            this.text = android.text.format.DateFormat.format("dd/MM/yyyy", calendar).toString()
        } else {
            this.text = ""
        }
    }

    private fun goToItem(currentItem: Post, context: Context) {
//        val intent = Intent(context, AddActivity::class.java)
//        intent.putExtra("idItem", currentItem.idList)
//        context.startActivity(intent)
    }

    private fun shared(currentItem: Post, c: Context) {
//        FirestoreRepository()
//            .getListShared(FirebaseAuth.getInstance().currentUser?.uid.toString(), currentItem.idList.toString())
//            .addOnSuccessListener { itemList ->
//                val newList = mutableListOf<DataItem>()
//                for (dc in itemList!!.documentChanges) {
//                    dc.document.toObject(DataItem::class.java).let { entity ->
//                        newList.add(entity)
//                    }
//                }
//
//                var total = 0.00
//                newList.map {
//                    if (it.comprado == true) {
//                        val qtf = it.qt?.toDouble()
//                        val valor = it.preco?.toDouble()
//
//                        if (qtf != null && valor != null) {
//                            total += Utils.multiply(qtf, valor)
//                        }
//                    }
//                }
//                Collections.sort(newList, Comparator<DataItem> { lhs, rhs ->
//                    lhs.comprado!!.compareTo(rhs.comprado!!);
//                })
//                var itens: String = ""
//                for(i in newList) {
//                    itens += Utils.templateItens(i)
//                }
//                val mensagem: String = Utils.templateShared(currentItem, itens, total)
//
//                Utils.sendShared(c, mensagem)
//
//            }
    }
}