package yangfentuozi.oplusautodc.daemon

import android.content.Context
import android.hardware.display.DisplayManagerGlobal
import android.os.Handler
import android.os.Looper
import android.view.Display
import yangfentuozi.hiddenapi.compat.DisplayManagerCompat
import yangfentuozi.hiddenapi.compat.PowerManagerCompat
import yangfentuozi.hiddenapi.compat.ServiceManagerCompat
import yangfentuozi.hiddenapi.compat.SettingsProviderClient
import yangfentuozi.hiddenapi.compat.adapter.DisplayManagerCallbackAdapter
import java.io.File
import kotlin.math.abs
import kotlin.math.roundToInt

private const val TAG = "DimmingDaemon"
private const val SETTING_KEY = "display_single_pulse_eyeprotection_switch"
private const val REFRESH_LIMIT_HZ = 120.5f
private const val EVENT_CONFIRM_DELAY_MS = 1000L
private const val REFRESH_RATE_CHANGE_EPSILON_HZ = 0.01f

class DimmingDaemon(
    private val moduleDir: File,
    private val modulePropFile: File
) {
    private val handler = Handler(Looper.getMainLooper())
    private val logger = ModuleLogger(moduleDir)
    private val settingsProvider = SettingsProviderClient(0) { message, throwable ->
        logger.log("SettingsProviderClient", message, throwable)
    }
    private var displayCallback: DisplayManagerCallbackAdapter? = null
    private var lastObservedRefreshRate: Float? = null
    private var stopped = false

    private val refreshCheckRunnable =
        Runnable { runRefreshCheck("显示事件", requireRefreshRateChanged = true) }

    fun start() {
        logger.log(TAG, "daemon starting: moduleDir=${moduleDir.absolutePath}")
        moduleDir.mkdirs()

        try {
            ServiceManagerCompat.waitServices(
                Context.ACTIVITY_SERVICE,
                Context.DISPLAY_SERVICE,
                Context.POWER_SERVICE
            )
            registerDisplayCallback()
            runRefreshCheck("启动检查")
            logger.log(TAG, "daemon started")
        } catch (t: Throwable) {
            logger.log(TAG, "daemon startup failed", t)
            val reason = t.message?.takeIf { it.isNotBlank() } ?: t.javaClass.simpleName
            updateState("启动失败；屏幕状态未知；调光：未知；刷新率：未知；$reason")
        }
    }

    fun stop() {
        stopped = true
        handler.removeCallbacks(refreshCheckRunnable)
        displayCallback = null
        logger.log(TAG, "daemon stopped")
        updateState("daemon 等待启动；屏幕状态未知；调光：未知；刷新率：未知")
    }

    private fun registerDisplayCallback() {
        displayCallback = object : DisplayManagerCallbackAdapter() {
            override fun onDisplayEvent(displayId: Int, event: Int) {
                if (stopped || displayId != Display.DEFAULT_DISPLAY) return

                logger.log(TAG, "display event: event=$event")
                handler.removeCallbacks(refreshCheckRunnable)
                handler.postDelayed(refreshCheckRunnable, EVENT_CONFIRM_DELAY_MS)
            }
        }
        DisplayManagerCompat.registerCallbackWithEventMask(
            requireNotNull(displayCallback),
            DisplayManagerGlobal.INTERNAL_EVENT_FLAG_DISPLAY_REFRESH_RATE
        )
    }

    private fun runRefreshCheck(scene: String, requireRefreshRateChanged: Boolean = false) {
        if (stopped) return

        val interactive = runCatching { PowerManagerCompat.isInteractive }
            .onFailure { logger.log(TAG, "failed to read interactive state", it) }
            .getOrDefault(false)

        val setting = EyeProtectionState.fromValue(settingsProvider.getSecureString(SETTING_KEY))

        val refreshRate = DisplayManagerCompat.getDisplayInfo(Display.DEFAULT_DISPLAY)
            ?.refreshRate ?: 0f
        if (refreshRate <= 0f) {
            updateState("$scene；亮屏；调光：$setting；刷新率：未知；刷新率不可用，未写入设置")
            return
        }

        val rounded = (refreshRate * 100f).roundToInt() / 100f
        val formattedRate =
            if (rounded % 1f == 0f) rounded.roundToInt().toString() else rounded.toString()

        val previousRefreshRate = lastObservedRefreshRate
        if (requireRefreshRateChanged &&
            previousRefreshRate != null &&
            abs(previousRefreshRate - refreshRate) < REFRESH_RATE_CHANGE_EPSILON_HZ
        ) {
            logger.log(
                TAG,
                "ignored display event without refresh rate change: refresh=${formattedRate}Hz"
            )
            return
        }
        lastObservedRefreshRate = refreshRate

        if (!interactive) {
            updateState("$scene；息屏；调光：$setting；刷新率：${formattedRate}Hz；息屏，未写入设置")
            return
        }

        if (setting != EyeProtectionState.PWM_STATE) {
            updateState("$scene；亮屏；调光：$setting；刷新率：${formattedRate}Hz；当前无需恢复")
            return
        }

        if (refreshRate <= REFRESH_LIMIT_HZ) {
            updateState(
                if (settingsProvider.putSecureString(
                        SETTING_KEY,
                        EyeProtectionState.DC_STATE.value
                    )
                ) {
                    logger.log(TAG, "restored default dimming: refresh=${formattedRate}")
                    "$scene；亮屏；调光：${EyeProtectionState.DC_STATE}；刷新率：${formattedRate}Hz；已恢复全亮度低频闪"
                } else {
                    "$scene；亮屏；调光：$setting；刷新率：${formattedRate}Hz；设置写入失败"
                }
            )
        } else {
            updateState("$scene；亮屏；调光：$setting；刷新率：${formattedRate}Hz；高刷场景，保持经典低频闪")
        }
    }

    private val descriptionLineRegex = Regex("(?m)^description=.*$")

    private fun updateState(description: String) {
        if (!modulePropFile.exists() || !modulePropFile.isFile) return
        runCatching {
            modulePropFile.writeText(
                modulePropFile.readText().replace(descriptionLineRegex, "description=$description")
            )
        }
    }
}
