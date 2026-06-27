package yangfentuozi.hiddenapi.compat

import android.os.ServiceManager

object ServiceManagerCompat {
    fun waitService(name: String) {
        while (ServiceManager.getService(name) == null) {
            Thread.sleep(1000)
        }
    }

    fun waitServices(vararg names: String) {
        names.forEach(::waitService)
    }
}
