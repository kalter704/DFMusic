package com.wiretech.df.dfmusic.classes;

import android.content.Context;
import android.content.Intent;

import com.wiretech.df.dfmusic.R;

public class Share {
    public static void share(Context context, String text) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, text);
        context.startActivity(Intent.createChooser(intent, context.getString(R.string.text_description_action)));
    }
}
