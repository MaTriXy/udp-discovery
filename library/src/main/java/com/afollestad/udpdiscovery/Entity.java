package com.afollestad.udpdiscovery;

import android.os.Parcelable;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

/**
 * @author Aidan Follestad (afollestad)
 */
@AutoValue public abstract class Entity implements Parcelable {

    public static Builder create() {
        return new AutoValue_Entity.Builder();
    }

    public abstract String address();

    public abstract String name();

    public Builder toBuilder() {
        return new AutoValue_Entity.Builder(this);
    }

    public static TypeAdapter<Entity> typeAdapter(Gson gson) {
        return new AutoValue_Entity.GsonTypeAdapter(gson);
    }

    @AutoValue.Builder
    public static abstract class Builder {

        public abstract Builder address(String address);

        public abstract Builder name(String name);

        public abstract Entity build();
    }
}
