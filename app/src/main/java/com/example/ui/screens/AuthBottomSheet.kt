package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.local.UserEntity
import com.example.ui.theme.BentoBorder
import com.example.ui.theme.BentoCardWhite
import com.example.ui.theme.BentoPrimaryBlue
import com.example.ui.theme.BentoSlate100
import com.example.ui.theme.BentoSlate500
import com.example.ui.theme.BentoSlate700
import com.example.ui.theme.BentoSlate900
import com.example.ui.viewmodel.AuthMode
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthBottomSheet(
    isOpen: Boolean,
    authMode: AuthMode,
    currentUser: UserEntity?,
    errorMessage: String?,
    successMessage: String?,
    onClose: () -> Unit,
    onSetMode: (AuthMode) -> Unit,
    onLogin: (email: String, pass: String) -> Unit,
    onSignUp: (name: String, email: String, pass: String, phone: String, locality: String, securityAnswer: String) -> Unit,
    onResetPassword: (email: String, securityAnswer: String, newPass: String) -> Unit,
    onUpdateProfile: (name: String, phone: String, locality: String, bio: String) -> Unit,
    onUpdateAvatar: (avatarUri: String?) -> Unit,
    onLogout: () -> Unit
) {
    if (!isOpen) return

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val context = LocalContext.current

    // Photo Picker for profile picture upload
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null && currentUser != null) {
            try {
                // Persist image to internal files dir
                val avatarFile = File(context.filesDir, "avatar_${currentUser.id.hashCode()}.jpg")
                context.contentResolver.openInputStream(uri)?.use { input ->
                    FileOutputStream(avatarFile).use { output ->
                        input.copyTo(output)
                    }
                }
                onUpdateAvatar(avatarFile.toURI().toString())
            } catch (_: Exception) {
                // fallback to original uri
                onUpdateAvatar(uri.toString())
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onClose,
        sheetState = sheetState,
        containerColor = BentoCardWhite,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .width(40.dp)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(BentoBorder)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header Row: Title & Close
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "BALASORE CONNECT",
                        style = MaterialTheme.typography.labelSmall.copy(
                            letterSpacing = 2.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = BentoPrimaryBlue
                    )
                    Text(
                        text = when (authMode) {
                            AuthMode.PROFILE -> "User Profile"
                            AuthMode.LOGIN -> "Welcome Back"
                            AuthMode.SIGNUP -> "Create Balasore Account"
                            AuthMode.FORGOT_PASSWORD -> "Reset Password"
                            AuthMode.EDIT_PROFILE -> "Edit Profile Details"
                        },
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = BentoSlate900
                    )
                }

                IconButton(onClick = onClose) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = BentoSlate500
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Error notice banner
            if (errorMessage != null) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFFEE2E2),
                    border = BorderStroke(1.dp, Color(0xFFFCA5A5)),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.ErrorOutline,
                            contentDescription = null,
                            tint = Color(0xFFDC2626),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = errorMessage,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF991B1B)
                        )
                    }
                }
            }

            // Success notice banner
            if (successMessage != null) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFDCFCE7),
                    border = BorderStroke(1.dp, Color(0xFF86EFAC)),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF16A34A),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = successMessage,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF166534)
                        )
                    }
                }
            }

            // Content switch based on AuthMode
            when (authMode) {
                AuthMode.PROFILE -> {
                    if (currentUser != null) {
                        ProfileView(
                            user = currentUser,
                            onPickPhoto = {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            onEdit = { onSetMode(AuthMode.EDIT_PROFILE) },
                            onLogout = onLogout
                        )
                    } else {
                        // User not logged in, prompt to log in or sign up
                        LoggedOutPrompt(
                            onLogin = { onSetMode(AuthMode.LOGIN) },
                            onSignUp = { onSetMode(AuthMode.SIGNUP) }
                        )
                    }
                }
                AuthMode.LOGIN -> {
                    LoginForm(
                        onLogin = onLogin,
                        onQuickDemoLogin = {
                            onLogin("niharbhuyan@gmail.com", "balasore123")
                        },
                        onForgotPassword = { onSetMode(AuthMode.FORGOT_PASSWORD) },
                        onGoToSignUp = { onSetMode(AuthMode.SIGNUP) }
                    )
                }
                AuthMode.SIGNUP -> {
                    SignUpForm(
                        onSignUp = onSignUp,
                        onGoToLogin = { onSetMode(AuthMode.LOGIN) }
                    )
                }
                AuthMode.FORGOT_PASSWORD -> {
                    ForgotPasswordForm(
                        onReset = onResetPassword,
                        onCancel = { onSetMode(AuthMode.LOGIN) }
                    )
                }
                AuthMode.EDIT_PROFILE -> {
                    if (currentUser != null) {
                        EditProfileForm(
                            user = currentUser,
                            onSave = { name, phone, locality, bio ->
                                onUpdateProfile(name, phone, locality, bio)
                                onSetMode(AuthMode.PROFILE)
                            },
                            onCancel = { onSetMode(AuthMode.PROFILE) }
                        )
                    }
                }
            }
        }
    }
}

