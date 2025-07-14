package com.arslan.data.cleaner

import android.app.Activity
import android.app.Application
import android.os.Bundle
import javax.inject.Inject
import javax.inject.Singleton
import java.lang.ref.WeakReference

@Singleton
class ActivityProviderImpl @Inject constructor(
    application: Application
) : ActivityProvider, Application.ActivityLifecycleCallbacks {

    private var currentActivityRef: WeakReference<Activity>? = null

    init {
        application.registerActivityLifecycleCallbacks(this)
    }

    override fun getCurrentActivity(): Activity? {
        return currentActivityRef?.get()
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        currentActivityRef = WeakReference(activity)
    }

    override fun onActivityStarted(activity: Activity) {
        currentActivityRef = WeakReference(activity)
    }

    override fun onActivityResumed(activity: Activity) {
        currentActivityRef = WeakReference(activity)
    }

    override fun onActivityPaused(activity: Activity) {
        // Keep the reference during pause
    }

    override fun onActivityStopped(activity: Activity) {
        // Keep the reference during stop
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        // No action needed
    }

    override fun onActivityDestroyed(activity: Activity) {
        // Clear reference if this is the current activity
        if (currentActivityRef?.get() == activity) {
            currentActivityRef = null
        }
    }
} 