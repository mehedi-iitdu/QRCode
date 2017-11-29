package com.example.mehedi.qrcode;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OTPListener {

    int PERMISSION_ALL = 1;
    String[] PERMISSIONS = {Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS, Manifest.permission.CAMERA};

    private RecyclerView recyclerView;
    private SMSAdapter adapter;
    private RecyclerView.LayoutManager linearLayoutManager;


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

        if(!hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new SMSAdapter(this, getAllSms());
        recyclerView.setAdapter(adapter);

        OtpReader.bind(this,"TrxID");
    }

    public void onClick(View v){
        Intent intent = new Intent(getApplicationContext(), QrScannerActivity.class);
        startActivity(intent);
    }

    @Override
    public void otpReceived(String messageText) {

        Toast.makeText(this,"Got "+messageText,Toast.LENGTH_LONG).show();
        recyclerView.notifyAll();
    }

    @Override
    public void itemClick(SMS sms) {

        if(sms.get_msg().trim().contains("TrxID")){

            String[] strings = sms.get_msg().trim().split("TrxID");
            String text2Qr = "TrxID "+ strings[1].substring(0,11);
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

        else {
            Toast.makeText(getApplicationContext(),"Sorry. This message doesn't have TrxID", Toast.LENGTH_SHORT).show();
        }

    }

    public List<SMS> getAllSms() {
        List<SMS> lstSms = new ArrayList<SMS>();
        SMS objSms = new SMS();
        Uri message = Uri.parse("content://sms/inbox");
        ContentResolver cr = getApplicationContext().getContentResolver();
        Long current_time= System.currentTimeMillis();
        Long sms_time;

        Cursor c = cr.query(message, null, null, null, null);
        this.startManagingCursor(c);
        int totalSMS = c.getCount();

        if (c.moveToFirst()) {
            for (int i = 0; i < totalSMS; i++) {

                objSms = new SMS();
                objSms.set_id(c.getString(c.getColumnIndexOrThrow("_id")));
                objSms.set_address(c.getString(c
                        .getColumnIndexOrThrow("address")));
                objSms.set_msg(c.getString(c.getColumnIndexOrThrow("body")));
                objSms.set_readState(c.getString(c.getColumnIndex("read")));

                sms_time = c.getLong(c.getColumnIndexOrThrow("date"));

                Log.d("Sms date", convertMilisecondsToDate(sms_time));
                Log.d("Current Date", convertMilisecondsToDate(current_time));

                if (convertMilisecondsToDate(sms_time).equals(convertMilisecondsToDate(current_time))){

                    objSms.set_time(convertMilisecondsToDate(sms_time));
                    lstSms.add(objSms);
                }

                c.moveToNext();
            }
        }
        else {
         throw new RuntimeException("You have no SMS in inbox");
        }
        c.close();

        return lstSms;
    }

    private String convertMilisecondsToDate(Long miliseconds){
        Date date = new Date(miliseconds);
        return new SimpleDateFormat("MM/dd/yyyy").format(date);
    }
}
