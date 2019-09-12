package com.project.com.androiddemo2;

        import android.content.BroadcastReceiver;
        import android.content.Context;
        import android.content.Intent;
        import android.os.Bundle;
        import android.telephony.SmsManager;
        import android.telephony.SmsMessage;
        import android.widget.Toast;

public class SMSReceiver extends BroadcastReceiver
{


    @Override
    public void onReceive(Context context, Intent intent)
    {



        Bundle myBundle = intent.getExtras();
        SmsMessage [] messages = null;
        String strMessage = "";

        if (myBundle != null)
        {
            Object [] pdus = (Object[]) myBundle.get("pdus");
            if(pdus!=null)
            {
                messages = new SmsMessage[pdus.length];
                if(messages!=null)
                {
                    for (int i = 0; i < messages.length; i++)
                    {
                        messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                        strMessage += messages[i].getOriginatingAddress();
                        strMessage += ":";
                        strMessage += messages[i].getMessageBody();
                    }

                    Toast.makeText(context, strMessage,6000).show();
                    Intent i=new Intent(context,Operator.class);
                    i.putExtra("msg", strMessage);
                    context.startService(i);
                }
            }

        }
    }



}
