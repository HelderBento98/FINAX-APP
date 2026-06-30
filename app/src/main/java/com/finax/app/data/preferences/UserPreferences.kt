package com.finax.app.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.finax.app.data.model.UserProfile
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

class UserPreferences(private val context: Context) {

    private val gson = Gson()

    companion object {
        val USER_PROFILE_KEY = stringPreferencesKey("user_profile")
    }

    val userProfileFlow: Flow<UserProfile> = context.dataStore.data.map { prefs ->
        val json = prefs[USER_PROFILE_KEY] ?: return@map UserProfile()
        try {
            gson.fromJson(json, UserProfile::class.java) ?: UserProfile()
        } catch (e: Exception) {
            UserProfile()
        }
    }

    suspend fun updateUserProfile(profile: UserProfile) {
        context.dataStore.edit { prefs ->
            prefs[USER_PROFILE_KEY] = gson.toJson(profile)
        }
    }
}
