package com.putaworks.mcalc;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;



public class MainActivity extends AppCompatActivity {


    private DatabaseHelper mDBHelper;
    private SQLiteDatabase mDb;
    EditText mEdit, gEdit,sEdit;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEdit   = (EditText)findViewById(R.id.lvl_edit);
        mEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                mEdit   = (EditText)findViewById(R.id.lvl_edit);
                String  level =mEdit.getText().toString();
                TextView tv = findViewById(R.id.Show_lvl);
                tv.setText("Level gives troops:\n"+getInformation(mDb,"Levelup_troops", level));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        gEdit   = (EditText)findViewById(R.id.garr_from);
        gEdit.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                gEdit   = (EditText)findViewById(R.id.garr_from);
                String  level =gEdit.getText().toString();
                TextView tv = findViewById(R.id.show);
                tv.setText(getGarrison(mDb,"Levelup_troops", level)+" m");
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        sEdit   = (EditText)findViewById(R.id.garr_money);
        sEdit.addTextChangedListener(new TextWatcher() {

                                         @Override
                                         public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                                         }

                                         @Override
                                         public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                                             sEdit   = (EditText)findViewById(R.id.garr_money);
                                             String cautious = sEdit.getText().toString();



                                             if(cautious.equals( "" )) {
                                                 cautious="0";
                                             }

                                             TextView tv = findViewById(R.id.show);


                                             String lvl = getlvl_by_Cost(mDb, "Levelup_troops", cautious);

                                             String price =getGarrisonCost(mDb,"Levelup_troops", lvl);


                                             tv.setText("You can do "  +lvl+ " lvl Castle\n It costs: "+price +"m");

                                         }
                                         @Override
                                         public void afterTextChanged(Editable editable) {

                                         }
                                     });
        mDBHelper = new DatabaseHelper(this);

        try {
            mDBHelper.updateDataBase();
        } catch (IOException mIOException) {
            throw new Error("UnableToUpdateDatabase");
        }

        try {
            mDb = mDBHelper.getWritableDatabase();
        } catch (SQLException mSQLException) {
            throw mSQLException;
        }



    }





    public void openAttackActivity(View view){
        Intent intent = new Intent(this,AttackActivity.class);
        startActivity(intent);
    }

    public void openDefenceActivity(View view){
        Intent intent = new Intent(this,DefenceActivity.class);
        startActivity(intent);
    }


    public void exitprogram(View view){
        finish();
        System.exit(0);
    }






    public void buttonClick_lvlfind(View view){

        gEdit   = (EditText)findViewById(R.id.garr_from);
        TextView tv = findViewById(R.id.show);
        tv.setText(gEdit.getText().toString());

    }

    public String getInformation(SQLiteDatabase db, String tableName, String find){

        Cursor allRows  = db.rawQuery("SELECT * FROM " + tableName, null);
        allRows.moveToFirst();
        do{
            if (find.equals(allRows.getString(0))){
                String price = allRows.getString(1);
                allRows.close();
                return price;
            }

        }while(allRows.moveToNext());
        allRows.close();
        return null;
    }


    public String getGarrison(SQLiteDatabase db, String tableName, String find){

        Cursor allRows  = db.rawQuery("SELECT * FROM " + tableName, null);
        allRows.moveToFirst();
        do{
            if (find.equals(allRows.getString(4))){
                String price = allRows.getString(5);
                String both = "Size: "+ price + "m\nCost: " + allRows.getString(6);

                allRows.close();
                return (both);
            }

        }while(allRows.moveToNext());
        allRows.close();
        return null;
    }


    public String getlvl_by_Cost(SQLiteDatabase db, String tableName, String find){

        double d=Double.parseDouble(find);

        if(d>3400) {
            return ("100");
        }

        Cursor allRows  = db.rawQuery("SELECT * FROM " + tableName, null);
        allRows.moveToFirst();
        do{
            if (d<Double.parseDouble(allRows.getString(6))){
                String price = allRows.getString(4);

                allRows.close();
                int priceD = Integer.parseInt(price)-1;
                 price = Integer.toString(priceD);


                return (price);
            }

        }while(allRows.moveToNext());
        allRows.close();
        return null;


    }

    public String getGarrisonCost(SQLiteDatabase db, String tableName, String find){



        Cursor allRows  = db.rawQuery("SELECT * FROM " + tableName, null);
        allRows.moveToFirst();
        do{
            if (find.equals(allRows.getString(4))){
                String price = allRows.getString(6);
                allRows.close();
                return (price);
            }

        }while(allRows.moveToNext());
        allRows.close();
        return null;
    }






}

