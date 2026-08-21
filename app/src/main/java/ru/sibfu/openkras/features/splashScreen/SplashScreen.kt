package ru.sibfu.openkras.features.splashScreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun SplashScreen(
    snackbarHostState: SnackbarHostState,
    onNavigateToLogin: () -> Unit,
    onNavigateToMain: () -> Unit,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    // Слушаем изменение стейта для навигации
    LaunchedEffect(state.navigationState) {
        when (state.navigationState) {
            is SplashNavigationState.NavigateToLogin -> onNavigateToLogin()
            is SplashNavigationState.NavigateToMain -> onNavigateToMain()
            is SplashNavigationState.ShowSnackbar -> {
                snackbarHostState.showSnackbar(
                    message = (state.navigationState as SplashNavigationState.ShowSnackbar).message
                )
            }
            is SplashNavigationState.Loading -> {  }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Открой Красноярск",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Spacer(modifier = Modifier.height(32.dp))

            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}