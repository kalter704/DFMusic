package com.wiretech.df.dfmusic.Classes;

import android.content.Context;
import android.content.Intent;

import com.wiretech.df.dfmusic.R;

public class Share {
    public static void share(Context context) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.text_for_share));
        context.startActivity(Intent.createChooser(intent, context.getString(R.string.text_description_action)));
    }
}
