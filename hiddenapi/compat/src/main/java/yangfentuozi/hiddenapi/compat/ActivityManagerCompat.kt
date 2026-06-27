package yangfentuozi.hiddenapi.compat

import android.app.IActivityManager
import android.content.AttributionSource
import android.content.Context
import android.content.IContentProvider
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.os.ServiceManager
import android.system.Os

object ActivityManagerCompat {
    private val service by lazy {
        IActivityManager.Stub.asInterface(ServiceManager.getService(Context.ACTIVITY_SERVICE))
    }

    fun contentProviderCall(
        authority: String,
        userId: Int,
        token: IBinder,
        method: String,
        arg: String?,
        extras: Bundle?
    ): Bundle? {
        var provider: IContentProvider? = null
        try {
            val holder = service.getContentProviderExternal(authority, userId, token, authority)
            provider = holder?.provider ?: error("Provider $authority is null")
            check(provider.asBinder().pingBinder()) { "Provider $authority is dead" }
            return provider.call(
                AttributionSource.Builder(Os.getuid()).setPackageName("root").build(),
                authority,
                method,
                arg,
                extras
            )
        } finally {
            if (provider != null) {
                runCatching { service.removeContentProviderExternal(authority, token) }
            }
        }
    }
}
