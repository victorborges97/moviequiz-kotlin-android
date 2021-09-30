package com.example.moviequiz.ui.create

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.moviequiz.R
import com.example.moviequiz.models.MovieIMDB
import com.google.firebase.Timestamp
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.card_movie_search.view.*
import kotlinx.android.synthetic.main.row_movie_card_create.view.*
import java.util.*

class CreateAdapter (
    private val listData: MutableList<MovieIMDB>,
    private val clickListener: (MovieIMDB, Int) -> Unit,
) : RecyclerView.Adapter<CreateAdapter.MainViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.row_movie_card_create,
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
                this.image?.url.let {
                    if(it != null){
                        try {
                            Picasso.get().load(it).fit()
                                .centerCrop()
                                .into(itemView.ivPhotoMovieCreate);
                        } catch (e: Exception){
                            Log.e("ERROR PICASSO", "${e.message}")
                        }
                    }
                }

                itemView.tvRowTitleCreate.text = this.title
                itemView.tvRowCountMovieCreate.text = getLetra(position)
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

    private fun goToItem(currentItem: MovieIMDB, context: Context) {
//        val intent = Intent(context, AddActivity::class.java)
//        intent.putExtra("idItem", currentItem.idList)
//        context.startActivity(intent)
    }

    private fun shared(currentItem: MovieIMDB, c: Context) {
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