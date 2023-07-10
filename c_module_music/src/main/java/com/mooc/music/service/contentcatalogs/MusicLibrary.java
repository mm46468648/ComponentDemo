/*
 * Copyright 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mooc.music.service.contentcatalogs;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.util.Log;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class MusicLibrary {

    public static final HashMap<String, MediaMetadataCompat> music = new LinkedHashMap<>();


    public static String getRoot() {
        return "com.moocxuetang";
    }


    public static List<MediaBrowserCompat.MediaItem> getMediaItems() {
        List<MediaBrowserCompat.MediaItem> result = new ArrayList<>();
        for (MediaMetadataCompat metadata : music.values()) {
            result.add(
                    new MediaBrowserCompat.MediaItem(
                            metadata.getDescription(), MediaBrowserCompat.MediaItem.FLAG_PLAYABLE));
        }
        return result;
    }


//    public static Bitmap getAlbumBitmap(String mediaId) {
//        Bitmap bitmap;
//        InputStream in = null;
//        try {
//            in = getInputStream(music.get(mediaId).getString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI));
//        } catch (IOException e) {
//            Log.i("error", e.getMessage());
//        } finally {
//            try {
//                if (in != null) {
//                    in.close();
//                }
//            } catch (IOException e) {
//                Log.i("error", e.getMessage());
//            }
//        }
//        bitmap = BitmapFactory.decodeStream(in);
//        return bitmap;
//    }
//
//
//    public static InputStream getInputStream(String path) throws IOException {
//        URL url = new URL(path);
//        HttpURLConnection httpURLconnection = (HttpURLConnection) url.openConnection();
//        httpURLconnection.setRequestMethod("GET");
//        httpURLconnection.setReadTimeout(6 * 1000);
//        InputStream in = null;
//        if (httpURLconnection.getResponseCode() == 200) {
//            in = httpURLconnection.getInputStream();
//            return in;
//        }
//        return null;
//    }


//    public static MediaMetadataCompat getTrackPlayWithTitle(String title) {
//        return music.get(title);
//    }
//
//    public static MediaMetadataCompat getTrackPlayWithId(String id) {
//        return music.get(id);
//    }

    public static MediaMetadataCompat getMetadata(Context context, String mediaId) {
        MediaMetadataCompat metadataWithoutBitmap = music.get(mediaId);

        if (metadataWithoutBitmap == null) {
            return null;
        }
        // Since MediaMetadataCompat is immutable, we need to create a copy to set the album art.
        // We don't set it initially on all items so that they don't take unnecessary memory.
        MediaMetadataCompat.Builder builder = new MediaMetadataCompat.Builder();
        for (String key :
                new String[]{
                        MediaMetadataCompat.METADATA_KEY_MEDIA_ID,
                        MediaMetadataCompat.METADATA_KEY_TITLE,
                        MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON_URI,
                        MediaMetadataCompat.METADATA_KEY_ALBUM,
                        MediaMetadataCompat.METADATA_KEY_GENRE
                        , MediaMetadataCompat.METADATA_KEY_MEDIA_URI
                        , MediaMetadataCompat.METADATA_KEY_WRITER
                        , MediaMetadataCompat.METADATA_KEY_AUTHOR
                        , MediaMetadataCompat.METADATA_KEY_COMPILATION
                }) {
            builder.putString(key, metadataWithoutBitmap.getString(key));
        }
        builder.putLong(MediaMetadataCompat.METADATA_KEY_BT_FOLDER_TYPE,
                metadataWithoutBitmap.getLong(MediaMetadataCompat.METADATA_KEY_BT_FOLDER_TYPE));
        builder.putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON_URI, metadataWithoutBitmap.getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON_URI));
        builder.putLong(
                MediaMetadataCompat.METADATA_KEY_DURATION,
                metadataWithoutBitmap.getLong(MediaMetadataCompat.METADATA_KEY_DURATION));
        builder.putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, metadataWithoutBitmap.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID));
        return builder.build();
    }

    public static int getMetaDataPosition(String mediaId) {
        if (music.size() == 0) {
            return -1;
        }
        Set<Map.Entry<String, MediaMetadataCompat>> entrySet = music.entrySet();

        Iterator<Map.Entry<String, MediaMetadataCompat>> it = entrySet.iterator();
        int i = 0;
        while (it.hasNext()) {
            i++;
            Map.Entry<String, MediaMetadataCompat> next = it.next();
            MediaMetadataCompat compat = next.getValue();
            if (compat.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID).equals(mediaId)) {
                return i - 1;
            }
        }

        return -1;
    }


    //需要title 、cover、orgznizer、progress、duration、url
    public static void createMediaMetadataCompat(
            String mediaId,
            String title,
            String cover,
            String orgznizer,
            long progress,
            long duration, String url, String is_enroll, String pv, long id, String content) {
        music.put(
                mediaId,
                new MediaMetadataCompat.Builder()
                        .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, mediaId)
                        .putString(MediaMetadataCompat.METADATA_KEY_TITLE, title)
                        .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON_URI, cover)
                        .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, orgznizer)
                        .putString(MediaMetadataCompat.METADATA_KEY_GENRE, progress + "")
                        .putLong(
                                MediaMetadataCompat.METADATA_KEY_DURATION,
                                duration)
                        .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, url)
                        .putString(MediaMetadataCompat.METADATA_KEY_WRITER, is_enroll)
                        .putString(MediaMetadataCompat.METADATA_KEY_AUTHOR, pv)
                        .putLong(MediaMetadataCompat.METADATA_KEY_BT_FOLDER_TYPE, id)
                        .putString(MediaMetadataCompat.METADATA_KEY_COMPILATION, content)
                        .build());
    }



}