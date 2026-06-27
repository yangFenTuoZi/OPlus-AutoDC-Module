package yangfentuozi.hiddenapi.compat

import android.content.Context
import android.hardware.display.IDisplayManager
import android.os.ServiceManager
import android.view.DisplayInfo
import yangfentuozi.hiddenapi.compat.adapter.DisplayManagerCallbackAdapter

object DisplayManagerCompat {

    private val service by lazy {
        IDisplayManager.Stub.asInterface(ServiceManager.getService(Context.DISPLAY_SERVICE))
    }

    fun registerCallbackWithEventMask(callback: DisplayManagerCallbackAdapter, eventsMask: Long) =
        service.registerCallbackWithEventMask(callback, eventsMask)

    fun getDisplayInfo(displayId: Int): DisplayInfo? =
        service.getDisplayInfo(displayId)
}
