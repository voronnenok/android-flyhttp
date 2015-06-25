/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.voronnenok.flyhttp.app.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.voronnenok.flyhttp.ImageLoader;
import com.voronnenok.flyhttp.RecycleImageView;
import com.voronnenok.flyhttp.app.R;
import com.voronnenok.flyhttp.app.provider.Images;
import com.voronnenok.flyhttp.cache.Utils;


/**
 * The main fragment that powers the ImageGridActivity screen. Fairly straight forward GridView
 * implementation with the key addition being the ImageWorker class w/ImageCache to load children
 * asynchronously, keeping the UI nice and smooth and caching thumbnails for quick retrieval. The
 * cache is retained over configuration changes like orientation change so the images are populated
 * quickly if, for example, the user rotates the device.
 */
public class ImageGridFragment extends Fragment{
    private static final String TAG = "ImageGridFragment";
    ImageLoader imageLoader;
    GridLayoutManager layoutManager;
    float imageSize;
    float imageSpace;
    LinearLayout.LayoutParams thumbsLayoutParams;

    /**
     * Empty constructor as per the Fragment documentation
     */
    public ImageGridFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        imageLoader = ((BaseActivity)getActivity()).getImageLoader();
        imageSize = getResources().getDimensionPixelSize(R.dimen.image_thumb_size);
        imageSpace = getResources().getDimensionPixelSize(R.dimen.image_thumb_space);
        thumbsLayoutParams = new LinearLayout.LayoutParams((int)imageSize, (int)imageSize);
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View v = inflater.inflate(R.layout.activity_main, container, false);
        final RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new GridLayoutManager(getActivity(), 3);
        recyclerView.setLayoutManager(layoutManager);
        final RecyclerView.Adapter mAdapter = new RecycleAdapter(getActivity());
        recyclerView.setAdapter(mAdapter);


        // This listener is used to get the final width of the GridView and then calculate the
        // number of columns and the width of each column. The width of each column is variable
        // as the GridView has stretchMode=columnWidth. The column width is used to set the height
        // of each view so we get nice square thumbnails.
        recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onGlobalLayout() {
                        Log.d(TAG, "RecyclerView OnGlobalLayout");

                        int recyclerViewWidth = recyclerView.getWidth();
                        int newSpanCount = (int) Math.floor(recyclerViewWidth / imageSize);
                        layoutManager.setSpanCount(newSpanCount);

                        if (Utils.hasJellyBean()) {
                            recyclerView.getViewTreeObserver()
                                    .removeOnGlobalLayoutListener(this);
                        } else {
                            recyclerView.getViewTreeObserver()
                                    .removeGlobalOnLayoutListener(this);
                        }
                    }
                });

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
//        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.ViewHolder> {
        private final Context context;

        private RecycleAdapter(Context context) {
            this.context = context;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(context).inflate(R.layout.imagegrid_item, viewGroup, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            Log.d(TAG, "OnBind ViewHolder " + i);
            ((RecycleImageView)viewHolder.imageView).setPosition(i);
        }

        @Override
        public void onViewAttachedToWindow(ViewHolder holder) {
            super.onViewAttachedToWindow(holder);
            int position = ((RecycleImageView)holder.imageView).getPosition();
            imageLoader.loadImage(holder.imageView, Images.imageThumbUrls[position]);
        }

        @Override
        public void onViewDetachedFromWindow(ViewHolder holder) {
            super.onViewDetachedFromWindow(holder);
            holder.imageView.setImageResource(android.R.drawable.ic_menu_mapmode);
        }

        @Override
        public int getItemCount() {
            return Images.imageThumbUrls.length;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;

            public ViewHolder(View itemView) {
                super(itemView);
                imageView = (ImageView)itemView.findViewById(R.id.image);
            }


        }

    }

}
