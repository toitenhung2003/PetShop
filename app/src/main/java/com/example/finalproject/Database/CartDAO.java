package com.example.finalproject.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.finalproject.Models.Cart;

import java.util.ArrayList;
import java.util.List;

public class CartDAO {
    SQLite sqLite;
    SQLiteDatabase database;

    public CartDAO(Context context) {
        sqLite = new SQLite(context);
        database = sqLite.getWritableDatabase();
    }

    public long insert(Cart cart) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(Cart.CL_ID_PRODUCT, cart.getIdProduct());
        contentValues.put(Cart.CL_QUALITY, cart.getQuality());

        return database.insert(Cart.TB_NAME, null, contentValues);
    }

    public int checkProduct(String id, long quality) {
        long result;
        Cursor cursor = database.rawQuery("SELECT * FROM " + Cart.TB_NAME
                + " WHERE " + Cart.CL_ID_PRODUCT + " = " + "\"" + id + "\"", null);

        cursor.moveToFirst();
        try {
            result = cursor.getLong(2);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return -1;
        }
        cursor.close();

        return update(id, quality + result);
    }

    public List<Cart> getAllData() {
        List<Cart> list = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM " + Cart.TB_NAME + " ORDER BY "
                + Cart.CL_ID + " DESC", null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Cart cart = new Cart();

            cart.setId(cursor.getInt(0));
            cart.setIdProduct(cursor.getString(1));
            cart.setQuality(cursor.getLong(2));

            list.add(cart);
            cursor.moveToNext();
        }
        cursor.close();

        return list;
    }

    public int update(String id, long quality) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(Cart.CL_QUALITY, quality);

        return database.update(Cart.TB_NAME, contentValues,
                Cart.CL_ID_PRODUCT + " = " + "\"" + id + "\"", null);
    }

    public int delete(String id) {
        return database.delete(Cart.TB_NAME, Cart.CL_ID_PRODUCT + " = " + "\"" + id + "\"", null);
    }

    public int deleteAllData() {
        return database.delete(Cart.TB_NAME, null, null);
    }
}
