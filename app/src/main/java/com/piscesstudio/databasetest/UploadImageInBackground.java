package com.piscesstudio.databasetest;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;

import com.piscesstudio.databasetest.Callback.ResultCallback;
import com.piscesstudio.databasetest.Utility.BitmapUtility;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class UploadImageInBackground extends AsyncTask<String, Void, String> {
    private String output;
    private Exception error = null;
    private ResultCallback delegate;

    public UploadImageInBackground(ResultCallback callback) {
        this.delegate = callback;
    }

    @Override
    protected String doInBackground(String... arg0) {
        final String param1 = "token";
        final String imagePath = arg0[0];

        final String lineEnd = "\r\n";
        final String twoHyphens = "--";
        final String boundary = "*****";

        String newFileName;
        File sourceFile = new File(imagePath);

        try {
            String fileNameSegments[] = imagePath.split("/");
            String fileName = fileNameSegments[fileNameSegments.length - 1];
            int extensionPos = fileName.lastIndexOf(".");

            newFileName = App.session.getUserID();
            fileName = fileName.replace(fileName.substring(0,extensionPos), newFileName);

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(imagePath, options);

            options.inSampleSize = BitmapUtility.calculateInSampleSize(options, 600);
            options.inJustDecodeBounds = false;
            Bitmap bmp = BitmapFactory.decodeFile(imagePath, options);

            ExifInterface exif = new ExifInterface(sourceFile.getPath());
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            int angle = 0;

            if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                angle = 90;
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
                angle = 180;
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
                angle = 270;
            }

            Matrix mat = new Matrix();
            mat.postRotate(angle);

            Bitmap rotated = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), mat, true);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            rotated.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] byte_arr = stream.toByteArray();

            Uri.Builder builder = new Uri.Builder();
            builder.scheme("http")
                    .encodedAuthority("192.168.1.100:8080")
                    .appendPath("upload.php")
                    .appendQueryParameter(param1, App.session.getToken());
            String link = builder.build().toString();

            URL url = new URL(link);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("ENCTYPE", "multipart/form-data");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            conn.setRequestProperty("uploaded_file", fileName);

            OutputStream os = conn.getOutputStream();
            os.write((twoHyphens + boundary + lineEnd).getBytes());
            os.write(("Content-Disposition: form-data; name=\"uploaded_file\";filename=\"" + fileName + "\"" + lineEnd).getBytes());
            os.write((lineEnd).getBytes());

            os.write(byte_arr);
            os.write((lineEnd).getBytes());
            os.write((twoHyphens + boundary + twoHyphens + lineEnd).getBytes());
            os.flush();
            os.close();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder("");
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
            output = sb.toString().trim();

            JSONObject main = new JSONObject(output);
            int code = main.getInt("code");
            String message = main.getString("message");

            if(code == 200) {
                output = message;
            } else {
                throw new Exception(message);
            }
            bufferedReader.close();
        } catch (Exception e) {
            error = e;
        }
        return output;
    }

    @Override
    protected void onPostExecute(String result) {
        delegate.done(error);
    }
}