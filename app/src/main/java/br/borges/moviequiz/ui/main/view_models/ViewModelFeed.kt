package br.borges.moviequiz.ui.main.view_models

import androidx.lifecycle.*
import br.borges.moviequiz.models.Post
import br.borges.moviequiz.ui.main.service.FirebaseFeedService
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ViewModelFeed : ViewModel() {

    private val _posts = MutableLiveData<List<Post>>()
    val posts: LiveData<List<Post>> = _posts

    private val _postsUser = MutableLiveData<List<Post>>()
    val postsUser: LiveData<List<Post>> = _postsUser

    init {
        viewModelScope.launch {
            val idUser = FirebaseAuth.getInstance().currentUser?.uid
            idUser.let { id ->
                FirebaseFeedService.getPosts().collect {
                    _posts.value = it?.filter { post ->
                        post.userId != id
                    }

                    _postsUser.value = it?.filter { post ->
                        post.userId == id
                    }
                }
            }
        }
    }

    //Rest of your viewmodel
}