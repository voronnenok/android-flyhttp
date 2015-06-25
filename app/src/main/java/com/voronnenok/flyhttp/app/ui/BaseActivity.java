package com.voronnenok.flyhttp.app.ui;

import android.support.v7.app.ActionBarActivity;

import com.voronnenok.flyhttp.ImageLoader;
import com.voronnenok.flyhttp.app.FlyApplication;

/**
 * Created by voronnenok on 24.06.15.
 */
public class BaseActivity extends ActionBarActivity {

    public ImageLoader getImageLoader() {
        return ((FlyApplication)getApplication()).getImageLoader();
    }

}
