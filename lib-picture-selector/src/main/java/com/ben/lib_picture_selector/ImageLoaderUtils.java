package com.ben.lib_picture_selector;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

/**
 * @author：luck
 * @date：2021/7/14 3:15 PM
 * @describe：ImageLoaderUtils
 */
public class ImageLoaderUtils {

    public static boolean assertValidRequest(Context context) {
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            return !isDestroy(activity);
        } else if (context instanceof ContextWrapper){
            ContextWrapper contextWrapper = (ContextWrapper) context;
            if (contextWrapper.getBaseContext() instanceof Activity){
                Activity activity = (Activity) contextWrapper.getBaseContext();
                return !isDestroy(activity);
            }
        }
        return true;
    }

    private static boolean isDestroy(Activity activity) {
        if (activity == null) {
            return true;
        }
        return activity.isFinishing() || activity.isDestroyed();
    }

    public static void load(Context context, ImageView imageView, Object uri) {
        try {
            Glide.with(context)
                    .load(uri)
                    .placeholder(imageView.getDrawable())
                    .skipMemoryCache(false)
                    .dontAnimate()
                    .centerCrop()
                    .apply(new RequestOptions().centerCrop().error(imageView.getDrawable()))
                    .into(imageView);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }
}
