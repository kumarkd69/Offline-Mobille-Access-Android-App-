package com.project.com.androiddemo2;

import android.app.ProgressDialog;
import android.content.Context;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;




/**
 * Created by APEXNEWPC1 on 21-Dec-17.
 */

public class ForgotpinActivity extends AppCompatActivity implements View.OnClickListener {

    EditText t1;
    Button b1;

    String email_server=Config.Main_URL+"/email_server.php";
    ProgressDialog pDialog;
    String URL_Response="";

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpin);

        context=this;

        t1 = (EditText) findViewById(R.id.email);

        b1 = (Button) findViewById(R.id.button2);
        b1.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        System.out.println("A BUTTON IS CLICKED");

        if(Validate.is_email(t1))
        {
            SQLiteDatabase db=openOrCreateDatabase(Config.DB_NAME,MODE_PRIVATE,null);
            Cursor c=db.rawQuery("select * from users",null,null);
            if(c.moveToFirst())
            {
                String db_email=c.getString(c.getColumnIndex("email"));
                String db_secretpin=c.getString(c.getColumnIndex("pin"));
                if(t1.getText().toString().equals(db_email))
                {

                    ServerConnect S_connect=new ServerConnect(this,email_server,"Success Mesg","Error Mesg",DashboardActivity.class,DashboardActivity.class);
                    S_connect.send_to_server(new String[]{db_email,db_secretpin});

                }
                else
                    Validate.pop_mesg(ForgotpinActivity.this,"Invalid Registered Email Id Provided !");
            }
            db.close();
        }
        else
            Validate.pop_mesg(ForgotpinActivity.this,"Invalid Email Id Provided !");

    }


}