// 1. Profile View
@Composable
private fun ProfileView(
    user: UserEntity,
    onPickPhoto: () -> Unit,
    onEdit: () -> Unit,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Avatar with Camera Pick Button
        Box(contentAlignment = Alignment.BottomEnd) {
            if (user.avatarUri != null) {
                AsyncImage(
                    model = user.avatarUri,
                    contentDescription = "Profile Picture",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(96.dp)
                        .clip(CircleShape)
                        .background(BentoSlate100)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .clip(CircleShape)
                        .background(BentoPrimaryBlue.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = user.fullName.take(2).uppercase(),
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = BentoPrimaryBlue
                    )
                }
            }

            // Edit Photo Floating Button
            Surface(
                shape = CircleShape,
                color = BentoPrimaryBlue,
                shadowElevation = 4.dp,
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .clickable { onPickPhoto() }
                    .testTag("upload_profile_picture_button")
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Change Profile Picture",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = user.fullName,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = BentoSlate900
        )
        Text(
            text = "${user.locality} • Balasore",
            style = MaterialTheme.typography.bodyMedium,
            color = BentoPrimaryBlue
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = user.bio,
            style = MaterialTheme.typography.bodyMedium,
            color = BentoSlate700
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Profile Details Card
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = BentoSlate100.copy(alpha = 0.5f),
            border = BorderStroke(1.dp, BentoBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                ProfileFieldRow(icon = Icons.Default.Mail, label = "Email", value = user.email)
                if (user.phoneNumber.isNotBlank()) {
                    ProfileFieldRow(icon = Icons.Default.Phone, label = "Phone", value = user.phoneNumber)
                }
                ProfileFieldRow(icon = Icons.Default.Place, label = "Locality", value = user.locality)
                ProfileFieldRow(icon = Icons.Default.Security, label = "Security Q", value = user.securityQuestion)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Buttons: Edit Profile & Logout
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onEdit,
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, BentoPrimaryBlue),
                modifier = Modifier.weight(1f).testTag("edit_profile_button")
            ) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = null, tint = BentoPrimaryBlue, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "Edit Profile", color = BentoPrimaryBlue)
            }

            Button(
                onClick = onLogout,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                modifier = Modifier.weight(1f).testTag("logout_button")
            ) {
                Icon(imageVector = Icons.Default.ExitToApp, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "Log Out")
            }
        }
    }
}

@Composable
private fun ProfileFieldRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = BentoSlate500, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = BentoSlate500)
            Text(text = value, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium), color = BentoSlate900)
        }
    }
}

