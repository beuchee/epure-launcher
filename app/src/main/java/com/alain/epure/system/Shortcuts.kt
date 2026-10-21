package com.alain.epure.system

import android.content.Intent
import android.provider.AlarmClock
import android.provider.MediaStore
import com.alain.epure.domain.Shortcut

// Default system action of each shortcut (F-2.2). Starting these implicit intents needs no permission.
object Shortcuts {

    fun defaultIntent(shortcut: Shortcut): Intent = when (shortcut) {
        Shortcut.CAMERA -> Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA)
        Shortcut.PHONE -> Intent(Intent.ACTION_DIAL)
        Shortcut.CLOCK -> Intent(AlarmClock.ACTION_SHOW_ALARMS)
    }
}
