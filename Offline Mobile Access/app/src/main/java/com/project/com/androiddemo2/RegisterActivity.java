package com.project.com.androiddemo2;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;

import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener,ActivityCompat.OnRequestPermissionsResultCallback,PermissionResultCallback
{

    EditText t1,t2,t3,t4,t5,t6,t7;
    Button b1;
    SQLiteDatabase db;
    public static boolean isMultiSimEnabled = false;
    ArrayList<String> Numbers = new ArrayList<String>();


    // list of permissions
    ArrayList<String> permissions=new ArrayList<>();
    String needed_perm[]=new String [] {Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.READ_PHONE_STATE};
    PermissionUtils permissionUtils;
    Context context;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db=openOrCreateDatabase("RMA",MODE_PRIVATE,null);
        db.execSQL("create table if not exists users (profile_name varchar,email varchar,ucontact varchar,sim1imsi varchar,sim2imsi varchar,acontact1 varchar,acontact2 varchar,pin varchar,auth varchar);");
        Cursor c=db.rawQuery("select * from users",null,null);
        if(c.moveToFirst())
        {
            db.close();
            startActivity(new Intent(RegisterActivity.this,DashboardActivity.class));
        }

        setContentView(R.layout.activity_register);
        context = this;

        if(Build.VERSION.SDK_INT >= 21)
          set_permissions();//set required permission and even it will get sims serial numbers
        else
        {
            //for lower version
            TelephonyInfo telephonyInfo = TelephonyInfo.getInstance(this);
            if (telephonyInfo.isDualSIM()) {
                // Check if the first SIM is ready
                if (telephonyInfo.isSIM1Ready()) {
                    String imsiSIM1 = telephonyInfo.getImsiSIM1();
                    Numbers.add(imsiSIM1);
                    Toast.makeText(this,"ID IS : "+imsiSIM1,Toast.LENGTH_LONG).show();
                }
                // Check if the second SIM is ready
                if (telephonyInfo.isSIM2Ready()) {
                    String imsiSIM2 = telephonyInfo.getImsiSIM2();
                    Numbers.add(imsiSIM2);
                    Toast.makeText(this,"ID IS : "+imsiSIM2,Toast.LENGTH_LONG).show();
                }
            }
            else
            {
                String imsiSIM1 = telephonyInfo.getImsiSIM1();
                Numbers.add(imsiSIM1);
                Toast.makeText(this,"ID IS : "+imsiSIM1,Toast.LENGTH_LONG).show();
            }
        }

        t1= (EditText) findViewById(R.id.profilename);
        t2= (EditText) findViewById(R.id.email);
        t3= (EditText) findViewById(R.id.contact);
        t4= (EditText) findViewById(R.id.alt_contact1);
        t5= (EditText) findViewById(R.id.alt_contact2);
        t6= (EditText) findViewById(R.id.pin);
        t7= (EditText) findViewById(R.id.cpin);

        b1= (Button) findViewById(R.id.button2);
        b1.setOnClickListener(this);
    }


    public void set_permissions()
    {
        System.out.println("MAIN FUNCTION");
            permissionUtils = new PermissionUtils(this);
            for (String p : needed_perm) {
                permissions.add(p);
                System.out.println("INSIDE");
            }
            permissionUtils.check_permission(permissions, "Explain here why the app needs permissions", 1);

    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        // Make sure it's our original READ_CONTACTS request
        boolean result=permissionUtils.onRequestPermissionsResult(requestCode,permissions,grantResults);
        while(!result)
        {
          System.out.println("WAITING");
        }
        if(result)
        {
            SubscriptionManager mSubscriptionManager;


            mSubscriptionManager = SubscriptionManager.from(context);

            List<SubscriptionInfo> subInfoList = mSubscriptionManager.getActiveSubscriptionInfoList();
            if (subInfoList.size() > 1) {
                isMultiSimEnabled = true;
            }
            for (SubscriptionInfo subscriptionInfo : subInfoList) {
                Numbers.add(subscriptionInfo.getIccId());
                Toast.makeText(this,"ID IS : "+subscriptionInfo.getIccId(),Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onClick(View v) {

        //System.out.println("A BUTTON IS CLICKED");
        //Toast.makeText(RegisterActivity.this,"A BUTTON IS CLIOSKED",Toast.LENGTH_LONG).show();
        EditText [] inputs={t1,t2,t3,t4,t5,t6,t7};
        if(Validate.check_empty(inputs))
        {
            EditText [] numbers={t3,t4,t5};
            if(Validate.is_multiple_num(numbers,10))
            {
               if(Validate.is_email(t2))
               {
                   if(Validate.compare_inputs(t6,t7))
                   {
                       if(Numbers.size()==1)
                       {
                           Numbers.add("0");
                       }
                       else if(Numbers.size()==0)//this is for emulator
                       {
                           Numbers.add("0");
                           Numbers.add("1");
                       }
                       //TelephonyManager t= (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                       //String sim_sid=t.getSimSerialNumber();
                       db=openOrCreateDatabase(Config.DB_NAME,MODE_PRIVATE,null);
                       db.execSQL("create table if not exists users (profile_name varchar,email varchar,ucontact varchar,sim1imsi varchar,sim2imsi varchar,acontact1 varchar,acontact2 varchar,pin varchar,auth varchar);");
                       db.execSQL("insert into users values ('"+t1.getText().toString()+"','"+t2.getText().toString()+"','"+t3.getText().toString()+"','"+Numbers.get(0)+"','"+Numbers.get(1)+"','"+t4.getText().toString()+"','"+t5.getText().toString()+"','"+t6.getText().toString()+"','no');");
                       db.close();
                       Toast.makeText(this,"Profile Setup Done !",Toast.LENGTH_LONG).show();
                       Intent i=new Intent(RegisterActivity.this,DashboardActivity.class);
                       startActivity(i);
                   }
                   else
                       Toast.makeText(RegisterActivity.this,"Secret Pin Doesn`t Match !",Toast.LENGTH_LONG).show();

               }
               else
                   Toast.makeText(RegisterActivity.this,"Invalid Email Provided !",Toast.LENGTH_LONG).show();
            }
            else
                   Toast.makeText(RegisterActivity.this,"Invalid Contact Nos Provided !",Toast.LENGTH_LONG).show();
        }
        else
            Toast.makeText(RegisterActivity.this,"Invalid Inputs Provided !",Toast.LENGTH_LONG).show();
        }

    @Override
    public void PermissionGranted(int request_code) {

    }

    @Override
    public void PartialPermissionGranted(int request_code, ArrayList<String> granted_permissions) {

    }

    @Override
    public void PermissionDenied(int request_code) {

    }

    @Override
    public void NeverAskAgain(int request_code) {

    }
}

