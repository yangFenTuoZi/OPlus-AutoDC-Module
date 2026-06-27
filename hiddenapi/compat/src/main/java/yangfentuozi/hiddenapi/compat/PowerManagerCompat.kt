package yangfentuozi.hiddenapi.compat

import android.content.Context
import android.os.IPowerManager
import android.os.ServiceManager

object PowerManagerCompat {
    private val service by lazy {
        IPowerManager.Stub.asInterface(ServiceManager.getService(Context.POWER_SERVICE))
    }

    val isInteractive: Boolean
        get() = service.isInteractive
}
