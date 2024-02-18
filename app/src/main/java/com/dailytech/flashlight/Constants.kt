package com.dailytech.flashlight

import android.content.Context

fun getPlayStoreDeepLink(context: Context): String {
    return "https://play.google.com/store/apps/details?id=${context.packageName}"
}