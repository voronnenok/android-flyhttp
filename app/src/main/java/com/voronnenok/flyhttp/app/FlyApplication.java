package com.voronnenok.flyhttp.app;

import android.app.Application;
import android.os.Environment;

import com.voronnenok.flyhttp.FlyHttp;
import com.voronnenok.flyhttp.ImageLoader;
import com.voronnenok.flyhttp.cache.NetworkCache;

/**
 * Created by voronnenok on 24.06.15.
 */
public class FlyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        FlyHttp.init(new NetworkCache.Builder(Environment.getExternalStorageDirectory() + "/flyhttp/")
                .build());
    }

    public ImageLoader getImageLoader() {
        return FlyHttp.getInstance().getImageLoader();
    }
}
