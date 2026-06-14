package ru.sibfu.openkras.features.favorites

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import ru.sibfu.openkras.features.excursion.ExcursionItem
import ru.sibfu.openkras.features.excursion.SearchWithFilter

@Composable
fun FavoritesScreen(
    navController: NavController = rememberNavController(),
    viewModel: FavoritesViewModel = hiltViewModel(),
    snackbarHostState: SnackbarHostState,
    onNavigateToExcursion: (Int) -> Unit = {},
){
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when(effect){
                is FavoritesEffect.NavigateToExcursion -> onNavigateToExcursion(effect.excursionId)
                is FavoritesEffect.ShowSnackbar -> snackbarHostState.showSnackbar(
                    message = effect.message,
                    duration = SnackbarDuration.Short
                )
            }

        }
    }
    FavoriteScreenContent(
        state = state,
        onIntent = viewModel::handleIntent
    )
}

@Composable
fun FavoriteScreenContent(
    modifier: Modifier = Modifier,
    state: FavoritesState,
    onIntent: (FavoritesIntent) -> Unit = {},
){
    Box(modifier = Modifier.fillMaxSize()) {
        if (state.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
        LazyColumn {
            item {
                SearchWithFilter(
                    searchQuery = state.queryField,
                    onQueryChange = {
                        onIntent(FavoritesIntent.QueryChange(it))
                    },

                    onCategorySelected ={ category ->
                        onIntent(FavoritesIntent.SelectCategory(category = category))
                    },
                    selectedCategory = state.selectedCategory,
                    category = state.categoryItems,
                )
            }
            items(state.displayedItems) { excursion ->
                ExcursionItem(
                    modifier = Modifier,
                    excursion = excursion,
                    onClick = { onIntent(FavoritesIntent.onNavigateToExcursionClick(excursion.id)) }
                )
            }
        }
        if (!state.isLoading && state.allFavoriteItems.isEmpty()) {
            Text(
                text = "У вас пока нет избранных экскурсий",
                modifier = Modifier.align(Alignment.Center)
            )
        } else if (!state.isLoading && state.displayedItems.isEmpty()) {
            Text(
                text = "По вашему запросу ничего не найдено",
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}