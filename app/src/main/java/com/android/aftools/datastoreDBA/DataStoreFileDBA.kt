package com.android.aftools.datastoreDBA

import android.content.Context
import java.io.File

fun Context.dataStoreFileDBA(fileName: String): File {
    val deviceContext = createDeviceProtectedStorageContext()
    return File(deviceContext.filesDir, "datastore/$fileName")
}