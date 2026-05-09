package ru.sibfu.data.repository.di

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
import ru.sibfu.domain.interfaces.ICategoryRepository
import ru.sibfu.domain.interfaces.IExcursionRepository
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


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

    @Provides
    @Singleton
    fun provideOkHttpClient(loggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            // Здесь же можно настроить тайм-ауты, если бэкенд в Docker долго "просыпается"
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {

        return Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8000/") // IP для эмулятора
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Singleton
    fun provideMuseumApi(retrofit: Retrofit): MuseumApi {
        return retrofit.create(MuseumApi::class.java)
    }

    @Provides
    @Singleton
    fun provideExcursionRepository(api: MuseumApi): IExcursionRepository {
        return ExcursionRepositoryImpl(api)
    }

    @Provides
    @Singleton
    fun provideCategoryRepository(api: MuseumApi): ICategoryRepository = CategoryRepositoryImpl(api)
}