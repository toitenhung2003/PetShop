package com.example.finalproject.Utils;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import android.widget.EditText;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CommonActivity {
    public static boolean isNullOrEmpty(Object input) {
        if (input == null) {
            return true;
        }
        if (input instanceof String) {
            return input.toString().trim().isEmpty();
        }
        if (input instanceof EditText) {
            return ((EditText) input).getText().toString().trim().isEmpty();
        }
        if (input instanceof List) {
            return ((List) input).isEmpty();
        }

        if (input instanceof HashMap) {
            return ((HashMap) input).isEmpty();
        }

        return input instanceof ArrayList && ((ArrayList) input).isEmpty();
    }

    public static String convertUriToBase64(Context context, Uri uri) {
        String base64String = null;

        try {
            ContentResolver contentResolver = context.getContentResolver();
            InputStream inputStream = contentResolver.openInputStream(uri);

            if (inputStream != null) {
                byte[] inputData = getBytesFromInputStream(inputStream);
                base64String = Base64.encodeToString(inputData, Base64.DEFAULT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return base64String;
    }

    private static byte[] getBytesFromInputStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;

        while ((length = inputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, length);
        }

        inputStream.close();
        return byteArrayOutputStream.toByteArray();
    }

    public static Bitmap convertBase64ToBitmap(String base64String) {
        byte[] decodedBytes = Base64.decode(base64String, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    public static String currencyFormat(long amount) {
        NumberFormat formatter = new DecimalFormat("#,###");
        return formatter.format(amount);
    }

    public static String truncateString(String input, int maxLength) {
        if (input != null && input.length() > maxLength) {
            return input.substring(0, maxLength) + " ...";
        } else {
            return input;
        }
    }
}
