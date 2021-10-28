package com.example.moviequiz.services

import com.example.moviequiz.R
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class NetworkService {

        companion object {

            /** Retorna uma Instância do Client Retrofit para Requisições
             * @param path Caminho Principal da API
             */
            fun getRetrofit(path: String): Retrofit {
                return Retrofit.Builder()
                    .baseUrl(path.toString())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }
        }
}