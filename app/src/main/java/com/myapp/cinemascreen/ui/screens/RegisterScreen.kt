package com.myapp.cinemascreen.ui.screens

import android.widget.Toast
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateIntOffsetAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.myapp.cinemascreen.ui.screens.components.FormPasswordTextField
import com.myapp.cinemascreen.ui.screens.components.FormTextField
import com.myapp.cinemascreen.ui.screens.components.LargeDropdownMenu
import com.myapp.cinemascreen.R
import com.myapp.cinemascreen.data.models.UserRegistration
import com.myapp.cinemascreen.fontFamily
import com.myapp.cinemascreen.ui.theme.YellowMain
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

@Composable
fun RegisterScreen(
    navigateToLogin: () -> Unit,
    viewModel: RegisterViewModel = hiltViewModel()
) {
    var isCardVisible by remember { mutableStateOf(false) }

    val registerState by viewModel.registerState.collectAsStateWithLifecycle()

    val context = LocalContext.current

    LaunchedEffect(key1 = registerState) {
        if(!registerState.isLoading && registerState.isRegisterSuccess){
            navigateToLogin()
        }else if(!registerState.isRegisterSuccess && registerState.errorMessage!=null){
            Toast.makeText(context,registerState.errorMessage,Toast.LENGTH_LONG).show()
            viewModel.setErrorMessage(null)
        }
    }

    LaunchedEffect(true) {
        delay(100)
        isCardVisible = true
    }

    Surface(
        Modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .imePadding(),
        color = YellowMain
    ) {

        val keyboardController = LocalSoftwareKeyboardController.current
        val focusManager = LocalFocusManager.current
        val interactionSource = remember { MutableInteractionSource() }

        var valueOfEmail by remember { mutableStateOf("") }
        var valueOfFullname by remember { mutableStateOf("") }
        var valueOfUsername by remember { mutableStateOf("") }
        var valueOfPassword by remember { mutableStateOf("") }
        var valueOfConfirmPassword by remember { mutableStateOf("") }
        var selectedCountryIndex by remember { mutableIntStateOf(-1) }
        val listCountries = listOf<String>("Indonesia", "Malaysia", "Vietnam", "Kamboja")

        val valueOfCountry by remember(selectedCountryIndex) {
            derivedStateOf {
                if (selectedCountryIndex != -1) {
                    listCountries[selectedCountryIndex]
                } else {
                    ""
                }
            }
        }

        var isEmailError by remember { mutableStateOf(false) }
        var isFullnameError by remember { mutableStateOf(false) }
        var isUsernameError by remember { mutableStateOf(false) }
        var isCountryError by remember { mutableStateOf(false) }
        var isPasswordError by remember { mutableStateOf(false) }
        var isConfirmPasswordError by remember { mutableStateOf(false) }

        val scrollState = rememberScrollState()

        Box(Modifier.fillMaxSize()) {

            Image(
                painter = painterResource(id = R.drawable.curvy_right_shape),
                contentDescription = "curvy shape",
                Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.3f),
                contentScale = ContentScale.FillBounds
            )

            BoxWithConstraints(
                Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null
                    ) {
                        keyboardController?.hide()
                        focusManager.clearFocus(true)
                    }
            ) {
                val offsetYtoMove = (maxHeight * 0.75f)
                val offsetYtoMovePx =
                    with(LocalDensity.current) { offsetYtoMove.toPx().roundToInt() }
                val cardHeight = (maxHeight * 0.75f) + 20.dp
                val offsetYNotMovePx = with(LocalDensity.current) { 20.dp.toPx().roundToInt() }

                val offset by animateIntOffsetAsState(
                    targetValue =
                    if (!isCardVisible) {
                        IntOffset(0, offsetYtoMovePx)
                    } else {
                        IntOffset(0, offsetYNotMovePx)
                    }, label = "offset", animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                )

                Column(
                    Modifier
                        .fillMaxSize()
                ) {
                    Text(
                        text = "Create Account",
                        fontFamily = fontFamily,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 32.dp)
                    )
                    Text(
                        text = "You can see movies and tv shows updates and save your favorite ones by creating account",
                        fontFamily = fontFamily,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.Black,
                        modifier = Modifier.padding(16.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .offset { offset }
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                        .background(Color.White)
                        .height(cardHeight)
//                    .fillMaxHeight(0.75f)
                        .fillMaxWidth()
                        .zIndex(4f)
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 16.dp)
                ) {

                    Column(
                        Modifier
                            .fillMaxWidth()
                            .verticalScroll(scrollState),
                    ) {
                        //Email Input
                        FormTextField(
                            value = valueOfEmail,
                            onValueChange = {
                                valueOfEmail = it
                                isEmailError = false
                            },
                            errorIndicator = isEmailError,
                            onErrorChange = { error -> isEmailError = error },
                            validatorFunction = { value -> isValidEmail(value) },
                            placeHolderText = "Email",
                            errorText = "email is not valid",
                            focusManager = focusManager,
                            modifier = Modifier
                                .padding(top = 56.dp)
                                .fillMaxWidth()
                        )

                        //Fullname Input
                        FormTextField(
                            value = valueOfFullname,
                            onValueChange = {
                                valueOfFullname = it
                                isFullnameError = false
                            },
                            errorIndicator = isFullnameError,
                            onErrorChange = { error -> isFullnameError = error },
                            validatorFunction = { value -> valueOfFullname.isNotBlank() && valueOfFullname.isNotEmpty() },
                            placeHolderText = "Full Name",
                            errorText = "fullname is empty",
                            focusManager = focusManager,
                            modifier = Modifier
                                .padding(top = 24.dp)
                                .fillMaxWidth()
                        )

                        //Username Input
                        FormTextField(
                            value = valueOfUsername,
                            onValueChange = {
                                valueOfUsername = it
                                isUsernameError = false
                            },
                            errorIndicator = isUsernameError,
                            onErrorChange = { error -> isUsernameError = error },
                            validatorFunction = { value -> valueOfUsername.isNotBlank() && valueOfUsername.isNotEmpty() },
                            placeHolderText = "Username",
                            errorText = "username is empty",
                            focusManager = focusManager,
                            modifier = Modifier
                                .padding(top = 24.dp)
                                .fillMaxWidth()
                        )

                        LargeDropdownMenu(
                            placeHolder = "Select Country",
                            dialogLabel = "Select Country",
                            items = listCountries,
                            selectedIndex = selectedCountryIndex,
                            onItemSelected = { index, _ -> selectedCountryIndex = index },
                            modifier = Modifier
                                .padding(top = 24.dp),
                            //                               .focusRequester(focusRequester),
                            toRequestFocus = { focusManager.moveFocus(FocusDirection.Next) }
                        )

                        //Password Input
                        FormPasswordTextField(
                            value = valueOfPassword,
                            onValueChange = {
                                valueOfPassword = it
                                isPasswordError = false
                            },
                            errorIndicator = isPasswordError,
                            onErrorChange = { boolean -> isPasswordError = boolean },
                            validatorFunction = { password -> isValidPassword(password) },
                            placeHolderText = "Password",
                            errorText = "Password must be at least 8 characters with lowercase, uppercase, number and symbol",
                            focusManager = focusManager,
                            isInTheEnd = false,
                            modifier = Modifier
                                .padding(top = 24.dp)
                                .fillMaxWidth()
                        )

                        //Confirm Password Input
                        FormPasswordTextField(
                            value = valueOfConfirmPassword,
                            onValueChange = {
                                valueOfConfirmPassword = it
                                isConfirmPasswordError = false
                            },
                            errorIndicator = isConfirmPasswordError,
                            onErrorChange = { boolean -> isConfirmPasswordError = boolean },
                            validatorFunction = { confirmPassword -> valueOfPassword == confirmPassword },
                            placeHolderText = "Confirm Password",
                            errorText = "password is not match",
                            focusManager = focusManager,
                            isInTheEnd = true,
                            modifier = Modifier
                                .padding(top = 24.dp)
                                .fillMaxWidth()
                        )

                        Button(
                            onClick = {
                                validateEmail(valueOfEmail, { boolean -> isEmailError = boolean })
                                validateFullName(valueOfFullname,
                                    { boolean -> isFullnameError = boolean })
                                validateUsername(valueOfUsername,
                                    { boolean -> isUsernameError = boolean })
                                validateCountry(valueOfCountry,
                                    { boolean -> isCountryError = boolean })
                                validatePassword(valueOfPassword,
                                    { boolean -> isPasswordError = boolean })
                                validateConfirmPassword(
                                    valueOfPassword,
                                    valueOfConfirmPassword,
                                    { boolean -> isConfirmPasswordError = boolean })
                                if (!isEmailError && !isFullnameError && !isUsernameError && !isCountryError && !isPasswordError && !isConfirmPasswordError) {
                                    viewModel.createUser(userRegistration = UserRegistration(
                                        email = valueOfEmail,
                                        password = valueOfPassword,
                                        fullname = valueOfFullname,
                                        username = valueOfUsername,
                                        selectedCountry = valueOfCountry
                                    )
                                    )
                                }
                            },
                            modifier = Modifier
                                .padding(top = 32.dp)
                                .fillMaxWidth()
                                .height(48.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondary,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            if(registerState.isLoading){
                                CircularProgressIndicator()
                            }else{
                                Text(text = "Register")
                            }
                        }

                        Row(
                            modifier = Modifier
                                .padding(36.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Already have an account? ",
                                fontFamily = fontFamily,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.Black
                            )
                            Text(
                                text = "Sign In",
                                fontFamily = fontFamily,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.tertiary,
                                modifier = Modifier.clickable {
                                    navigateToLogin()
                                },
                            )
                        }
                    }

                }
            }
        }
    }
}

