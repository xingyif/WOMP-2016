//package com.example.yifanxing.myapplication;
//
//
//import android.util.Base64;
//
//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//
//public class FileInputStream {
//    InputStream inputStream = new FileInputStream("cam_image.jpg");//You can get an inputStream using any IO API
//    byte[] bytes;
//    byte[] buffer = new byte[8192];
//    int bytesRead;
//    ByteArrayOutputStream output = new ByteArrayOutputStream();
//    try {
//        while ((bytesRead = inputStream.read(buffer)) != -1) {
//            output.write(buffer, 0, bytesRead);
//        }
//    } catch (IOException e) {
//        e.printStackTrace();
//    }
//    bytes = output.toByteArray();
//    String encodedString = Base64.encodeToString(bytes, Base64.DEFAULT);
//}
