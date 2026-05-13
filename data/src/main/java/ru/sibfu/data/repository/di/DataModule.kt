package ru.sibfu.data.repository.di

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.sibfu.data.repository.CategoryRepositoryImpl
import ru.sibfu.data.repository.ExcursionRepositoryImpl
import ru.sibfu.data.repository.source.remote.api.MuseumApi
import ru.sibfu.data.repository.source.remote.interceptor.AuthInterceptor
import ru.sibfu.domain.interfaces.ICategoryRepository
import ru.sibfu.domain.interfaces.IExcursionRepository
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import ru.sibfu.data.repository.AuthRepositoryImpl
import ru.sibfu.data.repository.core.AuthNetwork
import ru.sibfu.data.repository.core.MainNetwork
import ru.sibfu.data.repository.core.TokenManager
import ru.sibfu.data.repository.source.remote.api.AuthApi
import ru.sibfu.domain.interfaces.IAuthRepository


@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    // 2. OkHttpClient для обычных запросов (БЕЗ AuthInterceptor)
    @Provides
    @Singleton
    @MainNetwork
    fun providePublicOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    // 3. OkHttpClient для защищенных запросов (С AuthInterceptor)
    @Provides
    @Singleton
    @AuthNetwork
    fun provideAuthenticatedOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        authInterceptor: AuthInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    // 4. Retrofit для авторизации
    @Provides
    @Singleton
    @MainNetwork
    fun provideAuthRetrofit(@MainNetwork okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8000/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

    // 5. Retrofit для данных музея
    @Provides
    @Singleton
    @AuthNetwork
    fun provideMainRetrofit(@AuthNetwork okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8000/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

    // 6. Создание API
    @Provides
    @Singleton
    fun provideAuthApi(@MainNetwork retrofit: Retrofit): AuthApi {
        return retrofit.create(AuthApi::class.java)
    }

    @Provides
    @Singleton
    fun provideMuseumApi(@AuthNetwork retrofit: Retrofit): MuseumApi {
        return retrofit.create(MuseumApi::class.java)
    }

    // 7. Репозитории
    @Provides
    @Singleton
    fun provideAuthRepository(
        api: AuthApi,
        tokenManager: TokenManager
    ): IAuthRepository {
        return AuthRepositoryImpl(api, tokenManager)
    }

    @Provides
    @Singleton
    fun provideExcursionRepository(api: MuseumApi): IExcursionRepository {
        return ExcursionRepositoryImpl(api)
    }

    @Provides
    @Singleton
    fun provideCategoryRepository(api: MuseumApi): ICategoryRepository {
        return CategoryRepositoryImpl(api)
    }
}