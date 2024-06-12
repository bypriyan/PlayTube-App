package com.bypriyan.m24.register;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.widget.EditText;

import com.bypriyan.m24.register.OtpActivity;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;

public class SmsBroadCastReciver extends BroadcastReceiver {

    private OTPListener otpListener;

    public SmsBroadCastReciver(OTPListener otpListener) {
        this.otpListener = otpListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Object[] pdus = (Object[]) bundle.get("pdus");
                if (pdus != null) {
                    for (Object pdu : pdus) {
                        SmsMessage sms = SmsMessage.createFromPdu((byte[]) pdu);
                        String messageBody = sms.getMessageBody();

                        // Parse the OTP from the message
                        String otp = parseOtpFromMessage(messageBody);
                        if (otp != null && otpListener != null) {
                            otpListener.onOTPReceived(otp);
                        }
                    }
                }
            }
        }
    }

    private String parseOtpFromMessage(String message) {
        // Implement your parsing logic here
        // This is just a placeholder example
        return message.replaceAll("[^0-9]", ""); // Extract digits
    }

    public interface OTPListener {
        void onOTPReceived(String otp);
    }
}

