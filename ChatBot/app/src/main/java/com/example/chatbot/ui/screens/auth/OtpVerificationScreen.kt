package com.example.chatbot.ui.screens.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.chatbot.R
import com.example.chatbot.ui.viewmodel.OtpVerificationViewModel

@Composable
fun OtpVerificationScreen(
    modifier: Modifier = Modifier,
    otpVerificationViewModel: OtpVerificationViewModel = hiltViewModel()
) {
    val otpState by otpVerificationViewModel.otpUiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(10.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.chat_bot),
                contentDescription = "Logo Icon",
                modifier = Modifier.size(100.dp)
            )
            Text(
                text = "ChatBot",
                style = MaterialTheme.typography.displayMedium
            )

            Spacer(Modifier.height(30.dp))

            Text(
                text = "Enter OTP sent to your email",
                style = MaterialTheme.typography.headlineSmall
            )

            OtpTextEdit(
                otpState.otpValues,
                otpState.loading,
                otpState.error,
                otpVerificationViewModel::updateOtpUi,
                otpVerificationViewModel::verifyOtp
            )
        }
    }
}

@Composable
fun OtpTextEdit(
    otpValues: List<String>,
    loading: Boolean,
    error: Boolean,
    updateOtpValues: (Int, String) -> Unit,
    verifyOtp: () -> Unit
) {
    val otpLength = 6
    val focusRequester = List(otpLength) { FocusRequester() }

    Spacer(Modifier.height(10.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        otpValues.forEachIndexed { index, str ->
            OutlinedTextField(
                value = str,
                onValueChange = { value ->
                    if(value.length <= 1) {
                        updateOtpValues(index, value)

                        if(value.isNotEmpty() && index < otpLength - 1) {
                            focusRequester[index + 1].requestFocus()
                        }
                    }
                },
                textStyle = TextStyle(textAlign = TextAlign.Center, fontSize = 17.sp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = MaterialTheme.shapes.large,
                modifier = Modifier
                    .size(50.dp)
                    .focusRequester(focusRequester[index])
            )
        }
    }

    if(error) {
        Text(
            text = "Invalid OTP",
            color = MaterialTheme.colorScheme.error
        )
    }

    Spacer(Modifier.height(10.dp))

    Button(
        onClick = verifyOtp,
        modifier = Modifier.fillMaxWidth()
    ) {
        if(loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
        else {
            Text("Verify")
        }
    }
}