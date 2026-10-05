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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.UserProfile
import com.example.ui.theme.AmberTertiary
import com.example.ui.theme.EmeraldSecondary
import com.example.ui.theme.IndigoPrimary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfileLoginDialog(
    userProfile: UserProfile?,
    onSaveProfile: (name: String, email: String, phone: String, avatar: String) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(userProfile?.userName ?: "Monu Gupta") }
    var email by remember { mutableStateOf(userProfile?.userEmail ?: "monugupta7478@gmail.com") }
    var phone by remember { mutableStateOf(userProfile?.userPhone ?: "+91 98765 43210") }
    var selectedAvatar by remember { mutableStateOf(userProfile?.avatarEmoji ?: "⚡") }
    var isSavedNotificationVisible by remember { mutableStateOf(false) }

    val avatarOptions = listOf("⚡", "🎓", "🚀", "🌟", "🦁", "🎧", "🎮", "🌸", "👑", "🔥", "💎", "🎯")

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
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
                            text = "User Profile & Login",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Data saved securely on device",
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

                Spacer(modifier = Modifier.height(16.dp))

                // Avatar Icon Preview
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(IndigoPrimary.copy(alpha = 0.15f))
                        .border(2.dp, IndigoPrimary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = selectedAvatar, fontSize = 40.sp)
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Choose Your Avatar Icon",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Avatar Selector FlowRow
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(horizontal = 4.dp)
                ) {
                    avatarOptions.forEach { emoji ->
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

                Spacer(modifier = Modifier.height(16.dp))

                // Name Input Field
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Full Name") },
                    leadingIcon = {
                        Icon(Icons.Default.Person, contentDescription = null, tint = IndigoPrimary)
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_profile_name")
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Email Input Field (Requested by user)
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email Address (Login)") },
                    leadingIcon = {
                        Icon(Icons.Default.Email, contentDescription = null, tint = IndigoPrimary)
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_profile_email")
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Phone Number Input Field (Requested by user)
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone Number (Login)") },
                    leadingIcon = {
                        Icon(Icons.Default.Phone, contentDescription = null, tint = IndigoPrimary)
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_profile_phone")
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Stats Summary Strip
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "🔥 ${userProfile?.streak ?: 3} Days", fontWeight = FontWeight.Bold, color = AmberTertiary)
                            Text(text = "Streak", style = MaterialTheme.typography.labelSmall)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "⭐ ${userProfile?.xp ?: 240} XP", fontWeight = FontWeight.Bold, color = IndigoPrimary)
                            Text(text = "Points", style = MaterialTheme.typography.labelSmall)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "🎯 ${userProfile?.overallAccuracy ?: 88}%", fontWeight = FontWeight.Bold, color = EmeraldSecondary)
                            Text(text = "Accuracy", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }

                if (isSavedNotificationVisible) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(4.dp)
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldSecondary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Login & Profile details saved successfully!",
                            style = MaterialTheme.typography.bodySmall,
                            color = EmeraldSecondary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Save Button
                Button(
                    onClick = {
                        onSaveProfile(name.trim(), email.trim(), phone.trim(), selectedAvatar)
                        isSavedNotificationVisible = true
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("save_profile_button")
                ) {
                    Text(
                        text = "Save Profile & Login Details",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}
