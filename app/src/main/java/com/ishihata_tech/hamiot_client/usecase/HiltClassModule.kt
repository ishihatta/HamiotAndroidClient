package com.ishihata_tech.hamiot_client.usecase

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import iroha.protocol.Commands

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

    @Binds
    abstract fun bindCreateNewAccount(
        createNewAccountImpl: CreateNewAccountImpl
    ): CreateNewAccount

    @Binds
    abstract fun bindGetBalance(
        getBalanceImpl: GetBalanceImpl
    ): GetBalance

    @Binds
    abstract fun bindGetDisplayName(
        getDisplayNameImpl: GetDisplayNameImpl
    ): GetDisplayName

    @Binds
    abstract fun bindTransferAsset(
        transferAssetImpl: TransferAssetImpl
    ): TransferAsset

    @Binds
    abstract fun bindTransferAssetOnServer(
            transferAssetOnServerImpl: TransferAssetOnServerImpl
    ): TransferAssetOnServer

    @Binds
    abstract fun bindGetFcmToken(
        getFcmTokenImpl: GetFcmTokenImpl
    ): GetFcmToken

    @Binds
    abstract fun bindBackupAccount(
            backupAccountImpl: BackupAccountImpl
    ): BackupAccount

    @Binds
    abstract fun bindRestoreAccount(
            restoreAccountImpl: RestoreAccountImpl
    ): RestoreAccount

    @Binds
    abstract fun bindSetIrohaAccountDetail(
            setIrohaAccountDetailImpl: SetIrohaAccountDetailImpl
    ): SetIrohaAccountDetail

    @Binds
    abstract fun bindGetIrohaAccountDetail(
            getIrohaAccountDetailImpl: GetIrohaAccountDetailImpl
    ): GetIrohaAccountDetail

    @Binds
    abstract fun bindGetAssetTransactions(
            getAssetTransactionsImpl: GetAssetTransactionsImpl
    ): GetAssetTransactions
}