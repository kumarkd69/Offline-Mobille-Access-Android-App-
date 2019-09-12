package com.project.com.androiddemo2;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.support.v4.app.ActivityCompat.shouldShowRequestPermissionRationale;

/**
 * Created by APEXNEWPC1 on 22-Dec-17.
 */

public class Validate extends Activity
{
    private static Pattern pattern;
    private static Matcher matcher;



    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

  /*  Description

    ^			#start of the line
    [_A-Za-z0-9-\\+]+	#  must start with string in the bracket [ ], must contains one or more (+)
    (			#   start of group #1
        \\.[_A-Za-z0-9-]+	#     follow by a dot "." and string in the bracket [ ], must contains one or more (+)
    )*			#   end of group #1, this group is optional (*)
    @			#     must contains a "@" symbol
    [A-Za-z0-9-]+      #       follow by string in the bracket [ ], must contains one or more (+)
    (			#         start of group #2 - first level TLD checking
    \\.[A-Za-z0-9]+  #           follow by a dot "." and string in the bracket [ ], must contains one or more (+)
    )*		#         end of group #2, this group is optional (*)
    (			#         start of group #3 - second level TLD checking
    \\.[A-Za-z]{2,}  #           follow by a dot "." and string in the bracket [ ], with minimum length of 2
        )			#         end of group #3
    $			#end of the line
    The combination means, email address must start with “_A-Za-z0-9-\\+” , optional follow by “.[_A-Za-z0-9-]”, and end with a “@” symbol. The email’s domain name must start with “A-Za-z0-9-“, follow by first level Tld (.com, .net) “.[A-Za-z0-9]” and optional follow by a second level Tld (.com.au, .com.my) “\\.[A-Za-z]{2,}”, where second level Tld must start with a dot “.” and length must equal or more than 2 characters.
  */

    public static boolean check_empty(EditText[] inputs)
    {
        for (EditText in:inputs) {
            if(in.getText().toString().equals(""))
            {
                return false;
            }
        }
        return true;
    }

    public static void pop_mesg(Context context,String message)
    {
        Toast.makeText(context,message,Toast.LENGTH_LONG).show();
    }


    public static boolean is_num(String user_no,int size)
    {
        try
        {
            double temp=Double.parseDouble(user_no);
            if(user_no.length()==size)
                return true;
            else
                return false;

        }
        catch(Exception e)
        {
            return false;
        }

    }


    public static boolean is_multiple_num(EditText[] inputs, int size)
    {
        for (EditText in:inputs) {
            try
            {
                double temp=Double.parseDouble(in.getText().toString());
                if(in.getText().toString().length()!=size)
                    return false;


            }
            catch(Exception e)
            {
                return false;
            }
        }
        return true;
    }

    public static boolean is_email(EditText email_input)
    {
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email_input.getText().toString());
        return matcher.matches();
    }

    public static boolean compare_inputs(EditText p1,EditText p2)
    {
        if(p1.getText().toString().equals(p2.getText().toString()))
        {
            return true;
        }
        else
            return false;
    }



    @TargetApi(Build.VERSION_CODES.M)
    @RequiresApi(api = Build.VERSION_CODES.M)
        public void request_permission(Context context, String[] permissions)
        {
            try {
                if (Build.VERSION.SDK_INT >= 21)
                {
                    if (ActivityCompat.checkSelfPermission(context, permissions[0]) != PackageManager.PERMISSION_GRANTED ) {
                        if (shouldShowRequestPermissionRationale(
                                Manifest.permission.READ_PHONE_STATE)) {
                            // Show our own UI to explain to the user why we need to read the contacts
                            // before actually requesting the permission and showing the default UI
                        }

                        // Fire off an async request to actually get the permission
                        // This will show the standard permission request dialog UI
                        requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE},1);
                        return;
                    }
                }
                else
                {

                }

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
    }



    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        // Make sure it's our original READ_CONTACTS request
        if (requestCode == 1) {
            if (grantResults.length == 1 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Toast.makeText(this, "Read Contacts permission granted", Toast.LENGTH_SHORT).show();
            } else {
                // showRationale = false if user clicks Never Ask Again, otherwise true
                if (Build.VERSION.SDK_INT >= 21) {
                    boolean showRationale = shouldShowRequestPermissionRationale(Manifest.permission.READ_PHONE_STATE);


                    if (showRationale) {
                        // do something here to handle degraded mode
                    }
                    else
                    {

                    }
                }
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }









}
