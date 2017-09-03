package com.wiretech.df.dfmusicbeta.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;

import com.wiretech.df.dfmusicbeta.classes.Player;
import com.wiretech.df.dfmusicbeta.classes.PlayerManager;

import static android.view.KeyEvent.ACTION_DOWN;
import static android.view.KeyEvent.KEYCODE_HEADSETHOOK;
import static android.view.KeyEvent.KEYCODE_MEDIA_CLOSE;
import static android.view.KeyEvent.KEYCODE_MEDIA_NEXT;
import static android.view.KeyEvent.KEYCODE_MEDIA_PAUSE;
import static android.view.KeyEvent.KEYCODE_MEDIA_PLAY;
import static android.view.KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE;
import static android.view.KeyEvent.KEYCODE_MEDIA_PREVIOUS;
import static android.view.KeyEvent.KEYCODE_MEDIA_STOP;

public class RemoteControlReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_MEDIA_BUTTON.equals(intent.getAction())) {
            KeyEvent event = intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
            if (event.getAction() == ACTION_DOWN) {
                switch (event.getKeyCode()) {
                    case KEYCODE_MEDIA_PLAY_PAUSE:
                    case KEYCODE_HEADSETHOOK:
                        if (Player.get().getState() == Player.PlayerState.PLAYING) {
                            PlayerManager.get().pause(context);
                        } else if (Player.get().getState() == Player.PlayerState.PAUSE) {
                            PlayerManager.get().resume(context);
                        }
                        break;
                    case KEYCODE_MEDIA_PLAY:
                        PlayerManager.get().resume(context);
                        break;
                    case KEYCODE_MEDIA_PAUSE:
                        PlayerManager.get().pause(context);
                        break;
                    case KEYCODE_MEDIA_NEXT:
                        PlayerManager.get().next(context);
                        break;
                    case KEYCODE_MEDIA_PREVIOUS:
                        PlayerManager.get().previous(context);
                        break;
                    case KEYCODE_MEDIA_CLOSE:
                    case KEYCODE_MEDIA_STOP:
                        PlayerManager.get().stop(context);
                        break;
                }
            }
        }
    }
}
