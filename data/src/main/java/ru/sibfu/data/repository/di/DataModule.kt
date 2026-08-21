package ru.sibfu.data.repository.di

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.sibfu.data.repository.AuthRepositoryImpl
import ru.sibfu.data.repository.CategoryRepositoryImpl
import ru.sibfu.data.repository.ExcursionRepositoryImpl
import ru.sibfu.data.repository.LocalExcursionRepositoryImpl
import ru.sibfu.data.repository.core.AuthNetwork
import ru.sibfu.data.repository.core.MainNetwork
import ru.sibfu.data.repository.core.TokenManager
import ru.sibfu.data.repository.source.local.dao.ExcursionDao
import ru.sibfu.data.repository.source.local.entity.ExcursionLocalEntity
import ru.sibfu.data.repository.source.local.entity.PointLocalEntity
import ru.sibfu.data.repository.source.remote.api.AuthApi
import ru.sibfu.data.repository.source.remote.api.MuseumApi
import ru.sibfu.data.repository.source.remote.interceptor.AuthInterceptor
import ru.sibfu.domain.interfaces.IAuthRepository
import ru.sibfu.domain.interfaces.ICategoryRepository
import ru.sibfu.domain.interfaces.IExcursionRepository
import ru.sibfu.domain.interfaces.ILocalExcursionRepository
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


    @Provides
    @Singleton
    fun provideAuthRepository(
        api: AuthApi,
        museumApi: MuseumApi,
        tokenManager: TokenManager
    ): IAuthRepository {
        return AuthRepositoryImpl(api, museumApi, tokenManager)
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

    @Provides
    @Singleton
    fun provideFusedLocationClient(
        @ApplicationContext context: Context
    ): FusedLocationProviderClient {
        return LocationServices.getFusedLocationProviderClient(context)
    }

    @Provides
    @Singleton
    fun bindLocalExcursionRepository(
        impl: LocalExcursionRepositoryImpl
    ): ILocalExcursionRepository {
        return impl
    }

    @Database(entities = [ExcursionLocalEntity::class, PointLocalEntity::class], version = 1)
    abstract class AppDatabase : RoomDatabase() {
        abstract fun excursionDao(): ExcursionDao
    }


    @Provides@Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "openkras_db"
        ).build()
    }

    @Provides
    fun provideExcursionDao(db: AppDatabase): ExcursionDao {
        return db.excursionDao()
    }
}