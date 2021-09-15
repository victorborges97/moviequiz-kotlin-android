package com.example.moviequiz.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Space
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.moviequiz.R
import com.example.moviequiz.models.MovieChoice
import com.example.moviequiz.models.Post
import com.google.firebase.Timestamp
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.card_post.view.*
import kotlinx.android.synthetic.main.row_movie_card.view.*
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
                itemView.tvTitleMovie.text = this.title
                itemView.tvNameUser.text = this.userName

                if(this.userPhoto != ""){
                    Picasso.get().load(this.userPhoto).fit()
                        .centerCrop()
                        .into(itemView.ivPhotoUser);
                } else {
                    Picasso.get().load(R.drawable.ic_user).fit()
                        .centerCrop()
                        .into(itemView.ivPhotoUser);
                }

                setMoviesData(this.movies)
            }
        }

        private fun Post.setMoviesData(movies: List<MovieChoice>) {
            var count = 0;
            movies.map {
                // Parte dos filmes
                val view = LayoutInflater.from(this@MainViewHolder.itemView.context)
                    .inflate(R.layout.row_movie_card, null, false);

                val tvRowTitle = view.tvRowTitle
                val tvWishesotals = view.tvWishesotals
                val tvPercentage = view.tvPercentage
                val ivPhotoMovie = view.ivPhotoMovie

                view.tvRowCountMovie.text = getLetra(count)
                tvRowTitle.text = it.title
                tvWishesotals.text = "0 Votos -"
                tvPercentage.text = "0%"

                Picasso.get().load(it.photo).fit()
                    .centerCrop()
                    .into(ivPhotoMovie);

                this@MainViewHolder.itemView.contentSurvey.addView(view)
                val space = Space(view.context)
                space.minimumHeight = 20
                this@MainViewHolder.itemView.contentSurvey.addView(space)
                count += 1
            }
        }

    }

    private fun getLetra(count: Int): CharSequence? {
        if(count == 0) {
            return "A"
        } else if(count == 1){
            return "B"
        }else if(count == 2){
            return "C"
        }else if(count == 3){
            return "D"
        }else {
            return "E"
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