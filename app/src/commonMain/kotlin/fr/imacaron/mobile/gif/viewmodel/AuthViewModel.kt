package fr.imacaron.mobile.gif.viewmodel

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import fr.imacaron.mobile.gif.TOKEN
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

abstract class AuthViewModel(private val pref: DataStore<Preferences>): ViewModel() {
	protected suspend fun getToken(): String? {
		return pref.data.map {
			it[TOKEN]?.toString()
		}.firstOrNull()
	}
}