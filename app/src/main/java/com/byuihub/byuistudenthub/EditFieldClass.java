package com.byuihub.byuistudenthub;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import static com.byuihub.byuistudenthub.R.id.message;

public class EditFieldClass extends AppCompatActivity {

    private static final String TAG = "EditFieldClass";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("EditFieldClass","entering on create");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.to_do_layout);
    }

    /**
     * This method takes input from the user, saves it into a database, which
     * is then added to the To-Do list.
     *
     * @param v
     */
    public void saveButtonClicked02(View v) {

        Log.i("EditFieldClass","Entering saved Button");
        String messageText = ((EditText)findViewById(message)).getText().toString();
        if(messageText.equals("")){
        }
        else{
            SharedPreferences sharedPref = getSharedPreferences("list", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            int arraySize = sharedPref.getInt("listSize", 0);
            arraySize += 1;
            Log.i("EditFieldClass","arraySize updated: " + arraySize);
            editor.putInt("listSize", arraySize );
            editor.putString("listItem_" + arraySize,messageText);
            Log.i("EditFieldClass","messageText added" + messageText);
            editor.apply();


            Intent intent = new Intent();
            intent.putExtra(Intent_Constants.INTENT_MESSAGE_FIELD,messageText);
            setResult(Intent_Constants.INTENT_RESULT_CODE, intent);
            finish();

        }
    }
}
