package ru.sibfu.data.repository.source.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey


@Entity(
    tableName = "points",
    foreignKeys = [
        ForeignKey(
            entity = ExcursionLocalEntity::class,
            parentColumns = ["id"],
            childColumns = ["excursionId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class PointLocalEntity(
    @PrimaryKey(autoGenerate = true) val localId: Int = 0,
    val excursionId: Int,
    val pointId: Int,
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val description: String,
    val position: Int,
    val audioUrl: String // Сохраняем как строку (или используем TypeConverter для списка)
)