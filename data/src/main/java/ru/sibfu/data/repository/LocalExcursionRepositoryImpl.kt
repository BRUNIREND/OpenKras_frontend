package ru.sibfu.data.repository

import android.content.Context
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import dagger.hilt.android.qualifiers.ApplicationContext
import ru.sibfu.data.repository.source.local.dao.ExcursionDao
import ru.sibfu.data.repository.source.local.entity.ExcursionLocalEntity
import ru.sibfu.data.repository.source.local.entity.PointLocalEntity
import ru.sibfu.data.repository.source.local.worker.DownloadMediaWorker
import ru.sibfu.domain.ExcursionDetailModel
import ru.sibfu.domain.interfaces.ILocalExcursionRepository
import javax.inject.Inject

class LocalExcursionRepositoryImpl @Inject constructor(
    private val excursionDao: ExcursionDao,
    @ApplicationContext private val context: Context
) : ILocalExcursionRepository {

    override suspend fun saveExcursion(excursion: ExcursionDetailModel) {
        // 1. Маппим во фронт-сущность
        val excursionEntity = ExcursionLocalEntity(
            id = excursion.id,
            title = excursion.title,
            description = excursion.description,
            duration = excursion.duration,
            distance = excursion.distance,
            coverUrl = excursion.coverUrl
        )

        // 2. Маппим список точек
        val pointEntities = excursion.points.map { point ->
            PointLocalEntity(
                excursionId = excursion.id,
                pointId = point.id,
                name = point.name,
                address = point.address,
                latitude = point.latitude,
                longitude = point.longitude,
                description = point.description,
                position = point.position,
                audioUrl = point.audioUrl.firstOrNull() ?: ""
            )
        }

        // 3. Сохраняем в БД через транзакцию
        excursionDao.insertExcursionWithPoints(excursionEntity, pointEntities)

        val mediaUrls = (excursion.images + excursion.points.flatMap { it.audioUrl }).filterNotNull()

        val downloadRequest = OneTimeWorkRequestBuilder<DownloadMediaWorker>()
            .setInputData(
                workDataOf(
                    "excursion_id" to excursion.id,
                    "urls" to mediaUrls.toTypedArray()
                )
            )
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED) // Качать только с инетом
                    .build()
            )
            .build()

        WorkManager.getInstance(context).enqueue(downloadRequest)
    }

    override suspend fun isExcursionSaved(id: Int): Boolean {
        return excursionDao.exists(id)
    }

    override suspend fun deleteExcursion(id: Int) {
        excursionDao.deleteById(id)
    }
}