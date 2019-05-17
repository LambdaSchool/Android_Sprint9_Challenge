package com.example.israel.sprint9;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.Nullable;

public class MarkerDropAudioSPDAO {

    private static final String NAME = "marker_drop_audio";
    private static final String MARKER_DROP_AUDIO_KEY = "marker_drop_audio";

    @Nullable
    static public Uri getMarkerDropAudio(Context context) {
        SharedPreferences sp = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);

        String markerDropAudioStr = sp.getString(MARKER_DROP_AUDIO_KEY, null);
        if (markerDropAudioStr == null) {
            return null;
        }

        return Uri.parse(markerDropAudioStr);
    }

    static public void setMarkerDropAudio(Context context, Uri audioUri) {
        SharedPreferences sp = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(MARKER_DROP_AUDIO_KEY, audioUri.toString());
        editor.apply();

    }

}
