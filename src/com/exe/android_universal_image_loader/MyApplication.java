package com.exe.android_universal_image_loader;

import android.app.Application;
import android.text.TextUtils;
import android.util.Log;

import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class MyApplication extends Application {

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		ImageLoaderConfiguration configuration = new ImageLoaderConfiguration
				.Builder(this).memoryCache(new UsingFreqLimitedMemoryCache(10)).threadPoolSize(1).writeDebugLogs().build();

		ImageLoader.getInstance().init(configuration);
	
	}

}