// 2. Login Form
@Composable
private fun LoginForm(
    onLogin: (email: String, pass: String) -> Unit,
    onQuickDemoLogin: () -> Unit,
    onForgotPassword: () -> Unit,
    onGoToSignUp: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Sign in to post reviews, save favorite coastal spots, and connect with Balasore.",
            style = MaterialTheme.typography.bodyMedium,
            color = BentoSlate700
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Quick Demo Sign-In Card
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = BentoPrimaryBlue.copy(alpha = 0.08f),
            border = BorderStroke(1.dp, BentoPrimaryBlue.copy(alpha = 0.25f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onQuickDemoLogin() }
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "⚡ Quick Demo Account",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = BentoPrimaryBlue
                    )
                    Text(
                        text = "Sign in as Nihar Bhuyan (One-tap)",
                        style = MaterialTheme.typography.bodySmall,
                        color = BentoSlate700
                    )
                }
                Button(
                    onClick = onQuickDemoLogin,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BentoPrimaryBlue),
                    modifier = Modifier.testTag("one_tap_demo_login")
                ) {
                    Text("1-Tap Demo")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email Address") },
            placeholder = { Text("e.g. nihar@gmail.com") },
            singleLine = true,
            leadingIcon = { Icon(Icons.Default.Mail, contentDescription = null, tint = BentoSlate500) },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = BentoPrimaryBlue,
                unfocusedBorderColor = BentoBorder
            ),
            modifier = Modifier.fillMaxWidth().testTag("login_email_input")
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = BentoSlate500) },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = BentoPrimaryBlue,
                unfocusedBorderColor = BentoBorder
            ),
            modifier = Modifier.fillMaxWidth().testTag("login_password_input")
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = onForgotPassword) {
                Text("Forgot Password?", color = BentoPrimaryBlue, style = MaterialTheme.typography.labelMedium)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = { onLogin(email, password) },
            enabled = email.isNotBlank() && password.isNotBlank(),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = BentoPrimaryBlue),
            modifier = Modifier.fillMaxWidth().height(48.dp).testTag("login_submit_button")
        ) {
            Text("Log In", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Don't have an account?", style = MaterialTheme.typography.bodyMedium, color = BentoSlate700)
            TextButton(onClick = onGoToSignUp) {
                Text("Sign Up", color = BentoPrimaryBlue, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// 3. Sign Up Form
@Composable
private fun SignUpForm(
    onSignUp: (name: String, email: String, pass: String, phone: String, locality: String, securityAnswer: String) -> Unit,
    onGoToLogin: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var locality by remember { mutableStateOf("Balasore Town") }
    var securityAnswer by remember { mutableStateOf("") }

    val localities = listOf("Balasore Town", "Chandipur", "Remuna", "Nilagiri", "Jaleswar", "Soro", "Bhograi")

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Join the Balasore citizen and visitor network to contribute community reports, ratings, and feedback.",
            style = MaterialTheme.typography.bodyMedium,
            color = BentoSlate700
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Full Name") },
            placeholder = { Text("e.g. Ramesh Chandra Das") },
            singleLine = true,
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = BentoSlate500) },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BentoPrimaryBlue, unfocusedBorderColor = BentoBorder),
            modifier = Modifier.fillMaxWidth().testTag("signup_name_input")
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email Address") },
            placeholder = { Text("e.g. ramesh@example.com") },
            singleLine = true,
            leadingIcon = { Icon(Icons.Default.Mail, contentDescription = null, tint = BentoSlate500) },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BentoPrimaryBlue, unfocusedBorderColor = BentoBorder),
            modifier = Modifier.fillMaxWidth().testTag("signup_email_input")
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Phone Number") },
            placeholder = { Text("+91 94370 ...") },
            singleLine = true,
            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = BentoSlate500) },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BentoPrimaryBlue, unfocusedBorderColor = BentoBorder),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(text = "Locality / Block:", style = MaterialTheme.typography.labelSmall, color = BentoSlate700)
        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
            OutlinedTextField(
                value = locality,
                onValueChange = { locality = it },
                label = { Text("Area / Sub-division") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BentoPrimaryBlue, unfocusedBorderColor = BentoBorder),
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password (min 4 chars)") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = BentoSlate500) },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BentoPrimaryBlue, unfocusedBorderColor = BentoBorder),
            modifier = Modifier.fillMaxWidth().testTag("signup_password_input")
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = securityAnswer,
            onValueChange = { securityAnswer = it },
            label = { Text("Security Q: What is your favorite Balasore spot?") },
            placeholder = { Text("e.g. Chandipur / Remuna") },
            singleLine = true,
            leadingIcon = { Icon(Icons.Default.Security, contentDescription = null, tint = BentoSlate500) },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BentoPrimaryBlue, unfocusedBorderColor = BentoBorder),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { onSignUp(name, email, password, phone, locality, securityAnswer) },
            enabled = name.isNotBlank() && email.isNotBlank() && password.length >= 4,
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = BentoPrimaryBlue),
            modifier = Modifier.fillMaxWidth().height(48.dp).testTag("signup_submit_button")
        ) {
            Text("Create Account", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Already registered?", style = MaterialTheme.typography.bodyMedium, color = BentoSlate700)
            TextButton(onClick = onGoToLogin) {
                Text("Log In", color = BentoPrimaryBlue, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// 4. Password Reset Form
@Composable
private fun ForgotPasswordForm(
    onReset: (email: String, securityAnswer: String, newPass: String) -> Unit,
    onCancel: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var securityAnswer by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Enter your registered email and answer your security question to reset your password.",
            style = MaterialTheme.typography.bodyMedium,
            color = BentoSlate700
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Registered Email") },
            singleLine = true,
            leadingIcon = { Icon(Icons.Default.Mail, contentDescription = null, tint = BentoSlate500) },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BentoPrimaryBlue, unfocusedBorderColor = BentoBorder),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = securityAnswer,
            onValueChange = { securityAnswer = it },
            label = { Text("Security Answer (Favorite Place in Balasore)") },
            placeholder = { Text("e.g. Chandipur / Remuna") },
            singleLine = true,
            leadingIcon = { Icon(Icons.Default.Security, contentDescription = null, tint = BentoSlate500) },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BentoPrimaryBlue, unfocusedBorderColor = BentoBorder),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = newPassword,
            onValueChange = { newPassword = it },
            label = { Text("New Password (min 4 characters)") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = BentoSlate500) },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BentoPrimaryBlue, unfocusedBorderColor = BentoBorder),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(18.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedButton(
                onClick = onCancel,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text("Cancel")
            }

            Button(
                onClick = { onReset(email, securityAnswer, newPassword) },
                enabled = email.isNotBlank() && securityAnswer.isNotBlank() && newPassword.length >= 4,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BentoPrimaryBlue),
                modifier = Modifier.weight(1f)
            ) {
                Text("Reset Password")
            }
        }
    }
}

