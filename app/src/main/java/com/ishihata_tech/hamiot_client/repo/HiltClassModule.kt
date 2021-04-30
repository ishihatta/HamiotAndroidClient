package com.ishihata_tech.hamiot_client.repo

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class HiltClassModule {
    @Binds
    abstract fun bindUserAccountRepository(
        userAccountRepositoryImpl: UserAccountRepositoryImpl
    ): UserAccountRepository

    @Binds
    abstract fun bindFirebaseFunctionsRepository(
        firebaseFunctionsRepositoryImpl: FirebaseFunctionsRepositoryImpl
    ): FirebaseFunctionsRepository
}