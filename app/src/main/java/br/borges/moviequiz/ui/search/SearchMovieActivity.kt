package br.borges.moviequiz.ui.search

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.SearchView.*
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import br.borges.moviequiz.R
import br.borges.moviequiz.models.MovieIMDB
import br.borges.moviequiz.models.ResponseMovieIMDB
import br.borges.moviequiz.services.MovieService
import br.borges.moviequiz.services.NetworkService
import kotlinx.android.synthetic.main.activity_search_movie.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SearchMovieActivity : AppCompatActivity() {
    private var listMoviesSearch: MutableList<MovieIMDB> = mutableListOf()
    private var listMoviesEscolhida: ArrayList<MovieIMDB> = arrayListOf()
    private var textSearch: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_movie)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        initialComponents()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return true
    }

    private fun initialComponents() {
        supportActionBar?.title = "Pesquisar filmes"

        tvAvisos.text = "Faça sua pesquisa..."
        tvAvisos.visibility = View.VISIBLE

        rvSearchMovie.apply {
            layoutManager = GridLayoutManager(applicationContext, 2)
            setHasFixedSize(true)
        }

        svMovie.setOnQueryTextListener(object : OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
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

    private fun changedUiLogin(el: Boolean) {
        blocksFields(el)
        progressBarSearch.visibility = if(el) View.VISIBLE else View.INVISIBLE
         if(tvAvisos.isVisible) {
             tvAvisos.visibility = View.INVISIBLE
        }
    }

    private fun blocksFields(b: Boolean) {
        svMovie.isEnabled = !b
    }

    private fun getRetrofit(filme: String) {
        listMoviesSearch.clear()
        changedUiLogin(true)

        val retrofitClient = NetworkService.getRetrofit(getString(R.string.url_imdb_rapidapi))
        val map: HashMap<String, String> = hashMapOf(
            "x-rapidapi-host" to getString(R.string.rapidapi_host),
            "x-rapidapi-key" to getString(R.string.rapidapi_key)
        )
        val endpoint = retrofitClient.create(MovieService::class.java)
        val call = endpoint.list(map, filme)

        CoroutineScope(Dispatchers.IO).launch {

            call.enqueue(object : Callback<ResponseMovieIMDB> {
                override fun onResponse(call: Call<ResponseMovieIMDB>, response: Response<ResponseMovieIMDB>) {
                    if (response.code() == 200) {
                        response.body().let {
                            if (it != null) {
                                if(it.results.isEmpty()){
                                    //TODO: LIBERAR MENSAGEM QUE NÂO FOI ENCONTRADO O RESULTADO ESPERADO
                                    tvAvisos.text = "Nenhume resultado foi encontrado na pesquisa..."
                                    tvAvisos.visibility = View.VISIBLE
                                    changedUiLogin(false)
                                } else {
                                    it.results.forEach{ filmeResponse ->
                                        if(filmeResponse.title.isNotEmpty() && filmeResponse.image?.url.toString().isNotEmpty()){
                                            listMoviesSearch.add(filmeResponse)
                                        }
                                    }
                                    changedUiLogin(false)
                                    setAdapterRv()
                                }
                            }
                        }
                    }
                }
                override fun onFailure(call: Call<ResponseMovieIMDB>, t: Throwable) {
                    changedUiLogin(false)
                    Log.d("API", "ERROR API: ${t.message}")
                }
            })

        }


    }

    private fun itemOnClickRv(itemDto: MovieIMDB, position: Int) {
        listMoviesEscolhida.add(itemDto)
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