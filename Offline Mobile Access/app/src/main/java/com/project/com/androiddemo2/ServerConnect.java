package com.project.com.androiddemo2;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
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

/**
 * Created by Intkonsys on 24/12/2017.
 */

public class ServerConnect {

    Context context;
    Activity current_activity;
    String URL;
    String SUCCESS_MESG="";
    String ERROR_MESG="";

    ProgressDialog pDialog;
    String URL_Response="";
    boolean multiuser=false;
    String [] user_types;


    Class<? extends Activity> reciever_success_activty;


    Class<? extends Activity> reciever_error_activty;


    public ServerConnect(Context context, String url, String succes_mesg, String error_mesg, final Class<? extends Activity> R_succ_activity, final Class<? extends Activity> R_err_activity)
    {
        this.context=context;
        this.current_activity= (Activity) context;
        this.URL=url;
        this.SUCCESS_MESG=succes_mesg;
        this.ERROR_MESG=error_mesg;


        this.reciever_success_activty= R_succ_activity;


        this.reciever_error_activty=R_err_activity;




    }

    //USE IT FOR MULTIPLE USERTYPES FOR LOGIN
    public ServerConnect(Context context, String url, String succes_mesg, String error_mesg, final Class<? extends Activity> R_succ_activity, final Class<? extends Activity> R_err_activity,boolean multiuserlogin,String [] user_types)
    {
        this.context=context;
        this.current_activity= (Activity) context;
        this.URL=url;
        this.SUCCESS_MESG=succes_mesg;
        this.ERROR_MESG=error_mesg;


        this.reciever_success_activty= R_succ_activity;


        this.reciever_error_activty=R_err_activity;


        this.multiuser=multiuserlogin;
        this.user_types=user_types;

    }



    public void send_to_server(String[] data_to_be_sent) {

        new send_data().execute(data_to_be_sent);
    }




    private class send_data extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            pDialog = new ProgressDialog(context);
            pDialog.setMessage("Connecting to Server");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... arg0) {
            java.net.URL myUrl = null;
            HttpURLConnection conn = null;
            String response = "";
            try
            {
                myUrl = new URL(URL);
                conn = (HttpURLConnection) myUrl.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                System.out.println("dssdhfjd :"+arg0[0]);
                System.out.println("dssdhfjd ddjfldsjlkjsd:"+arg0[1]);
                String postData = URLEncoder.encode("email", "UTF-8")+"="+URLEncoder.encode(arg0[0], "UTF-8") + "&" +
                        URLEncoder.encode("pin", "UTF-8")+"="+URLEncoder.encode(arg0[1], "UTF-8") ;

                OutputStream os = conn.getOutputStream();

                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                bufferedWriter.write(postData);
                bufferedWriter.flush();
                bufferedWriter.close();
                os.close();

                InputStream inputStream = conn.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                String line = "";
                while ((line = bufferedReader.readLine()) != null) {
                    response += line;
                }
                bufferedReader.close();
                inputStream.close();
                conn.disconnect();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d("REST","OUTPUT IS : "+response);
            if(response.equals("DONE"))
            {
                URL_Response="DONE";
            }
            else if(response.equals("EXISTS"))
            {
                URL_Response="EXISTS";
            }
            else if(response.contains("USER"))
                URL_Response=response;

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (pDialog.isShowing())
                pDialog.dismiss();
            Log.d("TAP","the URL VALU IS : "+URL_Response);
            if(URL_Response.equals("DONE")) {
                Toast.makeText(context, SUCCESS_MESG, Toast.LENGTH_LONG).show();
                Intent i = new Intent(context, reciever_success_activty);
                context.startActivity(i);
            }
            else if(URL_Response.contains("USER"))
            {
                String u_data[]=URL_Response.split("#");
                if(u_data[1].equals("ERROR"))
                {
                    Toast.makeText(context,ERROR_MESG, Toast.LENGTH_LONG).show();
                }
                else
                {
                    if(multiuser)
                    {
                        if(user_types[1].equals(u_data[1]))
                        {
                            Intent i = new Intent(context, reciever_success_activty);
                            context.startActivity(i);
                        }
                        if(user_types[2].equals(u_data[1]))
                        {
                            Intent i = new Intent(context, reciever_error_activty);
                            context.startActivity(i);
                        }
                    }
                }
            }
            else
            {
                Toast.makeText(context,ERROR_MESG, Toast.LENGTH_LONG).show();
                Intent i = new Intent(context, reciever_error_activty);
                context.startActivity(i);
            }
        }

    }
}
