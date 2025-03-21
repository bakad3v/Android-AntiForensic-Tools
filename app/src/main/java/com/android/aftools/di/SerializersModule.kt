package com.android.aftools.di

import com.sonozaki.entities.ButtonSettings
import com.sonozaki.data.files.entities.FilesList
import com.sonozaki.data.logs.entities.LogList
import com.sonozaki.data.profiles.entities.IntList
import com.sonozaki.data.settings.entities.SettingsV1
import com.sonozaki.data.settings.entities.UsbSettingsV1
import com.sonozaki.encrypteddatastore.BaseSerializer
import com.sonozaki.encrypteddatastore.encryption.EncryptedSerializer
import com.sonozaki.encrypteddatastore.encryption.EncryptionAlias
import com.sonozaki.encrypteddatastore.encryption.EncryptionManager
import com.sonozaki.entities.BruteforceSettings
import com.sonozaki.entities.LogsData
import com.sonozaki.entities.Permissions
import com.sonozaki.entities.Settings
import com.sonozaki.entities.UsbSettings
import com.sonozaki.password.entities.PasswordStatus
import com.sonozaki.resources.IO_DISPATCHER
import com.sonozaki.root.entities.RootDomain
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

    @Provides
    @Singleton
    fun bindSettingsBaseSerializer(@Named(IO_DISPATCHER) ioDispatcher: CoroutineDispatcher) =
        BaseSerializer<Settings>(ioDispatcher, Settings.serializer(), Settings())

    @Provides
    @Singleton
    fun bindSettingsV1BaseSerializer(@Named(IO_DISPATCHER) ioDispatcher: CoroutineDispatcher) =
        BaseSerializer<SettingsV1>(ioDispatcher, SettingsV1.serializer(), SettingsV1())

    @Provides
    @Singleton
    fun bindBruteforceBaseSerializer(@Named(IO_DISPATCHER) ioDispatcher: CoroutineDispatcher) =
        BaseSerializer<BruteforceSettings>(ioDispatcher, BruteforceSettings.serializer(),
            BruteforceSettings())

    @Provides
    @Singleton
    fun bindButtonBaseSerializer(@Named(IO_DISPATCHER) ioDispatcher: CoroutineDispatcher) =
        BaseSerializer<ButtonSettings>(ioDispatcher, ButtonSettings.serializer(),
            ButtonSettings())

    @Provides
    @Singleton
    fun bindFilesListBaseSerializer(@Named(IO_DISPATCHER) ioDispatcher: CoroutineDispatcher): BaseSerializer<FilesList> =
        BaseSerializer<FilesList>(ioDispatcher, FilesList.serializer(),
            FilesList())

    @Provides
    @Singleton
    fun bindLogsDataBaseSerializer(@Named(IO_DISPATCHER) ioDispatcher: CoroutineDispatcher) =
        BaseSerializer<LogsData>(ioDispatcher, LogsData.serializer(),
            LogsData())

    @Provides
    @Singleton
    fun bindLogListBaseSerializer(@Named(IO_DISPATCHER) ioDispatcher: CoroutineDispatcher) =
        BaseSerializer<LogList>(ioDispatcher, LogList.serializer(),
            LogList())

    @Provides
    @Singleton
    fun bindPasswordStatusBaseSerializer(@Named(IO_DISPATCHER) ioDispatcher: CoroutineDispatcher) =
        BaseSerializer<PasswordStatus>(ioDispatcher, PasswordStatus.serializer(),
            PasswordStatus())

    @Provides
    @Singleton
    fun bindPermissionsBaseSerializer(@Named(IO_DISPATCHER) ioDispatcher: CoroutineDispatcher) =
        BaseSerializer<Permissions>(ioDispatcher, Permissions.serializer(),
            Permissions())

    @Provides
    @Singleton
    fun bindProfilesBaseSerializer(@Named(IO_DISPATCHER) ioDispatcher: CoroutineDispatcher) =
        BaseSerializer<IntList>(ioDispatcher, IntList.serializer(),
            IntList())

    @Provides
    @Singleton
    fun bindRootBaseSerializer(@Named(IO_DISPATCHER) ioDispatcher: CoroutineDispatcher) =
        BaseSerializer<RootDomain>(ioDispatcher, RootDomain.serializer(),
            RootDomain())

    @Provides
    @Singleton
    fun bindUSBBaseSerializer(@Named(IO_DISPATCHER) ioDispatcher: CoroutineDispatcher) =
        BaseSerializer<UsbSettings>(ioDispatcher, UsbSettings.serializer(),
            UsbSettings.DO_NOTHING)

    @Provides
    @Singleton
    fun bindUSBV1BaseSerializer(@Named(IO_DISPATCHER) ioDispatcher: CoroutineDispatcher) =
        BaseSerializer<UsbSettingsV1>(ioDispatcher, UsbSettingsV1.serializer(),
            UsbSettingsV1())
}