package com.example.moviequiz.ui.search

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.widget.SearchView.*
import androidx.recyclerview.widget.GridLayoutManager
import com.example.moviequiz.R
import com.example.moviequiz.models.MovieIMDB
import com.example.moviequiz.models.ResponseMovieIMDB
import com.example.moviequiz.services.MovieService
import kotlinx.android.synthetic.main.activity_search_movie.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.concurrent.timerTask

class SearchMovieActivity : AppCompatActivity() {
    private var listMoviesSearch: MutableList<MovieIMDB> = mutableListOf()
    private var listMoviesEscolhida: ArrayList<MovieIMDB> = arrayListOf()
    private var textSearch: String = ""
    private var loading: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_movie)

        rvSearchMovie.apply {
            layoutManager = GridLayoutManager(applicationContext, 2)
            setHasFixedSize(true)
        }

        svMovie.setOnQueryTextListener(object : OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                Log.d("FILME", "$textSearch")
                getRetrofit(textSearch)
                return false
            }
            override fun onQueryTextChange(p0: String?): Boolean {
                if (p0 != null) {
                    textSearch = p0
                }
                return false
            }
        })
    }

    private fun setAdapterRv() {
        rvSearchMovie.adapter = SearchAdapter(listMoviesSearch) { itemDto: MovieIMDB, position: Int ->
            itemOnClickRv(itemDto, position)
        }
        rvSearchMovie.adapter?.apply {
            notifyDataSetChanged()
        }
    }

    private fun getRetrofit(filme: String) {
        listMoviesSearch.clear()
        val retrofit =  Retrofit.Builder()
            .baseUrl(getString(R.string.url_imdb_rapidapi))
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val map: HashMap<String, String> = hashMapOf(
            "x-rapidapi-host" to getString(R.string.rapidapi_host),
            "x-rapidapi-key" to getString(R.string.rapidapi_key)
        )
        val service = retrofit.create(MovieService::class.java)
        val call = service.list(map, filme)

        CoroutineScope(Dispatchers.IO).launch {

            call.enqueue(object : Callback<ResponseMovieIMDB> {
                override fun onResponse(call: Call<ResponseMovieIMDB>, response: Response<ResponseMovieIMDB>) {
                    if (response.code() == 200) {
                        Log.d("API", "OK")
                        response.body().let {
                            if (it != null) {
                                it.results.forEach{ filmeResponse ->
                                    if(filmeResponse.title.isNotEmpty() && filmeResponse.image?.url.toString().isNotEmpty()){
                                        listMoviesSearch.add(filmeResponse)
                                    }
                                }
                                Log.d("API", it.results.toString())
                                setAdapterRv()
                            }
                        }
                    }
                }
                override fun onFailure(call: Call<ResponseMovieIMDB>, t: Throwable) {
                    Log.d("API", "ERROR API: ${t.message}")
                }
            })

        }


    }

    private fun itemOnClickRv(itemDto: MovieIMDB, position: Int) {
        listMoviesEscolhida.add(itemDto)
        Log.i("CALLBACK", "Clicked on item  ${itemDto.title} at position $position")
        onBackPressed()
    }

    override fun onBackPressed() {
        setResult(
            Activity.RESULT_OK,
            Intent().putParcelableArrayListExtra("filmes", listMoviesEscolhida)
        )

        finish()
        super.onBackPressed()
    }
}