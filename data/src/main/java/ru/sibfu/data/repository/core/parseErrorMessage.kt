package ru.sibfu.data.repository.core

import org.json.JSONObject


internal fun parseErrorMessage(jsonString: String?): String? {
    if (jsonString.isNullOrBlank()) return null
    return try {
        val jsonObject = JSONObject(jsonString)
        jsonObject.getString("detail") // Достаем текст ошибки от бэка
    } catch (e: Exception) {
        null
    }
}