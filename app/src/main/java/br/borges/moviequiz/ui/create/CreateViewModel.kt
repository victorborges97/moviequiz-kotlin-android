package br.borges.moviequiz.ui.create

import androidx.lifecycle.ViewModel

class CreateViewModel: ViewModel() {
    var counter = 0

    fun increment() {
        counter++
    }
}