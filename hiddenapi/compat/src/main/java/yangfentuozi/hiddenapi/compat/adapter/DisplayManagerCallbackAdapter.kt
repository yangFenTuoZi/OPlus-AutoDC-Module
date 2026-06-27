package yangfentuozi.hiddenapi.compat.adapter

import android.hardware.display.IDisplayManagerCallback
import android.os.Parcel
import android.os.RemoteException
import androidx.annotation.Keep

@Keep
abstract class DisplayManagerCallbackAdapter : IDisplayManagerCallback.Stub() {
    @Throws(RemoteException::class)
    override fun onTransact(code: Int, data: Parcel, reply: Parcel?, flags: Int): Boolean {
        return try {
            super.onTransact(code, data, reply, flags)
        } catch (_: Throwable) {
            true
        }
    }
}
