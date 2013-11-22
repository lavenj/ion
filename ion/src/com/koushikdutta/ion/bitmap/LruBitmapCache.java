package com.koushikdutta.ion.bitmap;

import android.graphics.Bitmap;
import android.util.Log;

class LruBitmapCache extends LruCache<String, BitmapInfo> {
    public LruBitmapCache(int maxSize) {
        super(maxSize);
    }

	@Override
	protected void entryRemoved(boolean evicted, String key, BitmapInfo oldValue, BitmapInfo newValue) {
		super.entryRemoved(evicted, key, oldValue, newValue);

		if( oldValue.bitmaps != null ) {
			for(Bitmap b:oldValue.bitmaps) {
				Log.i("LruCache", "Recycling entry in " + key);
				b.recycle();
			}
		}
	}

	@Override
    protected int sizeOf(String key, BitmapInfo info) {
        return info.sizeOf();
    }
}
