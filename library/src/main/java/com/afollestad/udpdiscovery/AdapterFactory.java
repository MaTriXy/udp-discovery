package com.afollestad.udpdiscovery;

import com.google.gson.TypeAdapterFactory;
import com.ryanharter.auto.value.gson.GsonTypeAdapterFactory;

/**
 * @author Aidan Follestad (afollestad)
 */
@GsonTypeAdapterFactory
public abstract class AdapterFactory implements TypeAdapterFactory {

    public static AdapterFactory create() {
        return new AutoValueGson_AdapterFactory();
    }
}