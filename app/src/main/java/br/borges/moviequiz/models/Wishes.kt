package br.borges.moviequiz.models

data class Wishes (
    var idMovie: String = "",
    var userId: String = "",
) {
    override fun toString(): String {
        return "Wishes(idMovie='$idMovie', userId='$userId')"
    }

    fun toMap(): Map<String, Any?> {
        return mapOf(
            "idMovie" to this.idMovie,
            "userId" to this.userId,
        )
    }
}