@file:JvmName("ThreadUtils")
package com.kotetsky.intermittenbaractivity

import android.os.Handler
import android.os.Looper

val mainHandler = Handler(Looper.getMainLooper())

inline fun doOnUi(crossinline job: () -> Unit) {
    mainHandler.post { job() }
}
