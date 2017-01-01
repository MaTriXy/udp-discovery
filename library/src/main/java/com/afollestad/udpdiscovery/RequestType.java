package com.afollestad.udpdiscovery;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.afollestad.udpdiscovery.RequestType.DISCOVER;
import static com.afollestad.udpdiscovery.RequestType.RESPOND;

/**
 * @author Aidan Follestad (afollestad)
 */
@Retention(RetentionPolicy.SOURCE)
@StringDef({
        DISCOVER,
        RESPOND
})
public @interface RequestType {

    String DISCOVER = "discover";
    String RESPOND = "respond";
}
