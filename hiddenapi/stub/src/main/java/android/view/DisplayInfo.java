package android.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.os.Parcel;
import android.os.Parcelable;
public final class DisplayInfo implements Parcelable {
    public static final @NonNull Creator<DisplayInfo> CREATOR = new Creator<>() {
        @Override
        public DisplayInfo createFromParcel(Parcel source) {
            throw new UnsupportedOperationException("STUB");
        }

        @Override
        public DisplayInfo[] newArray(int size) {
            throw new UnsupportedOperationException("STUB");
        }
    };

    @Override
    public boolean equals(@Nullable Object o) {
        throw new UnsupportedOperationException("STUB");
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        throw new UnsupportedOperationException("STUB");
    }

    @Override
    public int describeContents() {
        throw new UnsupportedOperationException("STUB");
    }

    /**
     * Returns the refresh rate the application would experience.
     */
    public float getRefreshRate() {
        throw new UnsupportedOperationException("STUB");
    }
}