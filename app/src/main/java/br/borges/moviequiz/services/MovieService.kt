package br.borges.moviequiz.services

import br.borges.moviequiz.models.ResponseMovieIMDB
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.Query

interface MovieService {
    @GET("/title/find?")
    fun list(@HeaderMap headers: Map<String, String>, @Query("q") q: String): Call<ResponseMovieIMDB>
}