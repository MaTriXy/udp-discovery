package com.afollestad.udpdiscovery;

import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;

/**
 * @author Aidan Follestad (afollestad)
 */
@AutoValue public abstract class Request implements Parcelable {

    public static Builder create() {
        return new AutoValue_Request.Builder();
    }

    @RequestType public abstract String type();

    @SerializedName("version") public abstract int version();

    @Nullable public abstract Entity entity();

    public Builder toBuilder() {
        return new AutoValue_Request.Builder(this);
    }

    public static TypeAdapter<Request> typeAdapter(Gson gson) {
        return new AutoValue_Request.GsonTypeAdapter(gson);
    }

    @AutoValue.Builder public static abstract class Builder {

        public abstract Builder type(@RequestType String type);

        public abstract Builder version(int version);

        public abstract Builder entity(@Nullable Entity address);

        public abstract Request build();
    }
}
