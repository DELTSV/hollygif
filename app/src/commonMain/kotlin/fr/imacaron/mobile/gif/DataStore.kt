package fr.imacaron.mobile.gif

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.internal.SynchronizedObject
import kotlinx.coroutines.internal.synchronized
import okio.Path.Companion.toPath

val TOKEN = stringPreferencesKey("token")

expect fun createDataStore(): DataStore<Preferences>

internal const val dataStoreFileName = "settings.preferences_pb"

private lateinit var dataStore: DataStore<Preferences>
@OptIn(InternalCoroutinesApi::class)
private val lock = SynchronizedObject()

@OptIn(InternalCoroutinesApi::class)
fun getDataStore(producePath: () -> String): DataStore<Preferences> =
	synchronized(lock) {
		if (::dataStore.isInitialized) {
			dataStore
		} else {
			PreferenceDataStoreFactory.createWithPath(produceFile = { producePath().toPath() })
				.also { dataStore = it }
		}
	}