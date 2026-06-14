package ru.sibfu.openkras.features.authentification.signUp

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class UserRegistrationScreenTest {

    // 1. Главное правило для Compose-тестов. Оно инициализирует тестовый контур
    // и позволяет управлять жизненным циклом экрана.
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun initialScreenState_displaysEmptyFieldsAndActiveButton() {
        // Given - Создаем дефолтный пустой стейт
        val initialState = UserRegistrationState()

        // When - "Рендерим" наш глупой экран в тестовом окне
        composeTestRule.setContent {
            UserRegistrationScreenContent(
                state = initialState,
                onIntent = {}
            )
        }

        // Then - Проверяем, что поля ввода отображаются и они пустые по умолчанию
        composeTestRule.onNodeWithTag("NameInput")
            .assertIsDisplayed()
            .assertTextContains("") // Ожидаем пустую строку внутри

        composeTestRule.onNodeWithTag("EmailInput")
            .assertIsDisplayed()
            .assertTextContains("")

        // Проверяем, что кнопка регистрации на месте и доступна для клика
        composeTestRule.onNodeWithTag("RegisterButton")
            .assertIsDisplayed()
            .assertIsEnabled()
    }

    @Test
    fun whenStateIsLoading_registerButtonIsDisabled() {
        // Given - Имитируем состояние, когда запрос отправлен на бэк FastAPI
        val loadingState = UserRegistrationState(isLoading = true)

        // When
        composeTestRule.setContent {
            UserRegistrationScreenContent(
                state = loadingState,
                onIntent = {},
            )
        }

        // Then - Кнопка должна заблокироваться, чтобы юзер не нажал её повторно
        composeTestRule.onNodeWithTag("RegisterButton")
            .assertIsNotEnabled()
    }

    @Test
    fun whenStateHasError_errorMessageIsVisibleToUser() {
        val serverErrorMessage = "Пользователь с таким Email уже зарегистрирован"

        // Given - Моделируем ошибку, пришедшую от репозитория
        val errorState = UserRegistrationState(
            isLoading = false,
            error = serverErrorMessage
        )

        // When
        composeTestRule.setContent {
            UserRegistrationScreenContent(
                state = errorState,
                onIntent = {}
            )
        }

        // Then - Ищем компонент ошибки по тегу и проверяем его текст
        composeTestRule.onNodeWithTag("ErrorText")
            .assertIsDisplayed()
            .assertTextContains(serverErrorMessage)
    }

    @Test
    fun userTypingName_triggersCorrectNameChangeIntent() {
        // Given - Экран находится в исходном состоянии
        val testState = UserRegistrationState()
        var capturedIntent: UserRegistrationIntent? = null

        composeTestRule.setContent {
            UserRegistrationScreenContent(
                state = testState,
                onIntent = { intent ->
                    // Перехватываем интент, который выплюнет экран
                    capturedIntent = intent
                }
            )
        }

        // When - Имитируем реальный ввод текста пользователем в текстовое поле
        composeTestRule.onNodeWithTag("NameInput").performTextInput("Иван")

        // Then - Проверяем контракт UDF: UI не должен сам менять стейт,
        // он обязан отправить Intent.NameChange во внешнюю систему (ViewModel)
        assert(capturedIntent is UserRegistrationIntent.NameChange)
        assertEquals("Иван", (capturedIntent as UserRegistrationIntent.NameChange).name)
    }

    @Test
    fun clickingRegisterButton_triggersRegisterIntent() {
        // Given
        val testState = UserRegistrationState(
            name = "Иван",
            email = "ivan@sibfu.ru",
            password = "password123"
        )
        var isRegisterIntentSent = false

        composeTestRule.setContent {
            UserRegistrationScreenContent(
                state = testState,
                onIntent = { intent ->
                    if (intent is UserRegistrationIntent.Register) {
                        isRegisterIntentSent = true
                    }
                }
            )
        }

        // When - Пользователь кликает на кнопку "Зарегистрироваться"
        composeTestRule.onNodeWithTag("RegisterButton").performClick()

        // Then - Проверяем, что интент улетел наверх
        assert(isRegisterIntentSent)
    }
}