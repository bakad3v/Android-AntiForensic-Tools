package com.sonozaki.bedatastore.encryption

import androidx.annotation.GuardedBy

internal object EncryptionManagerFactory {
    private val lock = Any()

    @GuardedBy("lock")
    @Volatile
    private var INSTANCE: EncryptionManager? = null

    fun get(): EncryptionManager {
        return INSTANCE ?: synchronized(lock) {
            if (INSTANCE == null) {
                INSTANCE = EncryptionManagerImpl()
            }
            INSTANCE!!
        }
    }
 }