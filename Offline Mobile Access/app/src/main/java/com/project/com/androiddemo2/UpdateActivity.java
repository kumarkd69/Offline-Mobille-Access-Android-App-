package com.project.com.androiddemo2;

import android.content.ContentValues;
import android.content.Intent;
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

public class UpdateActivity extends AppCompatActivity implements View.OnClickListener {
    EditText t1, t2, t3, t4, t5, t6, t7,t8;
    Button b1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        t1 = (EditText) findViewById(R.id.oldpin);
        t2 = (EditText) findViewById(R.id.newpin);
        t3 = (EditText) findViewById(R.id.confpin);
        t4 = (EditText) findViewById(R.id.profilename);
        t5 = (EditText) findViewById(R.id.email);
        t6 = (EditText) findViewById(R.id.contact);
        t7 = (EditText) findViewById(R.id.alt_contact1);
        t8 = (EditText) findViewById(R.id.alt_contact2);

        b1 = (Button) findViewById(R.id.button2);
        b1.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        System.out.println("A BUTTON IS CLICKED");

        if(Validate.check_empty(new EditText[]{t1,t2,t3,t4,t5,t6,t7,t8}))
        {
            if(Validate.compare_inputs(t2,t3))
            {
                if(Validate.is_email(t5))
                {
                     if(Validate.is_multiple_num(new EditText[]{t6,t7,t8},10))
                     {
                         SQLiteDatabase db=openOrCreateDatabase(Config.DB_NAME,MODE_PRIVATE,null);
                         Cursor c=db.rawQuery("select * from users",null,null);
                         if(c.moveToFirst())
                         {
                             String db_secretpin=c.getString(c.getColumnIndex("pin"));
                             if(db_secretpin.equals(t1.getText().toString()))
                             {
                                 ContentValues cv = new ContentValues();
                                 cv.put("pin",t2.getText().toString()); //These Fields should be your String values of actual column names
                                 cv.put("profile_name",t4.getText().toString());
                                 cv.put("email",t5.getText().toString());
                                 cv.put("ucontact",t6.getText().toString());
                                 cv.put("acontact1",t7.getText().toString());
                                 cv.put("acontact2",t8.getText().toString());
                                 db.update("users", cv, null, null);
                                 //db.update("users", cv, "_id="+id, null);
                                 db.close();
                                 Validate.pop_mesg(UpdateActivity.this,"Pin And Profile Details Updated Successfully !");
                                 Intent i = new Intent(UpdateActivity.this, DashboardActivity.class);
                                 startActivity(i);
                             }
                             else
                                 Validate.pop_mesg(UpdateActivity.this,"Invalid Old Secret Pin Provided !");
                         }

                     }
                     else
                         Validate.pop_mesg(UpdateActivity.this,"Invalid Contact Numbers Provided !");
                }
                else
                    Validate.pop_mesg(UpdateActivity.this,"Invalid Email !");
            }
            else
                Validate.pop_mesg(UpdateActivity.this,"New Secret Pins Do not Match!");
        }
        else
         Validate.pop_mesg(UpdateActivity.this,"Invalid Inputs Provided");


    }
}