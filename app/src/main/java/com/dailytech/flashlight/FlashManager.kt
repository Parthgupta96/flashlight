package com.dailytech.flashlight

import android.content.Context
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object FlashManager {

    private const val MORSE_DOT = 300L
    private const val MORSE_OFF = 300L
    private const val MORSE_DASH = 900L

    private var cameraManager: CameraManager? = null
    private var cameraId = ""

    private val SOSsleepTime = listOf(
        MORSE_DOT, MORSE_OFF, MORSE_DOT, MORSE_OFF, MORSE_DOT, MORSE_OFF,
        MORSE_DASH, MORSE_OFF, MORSE_DASH, MORSE_OFF, MORSE_DASH, MORSE_OFF,
        MORSE_DOT, MORSE_OFF, MORSE_DOT, MORSE_OFF, MORSE_DOT, 3 * MORSE_OFF,
    )
    val throbbingList: List<ThrobClass> = listOf(
        ThrobClass("SOS", SOSsleepTime, false),
        ThrobClass("1", emptyList(), true),
        ThrobClass("2", listOf(400, 400), false),
        ThrobClass("3", listOf(300, 300), false),
        ThrobClass("4", listOf(250, 250), false),
        ThrobClass("5", listOf(200, 200), false),
        ThrobClass("6", listOf(150, 150), false),
        ThrobClass("7", listOf(100, 100), false),
        ThrobClass("8", listOf(50, 50), false),
    )

    var selectedThrobbingItem: ThrobClass = throbbingList[2]
        set(value) {
            currentThrobbingJob?.cancel()
            field = value
        }
    private var currentThrobbingJob: Job? = null

    class ThrobClass(val name: String, val onOffPeriods: List<Long>, val isFinite: Boolean = true)

    fun toggleFlash(context: Context, isOn: Boolean, coroutineScope: CoroutineScope) {
        if (cameraManager == null) {
            cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager?

            try {
                // O means back camera unit,
                // 1 means front camera unit
                cameraId = cameraManager!!.cameraIdList[0]
            } catch (e: CameraAccessException) {
                e.printStackTrace()
            }

        }
        currentThrobbingJob?.cancel()
        val mCameraManager = cameraManager
        if (mCameraManager != null) {
            if (isOn) {
                startThrobbingTorch(mCameraManager, coroutineScope)
            } else {
                mCameraManager.setTorchMode(cameraId, false)
            }

        } else {
            Toast.makeText(context, R.string.no_flash, Toast.LENGTH_SHORT).show()
        }
    }

    private fun startThrobbingTorch(
        mCameraManager: CameraManager,
        coroutineScope: CoroutineScope,
    ) {
        currentThrobbingJob = coroutineScope.launch {
            do {
                Log.v("parth", "starting")
                var isOn = true
                if (selectedThrobbingItem.onOffPeriods.isNotEmpty()) {
                    selectedThrobbingItem.onOffPeriods.forEach {
                        Log.v("parth", "on: $isOn")
                        mCameraManager.setTorchMode(cameraId, isOn)
                        isOn = !isOn
                        delay(it)
                    }
                } else {
                    mCameraManager.setTorchMode(cameraId, true)
                }
            } while (!selectedThrobbingItem.isFinite)
        }

    }
}