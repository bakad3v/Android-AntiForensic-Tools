package com.android.aftools.di

import com.android.aftools.domain.entities.ButtonSettings
import com.sonozaki.encrypteddatastore.encryption.EncryptedSerializer
import com.sonozaki.encrypteddatastore.encryption.EncryptionAlias
import com.sonozaki.encrypteddatastore.encryption.EncryptionManager
import com.sonozaki.entities.BruteforceSettings
import com.sonozaki.entities.LogsData
import com.sonozaki.entities.Permissions
import com.sonozaki.entities.Settings
import com.sonozaki.entities.UsbSettings
import com.sonozaki.files.entities.FilesList
import com.sonozaki.logs.entities.LogList
import com.sonozaki.password.entities.PasswordStatus
import com.sonozaki.profiles.entities.IntList
import com.sonozaki.resources.IO_DISPATCHER
import com.sonozaki.root.entities.RootDomain
import com.sonozaki.settings.entities.SettingsV1
import com.sonozaki.settings.entities.UsbSettingsV1
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class SerializersModule {
    @Provides
    @Singleton
    fun bindBruteforceSettingsSerializer(encryptionManager: EncryptionManager, @Named(IO_DISPATCHER) ioDispatcher: CoroutineDispatcher): EncryptedSerializer<BruteforceSettings> =
        EncryptedSerializer(
            encryptionManager,
            ioDispatcher,
            BruteforceSettings.serializer(),
            BruteforceSettings()
        )

    @Provides
    @Singleton
    fun bindButtonSettingsSerializer(encryptionManager: EncryptionManager, @Named(IO_DISPATCHER) ioDispatcher: CoroutineDispatcher): EncryptedSerializer<ButtonSettings> =
        EncryptedSerializer(
            encryptionManager,
            ioDispatcher,
            ButtonSettings.serializer(),
            ButtonSettings()
        )

    @Provides
    @Singleton
    fun bindFilesListSerializer(encryptionManager: EncryptionManager, @Named(IO_DISPATCHER) ioDispatcher: CoroutineDispatcher): EncryptedSerializer<FilesList> =
        EncryptedSerializer(
            encryptionManager,
            ioDispatcher,
            FilesList.serializer(),
            FilesList()
        )

    @Provides
    @Singleton
    fun bindLogsDataSerializer(encryptionManager: EncryptionManager, @Named(IO_DISPATCHER) ioDispatcher: CoroutineDispatcher): EncryptedSerializer<LogsData> =
        EncryptedSerializer(
            encryptionManager,
            ioDispatcher,
            LogsData.serializer(),
            LogsData()
        )

    @Provides
    @Singleton
    fun bindLogsSerializer(encryptionManager: EncryptionManager, @Named(IO_DISPATCHER) ioDispatcher: CoroutineDispatcher): EncryptedSerializer<LogList> =
        EncryptedSerializer(
            encryptionManager,
            ioDispatcher,
            LogList.serializer(),
            LogList()
        )

    @Provides
    @Singleton
    fun bindPasswordStatusSerializer(encryptionManager: EncryptionManager, @Named(IO_DISPATCHER) ioDispatcher: CoroutineDispatcher): EncryptedSerializer<PasswordStatus> =
        EncryptedSerializer(
            encryptionManager,
            ioDispatcher,
            PasswordStatus.serializer(),
            PasswordStatus(),
            EncryptionAlias.PASSWORD
        )

    @Provides
    @Singleton
    fun bindPermissionsSerializer(encryptionManager: EncryptionManager, @Named(IO_DISPATCHER) ioDispatcher: CoroutineDispatcher): EncryptedSerializer<Permissions> =
        EncryptedSerializer(
            encryptionManager,
            ioDispatcher,
            Permissions.serializer(),
            Permissions()
        )

    @Provides
    @Singleton
    fun bindProfilesSerializer(encryptionManager: EncryptionManager, @Named(IO_DISPATCHER) ioDispatcher: CoroutineDispatcher): EncryptedSerializer<IntList> =
        EncryptedSerializer(
            encryptionManager,
            ioDispatcher,
            IntList.serializer(),
            IntList()
        )

    @Provides
    @Singleton
    fun bindRootSerializer(encryptionManager: EncryptionManager, @Named(IO_DISPATCHER) ioDispatcher: CoroutineDispatcher): EncryptedSerializer<RootDomain> =
        EncryptedSerializer(
            encryptionManager,
            ioDispatcher,
            RootDomain.serializer(),
            RootDomain()
        )

    @Provides
    @Singleton
    fun bindSettingsSerializer(encryptionManager: EncryptionManager, @Named(IO_DISPATCHER) ioDispatcher: CoroutineDispatcher): EncryptedSerializer<Settings> =
        EncryptedSerializer(
            encryptionManager,
            ioDispatcher,
            Settings.serializer(),
            Settings()
        )

    @Provides
    @Singleton
    fun bindSettingsV1Serializer(encryptionManager: EncryptionManager, @Named(IO_DISPATCHER) ioDispatcher: CoroutineDispatcher): EncryptedSerializer<SettingsV1> =
        EncryptedSerializer(
            encryptionManager,
            ioDispatcher,
            SettingsV1.serializer(),
            SettingsV1()
        )

    @Provides
    @Singleton
    fun bindUSBSerializer(encryptionManager: EncryptionManager, @Named(IO_DISPATCHER) ioDispatcher: CoroutineDispatcher): EncryptedSerializer<UsbSettings> =
        EncryptedSerializer(
            encryptionManager,
            ioDispatcher,
            UsbSettings.serializer(),
            UsbSettings.DO_NOTHING
        )

    @Provides
    @Singleton
    fun bindUSBV1Serializer(encryptionManager: EncryptionManager, @Named(IO_DISPATCHER) ioDispatcher: CoroutineDispatcher): EncryptedSerializer<UsbSettingsV1> =
        EncryptedSerializer(
            encryptionManager,
            ioDispatcher,
            UsbSettingsV1.serializer(),
            UsbSettingsV1()
        )
}