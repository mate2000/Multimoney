package com.multimoney.data.base

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import com.multimoney.data.util.GsonHelper
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import java.io.IOException

abstract class BasePreference(
    val context: Context,
    val preferenceKey: String,
    val gsonHelper: GsonHelper
) {


    protected fun <T : Any> getValue(key: Preferences.Key<T>, def: T) =
        context.dataStore.data.catch { exception ->
            if (exception is IOException) {
                emptyPreferences()
            } else throw exception
        }.map { it[key] ?: def }

    protected suspend fun <T : Any> putValue(key: Preferences.Key<T>, value: T) {
        context.dataStore.edit {
            it[key] = value
        }
    }

    protected suspend fun <T : Any> removeValue(key: Preferences.Key<T>) {
        context.dataStore.edit { it.remove(key) }
    }

    protected suspend inline fun <reified T : Any> getList(key: Preferences.Key<String>): List<T> {
        var list: List<T> = listOf()
        getValue(key, "").collect { listJson ->
            list = gsonHelper.convertToListData(listJson)
        }
        return list
    }

    protected suspend inline fun <reified T : Any> putList(
        key: Preferences.Key<String>,
        list: List<T>
    ) = putValue(key, gsonHelper.convertToString(list))

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

    companion object {
        private val Context.dataStore by preferencesDataStore("multimoney_preferences")
    }
}