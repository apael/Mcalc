package com.putaworks.mcalc;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.text.DecimalFormat;

public class DefenceActivity extends AppCompatActivity {


    private DatabaseHelper mDBHelper;
    private SQLiteDatabase mDb;
    private  double caumoney;
    EditText sEdit, gEdit,tEdit,mEdit,slEdit,gtEdit,cEdit;
    DecimalFormat millions = new DecimalFormat("###.##m");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_defence);



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

    public void calculate_btn(View view) {
        boolean etsi=false;
        gEdit = (EditText) findViewById(R.id.garr_to);
        String level = gEdit.getText().toString();

        sEdit = (EditText) findViewById(R.id.striker);
        String striker = sEdit.getText().toString();

        mEdit = (EditText) findViewById(R.id.multiplier);
        String multiplier = mEdit.getText().toString();

        tEdit = (EditText) findViewById(R.id.troops);
        String troops = tEdit.getText().toString();

        slEdit = (EditText) findViewById(R.id.salvager);
        String salvager = slEdit.getText().toString();

        cEdit = (EditText) findViewById(R.id.cautious);
        String cautious = cEdit.getText().toString();

        gtEdit = (EditText) findViewById(R.id.garr_from);
        String garr_fr = gtEdit.getText().toString();

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
        if(salvager.equals( "" )) {
            salvager="0";
        }
        if(cautious.equals( "" )) {
            cautious="0";
        }
        if(garr_fr.equals( "" )) {
            garr_fr="42";
        }


        double t = Double.parseDouble(troops);
        double s = Double.parseDouble(striker);
        double q = Double.parseDouble(garr_fr);

        if(q<42){
            q=42;
        }
        if(q>100){
            q=100;
        }




        String pwr = Double.toString(t * (s / 100 + 1));

        if(t * (s / 100 + 1)>2000){
            TextView tv = findViewById(R.id.show_info);

            tv.setText("To high power to catch w/o troops");
            return;

        }

        int result2 = Integer.parseInt(level);
        if (result2 > 100) {
            level="100";
        }
        if (result2 < 42) {

            level = getlvl_by_pwr(mDb, "Levelup_troops", t * (s / 100 + 1));

             etsi=true;
        }

        double multiplierS = Double.parseDouble(multiplier);


        String gsize = getGarrison(mDb, "Levelup_troops", level);
                gsize = String.format("%.4s", gsize);

        double gsizeD = Double.parseDouble(gsize);

        double power_multiplier = ((t * (s / 100 + 1)) / gsizeD);

        String powerS = Double.toString(power_multiplier);


        String mplier = getmMltiplier(mDb, "Levelup_troops", powerS);

        double mpli = Double.parseDouble(mplier);


        double alive = t * (mpli / 100);
        double deaths = t - alive;

        double xp_total = (deaths * 2 * (multiplierS / 100));

        double salmoney = salvager_money(salvager, deaths);


            caumoney = cautious_money(cautious, q, level);

        if (deaths==t) {
            caumoney = 0;
            xp_total = deaths * 2 * (multiplierS / 100);
        }

         double result = cautious_money("100", q, level);
        double total_money = (salmoney + caumoney) - result;
        String total_moneyS = millions.format(total_money);
        String resultS = millions.format(result);


        String aliveS = Double.toString(alive);
        aliveS = String.format("%.4s", aliveS);

        String xp_totalS = Double.toString(xp_total);
        xp_totalS = String.format("%.4s", xp_totalS);

        pwr =String.format("%.4s",pwr);
        TextView tv = findViewById(R.id.show_info);
        tv.setMovementMethod(new ScrollingMovementMethod());
        tv.setText("Enemy pwr:\n" + pwr + "m" + "\n\n" + "Troops left:\n" + aliveS + "m" + "\n\n" + "Xp: \n" + xp_totalS + "m" + "\n\n" + "Profit:\n " + total_moneyS + "\n\n" + "Cost to make: \n" + resultS);

        if (etsi == true){
        tv = findViewById(R.id.show_info);
        tv.setText("Enemy pwr:\n" + pwr + "m" + "\n\n" + "Castle lvl to catch:\n" + level + "\n\n" + "Xp: \n" + xp_totalS + "m" + "\n\n" + "Profit:\n " + total_moneyS + "\n\n" + "Cost to make: \n" + resultS);
    }
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
    public double salvager_money(String salvager,double kills){


        double slvgrD=Double.parseDouble(salvager)/100;

        slvgrD = slvgrD*kills;

        return slvgrD;

    }

    public double cautious_money(String cautious,double low, String high){


        int i = (int) low;

        double cauD=Double.parseDouble(cautious);

        String lowS = getGarrison_cost(mDb, "Levelup_troops",   Integer.toString(i));
        double lowD=Double.parseDouble(lowS);

        String highS = getGarrison_cost(mDb, "Levelup_troops",  high);
        double highD=Double.parseDouble(highS);


        return ((highD-lowD)*(cauD/100));

    }

    public String getGarrison_cost(SQLiteDatabase db, String tableName, String y){

        Cursor allRows  = db.rawQuery("SELECT * FROM " + tableName, null);
        allRows.moveToFirst();
        do{
            if (y.equals(allRows.getString(4))){
                String price = allRows.getString(6);
                allRows.close();
                return price;
            }

        }while(allRows.moveToNext());
        allRows.close();
        return null;
    }

    public String getlvl_by_pwr(SQLiteDatabase db, String tableName, double find){



        Cursor allRows  = db.rawQuery("SELECT * FROM " + tableName, null);
        allRows.moveToFirst();
        do{
            if (find<Double.parseDouble(allRows.getString(5))){
                String price = allRows.getString(4);

                allRows.close();

                return (price);
            }

        }while(allRows.moveToNext());
        allRows.close();
        return null;


    }


}