// 5. Edit Profile Form
@Composable
private fun EditProfileForm(
    user: UserEntity,
    onSave: (name: String, phone: String, locality: String, bio: String) -> Unit,
    onCancel: () -> Unit
) {
    var name by remember { mutableStateOf(user.fullName) }
    var phone by remember { mutableStateOf(user.phoneNumber) }
    var locality by remember { mutableStateOf(user.locality) }
    var bio by remember { mutableStateOf(user.bio) }

    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Full Name") },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BentoPrimaryBlue, unfocusedBorderColor = BentoBorder),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Phone Number") },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BentoPrimaryBlue, unfocusedBorderColor = BentoBorder),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = locality,
            onValueChange = { locality = it },
            label = { Text("Locality / Block in Balasore") },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BentoPrimaryBlue, unfocusedBorderColor = BentoBorder),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = bio,
            onValueChange = { bio = it },
            label = { Text("Short Bio") },
            minLines = 2,
            maxLines = 4,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BentoPrimaryBlue, unfocusedBorderColor = BentoBorder),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(18.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedButton(
                onClick = onCancel,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text("Cancel")
            }

            Button(
                onClick = { onSave(name, phone, locality, bio) },
                enabled = name.isNotBlank(),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BentoPrimaryBlue),
                modifier = Modifier.weight(1f)
            ) {
                Text("Save Changes")
            }
        }
    }
}

// 6. Logged Out Prompt
@Composable
private fun LoggedOutPrompt(
    onLogin: () -> Unit,
    onSignUp: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(BentoPrimaryBlue.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = BentoPrimaryBlue,
                modifier = Modifier.size(36.dp)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "Welcome to Balasore Live",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = BentoSlate900
        )
        Text(
            text = "Log in to manage your profile, write reviews on tourist spots, share weather ground reports, and save articles.",
            style = MaterialTheme.typography.bodyMedium,
            color = BentoSlate500,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = onLogin,
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = BentoPrimaryBlue),
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            Text("Log In", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedButton(
            onClick = onSignUp,
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.dp, BentoPrimaryBlue),
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            Text("Create Account", color = BentoPrimaryBlue, fontWeight = FontWeight.Bold)
        }
    }
}
