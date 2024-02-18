package com.dailytech.flashlight

import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings

object RemoteConfig {


    private fun getRemoteConfig() = Firebase.remoteConfig

    fun initialize() {
        val remoteConfig = Firebase.remoteConfig
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
        val cacheTime: Long = if (BuildConfig.DEBUG) 0 else 5 * 60 * 60

        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = cacheTime
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.fetchAndActivate()
    }

    fun isAdsEnabled() = getRemoteConfig().getBoolean("is_ads_enabled")

}