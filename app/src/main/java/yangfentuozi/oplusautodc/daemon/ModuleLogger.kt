package yangfentuozi.oplusautodc.daemon

import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ModuleLogger(moduleDir: File) {
    private val runDir = File(moduleDir, "run")
    private val logFile = File(runDir, "server.log")
    private val timestampFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())

    fun log(tag: String, message: String, throwable: Throwable? = null) {
        runCatching {
            runDir.mkdirs()
            val text = buildString {
                append('[')
                append(timestampFormat.format(Date()))
                append("] ")
                append(tag)
                append(": ")
                append(message)
                append('\n')
                if (throwable != null) {
                    append(throwable.stackTraceToString())
                    append('\n')
                }
            }
            logFile.appendText(text)
        }
    }
}
