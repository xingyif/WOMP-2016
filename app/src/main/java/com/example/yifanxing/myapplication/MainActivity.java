package com.example.yifanxing.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.logging.Logger;

public class MainActivity extends Activity {
    private static final int OPEN_PHOTO_FOLDER_REQUEST_CODE = 1;  // // TODO: 1/23/16  
    Button button;
    ImageView imageView;
    String ba1;
    static final int CAM_REQUEST = 1;

    Logger mLogger = Logger.getLogger("APP");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = (Button) findViewById(R.id.button);
        imageView = (ImageView) findViewById(R.id.image_view);
        mLogger.warning("=====  setting up button");
        button.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                mLogger.warning("===== button clicked");
                Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File file = getFile();
                camera_intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                startActivityForResult(camera_intent, CAM_REQUEST);
            }
        });
    }

    private File getFile() {
        File internalDir = getExternalCacheDir();
        return new File(internalDir, "cam_image.jpg");


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mLogger.warning("==== Received file: ");
        mLogger.warning("" + getFile().exists());
        imageView.setImageDrawable(Drawable.createFromPath(getFile().getAbsolutePath()));
        // converst the image to base64
        Bitmap bm = BitmapFactory.decodeFile(getFile().getAbsolutePath());
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte[] ba = bao.toByteArray();
        ba1 = Base64.encodeToString(ba,Base64.DEFAULT);

        Log.d("encoded", ba1);
    }



}
// // TODO: 1/23/16  
//    cam_image.jpg.buildDrawingCache();
//    Bitmap bmap = profile_image.getDrawingCache();
//    String encodedImageData =getEncoded64ImageStringFromBitmap(bmap);
//
//
//    public String getEncoded64ImageStringFromBitmap(Bitmap bitmap) {
//        ByteArrayOutputStream stream = new ByteArrayOutputStream();
//        bitmap.compress(CompressFormat.JPEG, 70, stream);
//        byte[] byteFormat = stream.toByteArray();
//        // get the base 64 string
//        String imgString = Base64.encodeToString(byteFormat, Base64.NO_WRAP);
//
//        return imgString;
//    }

    // find the pic
//    public void onEncodeClicked(View view) {
//
//        //select picture
//        Intent intent = new Intent();
//        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        startActivityForResult(intent, OPEN_PHOTO_FOLDER_REQUEST_CODE);
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if(OPEN_PHOTO_FOLDER_REQUEST_CODE == requestCode   && RESULT_OK == resultCode) {
//
//            //encode the image
//            Uri uri = data.getData();
//            try {
//                //get the image path
//                String[] projection = {MediaStore.Images.Media.DATA};
//                CursorLoader cursorLoader = new CursorLoader(this,uri,projection,null,null,null);
//                Cursor cursor = cursorLoader.loadInBackground();
//                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//                cursor.moveToFirst();
//
//                String path = cursor.getString(column_index);
//                Log.d(TAG,"real path: "+path);
//                encode(path);
//            } catch (Exception ex) {
//                Log.e(TAG, "failed." + ex.getMessage());
//            }
//        }
//    }
//
//// converts the pic to bitmap
//    private void encode(String path) {
//        //decode to bitmap
//        Bitmap bitmap = BitmapFactory.decodeFile(path);
//        Log.d(TAG, "bitmap width: " + bitmap.getWidth() + " height: " + bitmap.getHeight());
//        //convert to byte array
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
//        byte[] bytes = baos.toByteArray();
//
//        //base64 encode
//        byte[] encode = Base64.encode(bytes,Base64.DEFAULT);
//        String encodeString = new String(encode);
//        mTvShow.setText(encodeString);
//    }
//
//// convert it back to a pic
//    public void onDecodeClicked(View view) {
//        byte[] decode = Base64.decode(mTvShow.getText().toString(),Base64.DEFAULT);
//        Bitmap bitmap = BitmapFactory.decodeByteArray(decode, 0, decode.length);
//        //save to image on sdcard
//        saveBitmap(bitmap);
//    }
//
//    private void saveBitmap(Bitmap bitmap) {
//        try {
//            String path = Environment.getExternalStorageDirectory().getPath()
//                    +"/decodeImage.jpg";
//            Log.d("linc","path is "+path);
//            OutputStream stream = new FileOutputStream(path);
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream);
//            stream.close();
//            Log.e("linc","jpg okay!");
//        } catch (IOException e) {
//            e.printStackTrace();
//            Log.e("linc","failed: "+e.getMessage());
//        }
//    }
//}