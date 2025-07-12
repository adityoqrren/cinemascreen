package com.myapp.cinemascreen.ui.screens

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.myapp.cinemascreen.data.CinemaScreenRepository
import com.myapp.cinemascreen.ui.UserPreferences
import com.myapp.cinemascreen.ui.screens.data.UserInfo
import com.myapp.cinemascreen.ui.states.LoginEvent
import com.myapp.cinemascreen.ui.states.LogoutEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userPreferences: UserPreferences,
    private val repository: CinemaScreenRepository
) : ViewModel(){
    private val auth = FirebaseAuth.getInstance()

    private var _isLogin = MutableStateFlow<Boolean>(false)
    val isLogin get() : StateFlow<Boolean> = _isLogin

    private var _logoutState = MutableStateFlow<LogoutEvent>(LogoutEvent())
    val logoutState get() : StateFlow<LogoutEvent> = _logoutState

    private var _userData = MutableStateFlow<UserInfo>(UserInfo())
    val userData get() : StateFlow<UserInfo> = _userData

    init {
        val user = auth.currentUser
        user?.let {
            getUserInfo(it.uid)
        }
    }

    fun setErrorMessage(msg : String?){
        _logoutState.update {
            it.copy(isLoading = false, errorMessage = msg)
        }
    }

    fun logout(){
        viewModelScope.launch {
            _logoutState.value = LogoutEvent(isLoading = true)
            //logout in firebase auth
            auth.signOut()
            //check if there is logged in user or not
            _isLogin.value = auth.currentUser!=null
            //send event
            if(!_isLogin.value){
                //delete all movie tv data saved in local
                repository.deleteAllMovieTVSaved()
                //reset data in datastore preferences
                userPreferences.saveUserData(false,"","")
                _logoutState.update {
                    it.copy(isLoading = false, isLogoutSuccess = true)
                }
            }else{
                _logoutState.update {
                    it.copy(isLoading = false, isLogoutSuccess = false, errorMessage = "Logout Error")
                }
            }
        }
    }

    fun getUserInfo(uid: String){
        viewModelScope.launch {
            val db = Firebase.firestore
            val userInfo = UserInfo()

            _userData.update {
                it.copy(email = userPreferences.emailLogin.first())
            }
            db.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener {
                        document ->
                    if(document != null){
                        val data = document.data
                        userInfo.username = data?.get("username") as String
                        userInfo.fullname = data.get("fullname") as String
                        userInfo.country = data.get("country") as String
                        _userData.value = userInfo
                    }else{
                        Log.d("ProfileViewModel", "getUserInfo:error NOT FOUND")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("ProfileViewModel", "getUserInfo:error ${exception.printStackTrace()}")
                }
        }
    }
}