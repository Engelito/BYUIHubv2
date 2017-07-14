package com.byuihub.byuistudenthub;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

import static com.google.android.gms.common.internal.safeparcel.SafeParcelable.NULL;

public class Todo extends AppCompatActivity {

    private static final String TAG = "Todo";
    ListView listView;
    ArrayList<String> arrayList;
    ArrayAdapter<String> arrayAdapter;
    String messageText;
    int position;

    public Button addErrand;

    /**
     *
     */
    public void init(){
        addErrand = (Button)findViewById(R.id.addErrand);
        addErrand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("todoClass","21");
                Intent intent = new Intent();
                Log.i("todoClass","22");
                intent.setClass(Todo.this,EditMessageClass.class);
//                Log.i("todoClass","23");
//                intent.putExtra(Intent_Constants.INTENT_MESSAGE_DATA,arrayList.get(position).toString());
//                Log.i("todoClass","24");
//                intent.putExtra(Intent_Constants.INTENT_ITEM_POSITION,position);
//                Log.i("todoClass","25");
                startActivityForResult(intent,Intent_Constants.INTENT_REQUEST_CODE_TWO);
//                Log.i("todoClass","26");
            }
        });
    }

    /**
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("todoClass","entering on create");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo);
        init();
        listView = (ListView)findViewById(R.id.listView);
        arrayList = new ArrayList<>();
        SharedPreferences sharedPref = getSharedPreferences("list", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("listSize", 1 );
        editor.putString("listItem_" + 1,"Dont crash");

        int arraySize = sharedPref.getInt("listSize", 0);
        arrayList = new ArrayList<>();
        for(int i = 1; i < arraySize; i++)
        {
            arrayList.add(sharedPref.getString("listItem_" + i, NULL));
        }
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line,arrayList);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent();
                intent.setClass(Todo.this,EditMessageClass.class);
                intent.putExtra(Intent_Constants.INTENT_MESSAGE_DATA,arrayList.get(position).toString());
                intent.putExtra(Intent_Constants.INTENT_ITEM_POSITION,position);
                startActivityForResult(intent,Intent_Constants.INTENT_REQUEST_CODE_TWO);
            }
        });
    }

    /**
     *
     * @param v
     */
    public void onClick(View v){
        Log.i("todoClass","1");
        Intent intent = new Intent();
        Log.i("todoClass","2");
        intent.setClass(Todo.this, EditMessageClass.class);
        Log.i("todoClass","3");
        startActivityForResult(intent, Intent_Constants.INTENT_REQUEST_CODE);
        Log.i("todoClass","4");
    }

    public void clearButton(View v)
    {
        Log.i("todoClass","inside of clear button");
        SharedPreferences sharedPref = getSharedPreferences("list", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();
        editor.putInt("arraySize", 1);
        editor.apply();

        Intent intent = new Intent(this, Todo.class);
        Log.i(TAG, "Redirecting to Todo");
        startActivity(intent);
    }


    /**
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {

        Log.i("todoClass","entering on activity result");
        SharedPreferences sharedPref = getSharedPreferences("list", Context.MODE_PRIVATE);
        int arraySize = sharedPref.getInt("listSize", 0);
        arrayList = new ArrayList<>();
        for(int i = 1; i <= arraySize; i++)
        {
            String tempString = sharedPref.getString("listItem_" + i, NULL);
            Log.i("todoClass",tempString);
            arrayList.add(i - 1,tempString);
        }
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line,arrayList);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent();
                intent.setClass(Todo.this,EditMessageClass.class);
                intent.putExtra(Intent_Constants.INTENT_MESSAGE_DATA,arrayList.get(position).toString());
                intent.putExtra(Intent_Constants.INTENT_ITEM_POSITION,position);
                startActivityForResult(intent,Intent_Constants.INTENT_REQUEST_CODE_TWO);

//        if(resultCode==Intent_Constants.INTENT_REQUEST_CODE){
//            messageText = data.getStringExtra(Intent_Constants.INTENT_MESSAGE_FIELD);
//            arrayList.add(messageText);
//            arrayAdapter.notifyDataSetChanged();
//        }
//        else if(resultCode==Intent_Constants.INTENT_REQUEST_CODE_TWO){
//            messageText = data.getStringExtra(Intent_Constants.INTENT_CHANGED_MESSAGE);
//            position = data.getIntExtra(Intent_Constants.INTENT_ITEM_POSITION,-1);
//            arrayList.add(position,messageText);
//            arrayAdapter.notifyDataSetChanged();
//        }
    }
        });
    }
}