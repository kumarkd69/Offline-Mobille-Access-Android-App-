package com.project.com.androiddemo2;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.telephony.SmsManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class TestReciever extends BroadcastReceiver
{
    SQLiteDatabase db;
    String amobile1="",amobile2="",db_imsi1="",db_imsi2="";
    Timer timer;
    int mesg_sent=0;
    public static boolean isMultiSimEnabled = false;

    String phone_imsi1="";
    String phone_imsi2="";
    String IMEI="";

    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        //Toast.makeText(context, "INSIDE BOOT UP", Toast.LENGTH_LONG);
        Log.d("MOBILE ACCESS","Boot completed") ;
        db = context.openOrCreateDatabase("RMA", 0 , null);
        TelephonyManager localTelephonyManager1 = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        IMEI = localTelephonyManager1.getDeviceId();
        Cursor c=db.rawQuery("select * from users",null);
        if(c.moveToFirst())
        {
            amobile1=c.getString(c.getColumnIndex("acontact1"));
            amobile2=c.getString(c.getColumnIndex("acontact2"));
            db_imsi1=c.getString(c.getColumnIndex("sim1imsi"));
            db_imsi2=c.getString(c.getColumnIndex("sim2imsi"));
            Log.d("MOBILE ACCESS","The mobile number is : "+amobile1) ;
            Log.d("MOBILE ACCESS","The SSID is : "+db_imsi1) ;
            db.close();

        }




        if(Build.VERSION.SDK_INT >= 21) {
            SubscriptionManager mSubscriptionManager;


            mSubscriptionManager = SubscriptionManager.from(context);

            List<SubscriptionInfo> subInfoList = mSubscriptionManager.getActiveSubscriptionInfoList();
            if (subInfoList.size() > 1) {
                isMultiSimEnabled = true;
            }
            for (SubscriptionInfo subscriptionInfo : subInfoList) {
                if(phone_imsi1.equals(""))
                    phone_imsi1=subscriptionInfo.getIccId();
                else
                    phone_imsi2=subscriptionInfo.getIccId();

            }
        }
        else
        {
            //for lower version
            TelephonyInfo telephonyInfo = TelephonyInfo.getInstance(context);
            if (telephonyInfo.isDualSIM()) {
                // Check if the first SIM is ready
                if (telephonyInfo.isSIM1Ready()) {
                    phone_imsi1 = telephonyInfo.getImsiSIM1();

                }
                // Check if the second SIM is ready
                if (telephonyInfo.isSIM2Ready()) {
                    phone_imsi1 = telephonyInfo.getImsiSIM2();

                }
            }
            else
            {
                phone_imsi1 = telephonyInfo.getImsiSIM1();

            }
        }


        final SmsManager sms = SmsManager.getDefault();

        timer = new Timer();


        timer.scheduleAtFixedRate(new TimerTask()
        {
            @Override
            public void run() {
                if(mesg_sent==0)
                {
                    Log.d("MOBILE ACCESS","INSIDE TIMER TASK") ;
                    sms.sendTextMessage(amobile1, null,"THE MOBILE WITH IMEI NUMBER "+IMEI+" HAS BEEN JUST SWITCHED ON", null, null);
                    if(isMultiSimEnabled)
                    {
                        if(!db_imsi1.equals(phone_imsi1)&&!db_imsi1.equals(phone_imsi2)&&!db_imsi2.equals(phone_imsi1)&&!db_imsi2.equals(phone_imsi2))
                        {
                            sms.sendTextMessage(amobile1, null,"THE SIM OF MOBILE WITH IMEI NUMBER "+IMEI+" HAS BEEN JUST CHANGED", null, null);
                        }
                    }
                    else
                    if(!db_imsi1.equals(phone_imsi1)&&!db_imsi1.equals(phone_imsi2))
                    {
                        sms.sendTextMessage(amobile1, null,"THE SIM OF MOBILE WITH IMEI NUMBER "+IMEI+" HAS BEEN JUST CHANGED", null, null);
                    }
                    mesg_sent=1;
                }
            }
        }, 90000, 10000);
    }





}
