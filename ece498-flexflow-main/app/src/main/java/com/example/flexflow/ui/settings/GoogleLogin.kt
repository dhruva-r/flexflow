package com.example.flexflow.ui.auth

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.example.flexflow.MainActivity
import com.example.flexflow.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

@Composable
fun GoogleLogin() {
    val firebaseAuth = FirebaseAuth.getInstance()
    var isLoggedIn by remember { mutableStateOf(firebaseAuth.currentUser != null) }
    val context = LocalContext.current

    val googleSignInLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            println("activity: " + result.resultCode)
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                val account = task.getResult(ApiException::class.java)
                val idToken = account?.idToken
                val credential = GoogleAuthProvider.getCredential(idToken, null)

                firebaseAuth.signInWithCredential(credential)
                    .addOnCompleteListener { authResult ->
                        if (authResult.isSuccessful) {
                            isLoggedIn = true
                            println("logged in")
                        } else {
                            println("login failure")
                        }
                    }
            } else {
                println("login cancel")
            }
        }

    val activity = context as MainActivity
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(context.getString(R.string.web_client_id))
        .requestEmail()
        .build()
    val googleSignInClient = GoogleSignIn.getClient(activity, gso)


    if (isLoggedIn) {
        Button(onClick = {
            firebaseAuth.signOut()
        }) {
            Text("Log out")
        }
    } else {
        Button(
            onClick = {
                val signInIntent = googleSignInClient.signInIntent
                googleSignInLauncher.launch(signInIntent)
            }
        ) {
            Text("Log in with Google")
        }
    }
}

@Composable
fun GoogleLogoutButton() {
    val firebaseAuth = FirebaseAuth.getInstance()

    Button(onClick = {
        firebaseAuth.signOut()
    }) {
        Text("Log out")
    }
}