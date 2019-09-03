package com.putaworks.mcalc;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class AttackActivity extends AppCompatActivity {


    private DatabaseHelper mDBHelper;
    private SQLiteDatabase mDb;

    EditText sEdit, gEdit,tEdit,mEdit;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attack);






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



    public String getGarrison(SQLiteDatabase db, String tableName, String find){

        Cursor allRows  = db.rawQuery("SELECT * FROM " + tableName, null);
        allRows.moveToFirst();
        do{
            if (find.equals(allRows.getString(4))){
                String price = allRows.getString(5);
                allRows.close();
                return price;
            }

        }while(allRows.moveToNext());
        allRows.close();
        return null;
    }

    public void calculate_btn(View view){

        gEdit   = (EditText)findViewById(R.id.garr_from);
        String  level =gEdit.getText().toString();

        sEdit   = (EditText)findViewById(R.id.striker);
        String  striker =sEdit.getText().toString();

        mEdit   = (EditText)findViewById(R.id.salvager);
        String  multiplier =mEdit.getText().toString();

        tEdit   = (EditText)findViewById(R.id.troops);
        String  troops =tEdit.getText().toString();

        if(level.equals( "" )) {
            level="0";
        }
        if(striker.equals( "" )) {
            striker="1";
        }
        if(multiplier.equals( "" )) {
            multiplier="100";
        }
        if(troops.equals( "" )) {
            troops="1";
        }
        int result2 = Integer.parseInt(level);

        if(result2>100) {
            level="100";
        }
        if(result2<42) {
            level="42";
        }

        double t=Double.parseDouble(troops);
        double s=Double.parseDouble(striker);

        double multiplierS=Double.parseDouble(multiplier);



        String gsize = getGarrison(mDb,"Levelup_troops",level);

        gsize = String.format("%.4s", gsize);

        double gsizeD=Double.parseDouble(gsize);

        double power_multiplier=((t*(s/100+1))/gsizeD);

        String powerS = Double.toString(power_multiplier);


        String mplier = getmMltiplier(mDb,"Levelup_troops",powerS);

        double mpli=Double.parseDouble(mplier);



        double alive = t*(mpli/100);
        double deaths = t-alive;

        double xp_garr = gsizeD*(multiplierS/100);
        double xp_total = xp_garr+(deaths*(multiplierS/100));



        if(alive==0){

            xp_total=deaths*(multiplierS/100);
        }
        double result = xp_total/(deaths/2);


        String resultS = Double.toString(result);
        resultS = String.format("%.4s",resultS);

        String aliveS = Double.toString(alive);
        aliveS = String.format("%.4s",aliveS);

        String xp_totalS = Double.toString(xp_total);
        xp_totalS = String.format("%.4s",xp_totalS);


        TextView tv = findViewById(R.id.show_info);
        tv.setText("Troops left:"+ aliveS+"m"+"\n" +"Xp: "+ xp_totalS+"m"+"\n" +"Rate: "+resultS);
    }


    public String getmMltiplier(SQLiteDatabase db, String tableName, String find){


        double d=Double.parseDouble(find);

       Cursor allRows  =db.query(tableName, new String[] { "percents" },
                null, null, null, null,
                "abs(multiplier - " + d + ")", "1");

        allRows.moveToFirst();
        String price = allRows.getString(0);

        allRows.close();
       return price;
    }

    public void Goback (View view){
        finish();
    }


    public void buttonClick_lvlfind(View view){

        gEdit   = (EditText)findViewById(R.id.garr_from);
        String  level =gEdit.getText().toString();

        TextView tv = findViewById(R.id.show_info);

        String  result = getGarrison(mDb,"Levelup_troops", level);

        result = String.format("%.4s", result);
        double d=Double.parseDouble(result);

        result = Double.toString(d);

        tv.setText(result);


       // tv.setText(getmMltiplier(mDb,"Levelup_troops", level));
    }

}
