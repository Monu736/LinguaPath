package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import com.example.R
import com.example.data.model.UserProfile
import com.example.ui.theme.AmberTertiary
import com.example.ui.theme.DuoBlue
import com.example.ui.theme.DuoGreen
import com.example.ui.theme.DuoRed
import com.example.ui.theme.EmeraldSecondary
import com.example.ui.theme.IndigoPrimary
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfileLoginDialog(
    userProfile: UserProfile?,
    onSaveProfile: (name: String, email: String, phone: String, avatar: String) -> Unit,
    onGoogleAuthenticated: ((name: String, email: String, photoUrl: String?) -> Unit)? = null,
    onSignOut: (() -> Unit)? = null,
    onAuthenticate: ((name: String, email: String, phone: String, authProvider: String) -> Unit)? = null,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val firebaseAuth = remember { FirebaseAuth.getInstance() }
    var currentFirebaseUser by remember { mutableStateOf(firebaseAuth.currentUser) }

    var authMode by remember {
        mutableStateOf(if (currentFirebaseUser != null || userProfile?.authProvider == "GOOGLE") "GOOGLE" else "EMAIL")
    }
    var name by remember {
        mutableStateOf(currentFirebaseUser?.displayName ?: userProfile?.userName ?: "Monu Gupta")
    }
    var email by remember {
        mutableStateOf(currentFirebaseUser?.email ?: userProfile?.userEmail ?: "monugupta7478@gmail.com")
    }
    var password by remember { mutableStateOf("••••••••") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var phone by remember { mutableStateOf(userProfile?.userPhone ?: "+91 98765 43210") }
    var selectedAvatar by remember { mutableStateOf(userProfile?.avatarEmoji ?: "⚡") }

    var isAuthenticating by remember { mutableStateOf(false) }
    var authErrorMessage by remember { mutableStateOf<String?>(null) }
    var authSuccessMessage by remember { mutableStateOf<String?>(null) }

    val ownedAvatars = remember(userProfile?.unlockedAvatars) {
        val list = userProfile?.unlockedAvatars?.split(",")?.map { it.trim() } ?: emptyList()
        val defaultAvatars = listOf("⚡", "🦉", "🦊", "🦁", "🌸", "👑", "🔥", "💎", "🎯")
        (list + defaultAvatars).distinct()
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .testTag("profile_login_dialog")
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header with close button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Lingopath Account",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "Cloud sync & verified profile",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_profile_dialog_button")
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Active Avatar & Frame Preview
                AvatarWithFrame(
                    avatarEmoji = selectedAvatar,
                    frameKey = userProfile?.avatarFrame ?: "DEFAULT",
                    photoUrl = currentFirebaseUser?.photoUrl?.toString() ?: userProfile?.profilePhotoUrl,
                    size = 76.dp,
                    fontSize = 38.sp
                )

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Frame: ${userProfile?.avatarFrame ?: "DEFAULT"}",
                    style = MaterialTheme.typography.labelSmall,
                    color = DuoBlue,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Auth Method Toggle Tabs
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (authMode == "GOOGLE") DuoBlue else MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { authMode = "GOOGLE" }
                    ) {
                        Text(
                            text = "🌐 Google Sign-In",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (authMode == "GOOGLE") Color.White else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(vertical = 10.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (authMode == "EMAIL") DuoBlue else MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { authMode = "EMAIL" }
                    ) {
                        Text(
                            text = "✉️ Email & Profile",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (authMode == "EMAIL") Color.White else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(vertical = 10.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (authMode == "GOOGLE") {
                    // Real Google Authentication Section
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            val activeUser = currentFirebaseUser

                            if (activeUser != null) {
                                // User is authenticated with Google
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = DuoGreen.copy(alpha = 0.15f),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(10.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = DuoGreen,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = "Google Account Connected",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = DuoGreen,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = "Real Firebase Auth session active",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    AvatarWithFrame(
                                        avatarEmoji = "👤",
                                        frameKey = "DEFAULT",
                                        photoUrl = activeUser.photoUrl?.toString(),
                                        size = 44.dp,
                                        fontSize = 20.sp
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = activeUser.displayName ?: "Google User",
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = activeUser.email ?: "",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                // Sign Out Button
                                OutlinedButton(
                                    onClick = {
                                        firebaseAuth.signOut()
                                        currentFirebaseUser = null
                                        onSignOut?.invoke()
                                        authSuccessMessage = "Signed out of Google account."
                                        authErrorMessage = null
                                    },
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = DuoRed),
                                    border = BorderStroke(1.dp, DuoRed.copy(alpha = 0.5f)),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("google_sign_out_button")
                                ) {
                                    Icon(Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Sign Out of Google", fontWeight = FontWeight.Bold)
                                }
                            } else {
                                // Not authenticated yet: Show real Google Sign-In prompt
                                Text(
                                    text = "Sign in with your Google Account for real cloud sync, authentic identity verification, and progress saving.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                if (isAuthenticating) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center,
                                        modifier = Modifier.padding(12.dp)
                                    ) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(24.dp),
                                            strokeWidth = 2.5.dp,
                                            color = DuoBlue
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(
                                            text = "Connecting to Google Account...",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                } else {
                                    // Official Google-branded Continue Button
                                    OutlinedButton(
                                        onClick = {
                                            isAuthenticating = true
                                            authErrorMessage = null
                                            authSuccessMessage = null

                                            coroutineScope.launch {
                                                try {
                                                    val serverClientId = context.getString(R.string.default_web_client_id)
                                                    val credentialManager = CredentialManager.create(context)
                                                    val googleIdOption = GetSignInWithGoogleOption.Builder(serverClientId)
                                                        .build()
                                                    val request = GetCredentialRequest.Builder()
                                                        .addCredentialOption(googleIdOption)
                                                        .build()

                                                    val response = credentialManager.getCredential(
                                                        request = request,
                                                        context = context
                                                    )

                                                    val credential = response.credential
                                                    if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                                                        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                                                        val idToken = googleIdTokenCredential.idToken
                                                        val authCredential = GoogleAuthProvider.getCredential(idToken, null)

                                                        firebaseAuth.signInWithCredential(authCredential)
                                                            .addOnSuccessListener { authResult ->
                                                                isAuthenticating = false
                                                                val user = authResult.user
                                                                currentFirebaseUser = user
                                                                val displayName = user?.displayName ?: googleIdTokenCredential.displayName ?: "Google User"
                                                                val userEmail = user?.email ?: googleIdTokenCredential.id
                                                                val photoUrl = user?.photoUrl?.toString()

                                                                name = displayName
                                                                email = userEmail
                                                                onGoogleAuthenticated?.invoke(displayName, userEmail, photoUrl)
                                                                onAuthenticate?.invoke(displayName, userEmail, phone, "GOOGLE")
                                                                authSuccessMessage = "Signed in as $displayName!"
                                                            }
                                                            .addOnFailureListener { exc ->
                                                                isAuthenticating = false
                                                                authErrorMessage = exc.localizedMessage ?: "Firebase Google sign-in failed."
                                                            }
                                                    } else {
                                                        isAuthenticating = false
                                                        authErrorMessage = "Received unsupported credential type from account picker."
                                                    }
                                                } catch (e: GetCredentialCancellationException) {
                                                    isAuthenticating = false
                                                    authErrorMessage = "Google Sign-In was cancelled."
                                                } catch (e: GetCredentialException) {
                                                    isAuthenticating = false
                                                    authErrorMessage = e.message ?: "Authentication error occurred with Google Identity."
                                                } catch (e: Exception) {
                                                    isAuthenticating = false
                                                    authErrorMessage = e.localizedMessage ?: "Unexpected error during Google Sign-In."
                                                }
                                            }
                                        },
                                        shape = RoundedCornerShape(12.dp),
                                        border = BorderStroke(1.5.dp, Color(0xFF4285F4)),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(48.dp)
                                            .testTag("google_sign_in_button")
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            Text(text = "🔴🟡🟢🔵", fontSize = 14.sp)
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Text(
                                                text = "Continue with Google",
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF4285F4),
                                                fontSize = 14.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // Email & Display Details Section
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Display Name") },
                            leadingIcon = {
                                Icon(Icons.Default.Person, contentDescription = null, tint = IndigoPrimary)
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Email Address") },
                            leadingIcon = {
                                Icon(Icons.Default.Email, contentDescription = null, tint = IndigoPrimary)
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("Password (for local profile)") },
                            leadingIcon = {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = IndigoPrimary)
                            },
                            trailingIcon = {
                                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                    Icon(
                                        imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = "Toggle password"
                                    )
                                }
                            },
                            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = { Text("Phone Number") },
                            leadingIcon = {
                                Icon(Icons.Default.Phone, contentDescription = null, tint = IndigoPrimary)
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // Error feedback banner
                if (authErrorMessage != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = DuoRed.copy(alpha = 0.12f),
                        border = BorderStroke(1.dp, DuoRed.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(10.dp)
                        ) {
                            Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = DuoRed, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = authErrorMessage ?: "",
                                style = MaterialTheme.typography.bodySmall,
                                color = DuoRed,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                // Success feedback banner
                if (authSuccessMessage != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = DuoGreen.copy(alpha = 0.15f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(10.dp)
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = DuoGreen, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = authSuccessMessage ?: "",
                                style = MaterialTheme.typography.bodySmall,
                                color = DuoGreen,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Avatar Icon Selection
                Text(
                    text = "Select Profile Avatar Emoji",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ownedAvatars.forEach { emoji ->
                        val isSelected = selectedAvatar == emoji
                        Surface(
                            shape = CircleShape,
                            color = if (isSelected) IndigoPrimary else MaterialTheme.colorScheme.surfaceVariant,
                            border = if (isSelected) BorderStroke(2.dp, AmberTertiary) else null,
                            modifier = Modifier
                                .size(40.dp)
                                .clickable { selectedAvatar = emoji }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(text = emoji, fontSize = 20.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Save Profile / Action Button
                Button(
                    onClick = {
                        val authType = if (currentFirebaseUser != null) "GOOGLE" else "EMAIL"
                        onAuthenticate?.invoke(name.trim(), email.trim(), phone.trim(), authType)
                        onSaveProfile(name.trim(), email.trim(), phone.trim(), selectedAvatar)
                        authSuccessMessage = "Profile updated and saved successfully!"
                        authErrorMessage = null
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("save_profile_button")
                ) {
                    Text(
                        text = "Save Profile Details",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}
