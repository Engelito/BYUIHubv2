package com.byuihub.byuistudenthub;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class Links extends AppCompatActivity {

    private static final String TAG = "Links";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "Creating Links Activity");
        setContentView(R.layout.activity_links);
    }

    /**
     * This method redirects the user to the Pulse website.
     * @param view
     */
    public void navPulse(View view) {
        Uri uri = Uri.parse("https://d2l.com/products/pulse");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        Log.i(TAG, "Redirecting to Pulse");
        startActivity(intent);
    }

    /**
     * This method redirects the user to the Scroll website.
     * @param view
     */
    public void navScroll(View view) {
        Uri uri = Uri.parse("https://byuiscroll.org");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        Log.i(TAG, "Redirecting to Scroll");
        startActivity(intent);
    }

    /**
     * This method redirects the user to the BYU-Idaho master calender.
     * @param view
     */
    public void navCal(View view) {
        Uri uri = Uri.parse("http://calendar.byui.edu/MasterCalendar.aspx");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        Log.i(TAG, "Redirecting to Master Calendar");
        startActivity(intent);
    }
}
