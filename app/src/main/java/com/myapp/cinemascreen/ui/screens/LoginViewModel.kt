package com.myapp.cinemascreen.ui.screens

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.firestore.firestore
import com.myapp.cinemascreen.data.CinemaScreenRepository
import com.myapp.cinemascreen.ui.UserPreferences
import com.myapp.cinemascreen.ui.screens.data.UserInfo
import com.myapp.cinemascreen.ui.states.LoginEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userPreferences: UserPreferences,
    private val repository: CinemaScreenRepository
) : ViewModel(){
    /*
    * State using LoginEvent
    * Assign with object creation: _loginState.value = LoginEvent(isLoading = value, errorMessage = value)
    * */

    private val auth = FirebaseAuth.getInstance()

    private var _loginState = MutableStateFlow<LoginEvent>(LoginEvent())
    val loginState get() : StateFlow<LoginEvent> = _loginState

    private var _isLogin = MutableStateFlow<Boolean>(false)
    val isLogin get() : StateFlow<Boolean> = _isLogin
    private var _emailLogin = MutableStateFlow<String>("")
    private var _usernameLogin = MutableStateFlow<String>("")

    init {
        Log.d("check loginviewmodel","initialized")
        viewModelScope.launch {
            checkLogin()
        }
    }

    fun isLoginValue(): Boolean{
        return isLogin.value
    }

    fun login(email: String, password: String){
        _loginState.value = LoginEvent(isLoading = true)

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if(task.isSuccessful){
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("LoginViewModel", "signInWithEmail:success")
                    val user = auth.currentUser
                    user?.let {
                        saveToPreferences(it.email.toString(),getUserInfo(it.uid).username)
                    }
                }else{
                    val errorMessage = when (val exception = task.exception) {
                        is FirebaseAuthInvalidUserException -> "Email not registered"
                        is FirebaseAuthInvalidCredentialsException -> "Incorrect password"
                        is FirebaseNetworkException -> "No internet connection"
                        else -> "Login failed: ${exception?.localizedMessage}"
                    }
                    Log.w("LoginViewModel", "signInWithEmail:failure", task.exception)
                    _loginState.value = LoginEvent(isLoading = false, errorMessage = errorMessage)
                }
            }
    }

    fun getUserInfo(uid: String) : UserInfo{
        val db = Firebase.firestore
        val userInfo = UserInfo()

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
                }else{
                    Log.d("LoginViewModel", "getUserInfo:error NOT FOUND")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("LoginViewModel", "getUserInfo:error ${exception.printStackTrace()}")
            }

        return userInfo
    }

    private fun saveToPreferences(email: String, username: String){
        viewModelScope.launch {
            userPreferences.saveUserData(email = email, username = username, isLogin = true)
            // Run checkLogin and wait for it to finish
//            val checkLoginJob = async {
                checkLogin()
//            }
//            checkLoginJob.await()
            _loginState.value = LoginEvent(isLoading = false, errorMessage = null)
        }
    }

    suspend fun checkLogin(){
        Log.d("loginviewmodel","checkLogin() method")

//        viewModelScope.launch {
            val isLoginNow = userPreferences.isLogin.first()
            val emailLogin = userPreferences.emailLogin.first()
            val usernameLogin = userPreferences.usernameLogin.first()
            _isLogin.value = isLoginNow
            _emailLogin.value = emailLogin
            _usernameLogin.value = usernameLogin
            Log.d("check checkLogin: ","isLogin value ${_isLogin.value}")
//        }
    }

    fun setErrorMessage(msg : String?){
        _loginState.value = LoginEvent(isLoading = false, errorMessage = msg)
    }

    fun forgotPassword(){

    }

}
