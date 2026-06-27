package android.hardware.display;

import android.hardware.display.IDisplayManagerCallback;
import android.view.DisplayInfo;

interface IDisplayManager {
    void registerCallbackWithEventMask(in IDisplayManagerCallback callback, long eventsMask);
    DisplayInfo getDisplayInfo(int displayId);
}
