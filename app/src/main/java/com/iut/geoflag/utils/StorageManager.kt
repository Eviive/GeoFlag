package com.iut.geoflag.utils

import android.content.Context
import android.util.Log
import java.io.File
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

class StorageManager {
    companion object  {

        fun <T> save(context: Context, key: String, values: T) : Boolean {

            val fileOutput = context.openFileOutput(key, Context.MODE_PRIVATE)

            return try {
                val data = ObjectOutputStream(fileOutput)
                data.writeObject(values)
                fileOutput.close()
                data.close()

                true
            } catch (e: Exception) {
                Log.e("StorageManager", "Error while saving data", e)
                false
            }
        }

        fun <T> load(context: Context, key: String) : T?  {

            return try {

                val file = File(key)
                if (!file.exists()){
                    return null
                }

                val fileInput = context.openFileInput(file.name)

                val data = ObjectInputStream(fileInput)
                val values = data.readObject() as T
                fileInput.close()
                data.close()
                values
            } catch (e: Exception) {
                Log.e("StorageManager", "Error while loading data", e)
                null
            }

        }
    }
}