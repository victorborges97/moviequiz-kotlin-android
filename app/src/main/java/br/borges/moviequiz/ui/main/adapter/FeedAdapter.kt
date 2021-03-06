package br.borges.moviequiz.ui.main.adapter

import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Space
import androidx.recyclerview.widget.RecyclerView
import br.borges.moviequiz.R
import br.borges.moviequiz.models.MovieChoice
import br.borges.moviequiz.models.Post
import br.borges.moviequiz.repository.FirebaseRepository
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.card_post.view.*
import kotlinx.android.synthetic.main.row_movie_card.view.*
import java.lang.Exception
import java.util.*

class FeedAdapter (
    private val listData: List<Post>,
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
                        .into(itemView.ivPhotoUser)
                } else {
                    Picasso.get().load(R.drawable.ic_user).fit()
                        .centerCrop()
                        .into(itemView.ivPhotoUser)
                }

                itemView.contentSurvey.removeAllViews()
                setMoviesData(this.movies, this)
            }
        }

        private fun setMoviesData(movies: List<MovieChoice>, post: Post) {
            var count = 0
            movies.map {
                val uid = FirebaseRepository().user?.uid
                val filme: MovieChoice = it
                val votoUser = post.wishes.filter { wishe ->
                    wishe.userId == uid
                }
                val totalVotos = post.wishes.size
                val isVoto = post.wishes.filter { wishe ->
                    wishe.idMovie == filme.idMovie
                }
                var quantVotos = 0
                if(isVoto.isNotEmpty()) {
                    quantVotos = isVoto.size
                }

                val votoFilmePorCem = (100 * quantVotos)

                val porcentagemVotos: Double = if(votoFilmePorCem <= 0 ) {
                    0.0
                } else {
                    (votoFilmePorCem / totalVotos).toDouble()
                }


                if(votoUser.isEmpty()){


                    val view = LayoutInflater.from(this@MainViewHolder.itemView.context)
                        .inflate(R.layout.row_movie_card, null, false)

                    view?.tvRowCountMovie?.text = getLetra(count)
                    // Verificando se ?? o usuario que postou
                    if(uid != post.userId){
                        view?.setOnClickListener {
                            post.votar(post, filme)
                        }
                    }

                    var quantVotos = 0
                    if(isVoto.isNotEmpty()) {
                        quantVotos = isVoto.size
                    }

                    view?.tvRowTitle?.text = it.title
                    view?.tvWishesotals?.text = "$quantVotos Votos -"
                    view?.tvPercentage?.text = "$porcentagemVotos%"

                    try {
                        Picasso.get().load(it.photo).fit()
                            .centerCrop()
                            .into(view?.ivPhotoMovie)
                    }
                    catch (e: Exception) {
                        Log.d("ERROR", "ERROR CARREGAR FOTO ${e.message}")
                    }

                    if(uid != post.userId) {
                        val attrs = intArrayOf(R.attr.selectableItemBackground)
                        val typedArray = view?.context?.obtainStyledAttributes(attrs)
                        val selectableItemBackground = typedArray?.getResourceId(0, 0)

                        view?.isClickable = true
                        view?.isFocusable = true
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            view?.foreground = selectableItemBackground?.let { it1 ->
                                view.context?.getDrawable(
                                    it1
                                )
                            }
                        }
                    }

                    this@MainViewHolder.itemView.contentSurvey.addView(view)
                    val space = Space(view?.context)
                    space.minimumHeight = 20
                    this@MainViewHolder.itemView.contentSurvey.addView(space)
                    count += 1

                }
                else {
                    if(votoUser.first().idMovie == filme.idMovie){
                        val view = LayoutInflater.from(this@MainViewHolder.itemView.context)
                            .inflate(R.layout.row_movie_card_isvoto, null, false)

                        // Verificando se ?? o usuario que postou
                        if(uid != post.userId){
                            view?.setOnClickListener {
                                post.votar(post, filme)
                            }
                        }

                        view?.tvRowTitle?.text = it.title
                        view?.tvWishesotals?.text = "$quantVotos Votos -"
                        view?.tvPercentage?.text = "$porcentagemVotos%"

                        try {
                            Picasso.get().load(it.photo).fit()
                                .centerCrop()
                                .into(view?.ivPhotoMovie)
                        }
                        catch (e: Exception) {
                            Log.d("ERROR", "ERROR CARREGAR FOTO ${e.message}")
                        }

                        if(uid != post.userId) {
                            val attrs = intArrayOf(R.attr.selectableItemBackground)
                            val typedArray = view?.context?.obtainStyledAttributes(attrs)
                            val selectableItemBackground = typedArray?.getResourceId(0, 0)

                            view?.isClickable = true
                            view?.isFocusable = true
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                view?.foreground = selectableItemBackground?.let { it1 ->
                                    view.context?.getDrawable(
                                        it1
                                    )
                                }
                            }
                        }

                        this@MainViewHolder.itemView.contentSurvey.addView(view)
                        val space = Space(view?.context)
                        space.minimumHeight = 20
                        this@MainViewHolder.itemView.contentSurvey.addView(space)
                        count += 1

                    }
                    else {
                        val view = LayoutInflater.from(this@MainViewHolder.itemView.context)
                            .inflate(R.layout.row_movie_card, null, false)
                        view?.tvRowCountMovie?.text = getLetra(count)

                        // Verificando se ?? o usuario que postou
                        if(uid != post.userId){
                            view?.setOnClickListener {
                                post.votar(post, filme)
                            }
                        }

                        view?.tvRowTitle?.text = it.title
                        view?.tvWishesotals?.text = "$quantVotos Votos -"
                        view?.tvPercentage?.text = "$porcentagemVotos%"

                        try {
                            Picasso.get().load(it.photo).fit()
                                .centerCrop()
                                .into(view?.ivPhotoMovie);
                        }
                        catch (e: Exception) {
                            Log.d("ERROR", "ERROR CARREGAR FOTO ${e.message}")
                        }

                        if(uid != post.userId) {
                            val attrs = intArrayOf(R.attr.selectableItemBackground)
                            val typedArray = view?.context?.obtainStyledAttributes(attrs)
                            val selectableItemBackground = typedArray?.getResourceId(0, 0)

                            view?.isClickable = true
                            view?.isFocusable = true
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                view?.foreground = selectableItemBackground?.let { it1 ->
                                    view?.context?.getDrawable(
                                        it1
                                    )
                                }
                            }
                        }

                        this@MainViewHolder.itemView.contentSurvey.addView(view)
                        val space = Space(view?.context)
                        space.minimumHeight = 20
                        this@MainViewHolder.itemView.contentSurvey.addView(space)
                        count += 1
                    }
                }
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
}