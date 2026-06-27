package yangfentuozi.oplusautodc.daemon

import android.os.Looper
import androidx.annotation.Keep
import java.io.File

object Main {
    @Keep
    @JvmStatic
    fun main(args: Array<String>) {
        @Suppress("DEPRECATION")
        if (Looper.getMainLooper() == null) {
            Looper.prepareMainLooper()
        }

        val moduleDir = args.firstOrNull { it.startsWith("--module-dir=") }
            ?.substringAfter("=")
            ?.takeIf { it.isNotBlank() }
            ?: "/data/adb/modules/oplus_auto_dc"
        val runtimeModuleProp = args.firstOrNull { it.startsWith("--runtime-module-prop=") }
            ?.substringAfter("=")
            ?.takeIf { it.isNotBlank() }
            ?: "/tmp/oplus_auto_dc/module.prop"

        val daemon = DimmingDaemon(
            moduleDir = File(moduleDir),
            modulePropFile = File(runtimeModuleProp)
        )
        Runtime.getRuntime().addShutdownHook(Thread { daemon.stop() })

        daemon.start()
        Looper.loop()
    }
}
