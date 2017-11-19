package com.example.mehedi.qrcode;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import android.util.Log;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements OTPListener {

    int PERMISSION_ALL = 1;
    String[] PERMISSIONS = {Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS, Manifest.permission.CAMERA};

    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        OtpReader.bind(this,"TrxID");

        if(!hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
    }

    public void onClick(View v){
        Intent intent = new Intent(getApplicationContext(), QrScannerActivity.class);
        startActivity(intent);
    }

    @Override
    public void otpReceived(String messageText) {
        //Toast.makeText(this,"Got "+messageText,Toast.LENGTH_LONG).show();
        Log.d("Otp",messageText);
        String[] strings = messageText.trim().split("TrxID");
        String text2Qr = "TrxID "+ strings[1].substring(0,10);
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(text2Qr, BarcodeFormat.QR_CODE,200,200);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            Intent intent = new Intent(getApplicationContext(), QrActivity.class);
            intent.putExtra("pic",bitmap);
            getApplicationContext().startActivity(intent);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }
}
