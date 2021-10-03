package by.pzmandroid.mac.utils.extensions

import androidx.annotation.IdRes
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import timber.log.Timber

fun NavController.safelyNavigate(directions: NavDirections) {
    try {
        navigate(directions)
    } catch (ex: Exception) {
        Timber.e(ex)
    }
}

fun NavController.safelyNavigate(@IdRes actionId: Int) {
    try {
        navigate(actionId)
    } catch (ex: Exception) {
        Timber.e(ex)
    }
}