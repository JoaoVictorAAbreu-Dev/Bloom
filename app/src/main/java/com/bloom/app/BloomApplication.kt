package com.bloom.app

import android.app.Application

class BloomApplication : Application() {
    val container by lazy { BloomAppContainer(this) }
}
