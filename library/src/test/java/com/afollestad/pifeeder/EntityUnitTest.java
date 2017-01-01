package com.afollestad.pifeeder;

import android.annotation.SuppressLint;

import com.afollestad.udpdiscovery.AdapterFactory;
import com.afollestad.udpdiscovery.Entity;
import com.afollestad.udpdiscovery.Request;
import com.afollestad.udpdiscovery.RequestType;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

@SuppressLint("DefaultLocale")
public class EntityUnitTest {

    Gson gson;
    Entity entity;
    Request request;

    @Before public void setup() {
        gson = new GsonBuilder()
                .registerTypeAdapterFactory(AdapterFactory.create())
                .create();

        entity = Entity.create()
                .address("192.168.68.2")
                .name("RaspberryPi")
                .build();

        request = Request.create()
                .entity(entity)
                .type(RequestType.DISCOVER)
                .version(2)
                .build();
    }

    @Test public void test_serialize() {
        String comparison = String.format("{\"address\":\"%s\",\"name\":\"%s\"}",
                "192.168.68.2", "RaspberryPi");
        String json = gson.toJson(entity, Entity.class);
        assertEquals(json, comparison);

        String comparison2 = String.format("{\"type\":\"%s\",\"version\":%d,\"entity\":%s}",
                "discover", 2, comparison);
        String json2 = gson.toJson(request, Request.class);
        assertEquals(json2, comparison2);
    }

    @Test public void test_deserialize() {
        String json = String.format("{\"address\":\"%s\",\"name\":\"%s\"}",
                "192.168.68.2", "RaspberryPi");
        Entity deserialized = gson.fromJson(json, Entity.class);
        assertEquals(deserialized, entity);

        String json2 = String.format("{\"type\":\"%s\",\"version\":%d,\"entity\":%s}",
                "discover", 2, json);
        Request deserialized2 = gson.fromJson(json2, Request.class);
        assertEquals(deserialized2, request);
    }
}