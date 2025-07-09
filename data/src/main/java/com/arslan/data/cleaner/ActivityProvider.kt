package com.arslan.data.cleaner

import android.app.Activity

/**
 * Interface to provide access to the current Activity
 * This allows dependency injection of Activity without tight coupling
 */
interface ActivityProvider {
    /**
     * Get the current Activity
     * @return The current Activity or null if no Activity is available
     */
    fun getCurrentActivity(): Activity?
} 