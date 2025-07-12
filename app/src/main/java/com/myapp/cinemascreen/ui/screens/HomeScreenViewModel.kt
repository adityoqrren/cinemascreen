package com.myapp.cinemascreen.ui.screens

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myapp.cinemascreen.Resource
import com.myapp.cinemascreen.data.CinemaScreenRepository
import com.myapp.cinemascreen.data.models.MovieListItem
import com.myapp.cinemascreen.data.models.MovieTVListItem
import com.myapp.cinemascreen.ui.states.UIstate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val cinemaScreenRepository: CinemaScreenRepository
) : ViewModel() {
//    private val _trendingNow = MutableStateFlow<List<MovieListItem>>(emptyList())
//    val trendingNow : StateFlow<List<MovieListItem>> get() = _trendingNow

    private val _trendingNow = MutableStateFlow<UIstate<List<MovieListItem>>>(UIstate.Loading)
    val trendingNow: StateFlow<UIstate<List<MovieListItem>>> get() = _trendingNow

    private val _nowInCinemas = MutableStateFlow<UIstate<List<MovieTVListItem>>>(UIstate.Loading)
    val nowInCinemas: StateFlow<UIstate<List<MovieTVListItem>>> get() = _nowInCinemas

    private val _airingNow = MutableStateFlow<UIstate<List<MovieTVListItem>>>(UIstate.Loading)
    val airingNow: StateFlow<UIstate<List<MovieTVListItem>>> get() = _airingNow

    private val _popularMovieTV = MutableStateFlow<UIstate<List<MovieTVListItem>>>(UIstate.Loading)
    val popularMovieTV: StateFlow<UIstate<List<MovieTVListItem>>> get() = _popularMovieTV

    private var _popularStreaming: UIstate<List<MovieTVListItem>> = UIstate.Loading
    private var _popularRent: UIstate<List<MovieTVListItem>> = UIstate.Loading
    private var _popularTV: UIstate<List<MovieTVListItem>> = UIstate.Loading
    private var _popularOnCinema: UIstate<List<MovieTVListItem>> = UIstate.Loading

//    private val _showToast = MutableStateFlow<Boolean>(false)
//    val showToast : StateFlow<Boolean> get() = _showToast

    private var _selectedPopularCategory = 1;

    private var _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> get() = _errorMessage

    private val _isRefresh = MutableStateFlow<Boolean>(false)
    val isRefresh: StateFlow<Boolean> get() = _isRefresh

    init {
        Log.d("check DI", "masuk HomeScreenViewModel")
        //_trendingNow.value = cinemaScreenRepository.getTrendingNow()
        getTrendingNow()
        getPopular(1)
        getPlayingNowMovies()
        getAiringNowTV()
    }

    private fun getTrendingNow() {
        Log.d("check refresh", "masuk getTrending")
        viewModelScope.launch {
            _trendingNow.value = UIstate.Loading
            val trendingMovies = cinemaScreenRepository.getTrendingNow()
            trendingMovies.collect { data ->
                when (data) {
                    is Resource.Success -> {
                        _trendingNow.value = UIstate.Success(data.data as List<MovieListItem>)
                        _isRefresh.value = false
                    }

                    is Resource.Error -> {
//                        withContext(Dispatchers.Main){
                        Log.d("check getTrendingNow() in vm", "error message: ${data.message}")
//                        }
                        _trendingNow.value = UIstate.Error(
                            message = data.message as String,
                            dataWhenError = data.data,
                            errorCode = 1
                        )
                        _errorMessage.value = data.message
                        _isRefresh.value = false
                        Log.d("check error message", "error getTrendingNow() message $_errorMessage")
                    }

                    is Resource.Loading -> {}
                }
            }
        }
    }

    private suspend fun getPopularStreaming() {
        Log.d("check getPopular", "masuk getPopularStreaming")
        //re-fetch from server
        cinemaScreenRepository.getPopularStreaming().collect { data ->
            when (data) {
                is Resource.Error -> {
                    _popularStreaming =
                        UIstate.Error(message = data.message!!, dataWhenError = null)
                }

                is Resource.Loading -> {
                    _popularStreaming = UIstate.Loading
                }

                is Resource.Success -> {
                    _popularStreaming = UIstate.Success(data.data!!)
                }
            }
        }
    }

    private suspend fun getPopularRent() {
        //re-fetch from server
        cinemaScreenRepository.getPopularRent().collect { data ->
            when (data) {
                is Resource.Error -> {
                    _popularRent = UIstate.Error(message = data.message!!, dataWhenError = null)
                }

                is Resource.Loading -> {
                    _popularRent = UIstate.Loading
                }

                is Resource.Success -> {
                    _popularRent = UIstate.Success(data.data!!)
                }
            }
        }
    }

    private suspend fun getPopularTV() {
        //re-fetch from server
        cinemaScreenRepository.getPopularTVonTV().collect { data ->
            when (data) {
                is Resource.Error -> {
                    _popularTV = UIstate.Error(message = data.message!!, dataWhenError = null)
                }

                is Resource.Loading -> {
                    _popularTV = UIstate.Loading
                }

                is Resource.Success -> {
                    _popularTV = UIstate.Success(data.data!!)
                }
            }
        }
    }

    private suspend fun getPopularMovieOnCinema() {
        //re-fetch from server
        cinemaScreenRepository.getPopularMovieOnCinema().collect { data ->
            when (data) {
                is Resource.Error -> {
                    _popularOnCinema = UIstate.Error(message = data.message!!, dataWhenError = null)
                }

                is Resource.Loading -> {
                    _popularOnCinema = UIstate.Loading
                }

                is Resource.Success -> {
                    _popularOnCinema = UIstate.Success(data.data!!)
                }
            }
        }
    }

    fun getPopular(popularParams: Int) {
        _selectedPopularCategory = popularParams
        viewModelScope.launch {
            when (popularParams) {
                1 -> {
                    if (_popularStreaming !is UIstate.Success) {
                        getPopularStreaming()
                    }
                    Log.d("check get popular", "masuk getPopular part 2")
                    _popularMovieTV.value = _popularStreaming
                    Log.d("check get popular", "masuk getPopular part 3 ${_popularMovieTV.value}")
                }
                2 -> {
                    if (_popularTV !is UIstate.Success) {
                        Log.d("check getPopular", "masuk popular tv")
                        getPopularTV()
                    }
                    _popularMovieTV.value = _popularTV
                }
                3 -> {
                    if (_popularRent !is UIstate.Success) {
                        Log.d("check getPopular", "masuk popular rent")
                        getPopularRent()
                    }
                    _popularMovieTV.value = _popularRent
                }
                4 -> {
                    if (_popularOnCinema !is UIstate.Success) {
                        Log.d("check getPopular", "masuk popular on cinema")
                        getPopularMovieOnCinema()
                    }
                    _popularMovieTV.value = _popularOnCinema
                }
            }
        }
    }

    private fun getPlayingNowMovies() {
        Log.d("check refresh", "masuk getPlayingNowMovies")
        viewModelScope.launch {
            _nowInCinemas.value = UIstate.Loading
            val playingNowMovies = cinemaScreenRepository.getPlayingNowMovies()
            playingNowMovies.collect { data ->
                when (data) {
                    is Resource.Success -> {
                        _nowInCinemas.value = UIstate.Success(data.data as List<MovieTVListItem>)
                    }

                    is Resource.Error -> {
//                        withContext(Dispatchers.Main){
                        Log.d("check getPlayingNowMovies() in vm", "error message: ${data.message}")
//                        }
                        _nowInCinemas.value = UIstate.Error(
                            message = data.message as String,
                            dataWhenError = data.data,
                            errorCode = 1
                        )
                        _errorMessage.value = data.message
                        _isRefresh.value = false
                        Log.d("check error message", "error getPlayingNowMovies() message $_errorMessage")
                    }

                    is Resource.Loading -> {}
                }
            }
        }
    }

    private fun getAiringNowTV() {
        Log.d("check refresh", "masuk getAiringNowTV()")
        viewModelScope.launch {
            _airingNow.value = UIstate.Loading
            val playingNowMovies = cinemaScreenRepository.getAiringTodayTV()
            playingNowMovies.collect { data ->
                when (data) {
                    is Resource.Success -> {
                        _airingNow.value = UIstate.Success(data.data as List<MovieTVListItem>)
                    }

                    is Resource.Error -> {
//                        withContext(Dispatchers.Main){
                        Log.d("check getAiringNowTV() in vm", "error message: ${data.message}")
//                        }
                        _airingNow.value = UIstate.Error(
                            message = data.message as String,
                            dataWhenError = data.data,
                            errorCode = 1
                        )
                        _errorMessage.value = data.message
                        _isRefresh.value = false
                        Log.d("check error message", "error getAiringNowTV() message $_errorMessage")
                    }

                    is Resource.Loading -> {}
                }
            }
        }
    }

    fun triggerErrorMessage(message: String?) {
        _errorMessage.value = message
    }

    fun setRefresh(boolean: Boolean) {
        _isRefresh.value = boolean
        if (_isRefresh.value) {
            viewModelScope.launch {
                Log.d("check setRefresh: ", "before delay")
                delay(2500)
                Log.d("check setRefresh: ", "after delay")
                getTrendingNow()
                //refresh all popular data
                getPopular(_selectedPopularCategory)
                getPlayingNowMovies()
                getAiringNowTV()
            }
        }
    }
}