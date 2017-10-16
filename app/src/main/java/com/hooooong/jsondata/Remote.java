package com.hooooong.jsondata;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Android Hong on 2017-10-16.
 */

public class Remote {

    public static String getData(String urlString) {
        StringBuilder result = new StringBuilder();
        try {
            // Network 처리
            URL url = new URL(urlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");

            // 통신이 성공적인지 체크
            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                // 여기서부터는 File 에서 Data 를 가져오는 방식과 동일
                InputStreamReader isr = new InputStreamReader(urlConnection.getInputStream());
                BufferedReader br = new BufferedReader(isr);

                String temp = "";
                while ((temp = br.readLine()) != null) {
                    result.append(temp).append("\n");
                }

                br.close();
                isr.close();

            } else {
                Log.e("ServerError", urlConnection.getResponseCode() + " , " + urlConnection.getResponseMessage());
            }
            urlConnection.disconnect();
        } catch (Exception e) {
            Log.e("Error", e.toString());
        }
        return result.toString();
    }

    public static Bitmap getImage(String src) {
        Bitmap bitmap = null;
        try {
            // Network 처리
            URL url = new URL(src);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");

            // 통신이 성공적인지 체크
            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                // 여기서부터는 File 에서 Data 를 가져오는 방식과 동일
                InputStream is = urlConnection.getInputStream();
                bitmap = BitmapFactory.decodeStream(is);
                
                is.close();
            } else {
                Log.e("ServerError", urlConnection.getResponseCode() + " , " + urlConnection.getResponseMessage());
            }
            urlConnection.disconnect();
        } catch (Exception e) {
            Log.e("Error", e.toString());
        }

        return bitmap;
    }
}
