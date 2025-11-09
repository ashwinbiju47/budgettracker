package com.example.budgettracker.ui.auth

import android.util.Patterns
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

import com.example.budgettracker.data.local.User

@Composable
fun AuthScreen(
    onAuthSuccess: (User) -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    var isSignUp by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    val message by viewModel.authMessage

    fun validate(): Boolean {
        if (email.isBlank() || password.isBlank()) {
            error = "Email and password cannot be empty."
            return false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            error = "Invalid email format."
            return false
        }
        if (password.length < 6) {
            error = "Password must be at least 6 characters."
            return false
        }
        if (isSignUp && password != confirmPassword) {
            error = "Passwords do not match."
            return false
        }
        return true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = if (isSignUp) "Create Account" else "Sign In",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(Modifier.height(20.dp))

            OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation()
            )

            if (isSignUp) {
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirm Password") },
                    visualTransformation = PasswordVisualTransformation()
                )
            }

            Spacer(Modifier.height(20.dp))

            Button(onClick = {
                error = null
                if (!validate()) return@Button
                if (isSignUp) {
                    viewModel.register(email, password)
                } else {
                    viewModel.login(email, password) { user ->
                        onAuthSuccess(user)
                    }
                }
            }) {
                Text(if (isSignUp) "Sign Up" else "Sign In")
            }

            Spacer(Modifier.height(10.dp))

            TextButton(onClick = {
                isSignUp = !isSignUp
                error = null
            }) {
                Text(if (isSignUp) "Already have an account? Sign In" else "New user? Sign Up")
            }

            error?.let {
                Spacer(Modifier.height(8.dp))
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            message?.let {
                Spacer(Modifier.height(8.dp))
                Text(it, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}
