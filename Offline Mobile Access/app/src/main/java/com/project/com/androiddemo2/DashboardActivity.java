package com.project.com.androiddemo2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by APEXNEWPC1 on 21-Dec-17.
 */

public class DashboardActivity extends AppCompatActivity implements View.OnClickListener
{

    Button b1,b2,b3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        b1= (Button) findViewById(R.id.button);
        b1.setOnClickListener(this);

        b2= (Button) findViewById(R.id.button3);
        b2.setOnClickListener(this);

        b3= (Button) findViewById(R.id.button4);
        b3.setOnClickListener(this);
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }

    @Override
    public void onClick(View v) {
        if(v==b1)
        {

            Toast.makeText(DashboardActivity.this,"A BUTTON3 IS CLIOSKED",Toast.LENGTH_LONG).show();
            Intent i = new Intent(DashboardActivity.this,ForgotpinActivity.class);
            startActivity(i);

        }
        if(v==b2)
        {
            Toast.makeText(DashboardActivity.this,"A BUTTON2 IS CLIOSKED",Toast.LENGTH_LONG).show();
            Intent i = new Intent(DashboardActivity.this,UpdateActivity.class);
            startActivity(i);
        }
        if(v==b3)
        {
            Toast.makeText(DashboardActivity.this,"A BUTTON3 IS CLIOSKED",Toast.LENGTH_LONG).show();
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startMain);
        }

    }
}
