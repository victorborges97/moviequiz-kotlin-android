package br.borges.moviequiz.ui.search

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.borges.moviequiz.R
import br.borges.moviequiz.models.MovieIMDB
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.card_movie_search.view.*
import kotlinx.android.synthetic.main.card_post.view.*
import kotlinx.android.synthetic.main.row_movie_card.view.*
import java.util.*

class SearchAdapter(
    private val listData: MutableList<MovieIMDB>,
    private val clickListener: (MovieIMDB, Int) -> Unit,
) : RecyclerView.Adapter<SearchAdapter.MainViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.card_movie_search,
            parent, false
        )

        return MainViewHolder(itemView)
    }
    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val currentItem = listData[position]

        holder.bind(currentItem, position)
        holder.itemView.setOnClickListener {
            clickListener(currentItem, position)
        }
    }
    override fun getItemCount(): Int {
        return listData.size
    }

    inner class MainViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(currentItem: MovieIMDB, position: Int) {
            with(currentItem){
                if(this.title.isEmpty() || this.image?.url.toString().isEmpty()){
                    return@with
                }
                this.image?.url.let {
                    if(it != null) {
                        try {
                            Picasso.get().load(it).fit()
                                .centerCrop()
                                .into(itemView.ivPhotoMovieSearch);
                        } catch (e: Exception){
                            Log.e("ERROR PICASSO", "${e.message}")
                        }
                    }
                }
                itemView.tvTitleSearch.text = this.title
            }
        }

    }

    private fun goToItem(currentItem: MovieIMDB, context: Context) {
//        val intent = Intent(context, AddActivity::class.java)
//        intent.putExtra("idItem", currentItem.idList)
//        context.startActivity(intent)
    }

}