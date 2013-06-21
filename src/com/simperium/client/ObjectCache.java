package com.simperium.client;

import com.simperium.util.Logger;

import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

class ObjectCache<T extends Syncable> {
    public static final String TAG="Simperium.Cache";

    public interface Provider<T extends Syncable> {
        public T get(String key);
        public void put(String key, T object);
        public void remove(String key);
    }

    final private Provider<T> provider;

    public ObjectCache(Provider<T> provider){
        this.provider = provider;
    }

    public static <T extends Syncable> ObjectCache<T> buildCache(Bucket<T> bucket){
        // TODO: build on top of LRU cache
        return new ObjectCache<T>(new Provider<T>(){
            final Map<String, T> cache = Collections.synchronizedMap(new HashMap<String, T>());
            @Override
            public T get(String key){
                return cache.get(key);
            }
            @Override
            public void put(String key, T object){
                cache.put(key, object);
            }
            @Override
            public void remove(String key){
                cache.remove(key);
            }
        });
    }
    
    public T get(String key){
        T object = provider.get(key);
        if (object == null) {
            Logger.log(TAG, String.format("Miss %s", key));
        } else {
            Logger.log(TAG, String.format("Hit %s %s", key, object));
        }
        return object;
    }

    public void put(String key, T object){
        Logger.log(TAG, String.format("Cache %s %s", key, object));
        provider.put(key, object);
    }

    public void remove(String key){
        provider.remove(key);
    }

}

