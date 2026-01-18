package com.echohabit.app.ui.screens.auth

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import com.echohabit.app.R
import com.echohabit.app.ui.theme.*
import com.echohabit.app.util.showToast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    viewModel: AuthViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    var username by remember { mutableStateOf("") }
    var selectedMode by remember { mutableStateOf(0) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                val idToken = account.idToken
                if (idToken != null) {
                    viewModel.signInWithGoogle(idToken)
                } else {
                    context.showToast("Failed to get ID token")
                }
            } catch (e: ApiException) {
                context.showToast("Google Sign-In failed")
            }
        }
    }

    LaunchedEffect(uiState) {
        when (uiState) {
            is AuthViewModel.AuthUiState.Success -> {
                onLoginSuccess()
            }
            is AuthViewModel.AuthUiState.Error -> {
                val message = (uiState as AuthViewModel.AuthUiState.Error).message
                context.showToast(message)
                viewModel.resetState()
            }
            else -> {}
        }
    }

    // ✅ BOX LAYOUT - OVERLAPPING EFFECT!
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
    ) {
        // ✅ FULL BANNER (BACKGROUND)
        AsyncImage(
            model = com.echohabit.app.R.drawable.login_banner,
            contentDescription = "Banner",
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.5f) // 50% height
                .align(Alignment.TopCenter),
            contentScale = ContentScale.Crop
        )

        // ✅ WHITE CARD OVERLAPPING BANNER!
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.68f) // 68% height (overlap effect!)
                .align(Alignment.BottomCenter)
                .zIndex(1f), // On top of banner!
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            color = Color.White,
            shadowElevation = 16.dp // Strong shadow for depth
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 28.dp, vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // WELCOME TEXT
                Text(
                    text = "Welcome Back!",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A)
                )
                Text(
                    text = "Sign in to continue your eco journey",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF666666),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                // MODE TABS
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFF5F5F5)),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ModeTab(
                        text = "GOOGLE",
                        isSelected = selectedMode == 0,
                        onClick = { selectedMode = 0 },
                        modifier = Modifier.weight(1f)
                    )
                    ModeTab(
                        text = "QUICK START",
                        isSelected = selectedMode == 1,
                        onClick = { selectedMode = 1 },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                AnimatedContent(
                    targetState = selectedMode,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(300)) togetherWith
                                fadeOut(animationSpec = tween(300))
                    },
                    label = "login_mode"
                ) { mode ->
                    when (mode) {
                        0 -> GoogleLoginContent(
                            isLoading = uiState is AuthViewModel.AuthUiState.Loading,
                            onGoogleClick = {
                                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                    .requestIdToken(context.getString(R.string.default_web_client_id))
                                    .requestEmail()
                                    .build()
                                val googleSignInClient = GoogleSignIn.getClient(context, gso)
                                launcher.launch(googleSignInClient.signInIntent)
                            }
                        )
                        1 -> UsernameLoginContent(
                            username = username,
                            onUsernameChange = { username = it },
                            isLoading = uiState is AuthViewModel.AuthUiState.Loading,
                            onLoginClick = {
                                if (username.isNotBlank()) {
                                    viewModel.signInWithUsername(username.trim())
                                } else {
                                    context.showToast("Please enter a username")
                                }
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // FOOTER
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Developed by",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF999999)
                    )
                    Text(
                        text = "Muhammad Nur Ihsan • 230104040214",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF666666)
                    )
                }
            }
        }
    }
}

@Composable
private fun ModeTab(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TextButton(
        onClick = onClick,
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (isSelected) PrimaryGreen else Color.Transparent),
        colors = ButtonDefaults.textButtonColors(
            contentColor = if (isSelected) Color.White else Color(0xFF666666)
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            modifier = Modifier.padding(vertical = 6.dp)
        )
    }
}

@Composable
private fun GoogleLoginContent(
    isLoading: Boolean,
    onGoogleClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Secure authentication with Google",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF666666),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onGoogleClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = !isLoading,
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryGreen,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(16.dp),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 2.dp,
                pressedElevation = 6.dp
            )
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Login,
                        contentDescription = null,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Continue with Google",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
private fun UsernameLoginContent(
    username: String,
    onUsernameChange: (String) -> Unit,
    isLoading: Boolean,
    onLoginClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = username,
            onValueChange = onUsernameChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Username") },
            placeholder = { Text("Enter your username") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Person,
                    contentDescription = null
                )
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = { if (username.isNotBlank()) onLoginClick() }
            ),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryGreen,
                focusedLabelColor = PrimaryGreen,
                focusedLeadingIconColor = PrimaryGreen,
                cursorColor = PrimaryGreen
            )
        )

        Button(
            onClick = onLoginClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = username.isNotBlank() && !isLoading,
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryGreen,
                contentColor = Color.White,
                disabledContainerColor = Color(0xFFE0E0E0),
                disabledContentColor = Color(0xFF999999)
            ),
            shape = RoundedCornerShape(16.dp),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 2.dp,
                pressedElevation = 6.dp
            )
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = "Get Started",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Text(
            text = "No sign-up required • Instant access",
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF999999),
            textAlign = TextAlign.Center
        )
    }
}