fun isValidEmail(email: String): Boolean {
    val emailRegex = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+".toRegex()
    return email.matches(emailRegex)
}

fun isValidPassword(password: String): Boolean {
    val regex =
        Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#\$%^&*()_+\\-=\\[\\]{};':\"|,.<>?/]).{8,}$")
    return password.length >= 8 && regex.matches(password)
}

fun validateEmail(value: String, errorState: (Boolean) -> Unit) {
    if (isValidEmail(value)) {
        errorState(false)
    } else {
        errorState(true)
    }
}

fun validateFullName(value: String, errorState: (Boolean) -> Unit) {
    if (value.isNotBlank() && value.isNotEmpty()) {
        errorState(false)
    } else {
        errorState(true)
    }
}

fun validateUsername(value: String, errorState: (Boolean) -> Unit) {
    if (value.isNotBlank() && value.isNotEmpty()) {
        errorState(false)
    } else {
        errorState(true)
    }
}

fun validateCountry(value: String, errorState: (Boolean) -> Unit) {
    if (value.isNotBlank() && value.isNotEmpty()) {
        errorState(false)
    } else {
        errorState(true)
    }
}

fun validatePassword(value: String, errorState: (Boolean) -> Unit) {
    if (isValidPassword(value)) {
        errorState(false)
    } else {
        errorState(true)
    }
}

fun validateConfirmPassword(
    password: String,
    confirmPassword: String,
    errorState: (Boolean) -> Unit
) {
    if (password == confirmPassword) {
        errorState(false)
    } else {
        errorState(true)
    }
}

