package net.wezu.jxg.util;

import android.content.Context;
import android.content.Intent;

/**
 * Created by snox on 2015/11/30.
 */
public final class MediaStoreUtils {

    private MediaStoreUtils() {
    }

    public static Intent getPickImageIntent(final Context context) {
        final Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        return Intent.createChooser(intent, "Select picture");
    }
}
