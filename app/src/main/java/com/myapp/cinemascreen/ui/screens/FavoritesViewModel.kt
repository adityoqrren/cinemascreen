package com.myapp.cinemascreen.ui.screens

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.firestore
import com.myapp.cinemascreen.utils.MediaType
import com.myapp.cinemascreen.data.CinemaScreenRepository
import com.myapp.cinemascreen.data.models.MovieTVFavorite
import com.myapp.cinemascreen.ui.states.UIstate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor() : ViewModel() {

    private lateinit var listenerRegistration: ListenerRegistration

    private val db = Firebase.firestore
    private val auth = Firebase.auth

    private val _idSelected = MutableStateFlow<Int>(1)
    val idSelected: StateFlow<Int> get() = _idSelected

    private val _uiState = MutableStateFlow<UIstate<List<MovieTVFavorite>>>(UIstate.Loading)
    val uiState: StateFlow<UIstate<List<MovieTVFavorite>>> get() = _uiState

    private var listFavoriteAll : MutableStateFlow<List<MovieTVFavorite>> = MutableStateFlow(emptyList())
    private var listFavoriteMovies : MutableStateFlow<List<MovieTVFavorite>> = MutableStateFlow(
        emptyList()
    )
    private var listFavoriteTV : MutableStateFlow<List<MovieTVFavorite>> = MutableStateFlow(emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val favoritesMovieTV: StateFlow<List<MovieTVFavorite>> = idSelected.flatMapLatest { id ->
        when(id){
            1 -> listFavoriteAll
            2 -> listFavoriteMovies
            else -> listFavoriteTV
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = emptyList()
    )

    private var _totalMoviesTV : MutableStateFlow<Int> = MutableStateFlow(0)
    val totalMoviesTV get() = _totalMoviesTV

    init {
        Log.d("FavoritesViewModel","FavoritesViewModel created")
        getFavorites()
    }

    private fun getFavorites(){
        viewModelScope.launch {
            // Set loading state
            _uiState.value = UIstate.Loading
            
            auth.uid?.let { uid ->
                listenerRegistration = db.collection("users")
                    .document(uid)
                    .collection("favorites")
                    .addSnapshotListener { value, e ->
                        if (e != null) {
                            Log.w("getFavorites in FavoritesViewModel","Listen failed", e)
                            _uiState.value = UIstate.Error(
                                message = "Failed to load favorites: ${e.message}",
                                dataWhenError = emptyList(),
                                errorCode = null
                            )
                            return@addSnapshotListener
                        }
                        
                        val favorites = mutableListOf<MovieTVFavorite>()
                        for (doc in value!!) {
                            val id = doc.getLong("id")!!.toInt()
                            val title = doc.getString("title").toString()
                            val posterPath = doc.getString("poster_path").toString()
                            val mediaType = doc.getString("media_type").toString()

                            favorites.add(
                                MovieTVFavorite(id, title, posterPath, mediaType)
                            )
                        }
                        
                        listFavoriteAll.value = favorites
                        _totalMoviesTV.value = favorites.size
                        listFavoriteMovies.value = favorites.filter {
                            it.media_type == MediaType.Movie
                        }
                        listFavoriteTV.value = favorites.filter {
                            it.media_type == MediaType.TV
                        }
                        
                        // Set success state
                        _uiState.value = UIstate.Success(favorites)
                    }
            } ?: run {
                // User not authenticated
                _uiState.value = UIstate.Error(
                    message = "User not authenticated",
                    dataWhenError = emptyList(),
                    errorCode = 401
                )
            }
        }
    }

    fun detachListener(){
        if(::listenerRegistration.isInitialized){
            listenerRegistration.remove()
        }
    }

    fun setIdSelected(id: Int){
        _idSelected.value = id
    }

    fun retry() {
        getFavorites()
    }
}