package com.zjw.apps3pluspro.utils;

import android.content.ComponentName;
import android.content.Context;
import android.media.MediaMetadata;
import android.media.session.MediaController;
import android.media.session.MediaSession;
import android.media.session.MediaSessionManager;
import android.media.session.PlaybackState;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import android.util.Log;

import com.zjw.apps3pluspro.application.BaseApplication;
import com.zjw.apps3pluspro.bleservice.MyNotificationsListenerService;

import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class MusicSyncManager {

    private static final String TAG = MusicSyncManager.class.getSimpleName();
    private static MusicSyncManager musicSyncManager;


    public static MusicSyncManager getInstance() {
        if (musicSyncManager == null) {
            musicSyncManager = new MusicSyncManager();
        }
        return musicSyncManager;
    }

    private MusicSyncManager() {
    }

    public interface MusicSyncListener {
        void onPlaybackStateChanged(PlaybackState state);

        void onMetadataChanged(String title);
    }

    private MusicSyncListener musicSyncListener;

    public boolean isSessionDestroyed = true;

    public void setMusicListener(MusicSyncListener musicSyncListener) {
        Log.i(TAG, "setMusicListener init");
        this.musicSyncListener = musicSyncListener;

        if (this.mediaController != null) {
            this.mediaController.unregisterCallback(mCallback);
        } else {
            Log.i(TAG, "mediaController = null");
        }

        if (!MyNotificationsListenerService.isEnabled(BaseApplication.getmContext())) {
            Log.i(TAG, "NotificationListenerService is not start");
            return;
        }

        MediaSessionManager mm = (MediaSessionManager) BaseApplication.getmContext().getSystemService(Context.MEDIA_SESSION_SERVICE);
        if (mm != null && MyNotificationsListenerService.context != null) {
            List<MediaController> controllers = mm.getActiveSessions(new ComponentName(MyNotificationsListenerService.context, MyNotificationsListenerService.class));
            for (int i = 0; i < controllers.size(); i++) {
                Log.i(TAG, " packname=" + controllers.get(i).getPackageName() + "  index=" + i);
                setCallBack(controllers.get(0), musicSyncListener);
            }
        }
    }

    MediaController.TransportControls mediaControllerCntrl;
    MediaController mediaController;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setCallBack(final MediaController mediaController, final MusicSyncListener musicSyncListener) {

        if (mediaController == null) {
            return;
        }

        mediaControllerCntrl = mediaController.getTransportControls();
        this.mediaController = mediaController;

        MediaMetadata metadata = mediaController.getMetadata();
        if (metadata != null) {
            String artist = metadata.getString(MediaMetadata.METADATA_KEY_ARTIST);
            String title = metadata.getString(MediaMetadata.METADATA_KEY_TITLE);
            SysUtils.logContentI(TAG, "init metadata title =" + title);
            if (title == null) {
                return;
            }
            musicSyncListener.onMetadataChanged(title);
        }

        if (mediaController.getPlaybackState() != null) {
            int state = mediaController.getPlaybackState().getState();
            Log.i(TAG, "init PlaybackState=" + state);
            musicSyncListener.onPlaybackStateChanged(mediaController.getPlaybackState());
        }

        isSessionDestroyed = false;
        mediaController.registerCallback(mCallback);
    }


    MediaController.Callback mCallback = new MediaController.Callback() {
        @Override
        public void onSessionDestroyed() {
            super.onSessionDestroyed();
            isSessionDestroyed = true;
            Log.d(TAG, "onSessionDestroyed");
        }

        @Override
        public void onSessionEvent(@NonNull String event, @Nullable Bundle extras) {
            super.onSessionEvent(event, extras);
            Log.d(TAG, "onSessionEvent extras=" + extras);
        }

        @Override
        public void onPlaybackStateChanged(@Nullable PlaybackState state) {
            super.onPlaybackStateChanged(state);
            Log.i(TAG, "onPlaybackStateChanged state=" + state.getState() + " speed=" + state.getPlaybackSpeed());
            musicSyncListener.onPlaybackStateChanged(mediaController.getPlaybackState());
        }

        @Override
        public void onMetadataChanged(@Nullable MediaMetadata metadata) {
            super.onMetadataChanged(metadata);
            if (metadata == null) {
                return;
            }
            String artist = metadata.getString(MediaMetadata.METADATA_KEY_ARTIST);
            String title = metadata.getString(MediaMetadata.METADATA_KEY_TITLE);

            SysUtils.logContentI(TAG, "onMetadataChanged ==============================");
            SysUtils.logContentI(TAG, "onMetadataChanged title=" + title);


//            String METADATA_KEY_ALBUM = metadata.getString(MediaMetadata.METADATA_KEY_ALBUM);
//            String METADATA_KEY_AUTHOR = metadata.getString(MediaMetadata.METADATA_KEY_AUTHOR);
//            String METADATA_KEY_WRITER = metadata.getString(MediaMetadata.METADATA_KEY_WRITER);
//            String METADATA_KEY_COMPOSER = metadata.getString(MediaMetadata.METADATA_KEY_COMPOSER);
//            String METADATA_KEY_COMPILATION = metadata.getString(MediaMetadata.METADATA_KEY_COMPILATION);
//            String METADATA_KEY_DATE = metadata.getString(MediaMetadata.METADATA_KEY_DATE);
//            String METADATA_KEY_GENRE = metadata.getString(MediaMetadata.METADATA_KEY_GENRE);
//            String METADATA_KEY_ART_URI = metadata.getString(MediaMetadata.METADATA_KEY_ART_URI);
//            String METADATA_KEY_ALBUM_ART_URI = metadata.getString(MediaMetadata.METADATA_KEY_ALBUM_ART_URI);
//            String METADATA_KEY_DISPLAY_TITLE = metadata.getString(MediaMetadata.METADATA_KEY_DISPLAY_TITLE);
//            String METADATA_KEY_DISPLAY_SUBTITLE = metadata.getString(MediaMetadata.METADATA_KEY_DISPLAY_SUBTITLE);
//            String METADATA_KEY_DISPLAY_DESCRIPTION = metadata.getString(MediaMetadata.METADATA_KEY_DISPLAY_DESCRIPTION);
//            String METADATA_KEY_DISPLAY_ICON_URI = metadata.getString(MediaMetadata.METADATA_KEY_DISPLAY_ICON_URI);
//            String METADATA_KEY_MEDIA_ID = metadata.getString(MediaMetadata.METADATA_KEY_MEDIA_ID);
//            String METADATA_KEY_MEDIA_URI = metadata.getString(MediaMetadata.METADATA_KEY_MEDIA_URI);
////
//            MyLog.i(TAG, "onMetadataChanged METADATA_KEY_ALBUM=" + METADATA_KEY_ALBUM);
//            MyLog.i(TAG, "onMetadataChanged METADATA_KEY_AUTHOR=" + METADATA_KEY_AUTHOR);
//            MyLog.i(TAG, "onMetadataChanged METADATA_KEY_WRITER=" + METADATA_KEY_WRITER);
//            MyLog.i(TAG, "onMetadataChanged METADATA_KEY_COMPOSER=" + METADATA_KEY_COMPOSER);
//            MyLog.i(TAG, "onMetadataChanged METADATA_KEY_COMPILATION=" + METADATA_KEY_COMPILATION);
//            MyLog.i(TAG, "onMetadataChanged METADATA_KEY_DATE=" + METADATA_KEY_DATE);
//            MyLog.i(TAG, "onMetadataChanged METADATA_KEY_GENRE=" + METADATA_KEY_GENRE);
//            MyLog.i(TAG, "onMetadataChanged METADATA_KEY_ART_URI=" + METADATA_KEY_ART_URI);
//            MyLog.i(TAG, "onMetadataChanged METADATA_KEY_ALBUM_ART_URI=" + METADATA_KEY_ALBUM_ART_URI);
//            MyLog.i(TAG, "onMetadataChanged METADATA_KEY_DISPLAY_TITLE=" + METADATA_KEY_DISPLAY_TITLE);
//            MyLog.i(TAG, "onMetadataChanged METADATA_KEY_DISPLAY_SUBTITLE=" + METADATA_KEY_DISPLAY_SUBTITLE);
//            MyLog.i(TAG, "onMetadataChanged METADATA_KEY_DISPLAY_DESCRIPTION=" + METADATA_KEY_DISPLAY_DESCRIPTION);
//            MyLog.i(TAG, "onMetadataChanged METADATA_KEY_DISPLAY_ICON_URI=" + METADATA_KEY_DISPLAY_ICON_URI);
//            MyLog.i(TAG, "onMetadataChanged METADATA_KEY_MEDIA_ID=" + METADATA_KEY_MEDIA_ID);
//            MyLog.i(TAG, "onMetadataChanged METADATA_KEY_MEDIA_URI=" + METADATA_KEY_MEDIA_URI);


            if (title == null) {
                return;
            }
            musicSyncListener.onMetadataChanged(title);
        }

        @Override
        public void onQueueChanged(@Nullable List<MediaSession.QueueItem> queue) {
            super.onQueueChanged(queue);
            Log.d(TAG, "onQueueChanged");
        }

        @Override
        public void onQueueTitleChanged(@Nullable CharSequence title) {
            super.onQueueTitleChanged(title);
            Log.d(TAG, "onQueueTitleChanged " + title);
        }

        @Override
        public void onExtrasChanged(@Nullable Bundle extras) {
            super.onExtrasChanged(extras);
            Log.d(TAG, "onExtrasChanged");
        }

        @Override
        public void onAudioInfoChanged(MediaController.PlaybackInfo info) {
            super.onAudioInfoChanged(info);
            Log.d(TAG, "onAudioInfoChanged");
        }
    };
}
