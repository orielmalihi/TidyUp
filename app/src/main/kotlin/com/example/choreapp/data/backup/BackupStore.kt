package com.example.choreapp.data.backup

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore

// The backup lives in Documents/TidyUp, outside the app's private storage, so it outlives an uninstall.
class BackupStore(context: Context) {
    private val resolver = context.contentResolver
    private val collection: Uri = MediaStore.Files.getContentUri("external")

    val isSupported = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

    private fun find(): Uri? {
        resolver.query(
            collection,
            arrayOf(MediaStore.Files.FileColumns._ID),
            "${MediaStore.Files.FileColumns.DISPLAY_NAME}=? AND ${MediaStore.Files.FileColumns.RELATIVE_PATH}=?",
            arrayOf(FILE_NAME, "$FOLDER/"),
            null
        )?.use { if (it.moveToFirst()) return ContentUris.withAppendedId(collection, it.getLong(0)) }
        return null
    }

    fun read(): String? = runCatching {
        val uri = find() ?: return null
        resolver.openInputStream(uri)?.use { it.readBytes().toString(Charsets.UTF_8) }
    }.getOrNull()

    fun write(text: String): Boolean = runCatching {
        val uri = find() ?: resolver.insert(collection, ContentValues().apply {
            put(MediaStore.Files.FileColumns.DISPLAY_NAME, FILE_NAME)
            put(MediaStore.Files.FileColumns.MIME_TYPE, "application/json")
            put(MediaStore.Files.FileColumns.RELATIVE_PATH, FOLDER)
        }) ?: return false
        resolver.openOutputStream(uri, "wt")?.use { it.write(text.toByteArray(Charsets.UTF_8)) } != null
    }.getOrDefault(false)

    private companion object {
        const val FILE_NAME = "tidyup-backup.json"
        const val FOLDER = "Documents/TidyUp"
    }
}