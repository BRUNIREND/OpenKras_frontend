package ru.sibfu.openkras.features.excursion

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil3.compose.AsyncImage
import ru.sibfu.domain.CategoryModel
import ru.sibfu.domain.ExcursionModel
import ru.sibfu.domain.PointModel
import ru.sibfu.openkras.R
import ru.sibfu.openkras.navigation.MyBottomNavigation
import ru.sibfu.openkras.ui.theme.ThemePreviews
import ru.sibfu.openkras.ui.theme.customShadow
import ru.sibfu.openkras.ui.theme.statusColor



//@ThemePreviews
@Composable
fun ListExcursionScreen(
    navController: NavController = rememberNavController(),
    viewModel: ExcursionViewModel = hiltViewModel()
){
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.handleIntent(ExcursionIntent.LoadData)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (state.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }


        LazyColumn {
            item {
                SearchWithFilter(
                    searchQuery = state.queryField ?: "",
                    onQueryChange = {
                        viewModel.handleIntent(ExcursionIntent.QueryChange(it))
                    },
                    onFilterClick = {
                        viewModel.handleIntent(ExcursionIntent.ChangeFilterCloseState)
                    },
                    onCategorySelected ={ category ->
                        viewModel.handleIntent(ExcursionIntent.SelectCategory(category = category))
                    },
                    selectedCategory = state.selectedCategory?.toInt() ?: 0,
                    category = state.categoryItems,
                    filterState = state.isFilterOpen
                )
            }
            items(state.items) { excursion ->
                ExcursionItem(
                    modifier = Modifier,
                    excursion = testExcursion, // TODO("Заменить мок данных")
                    onClick = {
                        navController.navigate("excursion/${excursion.id}") //TODO("Изменить на лаунч эффекты")
                    }
                )

            }
        }
        state.error?.let {
            Text(text = "Ошибка: $it", color = Color.Red)
        }
    }

}
@Composable
fun SearchWithFilter(
    selectedCategory: Int? = null,
    searchQuery: String,
    category: List<CategoryModel>,
    onQueryChange: (String) -> Unit,
    onCategorySelected: (String) -> Unit,
    onFilterClick: () -> Unit,
    filterState: Boolean,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp) // Отступ между поиском и кнопкой
    ) {
        // Поле поиска занимает всё свободное пространство
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onQueryChange,
            modifier = Modifier.weight(1f), // Забирает максимум места
            placeholder = { Text("Поиск") },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = null)
            },
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color.Transparent,
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        )

        // Кнопка категорий/фильтров
        FilterMenu(
            onFilterClick = onFilterClick,
            filterState = filterState,
            categories = category, //MockData
            selectedCategory = selectedCategory,
            onCategorySelected = {onCategorySelected},
        )
    }
}


//TODO(ПОлностью перелопатить)
@Composable
fun FilterMenu(
    filterState: Boolean,
    onFilterClick: () -> Unit,
    categories: List<CategoryModel>,
    selectedCategory: Int?,
    onCategorySelected: (Int) -> Unit
) {

    Box {
        // Твоя кнопка из примера выше
        FilledIconButton(
            onClick = onFilterClick,
            modifier = Modifier.size(56.dp), // Совпадает со стандартной высотой TextField
            shape = RoundedCornerShape(12.dp),
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_filter), // Твоя иконка категорий
                contentDescription = "Категории",
                modifier = Modifier.size(24.dp)
            )
        }

        // Само меню
        DropdownMenu(
            expanded = filterState,
            onDismissRequest = { onFilterClick() },
            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
        ) {
            categories.forEach { category ->
                DropdownMenuItem(
                    trailingIcon = {
                        if (category.id == selectedCategory) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = "Accept",
                                modifier = Modifier.fillMaxSize(),
                                tint = MaterialTheme.colorScheme.primary,
                            )
                        }
                    },
                    text = { Text(category.name) },
                    onClick = {
                        onCategorySelected(category.id)
                        onFilterClick()
                    }
                )
            }
        }
    }
}

val testExcursion = ExcursionModel(
    id = 1,
    title = "Название экскурсии",
    description = "Описание экскурсии",
    coverUrl = "https://img.freepik.com/free-photo/view-funny-animal_23-2151098313.jpg?semt=ais_hybrid&w=740&q=80",
    images = mutableListOf("https://img.freepik.com/free-photo/view-funny-animal_23-2151098313.jpg?semt=ais_hybrid&w=740&q=80"),
    points = mutableListOf(
        PointModel(
            id = 1,
            name = "Название точки",
            description = "Описание точки",
            address = "Адрес точки",
            latitude = 55.7558,
            longitude = 37.6176,
            radiusMeters = 20,
            audioUrl = mutableListOf("https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3"),
            images = mutableListOf("https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3"),
        )
    ),
    categoryId = 1,
    duration = 20,
    distance = 10.0
)


@ThemePreviews
@Composable
fun ExcursionItem(
    modifier: Modifier = Modifier,
    excursion: ExcursionModel = testExcursion,
    onClick: () -> Unit = {},
    isFavorite: Boolean = false,
    onFavoriteClick: () -> Unit = {}
){
    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(212.dp)
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .customShadow(
                offsetY = 2.dp,
                blurRadius = 1.dp
            ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        ),

        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)

    ){
        Column {
            Box(
                modifier = Modifier.fillMaxWidth().height(92.dp)
            ){

                ImageLoader(url = excursion.coverUrl)

                //Todo(Сделать чек наличия статуса от пользователя к экскурсии)
                StatusBadge(
                    status = "Пройдено", //Todo(Добавить нормальный статус),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                )
                IconButton(
                    onClick = onFavoriteClick,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                ){
                    Surface (
                        modifier = modifier,
                        color = statusColor, // Полупрозрачный фон
                        shape = RoundedCornerShape(8.dp)
                    ){
                        Icon(
                            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "В избранное",
                            tint = if (isFavorite) Color.Red else Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.SpaceBetween

            ) {
                Text(
                    text = excursion.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row (
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp), // Расстояние между элементами

                ){
                    InfoItem(iconId = R.drawable.ic_clock, text = "${excursion.duration} мин")
                    InfoItem(iconId = R.drawable.ic_path, text = "${excursion.distance} км")
                }
            }
        }
    }

}
@Composable
fun InfoItem(
    iconId: Int,
    text: String,
){
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            painter = painterResource(id = iconId),
            contentDescription = null,
            modifier = Modifier.size(13.dp),
            tint = MaterialTheme.colorScheme.onSecondaryContainer // Красим иконку в основной цвет темы
        )
        Text(
            text = text,
            modifier = Modifier.padding(start = 4.dp),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
@Composable
fun ImageLoader(url: String?){
    AsyncImage(
        model = url,
        contentDescription = "Фото экскурсии",
        placeholder = painterResource(R.drawable.img_mock),
        error = painterResource(R.drawable.img_mock_error),
        modifier = Modifier
            .fillMaxWidth()
            .height(92.dp)
            .clip(RoundedCornerShape(8.dp, 8.dp)),
        contentScale = ContentScale.Crop
    )
}

@Composable
fun StatusBadge(status: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        color = statusColor, // Полупрозрачный фон
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = status,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = Color.White // Текстовый цвет
        )
    }
}