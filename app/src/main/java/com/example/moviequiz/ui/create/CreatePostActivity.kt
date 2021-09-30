package com.example.moviequiz.ui.create

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moviequiz.R
import com.example.moviequiz.Uteis.Uteis
import com.example.moviequiz.models.MovieChoice
import com.example.moviequiz.models.MovieIMDB
import com.example.moviequiz.models.Post
import com.example.moviequiz.models.User
import com.example.moviequiz.repository.FirebaseRepository
import com.example.moviequiz.ui.search.SearchAdapter
import com.example.moviequiz.ui.search.SearchMovieActivity
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_create_post.*
import kotlinx.android.synthetic.main.activity_search_movie.*

class CreatePostActivity : AppCompatActivity() {
    private var listMoviesEscolhida: ArrayList<MovieIMDB> = arrayListOf()
    private lateinit var firestoreRepository: FirebaseRepository
    private lateinit var db: FirebaseFirestore
    private lateinit var user: User;
    private val TAG = "CREATEPOST"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)
        db = FirebaseFirestore.getInstance()
        firestoreRepository = FirebaseRepository()
        getUser()

        rvCreatePost.layoutManager = LinearLayoutManager(this)
        rvCreatePost.setHasFixedSize(true)

        val launchActivitySearch = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if(result.resultCode == Activity.RESULT_OK){
                val data: Intent? = result.data

                val moviesSearch = data?.getParcelableArrayListExtra<MovieIMDB>("filmes")

                if (moviesSearch != null && moviesSearch.size >= 1) {
                    listMoviesEscolhida.add(moviesSearch.first())
                    setAdapterRv()
                    Log.d("RESULT", "TAMANHO: ${moviesSearch.size} - ${moviesSearch[0]?.title}")
                } else {
                    Log.d("RESULT ERROR", "TAMANHO: ${moviesSearch?.size}")
                }

            }
        }

        fabAddMovie.setOnClickListener {
            val intent = Intent(this, SearchMovieActivity::class.java)
            launchActivitySearch.launch( intent )
        }

    }

    private fun addPost() {
        // TODO: IMPLEMENTAR ADD DE POST!
        var listMovies: List<MovieChoice> = listOf<MovieChoice>()

        val filme = MovieChoice(Uteis.generatedUid(), "American Pie 3", "https://i.pinimg.com/originals/d4/cc/2c/d4cc2c193ad2ee0b6b9b37e47fb84e7d.jpg")
        val filme2 = MovieChoice(Uteis.generatedUid(), "American Pie 2", "https://i.pinimg.com/originals/d4/cc/2c/d4cc2c193ad2ee0b6b9b37e47fb84e7d.jpg")
        val filme3 = MovieChoice(Uteis.generatedUid(), "American Pie 1", "https://i.pinimg.com/originals/d4/cc/2c/d4cc2c193ad2ee0b6b9b37e47fb84e7d.jpg")

        //Mandando a Lista Mutavel para o Adapter
        val post1 = Post(
            "",
            "Testando Para mostrar",
            user.name,
            user.url,
            user.idUser,
            listOf(filme, filme2, filme3),
            listOf(),
            Timestamp.now(),
            listOf(),
        )
//        firestoreRepository.addPost(post1)
    }

    private fun setAdapterRv() {
        rvCreatePost.adapter = CreateAdapter(listMoviesEscolhida) { itemDto: MovieIMDB, position: Int ->
            listMoviesEscolhida.remove(itemDto)
            rvCreatePost.adapter?.apply {
                notifyDataSetChanged()
            }
            Log.i("CALLBACK CREATE", "Removido item  ${itemDto.toString()} at position $position")
        }
        rvCreatePost.adapter?.apply {
            notifyDataSetChanged()
        }
    }

    fun getUser() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if(currentUser != null) {
            try {
                db.collection("users")
                    .document(currentUser.uid)
                    .get()
                    .addOnSuccessListener {
                        val userObject = it.toObject(User::class.java)
                        userObject?.idUser = it.id
                        Log.d("PERFIL", userObject.toString());
                        if (userObject != null) {
                            user = userObject
                            visibleInfor(user)
                            Log.d("PERFIL", user.name);
                        }
                    }
            } catch (e: Exception) {
                Log.e("ERROR PERFIL", "${e.message}");
            }
        }
    }

    private fun visibleInfor(user: User) {
        if(user.url != ""){
            Picasso.get().load(user.url).fit()
                .centerCrop()
                .placeholder(R.drawable.ic_user)
                .into(ivPhotoUser);
        } else {
            Picasso.get().load(R.drawable.ic_user).fit()
                .centerCrop()
                .into(ivPhotoUser);
        }
        tvNameUser.text = user.name
    }
}