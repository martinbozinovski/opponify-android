package com.opponify.auth.di

import com.opponify.auth.data.FirebaseAuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.opponify.auth.domain.AuthRepository
import dagger.Provides
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideAuthRepository(repository: FirebaseAuthRepository): AuthRepository = repository
}
