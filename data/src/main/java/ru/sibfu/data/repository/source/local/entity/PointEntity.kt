package ru.sibfu.data.repository.source.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "points")
data class PointEntity(
    @PrimaryKey val id: Int,
    val excursionId: Int,

)