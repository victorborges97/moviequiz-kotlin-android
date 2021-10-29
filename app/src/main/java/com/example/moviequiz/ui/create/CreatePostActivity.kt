package com.example.moviequiz.ui.create

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moviequiz.R
import com.example.moviequiz.Uteis.Uteis
import com.example.moviequiz.models.MovieChoice
import com.example.moviequiz.models.MovieIMDB
import com.example.moviequiz.models.Post
import com.example.moviequiz.models.User
import com.example.moviequiz.repository.FirebaseRepository
import com.example.moviequiz.ui.search.SearchMovieActivity
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_create_post.*

class CreatePostActivity : AppCompatActivity() {
    private var listMoviesEscolhida: ArrayList<MovieIMDB> = arrayListOf()
    private lateinit var firestoreRepository: FirebaseRepository
    private lateinit var db: FirebaseFirestore
    private lateinit var user: User;
    private val TAG = "CREATEPOST"

    val launchActivitySearch = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if(result.resultCode == Activity.RESULT_OK){
            val data: Intent? = result.data

            val moviesSearch = data?.getParcelableArrayListExtra<MovieIMDB>("filmes")

            if (moviesSearch != null && moviesSearch.size >= 1) {
                listMoviesEscolhida.add(moviesSearch.first())
                setAdapterRv()
                Log.d("RESULT", "TAMANHO: ${moviesSearch.size} - ${moviesSearch[0]?.title}")
                if(listMoviesEscolhida.size == 3) {
                    val new: Drawable? = getDrawable(R.drawable.ic_baseline_save_24)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        fabAddMovie.foreground = new
                    }
                }
            } else {
                Log.d("RESULT ERROR", "TAMANHO: ${moviesSearch?.size}")
            }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        initialComponents()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return true
    }

    private fun initialComponents() {
        supportActionBar?.title = "Criar enquete"
        db = FirebaseFirestore.getInstance()
        firestoreRepository = FirebaseRepository()
        getUser()

        fabAddMovie.setOnClickListener {
            if(listMoviesEscolhida.size < 3) {
                addMovie();
            } else {
                savePost()
            }
        }

        rvCreatePost.layoutManager = LinearLayoutManager(this)
        rvCreatePost.setHasFixedSize(true)
    }

    private fun addMovie() {
        val intent = Intent(this, SearchMovieActivity::class.java)
        launchActivitySearch.launch( intent )
    }

    private fun savePost() {
        addPost()
        finish()
    }

    private fun addPost() {
        val titulo = tvTitleMovie.text.toString().trim()

        if(listMoviesEscolhida.size >= 3){

            val filme = listMoviesEscolhida[0].image?.url?.let {
                MovieChoice(
                    Uteis.generatedUid(),
                    listMoviesEscolhida[0].title,
                    it,
                )
            }
            val filme2 = listMoviesEscolhida[1].image?.url?.let {
                MovieChoice(
                    Uteis.generatedUid(),
                    listMoviesEscolhida[1].title,
                    it,
                )
            }
            val filme3 = listMoviesEscolhida[2].image?.url?.let {
                MovieChoice(
                    Uteis.generatedUid(),
                    listMoviesEscolhida[2].title,
                    it,
                )
            }

            //Mandando a Lista Mutavel para o Adapter
            val post1 = Post(
                "",
                titulo,
                user.name,
                user.url,
                user.idUser,
                listOf(filme, filme2, filme3) as List<MovieChoice>,
                listOf(),
                Timestamp.now(),
            )

            firestoreRepository.addPost(post1)

        }
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

    private fun getUser() {
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