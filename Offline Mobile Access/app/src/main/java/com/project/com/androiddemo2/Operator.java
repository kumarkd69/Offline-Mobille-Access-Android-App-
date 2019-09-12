package com.project.com.androiddemo2;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.Manifest;
import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.PhoneLookup;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class Operator extends Service {
    String msg = "", dbpin = "", is_auth = "no";
    Location current_location;
    LocationManager manager;
    String Provider_name = "";
    boolean isGpsEnabled, isNetworkEnabled;
    Double latitude = null, longitude = null;
    SmsManager sms = SmsManager.getDefault();
    //String phoneNumber="919738805498";
    String incoming_mesg = "";
    String loc_stat = "";

    DevicePolicyManager deviceManger;
    ActivityManager activityManager;
    ComponentName compName;


    String[] msg_data;

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onStart(Intent intent, int startId) {
        // TODO Auto-generated method stub
        super.onStart(intent, startId);
        msg = intent.getStringExtra("msg");
        //compName=(ComponentName) intent.getExtras().get("comp");
        Log.d("Mobile Access", msg);
        msg_data = msg.split(":");
        Log.d("Mobile Access", "User Is:" + msg_data[0]);
        Log.d("Mobile Access", "Msg Is:" + msg_data[1]);

        deviceManger = (DevicePolicyManager) getSystemService(
                Context.DEVICE_POLICY_SERVICE);

        SQLiteDatabase db = openOrCreateDatabase("RMA", MODE_PRIVATE, null);
        Cursor c = db.rawQuery("select * from users", null);
        if (c.moveToFirst()) {
            dbpin = c.getString(c.getColumnIndex("pin"));
            is_auth = c.getString(c.getColumnIndex("auth"));
            db.close();

        }

        if (msg_data[1].indexOf("PIN") >= 0) {
            Log.d("Mobile Access", "INSIDE MESG PIN CHECK SECTION");
            incoming_mesg = msg_data[1].trim();
            String[] msg_split = incoming_mesg.split(" ");
            msg_split[1] = msg_split[1].trim();
            if (msg_split[1].equals(dbpin)) {
                db = openOrCreateDatabase("RMA", MODE_PRIVATE, null);
                ContentValues args = new ContentValues();
                args.put("auth", "yes");

                db.update("users", args, null, null);
                db.close();
                is_auth = "yes";
                Log.d("Mobile Access", "INSIDE AUTH DB UPDATE SECTION");
                mesg_option_list();
            } else {
                sms.sendTextMessage(msg_data[0], null, "INVALID PIN PROVIDED..", null, null);
            }

        }
        if (is_auth.equals("yes")) {
            if (msg_data[1].equalsIgnoreCase("STOP")) {
                Log.d("Mobile Access", "INSIDE STOP MESG SEND SECTION");
                db = openOrCreateDatabase("RMA", MODE_PRIVATE, null);
                ContentValues args = new ContentValues();
                args.put("auth", "no");
                db.update("users", args, null, null);
                db.close();
                is_auth = "no";
                Log.d("Mobile Access", "INSIDE AUTH DB UPDATE NO SECTION");
                sms.sendTextMessage(msg_data[0], null, "Application Connection has been stopped Successfully....", null, null);

            }
            if (msg_data[1].equalsIgnoreCase("SIMNO")) {
                Log.d("Mobile Access", "INSIDE SIMNO MESG SEND SECTION");
                TelephonyManager l = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                String mesg = "Your network sim number is " + l.getSimSerialNumber() + "\nIt can be used to trace your Mobile Number";
                sms.sendTextMessage(msg_data[0], null, mesg, null, null);

            }
            if (msg_data[1].equalsIgnoreCase("LOCK")) {
                Log.d("Mobile Access", "LOCKING THE PHONE SECTION");
                boolean active = deviceManger.isAdminActive(compName);


                Log.d("Mobile Access", "INSIDE ADMIN ACTIVE");
                deviceManger.lockNow();
                String mesg = "MOBILE LOCKED";
                sms.sendTextMessage(msg_data[0], null, mesg, null, null);

            }
            if (msg_data[1].equalsIgnoreCase("WIPEDATA")) {
                Log.d("Mobile Access", "WIPE THE PHONE SECTION");
                boolean active = deviceManger.isAdminActive(compName);

                String mesg = "MOBILE HAS BEEN WIPED OUT SUCCESSFULLY";
                sms.sendTextMessage(msg_data[0], null, mesg, null, null);
                Log.d("Mobile Access", "INSIDE ADMIN ACTIVE");
                deviceManger.wipeData(0);


            }
            if (msg_data[1].equalsIgnoreCase("SIMOP")) {
                Log.d("Mobile Access", "INSIDE SIMOP MESG SEND SECTION");
                TelephonyManager l = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                String mesg = "Operator is :" + l.getNetworkOperatorName();
                sms.sendTextMessage(msg_data[0], null, mesg, null, null);

            }
            if (msg_data[1].equalsIgnoreCase("IMEI")) {
                Log.d("Mobile Access", "INSIDE SIMIMEI MESG SEND SECTION");
                TelephonyManager localTelephonyManager1 = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                String mesg = "Your phone IMEI no. is " + localTelephonyManager1.getDeviceId() + "\nNever give your IMEI no. to anyone.\nIMEI number can help trace your phone when lost.";
                sms.sendTextMessage(msg_data[0], null, mesg, null, null);

            }
            if (msg_data[1].equalsIgnoreCase("SILENT")) {
                Log.d("Mobile Access", "INSIDE SILENT RINGER MESG SEND SECTION");
                AudioManager am = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
                am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                String mesg = "The phone has been switched to SILENT mode.\nTry turning it to ringer using the RINGER command ";
                sms.sendTextMessage(msg_data[0], null, mesg, null, null);

            }
            if (msg_data[1].equalsIgnoreCase("RINGER")) {
                Log.d("Mobile Access", "INSIDE SIMIMEI MESG SEND SECTION");
                AudioManager am = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
                am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                String mesg = "The phone has been switched to RINGER mode.\nTry turning it to silent using the SILENT command ";
                sms.sendTextMessage(msg_data[0], null, mesg, null, null);

            }
            if (msg_data[1].equalsIgnoreCase("SHUTDOWN")) {
			        /*Intent in = new Intent("android.intent.action.ACTION_REQUEST_SHUTDOWN");
					intent.putExtra("android.intent.extra.KEY_CONFIRM", false);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intent);	*/

					/*Intent dialogIntent = new Intent("android.intent.action.ACTION_REQUEST_SHUTDOWN");
					dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent.putExtra("android.intent.extra.KEY_CONFIRM", true);
					startActivity(dialogIntent);*/
            }
            if (msg_data[1].indexOf("LMESG") >= 0) {
                Log.d("Mobile Access", "INSIDE LMESG SEND SECTION");
                Cursor localCursor4;
                Uri localUri = Uri.parse("content://sms/inbox");
                localCursor4 = getContentResolver().query(localUri, null, null, null, null);
                Log.d("Mobile Access", "INSIDE LMESG 2 SEND SECTION");
                String[] msg_split = msg_data[1].split(" ");
                Log.d("Mobile Access", "INSIDE LMESG 3 SEND SECTION  " + msg_split.length);
                if (msg_split.length <= 1) {
                    Log.d("Mobile Access", "INSIDE LMESG 4 SEND SECTION");
                    Toast.makeText(Operator.this, "INSIDE LMESG", Toast.LENGTH_LONG).show();

                    if (localCursor4.moveToFirst()) {
                        String msg_body = localCursor4.getString(localCursor4.getColumnIndex("body")).toString();
                        String msg_addr = localCursor4.getString(localCursor4.getColumnIndex("address")).toString();
                        String msg = " Messages From:\n Number: " + msg_addr + "\nMessage: " + msg_body;
                        Toast.makeText(Operator.this, msg, Toast.LENGTH_LONG).show();
                        sms.sendTextMessage(msg_data[0], null, msg, null, null);
                        localCursor4.close();
                    }
                } else {
                    Log.d("Mobile Access", "INSIDE LMESG ELSE SECTION  " + msg_split.length);
                    msg_split[1] = msg_split[1].trim();
                    int mesg_number = Integer.parseInt(msg_split[1]);
                    int count = 0, is_mesg_count_valid = 1;
                    while (count < mesg_number)//6
                    {
                        if (localCursor4.moveToNext()) {
                            Log.d("Mobile Access", "SKIP MESG " + count);
                            count++;
                        } else {
                            sms.sendTextMessage(msg_data[0], null, "No any Message available at that location", null, null);
                            is_mesg_count_valid = 0;
                            localCursor4.close();
                            break;
                        }
                    }
                    if (is_mesg_count_valid == 1) {
                        String body = localCursor4.getString(localCursor4.getColumnIndex("body")).toString();
                        String number = localCursor4.getString(localCursor4.getColumnIndex("address")).toString();
                        String str22 = " Messages From:\n Number: " + number + "\nMessage: " + body;
                        // Toast.makeText(Operator.this,str22, Toast.LENGTH_LONG).show();
                        sms.sendTextMessage(msg_data[0], null, str22, null, null);
                        localCursor4.close();
                    }


                }


            }

            if (msg_data[1].equalsIgnoreCase("CALLS")) {
                int count = 0;
                StringBuffer sb = new StringBuffer();
                if (ActivityCompat.checkSelfPermission(this,Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    Activity#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for Activity#requestPermissions for more details.
                    return;
                }
                Cursor managedCursor = getContentResolver().query(android.provider.CallLog.Calls.CONTENT_URI, null, null, null, null);
                Cursor managedCursor1 = getContentResolver().query(android.provider.CallLog.Calls.CONTENT_URI, null, null, null, null);
                int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
                int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
                int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
                int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
                sb.append("Call Details :");
                int pos = 0;

                while (managedCursor1.moveToNext()) {
                    pos++;
                }
                pos--;
                while (managedCursor.moveToPosition(pos)) {
                    if (count > 5)
                        break;
                    String phNumber = managedCursor.getString(number);
                    String callType = managedCursor.getString(type);
                    String callDate = managedCursor.getString(date);
                    Date callDayTime = new Date(Long.valueOf(callDate));
                    String callDuration = managedCursor.getString(duration);
                    String dir = null;
                    int dircode = Integer.parseInt(callType);
                    switch (dircode) {
                        case CallLog.Calls.OUTGOING_TYPE:
                            dir = "OUTGOING";
                            break;

                        case CallLog.Calls.INCOMING_TYPE:
                            dir = "INCOMING";
                            break;

                        case CallLog.Calls.MISSED_TYPE:
                            dir = "MISSED";
                            break;
                    }
                    sb.append("\nPhone Number:" + phNumber + " \nCall Type: " + dir + " \nCall Date: " + callDayTime + " \nCall duration in sec : " + callDuration);
                    sb.append("\n");
                    count++;
                    pos--;
                }
                managedCursor.close();
                String final_data = new String(sb);
                if (final_data.length() > 129) {
                    ArrayList<String> parts = sms.divideMessage(final_data);
                    sms.sendMultipartTextMessage(msg_data[0], null, parts, null, null);
                } else {
                    sms.sendTextMessage(msg_data[0], null, final_data, null, null);
                }

            }

            if (msg_data[1].indexOf("WIFI") >= 0) {
                String[] msg_split = msg_data[1].split(" ");
                if (msg_split.length <= 1) {
                    sms.sendTextMessage(msg_data[0], null, "INVALID INPUT", null, null);
                } else {
                    WifiManager w = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
                    w.setWifiEnabled(true);
                    if (msg_split[1].equals("ON")) {
                        w.setWifiEnabled(true);
                    }
                    if (msg_split[1].equals("OFF")) {
                        w.setWifiEnabled(false);
                    }
                }

            }

            if (msg_data[1].indexOf("DATA") >= 0) {
                String[] msg_split = msg_data[1].split(" ");
                if (msg_split.length <= 1) {
                    sms.sendTextMessage(msg_data[0], null, "INVALID INPUT", null, null);
                } else {
                    ConnectivityManager dataManager;
                    dataManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    Method dataMtd = null;
                    try {
                        dataMtd = ConnectivityManager.class.getDeclaredMethod("setMobileDataEnabled", boolean.class);
                    } catch (SecurityException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    } catch (NoSuchMethodException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }

                    if (msg_split[1].equals("ON")) {
                        dataMtd.setAccessible(true);
                        try {
                            dataMtd.invoke(dataManager, true);
                        } catch (IllegalArgumentException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                    if (msg_split[1].equals("OFF")) {
                        dataMtd.setAccessible(false);
                        try {
                            dataMtd.invoke(dataManager, false);
                        } catch (IllegalArgumentException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }

            }
            if (msg_data[1].indexOf("FIND") >= 0) {
                String[] msg_split = msg_data[1].split(" ");
                String check_name = "";
                String mesg = "Matched Numbers are :\n";
                Log.d("Mobile Access", "INSIDE LMESG 3 SEND SECTION  " + msg_split.length);
                if (msg_split.length <= 1) {
                    sms.sendTextMessage(msg_data[0], null, "INVALID INPUT", null, null);
                } else {
                    if (msg_split.length == 3) {
                        check_name = msg_split[1] + " " + msg_split[2];

                    } else {
                        check_name = msg_split[1];

                    }
                    Log.d("Mobile Access", "INSIDE FIND SECTION query NAme" + check_name);
                    ContentResolver contentResolver = getContentResolver();
                    ContentResolver mContentResolver = getContentResolver();
                    Uri uri = ContactsContract.Contacts.CONTENT_URI;
                    String[] projection = new String[]{ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME};
                    String selection = ContactsContract.Contacts.DISPLAY_NAME + " like '%" + check_name + "%'";

                    //String selection = "(" + ContactsContract.Contacts.DISPLAY_NAME + " like '%"+check_name+ "%' COLLATE NOCASE)";

                    //check_name=check_name.toUpperCase();
                    //String selection = "UPPER(ContactsContract.Contacts.DISPLAY_NAME) "+ " like '%"+check_name+ "%'";

                    String[] selectionArguments = null;
                    //FETCHING ALL MATCHING NAMES
                    Cursor cursor = contentResolver.query(uri, projection, selection, selectionArguments, null);

                    if (cursor != null) {
                        while (cursor.moveToNext()) {
                            //FETCHING THE NUMBERS
                            Cursor cursor_number = mContentResolver.query(CommonDataKinds.Phone.CONTENT_URI, null, CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{cursor.getString(0)}, null);

                            while (cursor_number.moveToNext()) {

                                mesg += cursor_number.getString(cursor_number.getColumnIndex(CommonDataKinds.Phone.NUMBER)) + "\n";
                            }


                        }

                    } else
                        msg = "NO ANY CONATCT FOUND";

                    sms.sendTextMessage(msg_data[0], null, mesg, null, null);
                }


            }
            if (msg_data[1].indexOf("LOCATION") >= 0) {
                sendLocation();

                //Geocoder geocoder = new Geocoder(this, Locale.ENGLISH);


                String mesg = "Location:\n";
                mesg += "https://www.google.co.in/maps/@" + latitude + "," + longitude + "";


                if (latitude == null || !loc_stat.equals("")) {
                    sms.sendTextMessage(msg_data[0], null, "NO LOCATION AVAILABLE", null, null);
                } else
                    sms.sendTextMessage(msg_data[0], null, mesg, null, null);


            }

        }
        //	connect_to_server();
        this.stopSelf();

    }


    public void mesg_option_list() {
        Log.d("Mobile Access", "INSIDE MESG LIST SECTION");
        String out_mesg = "OPTIONS ARE AS BELOW";
        out_mesg += "\n";
        out_mesg += "STOP(To Disconnect)";
        out_mesg += "\n";
        out_mesg += "LOCK(Lock Phone)";
        out_mesg += "\n";
        out_mesg += "WIPEDATA(To wipe data)";
        out_mesg += "\n";
        out_mesg += "DATA ON/OFF(To enable/disable Data Connecn)";
        out_mesg += "\n";
        out_mesg += "WIFI ON/OFF(To enable/disable WIFI)";
        out_mesg += "\n";
        out_mesg += "LOCATION(To get Location)";
        out_mesg += "\n";
        out_mesg += "SIMNO(get SIM Number)";
        out_mesg += "\n";
        out_mesg += "SIMOP(SIM Operator)";
        out_mesg += "\n";
        out_mesg += "IMEI(CELL IMEI No)";
        out_mesg += "\n";
        out_mesg += "SILENT(Ringer Mode to Silent)";
        out_mesg += "\n";
        out_mesg += "RINGER(Ringer Mode to RINGER)";
        out_mesg += "\n";
        out_mesg += "LMESG(Last Recieved Mesg)";
        out_mesg += "\n";
        out_mesg += "LMESG n(Last nth Recieved Mesg)";
        out_mesg += "\n";
        out_mesg += "CALLS n(Call Details)";
        out_mesg += "\n";
        out_mesg += "FIND <name>(To get Contact Details of users)";
        out_mesg += "\n";
        Log.d("Mobile Access", "Mesg is " + out_mesg);

        ArrayList<String> parts = sms.divideMessage(out_mesg);
        sms.sendMultipartTextMessage(msg_data[0], null, parts, null, null);
        //sms.sendTextMessage(msg_data[0], null,out_mesg, null, null);

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void sendLocation() {
        manager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        isGpsEnabled = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworkEnabled = manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (isGpsEnabled || isNetworkEnabled) {
            if (isGpsEnabled) {
                Provider_name = LocationManager.GPS_PROVIDER;
            }
            if (isNetworkEnabled) {
                Provider_name = LocationManager.NETWORK_PROVIDER;
            }
        } else {
            Toast.makeText(this, "NO LOCATION DATA AVAILABLE", Toast.LENGTH_LONG).show();
        }

        Log.d("APMC", "THE PROVIDER NAME IS : " + Provider_name);
        if (ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }
        current_location = manager.getLastKnownLocation(Provider_name);
        LocationListener listener=new UserLocationListener();
        manager.requestLocationUpdates(Provider_name,60000, 1f, listener);


        Log.d("APMC","THE CURRENT LOC IS : "+current_location);
        if(current_location!=null)
        {

            latitude=current_location.getLatitude();
            longitude=current_location.getLongitude();
            Toast.makeText(this,"LATITUDE IS : "+latitude+"\n LONGITUDE IS :"+longitude+"",Toast.LENGTH_LONG).show();

        }
        else
        {
            loc_stat="NO STORED LOCATION AVAILABLE";
            Toast.makeText(this,"NO STORED LOCATION AVAILABLE",Toast.LENGTH_LONG).show();
        }

    }

    class UserLocationListener implements LocationListener
    {

        @Override
        public void onLocationChanged(Location location) {
            // TODO Auto-generated method stub
            current_location=location;

        }

        @Override
        public void onProviderDisabled(String provider) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // TODO Auto-generated method stub

        }

    }

}
