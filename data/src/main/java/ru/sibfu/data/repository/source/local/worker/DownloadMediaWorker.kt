package ru.sibfu.data.repository.source.local.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.io.File
import java.net.URL

@HiltWorker
class DownloadMediaWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val excursionId = inputData.getInt("excursion_id", -1)
        val urls = inputData.getStringArray("urls") ?: return Result.failure()

        return try {
            urls.forEach { urlString ->
                downloadFile(urlString, excursionId)
            }
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    private fun downloadFile(urlString: String, excursionId: Int) {
        val url = URL(urlString)
        val fileName = urlString.substringAfterLast("/")

        val directory = File(applicationContext.filesDir, "media/$excursionId")
        if (!directory.exists()) directory.mkdirs()

        val file = File(directory, fileName)
        if (file.exists()) return

        url.openStream().use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }
    }
}
