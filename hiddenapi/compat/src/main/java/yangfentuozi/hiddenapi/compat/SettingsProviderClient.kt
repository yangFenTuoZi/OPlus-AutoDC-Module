package yangfentuozi.hiddenapi.compat

import android.os.Binder
import android.os.Bundle

class SettingsProviderClient(
    private val userId: Int,
    private val onError: (String, Throwable) -> Unit = { _, _ -> }
) {
    private val token = Binder()

    fun getSecureString(name: String): String? {
        return try {
            val fields = SettingsProviderFields
            val extras = Bundle().apply {
                putInt(fields.callMethodUserKey, userId)
            }
            ActivityManagerCompat.contentProviderCall(
                authority = fields.settingsAuthority,
                userId = userId,
                token = token,
                method = fields.callMethodGetSecure,
                arg = name,
                extras = extras
            )?.getString(fields.nameValueValue)
        } catch (t: Throwable) {
            onError("getSecureString failed: $name", t)
            null
        }
    }

    fun putSecureString(name: String, value: String): Boolean {
        return try {
            val fields = SettingsProviderFields
            val extras = Bundle().apply {
                putInt(fields.callMethodUserKey, userId)
                putString(fields.nameValueValue, value)
            }
            ActivityManagerCompat.contentProviderCall(
                authority = fields.settingsAuthority,
                userId = userId,
                token = token,
                method = fields.callMethodPutSecure,
                arg = name,
                extras = extras
            )
            true
        } catch (t: Throwable) {
            onError("putSecureString failed: $name=$value", t)
            false
        }
    }
}

private object SettingsProviderFields {
    private val settingsClass by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        Class.forName("android.provider.Settings")
    }

    private val nameValueTableClass by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        Class.forName($$"android.provider.Settings$NameValueTable")
    }

    val settingsAuthority: String by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        settingsClass.staticStringField("AUTHORITY")
    }

    val callMethodGetSecure: String by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        settingsClass.staticStringField("CALL_METHOD_GET_SECURE")
    }

    val callMethodPutSecure: String by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        settingsClass.staticStringField("CALL_METHOD_PUT_SECURE")
    }

    val callMethodUserKey: String by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        settingsClass.staticStringField("CALL_METHOD_USER_KEY")
    }

    val nameValueValue: String by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        nameValueTableClass.staticStringField("VALUE")
    }

    private fun Class<*>.staticStringField(name: String): String {
        val field = getDeclaredField(name)
        field.isAccessible = true
        return field.get(null) as String
    }
}
