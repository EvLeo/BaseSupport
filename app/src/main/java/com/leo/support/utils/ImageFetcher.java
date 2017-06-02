package com.leo.support.utils;

import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;

import com.leo.support.app.BaseApp;
import com.leo.support.config.BaseConfig;
import com.nostra13.universalimageloader.cache.disc.DiskCache;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.MemoryCache;
import com.nostra13.universalimageloader.cache.memory.impl.FuzzyKeyMemoryCache;
import com.nostra13.universalimageloader.core.DefaultConfigurationFactory;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.BitmapDisplayer;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.utils.MemoryCacheUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * done
 * Created by leo on 2017/6/2.
 */

public class ImageFetcher {

    private static ImageFetcher _imageFetcher;

    private DiskCache mDiskCache;
    private MemoryCache mMemoryCache;

    private ImageFetcher() {
        initImageFetcher();
    }

    public static ImageFetcher getImageFetcher() {
        if (null == _imageFetcher)
            _imageFetcher = new ImageFetcher();
        return _imageFetcher;
    }

    private void initImageFetcher() {
        File rootDir = BaseConfig.getConfig().getAppRootDir();
        File cacheFile = new File(rootDir, "images");
        if (!cacheFile.exists()) {
            cacheFile.mkdirs();
        }
        mDiskCache = new UnlimitedDiskCache(cacheFile, null, new Md5FileNameGenerator());

        if (null == mMemoryCache) {
            mMemoryCache = DefaultConfigurationFactory.createMemoryCache(BaseApp.getAppContext(), 0);
        }
        mMemoryCache = new FuzzyKeyMemoryCache(mMemoryCache, MemoryCacheUtils.createFuzzyKeyComparator()) {
            @Override
            public boolean put(String key, Bitmap value) {
                try {
                    return super.put(key, value);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                return false;
            }
        };

        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(BaseApp.getAppContext())
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .diskCache(mDiskCache)
                .memoryCache(mMemoryCache)
                .build();

        ImageLoader.getInstance().init(configuration);

    }

    public Bitmap getBitmapInCache(String uri) {
        if (mMemoryCache != null) {
            DisplayMetrics displayMetrics = BaseApp.getAppContext().getResources().getDisplayMetrics();
            ImageSize targetSize = new ImageSize(displayMetrics.widthPixels, displayMetrics.heightPixels);
            String memoryCacheKey = MemoryCacheUtils.generateKey(uri, targetSize);
            return mMemoryCache.get(memoryCacheKey);
        }
        return null;
    }

    public Bitmap loadImageSync(String uri) {
        return ImageLoader.getInstance().loadImageSync(uri);
    }

    public Bitmap loadImageSync(String uri, int width, int height, int rotation) {
        return ImageLoader.getInstance().loadImageSync(uri, new ImageSize(width, height, rotation));
    }

    public void loadImage(String uri, ImageView imageView, int defaultRes, BitmapDisplayer disPlayer,
                          ImageFetcherListener listener, ImageLoadingProgressListener progressListener) {
        if (disPlayer == null) {
            disPlayer = new SimpleBitmapDisplayer();
        }
        ImageLoader.getInstance().cancelDisplayTask(imageView);
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(defaultRes)
                .showImageOnFail(defaultRes)
                .displayer(disPlayer)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        try {
            ImageLoader.getInstance().displayImage(uri == null ? "" : uri, imageView, options, listener, progressListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadImage(String uri, ImageView imageView, int defaultRes, BitmapDisplayer disPlayer,
                          ImageFetcherListener listener) {
        loadImage(uri, imageView, defaultRes, disPlayer, listener, null);
    }

    public void loadImage(String uri, ImageView imageView, int defaultRes, BitmapDisplayer disPlayer) {
        loadImage(uri, imageView, defaultRes, disPlayer, null);
    }

    public void loadImage(String uri, ImageView imageView, int defaultRes) {
        loadImage(uri, imageView, defaultRes, null);
    }

    public void loadImage(final String uri, final ImageSize imageSize, final Object tag,
                          final ImageFetcherListener listener, final ImageLoadingProgressListener progressListener) {
        UIThreadhandler.post(new Runnable() {
            @Override
            public void run() {
                DisplayImageOptions options = new DisplayImageOptions.Builder()
                        .showImageForEmptyUri(0)
                        .showImageOnFail(0)
                        .cacheInMemory(true)
                        .cacheOnDisk(true)
                        .considerExifParams(true)
                        .bitmapConfig(Bitmap.Config.RGB_565)
                        .build();

                if (null != listener) {
                    listener.setTag(tag);
                }
                String imageUrl = uri;
                if (imageUrl == null) {
                    imageUrl = "";
                }
                try {
                    ImageLoader.getInstance().loadImage(imageUrl, imageSize, options, listener, progressListener);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void loadImage(String uri, ImageSize imageSize, Object tag, ImageFetcherListener listener) {
        loadImage(uri, imageSize, tag, listener, null);
    }

    public void loadImage(String uri, Object tag, ImageFetcherListener listener) {
        loadImage(uri, null, tag, listener);
    }

    public File getCacheFilePath(String uri) {
        if (null != mDiskCache && mDiskCache.get(uri) != null) {
            return mDiskCache.get(uri);
        }
        return null;
    }

    public static abstract class ImageFetcherListener implements ImageLoadingListener {

        private Object tag;

        public void setTag(Object tag) {
            this.tag = tag;
        }

        public abstract void onLoadComplete(String imageUri, Bitmap bitmap, Object object);

        public void onLoadStarted(String imageUri, View view, Object tag) {
        }

        @Override
        public void onLoadingStarted(String imageUri, View view) {
            onLoadStarted(imageUri, view, tag);
        }

        @Override
        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
            File cacheFile = ImageFetcher.getImageFetcher().getCacheFilePath(imageUri);
            if (cacheFile != null && cacheFile.exists()) {
                cacheFile.delete();
            }
            onLoadComplete(imageUri, null, tag);
        }

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap bitmap) {
            onLoadComplete(imageUri, bitmap, tag);
            if (null != bitmap) {
                getImageFetcher().notifyOnLoadImage(imageUri, bitmap, tag);
            }
        }

        @Override
        public void onLoadingCancelled(String imageUri, View view) {
            File cacheFile = ImageFetcher.getImageFetcher().getCacheFilePath(imageUri);
            if (cacheFile != null && cacheFile.exists()) {
                cacheFile.delete();
            }
            onLoadComplete(imageUri, null, tag);
        }
    }

    private List<ImageFetcherListener> mImageFetcherListeners = new ArrayList<ImageFetcherListener>();

    public void addImageFetcherListener(ImageFetcherListener listener) {
        if (!mImageFetcherListeners.contains(listener)) {
            mImageFetcherListeners.add(listener);
        }
    }

    public void removeImageFetcherListener(ImageFetcherListener listener) {
        mImageFetcherListeners.remove(listener);
    }

    public void notifyOnLoadImage(String imageUri, Bitmap bitmap, Object object) {
        if (mImageFetcherListeners != null && mImageFetcherListeners.size() > 0) {
            for (ImageFetcherListener listener : mImageFetcherListeners) {
                listener.onLoadComplete(imageUri, bitmap, object);
            }
        }
    }

}
