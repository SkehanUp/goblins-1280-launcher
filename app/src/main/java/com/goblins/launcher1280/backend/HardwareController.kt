package com.goblins.launcher1280.backend

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.media.AudioManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import android.os.BatteryManager
import android.os.Handler
import android.os.Looper
import android.telephony.TelephonyManager

enum class RingerProfile {
    NORMAL,
    VIBRATE,
    SILENT,
}

data class SystemStatus(
    val batteryPercent: Int,
    val batteryBars: Int,
    val isCharging: Boolean,
    val signalBars: Int,
    val carrierLabel: String,
    val torchEnabled: Boolean,
    val ringerProfile: RingerProfile,
)

class HardwareController(private val context: Context) {
    private val appContext = context.applicationContext
    private val cameraManager = appContext.getSystemService(CameraManager::class.java)
    private val audioManager = appContext.getSystemService(AudioManager::class.java)
    private val telephonyManager = appContext.getSystemService(TelephonyManager::class.java)
    private val connectivityManager = appContext.getSystemService(ConnectivityManager::class.java)
    private val wifiManager = appContext.getSystemService(WifiManager::class.java)
    private val torchLock = Any()
    private var torchEnabled = false
    private var released = false

    private val torchCallback = object : CameraManager.TorchCallback() {
        override fun onTorchModeChanged(cameraId: String, enabled: Boolean) {
            synchronized(torchLock) {
                torchEnabled = enabled
            }
        }

        override fun onTorchModeUnavailable(cameraId: String) {
            synchronized(torchLock) {
                torchEnabled = false
            }
        }
    }

    init {
        runCatching {
            cameraManager?.registerTorchCallback(torchCallback, Handler(Looper.getMainLooper()))
        }
    }

    fun toggleTorch(): Boolean {
        val manager = cameraManager ?: return false
        val cameraId = runCatching {
            manager.cameraIdList.firstOrNull { id ->
                manager.getCameraCharacteristics(id)
                    .get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
            }
        }.getOrNull() ?: return false

        val nextEnabled = synchronized(torchLock) { !torchEnabled }
        return runCatching {
            manager.setTorchMode(cameraId, nextEnabled)
            synchronized(torchLock) {
                torchEnabled = nextEnabled
            }
            nextEnabled
        }.getOrDefault(false)
    }

    fun toggleVibrateMode(): RingerProfile {
        val manager = audioManager ?: return RingerProfile.NORMAL
        return runCatching {
            val nextMode = if (manager.ringerMode == AudioManager.RINGER_MODE_VIBRATE) {
                AudioManager.RINGER_MODE_NORMAL
            } else {
                AudioManager.RINGER_MODE_VIBRATE
            }
            manager.ringerMode = nextMode
            manager.ringerMode.toRingerProfile()
        }.getOrElse {
            runCatching { manager.ringerMode.toRingerProfile() }
                .getOrDefault(RingerProfile.NORMAL)
        }
    }

    fun readSystemStatus(): SystemStatus {
        val batteryIntent = appContext.registerReceiver(
            null,
            IntentFilter(Intent.ACTION_BATTERY_CHANGED),
        )
        val level = batteryIntent?.getIntExtra(BatteryManager.EXTRA_LEVEL, 0) ?: 0
        val scale = batteryIntent?.getIntExtra(BatteryManager.EXTRA_SCALE, 100) ?: 100
        val batteryPercent = if (scale > 0) {
            ((level * 100f) / scale).toInt().coerceIn(0, 100)
        } else {
            0
        }
        val status = batteryIntent?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
        val signalBars = readSignalBars()
        val carrierLabel = runCatching { telephonyManager?.networkOperatorName }
            .getOrNull()
            ?.takeIf { it.isNotBlank() }
            ?: "GOBLINS"

        return SystemStatus(
            batteryPercent = batteryPercent,
            batteryBars = ((batteryPercent.coerceIn(0, 100) * 7) / 100).coerceAtLeast(1),
            isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL,
            signalBars = signalBars,
            carrierLabel = carrierLabel,
            torchEnabled = synchronized(torchLock) { torchEnabled },
            ringerProfile = readRingerProfile(),
        )
    }

    fun release() {
        val shouldUnregister = synchronized(torchLock) {
            if (released) false else {
                released = true
                true
            }
        }
        if (shouldUnregister) {
            runCatching { cameraManager?.unregisterTorchCallback(torchCallback) }
        }
    }

    private fun readSignalBars(): Int = runCatching {
        val capabilities = connectivityManager?.let { manager ->
            manager.activeNetwork?.let(manager::getNetworkCapabilities)
        }
        when {
            capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true ->
                telephonyManager?.signalStrength?.level
                    ?.let { cellularLevel -> (cellularLevel + 3).coerceIn(1, 7) }
                    ?: 6

            capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true -> {
                val rssi = wifiManager?.connectionInfo?.rssi
                if (rssi == null || rssi <= -127) 6
                else (WifiManager.calculateSignalLevel(rssi, 7) + 1).coerceIn(1, 7)
            }

            else -> 6
        }
    }.getOrDefault(6)

    private fun readRingerProfile(): RingerProfile = runCatching {
        audioManager?.ringerMode?.toRingerProfile() ?: RingerProfile.NORMAL
    }.getOrDefault(RingerProfile.NORMAL)

    private fun Int.toRingerProfile(): RingerProfile = when (this) {
        AudioManager.RINGER_MODE_VIBRATE -> RingerProfile.VIBRATE
        AudioManager.RINGER_MODE_SILENT -> RingerProfile.SILENT
        else -> RingerProfile.NORMAL
    }
}
