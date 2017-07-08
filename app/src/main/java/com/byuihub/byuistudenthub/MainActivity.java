package com.byuihub.byuistudenthub;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "Creating Main.");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * This method redirects the user to the Map Navigation activity.
     *
     * @param view
     */
    public void navMap(View view) {
        Intent intent = new Intent(this, Map.class);
        Log.i(TAG, "Redirecting to Map");
        startActivity(intent);
    }

    /**
     * This method redirects the user to the Recommended Links activity.
     *
     * @param view
     */
    public void navLinks(View view) {
        Intent intent = new Intent(this, Links.class);
        Log.i(TAG, "Redirecting to Links");
        startActivity(intent);
    }

    /**
     * This method redirects the user to the To-Do List activity.
     *
     * @param view
     */
    public void navTodo(View view) {
        Intent intent = new Intent(this, Todo.class);
        Log.i(TAG, "Redirecting to Todo");
        startActivity(intent);
    }
}
