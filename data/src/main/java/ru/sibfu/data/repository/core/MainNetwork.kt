package ru.sibfu.data.repository.core

import jakarta.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class MainNetwork // Для данных музея (с интерцептором)
