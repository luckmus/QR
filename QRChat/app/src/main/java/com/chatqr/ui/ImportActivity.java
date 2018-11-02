package com.chatqr.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.ChangeTransform;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.chatqr.R;
import com.chatqr.bl.ChatController;
import com.chatqr.bl.QRCodeHelper;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

import java.nio.charset.Charset;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ImportActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import);
        ImageView picView = (ImageView) findViewById(R.id.QRImage);


        Intent receivedIntent = getIntent();
        String receivedAction = receivedIntent.getAction();
        String receivedType = receivedIntent.getType();





        // make sure it's an action and type we can handle
        if (receivedAction.equals(Intent.ACTION_SEND)) {
            if (receivedType.startsWith("image/")) {
                Uri receivedUri = (Uri) receivedIntent
                        .getParcelableExtra(Intent.EXTRA_STREAM);
                if (receivedUri != null) {
                    picView.setImageURI(receivedUri);// just for demonstration
                }
            }
        } else if (receivedAction.equals(Intent.ACTION_VIEW)) {

            if (receivedType.startsWith("image/")) {
                Uri receivedUri = (Uri) receivedIntent
                        .getParcelableExtra(Intent.EXTRA_STREAM);
                Toast.makeText(this, receivedUri.toString(), Toast.LENGTH_SHORT).show();
                if (receivedUri != null) {
                    picView.setImageURI(receivedUri);// just for demonstration
                }
            }

        }

    }


    private void scan() {
        //https://stackoverflow.com/questions/29649673/scan-barcode-from-an-image-in-gallery-android
        ImageView picView =findViewById(R.id.QRImage);
        Bitmap bMap = ((BitmapDrawable)picView.getDrawable()).getBitmap();;
        ChatController.getInstance().scan(this, bMap);
    }

    public void imp(View view){
        scan();
    }

    public void exp(View view){
        Bitmap bMap =QRCodeHelper.newInstance().setContent("hello").generate();
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, "download this image");
        String bitmapPath = MediaStore.Images.Media.insertImage(getContentResolver(), bMap,"title", null);
        Log.i("TAG", "bitmapPath: "+bitmapPath);
        Uri bitmapUri = Uri.parse(bitmapPath);
        intent.putExtra(Intent.EXTRA_STREAM, bitmapUri);
        intent.setType("image/*");
        startActivity(Intent.createChooser(intent, "Share image via..."));
    }
}
