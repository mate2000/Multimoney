package com.multimoney.data.base

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import com.multimoney.data.BuildConfig.DATA_STORE_KEY
import com.multimoney.data.util.GsonHelper
import com.multimoney.data.util.cryptography.CiphertextWrapper
import com.multimoney.data.util.cryptography.CryptographyHelper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.IOException
import java.lang.reflect.Type
import javax.crypto.Cipher

abstract class BaseDataStorePreferences(
    val dataStore: DataStore<Preferences>,
    private val cryptographyHelper: CryptographyHelper,
    val gsonHelper: GsonHelper
) {

    private val json = Json { encodeDefaults = true }

    protected fun <T : Any> getData(key: Preferences.Key<T>, def: T) =
        dataStore.data.catch { exception ->
            if (exception is IOException) {
                emptyPreferences()
            } else throw exception
        }.map { it[key] ?: def }

    protected suspend fun <T : Any> setData(key: Preferences.Key<T>, value: T) {
        dataStore.edit { it[key] = value }
    }

    protected inline fun <reified T : Any?> getListFlow(key: Preferences.Key<String>, type: Type) =
        dataStore.data.catch { exception ->
            if (exception is IOException) {
                emptyPreferences()
            } else throw exception
        }.map {
            gsonHelper.convertToListData<T>(it[key] ?: "", type)
        }

    protected suspend inline fun <reified T : Any?> putListFlow(
        key: Preferences.Key<String>,
        list: List<T>
    ) = setData(key, gsonHelper.convertToString(list))

    protected fun getVector(key: Preferences.Key<String>, def: ByteArray) =
        dataStore.data.catch { exception ->
            if (exception is IOException) {
                emptyPreferences()
            } else throw exception
        }.map {
            it[key]?.let { ciphertextWrapper ->
                gsonHelper.convertToData(
                    ciphertextWrapper,
                    CiphertextWrapper::class.java
                ).initializationVector
            } ?: run { def }
        }

    protected suspend fun <T : Any> removeData(key: Preferences.Key<T>) {
        dataStore.edit { it.remove(key) }
    }

    protected fun getSecuredData(
        key: Preferences.Key<String>,
        def: String
    ) = dataStore.data.catch { exception ->
        if (exception is IOException) {
            emptyPreferences()
        } else throw exception
    }.secureMap<String> {
        it[key] ?: def
    }

    protected suspend fun setSecuredData(key: Preferences.Key<String>, value: String) {
        dataStore.secureEdit(value) { prefs, encryptedValue ->
            prefs[key] = encryptedValue
        }
    }

    protected fun getSecuredData(
        key: Preferences.Key<String>,
        def: String,
        cipher: Cipher
    ) =
        dataStore.data.catch { exception ->
            if (exception is IOException) {
                emptyPreferences()
            } else throw exception
        }.secureMap<String>({ it[key] ?: def }, cipher)

    protected suspend fun setSecuredData(
        key: Preferences.Key<String>,
        value: String,
        cipher: Cipher
    ) {
        dataStore.secureEdit<String>(value, cipher) { prefs, encryptedValue ->
            prefs[key] = encryptedValue
        }
    }

    protected suspend inline fun <reified T : Any> getList(key: Preferences.Key<String>): List<T> {
        var list: List<T> = listOf()
        getData(key, "").collect { listJson ->
            // list = gsonHelper.convertToListData(listJson)
        }
        return list
    }

    protected suspend inline fun <reified T : Any> putList(
        key: Preferences.Key<String>,
        list: List<T>
    ) = setData(key, gsonHelper.convertToString(list))

    protected suspend inline fun <reified T : Any> addItemToList(
        key: Preferences.Key<String>,
        item: T
    ) {
        val savedList: MutableList<T> = getList<T>(key).toMutableList()
        savedList.add(item)
        putList(key, savedList.toList())
    }

    protected suspend inline fun <reified T : Any> removeItemFromList(
        key: Preferences.Key<String>,
        item: T
    ) {
        val savedList: MutableList<T> = getList<T>(key).toMutableList()
        savedList.remove(item)
        putList(key, savedList.toList())
    }

    /**
     * serializes data type into string
     * performs encryption
     * stores encrypted data in DataStore
     */
    private suspend inline fun <reified T> DataStore<Preferences>.secureEdit(
        value: T,
        crossinline editStore: (MutablePreferences, String) -> Unit
    ) {
        edit {
            val ciphertextWrapper = cryptographyHelper.encryptData(
                DATA_STORE_KEY,
                Json.encodeToString(value)
            )

            editStore.invoke(it, gsonHelper.convertToString(ciphertextWrapper))
        }
    }

    /**
     * serializes data type into string
     * performs encryption
     * stores encrypted data in DataStore
     * for authentication required
     */
    private suspend inline fun <reified T> DataStore<Preferences>.secureEdit(
        value: String,
        cipher: Cipher,
        crossinline editStore: (MutablePreferences, String) -> Unit
    ) {
        edit {
            val ciphertextWrapper =
                cryptographyHelper.encryptData(Json.encodeToString(value), cipher)
            editStore.invoke(it, gsonHelper.convertToString(ciphertextWrapper))
        }
    }

    /**
     * fetches encrypted data from DataStore
     * performs decryption
     * deserializes data into respective data type
     */
    private inline fun <reified T> Flow<Preferences>.secureMap(
        crossinline fetchValue: (value: Preferences) -> String
    ): Flow<T> {
        return map { value ->
            if (fetchValue(value).isNotEmpty()) {
                val ciphertextWrapper =
                    gsonHelper.convertToData(
                        fetchValue(value),
                        CiphertextWrapper::class.java
                    )

                val decryptedValue = cryptographyHelper.decryptData(
                    DATA_STORE_KEY,
                    ciphertextWrapper.ciphertext,
                    ciphertextWrapper.initializationVector
                )
                json.decodeFromString(decryptedValue)
            } else {
                fetchValue(value)
            } as T
        }
    }

    /**
     * fetches encrypted data from DataStore
     * performs decryption
     * deserializes data into respective data type
     * for authentication required
     */
    private inline fun <reified T> Flow<Preferences>.secureMap(
        crossinline fetchValue: (value: Preferences) -> String,
        cipher: Cipher
    ): Flow<T> {
        return map { value ->
            if (fetchValue(value).isNotEmpty()) {
                val ciphertextWrapper =
                    gsonHelper.convertToData(fetchValue(value), CiphertextWrapper::class.java)
                val decryptedValue =
                    cryptographyHelper.decryptData(ciphertextWrapper.ciphertext, cipher)
                json.decodeFromString(decryptedValue)
            } else {
                fetchValue(value)
            } as T
        }
    }
}
