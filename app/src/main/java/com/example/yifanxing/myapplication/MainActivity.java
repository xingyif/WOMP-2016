package com.example.yifanxing.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
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

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import android.os.AsyncTask;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class MainActivity extends Activity {
    private static final int OPEN_PHOTO_FOLDER_REQUEST_CODE = 1;  // // TODO: 1/23/16
    ImageView imageView;
    String ba1;
    static final int CAM_REQUEST = 1;

    Logger mLogger = Logger.getLogger("APP");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = (ImageView) findViewById(R.id.imageView);
        mLogger.warning("=====  setting up button");
        imageView.setImageResource(R.drawable.womplogo);
        imageView.setOnClickListener(new View.OnClickListener() {


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
        // converts the image to base64
        Bitmap bm = BitmapFactory.decodeFile(getFile().getAbsolutePath());
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte[] ba = bao.toByteArray();
        ba1 = Base64.encodeToString(ba,Base64.DEFAULT);

        Log.d("encoded", ba1);
        new ImageIdentifier().execute(ba1);
    }

    public void alertSimpleListView(List<String> items) {
        //list of food items to be shown in alert dialog
        final String[] array = items.toArray(new String[items.size()]);

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Make your selection");
        builder.setItems(array, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                String foodChoice = array[item];
                Log.d("item", (String) array[item]);
                new getCalories().execute(foodChoice);
                dialog.dismiss();
            }
        }).show();
    }

    public class ImageIdentifier extends AsyncTask<String, Void, JSONObject> {
        private static final String apiKey = "i8w9VkxHx45Sb6JuYUDZWg4CNsOfveZRvz8RuMrjQXMrPcMeTb";
        @Override
        protected JSONObject doInBackground (String...params){
            String image = params[0];
            HttpClient client = new DefaultHttpClient();
            String url = "https://www.metamind.io/vision/classify";
            HttpPost post = new HttpPost(url);
            post.addHeader("Authorization", apiKey);
            post.addHeader("Content-Type","appliation/json");
            JSONObject obj = new JSONObject();
            StringEntity entity = null;
            try {
                obj.put("classifier_id", "food-net");
                obj.put("image_url", "data:image/jpeg;base64," + image);
                entity = new StringEntity(obj.toString());
            }
            catch(JSONException | UnsupportedEncodingException f){

            }

            post.setEntity(entity);
            try{
                return new JSONObject(EntityUtils.toString(client.execute(post).getEntity()));

            }
            catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JSONObject error = new JSONObject();
            try {
                error.put("e","error");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return error;
        }

        //@Override
        protected void onPostExecute(JSONObject o){
            Log.d("objects", o.toString());
            JSONArray predictions = o.optJSONArray("predictions");
            List<String> classNames = new ArrayList<>(predictions.length());
            for (int i = 0; i < predictions.length(); i++) {
                classNames.add(predictions.optJSONObject(i).optString("class_name"));
            }
            alertSimpleListView(classNames);
        }
    }

    public class getCalories extends AsyncTask<String, Void, String> {
        final String appId = "69f1b02f";
        final String apiKey = "27ddbb03562b1460fe0d834e0a0c699f";
        @Override
        protected String doInBackground (String...params) {
            String search = params[0];
            try {
                search = URLEncoder.encode(search, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet("https://api.nutritionix.com/v1_1/search/"+search+ "?results=0%3A1&cal_min=0&cal_max=50000&fields=item_name%2Cnf_calories%2Cnf_total_fat&appId=" + appId + "&appKey=" + apiKey);
            HttpResponse response;
            try {
                response = client.execute(request);
                String responseData = IOUtils.toString(response.getEntity().getContent(), "UTF-8");
                Log.d("Response of GET request", responseData);
                return responseData;
            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return "error";
        }
    }

    //@Override
    protected void onPostExecute(String s) throws JSONException {
        //Log.d("objects", o.toString());
        JSONObject jsnobject = new JSONObject(s);
        JSONArray jsonCalories = jsnobject.optJSONArray("fields");
        List<String> calorieData = new ArrayList<>(jsonCalories.length());
        for (int i = 0; i < jsonCalories.length(); i++) {
            calorieData.add(jsonCalories.optJSONObject(i).optString("nf_calories"));
        }
        //alertSimpleListView(classNames);
    }

}
