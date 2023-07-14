package sem.ua.androidtest.data

import android.content.Context
import androidx.core.content.edit
import sem.ua.androidtest.model.User

class UserData(private val context: Context) {

    companion object {
        private const val PREFS_NAME = "USER_DATA"
        private const val KEY_EMAIL = "email"
        private const val KEY_BALANCE = "balance"
        private const val DEFAULT_BALANCE = -1
    }

    private val sharedPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    var currentUser: User? = null

    fun saveUser(user: User) {
        currentUser = user
        sharedPrefs.edit {
            putString(KEY_EMAIL, user.email)
            putInt(KEY_BALANCE, user.balance)
        }
    }

    fun exitAccount() {
        sharedPrefs.edit().clear().apply()
        currentUser = null
    }

    fun restoreUser(): User? {
        if (currentUser != null) {
            return currentUser
        }
        val email = sharedPrefs.getString(KEY_EMAIL, null)
        val balance = sharedPrefs.getInt(KEY_BALANCE, DEFAULT_BALANCE)
        return if (!email.isNullOrBlank()) {
            currentUser = User(email, balance)
            currentUser
        } else {
            null
        }
    }
}
