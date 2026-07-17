package com.yfy.kmp.core.common

import android.app.Activity
import android.annotation.SuppressLint

// Not a leak: KmpApplication nulls this on pause. Held because RevenueCat, Credential Manager and AdMob
// all need a live Activity.
@SuppressLint("StaticFieldLeak")
public object CurrentActivityHolder {
    @Volatile
    public var activity: Activity? = null
}
