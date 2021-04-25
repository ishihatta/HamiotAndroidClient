package com.ishihata_tech.hamiot_client.usecase

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class HiltClassModule {
    @Binds
    abstract fun bindGetUserKeyPair(
        getUserKeyPairImpl: GetUserKeyPairImpl
    ): GetUserKeyPair

    @Binds
    abstract fun bindCreateUserKeyPair(
        createUserKeyPairImpl: CreateUserKeyPairImpl
    ): CreateUserKeyPair

    @Binds
    abstract fun bindStoreUserKeyPair(
        storeUserKeyPairImpl: StoreUserKeyPairImpl
    ): StoreUserKeyPair
}