package com.example.moviequiz.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.io.Serializable
import java.util.*

data class ResponseMovieIMDB (
    @SerializedName("results") var results: List<MovieIMDB> = listOf(),
)

@Parcelize
data class MovieIMDB (
    @SerializedName("id") var id: String = "",
    @SerializedName("title") var title: String = "",
    @SerializedName("image") var image: Image? = null,
) : Parcelable {
    inner class Image : Serializable {
        @SerializedName("height")
        var height: String? = null
        @SerializedName("id")
        var id: String? = null
        @SerializedName("url")
        var url: String? = null
        @SerializedName("width")
        var width: String? = null
    }

    override fun toString(): String {
        return "\nTitulo: $title" +
                "\nImage: ${image?.url}" +
                "\nID: $id"
    }
}
