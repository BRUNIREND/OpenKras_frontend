package ru.sibfu.data.repository.source.remote.interceptor

import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import ru.sibfu.data.repository.core.TokenManager
import javax.inject.Inject

class AuthInterceptor @Inject constructor(private val tokenManager: TokenManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()

        val token = runBlocking {
            tokenManager.getAccessToken()
        }
        android.util.Log.d("AUTH_DEBUG", "Токен из DataStore: '$token'")
        if (token != null) {
            request.header("Authorization", "Bearer $token")
        }

        return chain.proceed(request.build())
    }
}