package com.saatco.murshadik.Helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.saatco.murshadik.model.Message;
import com.saatco.murshadik.model.User;

import java.util.ArrayList;


public class DatabaseHelper extends SQLiteOpenHelper {

    // Logcat tag
    private static final String LOG = "DatabaseHelper";

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "dataag";

    // Table Names

    private static final String TABLE_CART = "messages";

    private static final String TABLE_USERS = "users";

    //My cart table Columns
    private static final String KEY_ID = "id";
    private static final String KEY_IN_MSG = "message";
    private static final String KEY_USER_1 = "user_one";
    private static final String KEY_USER_2 = "user_two";
    private static final String KEY_IS_SEND_BY_ME = "is_by_me";
    private static final String KEY_TIME = "time";

    private static final String KEY_USER_NAME = "name";
    private static final String KEY_USER_UDID = "udid";
    private static final String KEY_USER_LASTMESSAGE = "last_message";
    private static final String KEY_USER_DATE = "last_message_date";

    // Table Create Statements

    private static final String CREATE_TABLE_MSGS = "CREATE TABLE "
            + TABLE_CART + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_IN_MSG
            + " TEXT NOT NULL"+");";

    private static final String CREATE_TABLE_USERS = "CREATE TABLE "
            + TABLE_USERS + "("+ KEY_USER_UDID
            + " TEXT NOT NULL," + KEY_USER_NAME + " TEXT,"  + KEY_USER_LASTMESSAGE + " TEXT," + KEY_USER_DATE + " TEXT"+");";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // creating required tables
        db.execSQL(CREATE_TABLE_MSGS);
        db.execSQL(CREATE_TABLE_USERS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
       // db.execSQL("DROP TABLE IF EXISTS " + TABLE_CART);
       // db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVORITES);

        if (newVersion > oldVersion) {
            Log.v("banana","inside new version 8");
            try {
               // db.execSQL("ALTER TABLE cart ADD COLUMN is_bogo INTEGER DEFAULT 0");
               // db.execSQL("DROP TABLE IF EXISTS " + TABLE_FILTER);
               // db.execSQL(CREATE_TABLE_FILTER);
                //db.execSQL("ALTER TABLE cart ADD COLUMN variant_id INTEGER DEFAULT 0");
            }catch (Exception ex){}
        }else{
            Log.v("banana","inside old version");
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_CART);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
            onCreate(db);
        }
        // create new tables

       // onCreate(db);
    }



    public void addToHistory(Message msg){

        ContentValues values = new ContentValues();
        values.put(KEY_IN_MSG,msg.getMessage());

        SQLiteDatabase db = getWritableDatabase();

        db.insert(TABLE_CART,null,values);
        db.close();
    }

    /*public void updateCart(Product product) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID,product.getId());
        values.put(KEY_NAME,product.getName());
        values.put(KEY_NAME_AR,product.getNameAr());
        values.put(KEY_ML,product.getMl());
        values.put(KEY_REGULAR_PRICE,product.getRegularPrice());
        values.put(KEY_SALE_PRICE,product.getSalePrice());
        values.put(KEY_IMAGE,product.getImage());
        values.put(KEY_QTY,product.getQty());
        values.put(KEY_ON_SALE,product.getIsOnSale());
        values.put(KEY_BRAND_ID,product.getBrandID());
        values.put(KEY_BOGO,product.getIsBogo());
        values.put(KEY_BOGO_MIN_QTY,product.getMinQty());
        values.put(KEY_VARIANT_ID,product.getVariantID());

        // updating row
        db.update(TABLE_CART, values, KEY_ID+ " = ?",
                new String[] { String.valueOf(product.getId())});
        db.close();
    }*/

    public void deleteFromMyCart(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CART, KEY_ID + " = ?",
                new String[] { String.valueOf(id) });
    }

    public int countMsgs(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor mCount= db.rawQuery("SELECT COUNT("+ KEY_ID +") FROM " + TABLE_CART, null);
        mCount.moveToFirst();
        int count= mCount.getInt(0);
        mCount.close();
        return count;
    }

    public String getProductIDsFromMyList() {

        String selectQuery = "SELECT group_concat(MyList.id) FROM MyList" ;

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery,null);
        c.moveToFirst();
        String IDs = c.getString(0);
        c.close();
        return IDs;
    }

    public ArrayList<Message> getHistory(String id) {
        ArrayList<Message> products = new ArrayList<Message>();
        String selectQuery = "SELECT  * FROM " + TABLE_CART + " WHERE messages.message ='" +id +"'";

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Message r = new Message();
                r.setId(c.getInt((c.getColumnIndex(KEY_ID))));
                r.setMessage(c.getString(c.getColumnIndex(KEY_IN_MSG)));
                products.add(r);
            } while (c.moveToNext());
        }
        return products;
    }

    public ArrayList<Message> getAllCount() {
        ArrayList<Message> products = new ArrayList<Message>();
        String selectQuery = "SELECT  * FROM " + TABLE_CART;

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Message r = new Message();
                r.setId(c.getInt((c.getColumnIndex(KEY_ID))));
                r.setMessage(c.getString(c.getColumnIndex(KEY_IN_MSG)));
                products.add(r);
            } while (c.moveToNext());
        }
        c.close();
        return products;
    }

    public void deleteFromMessageCount(String msg){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CART, KEY_IN_MSG + " = ?",
                new String[] { String.valueOf(msg) });
       // db.close();
    }

    public void addUserToChat(User user){

        ContentValues values = new ContentValues();
        values.put(KEY_USER_UDID,user.getUdid());
        values.put(KEY_USER_NAME,user.getFullname());
        values.put(KEY_USER_LASTMESSAGE,user.getLastMessage());
        values.put(KEY_USER_DATE,user.getLastMessageDate());

        SQLiteDatabase db = getWritableDatabase();

        db.insert(TABLE_USERS,null,values);
        db.close();
    }

    public void updateUsers(User user) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_USER_UDID,user.getUdid());
        values.put(KEY_USER_NAME,user.getFullname());
        values.put(KEY_USER_LASTMESSAGE,user.getLastMessage());
        values.put(KEY_USER_DATE,user.getLastMessageDate());

        // updating row
        db.update(TABLE_USERS, values, KEY_USER_UDID+ " = ?",
                new String[] { String.valueOf(user.getUdid())});
        db.close();
    }

    public boolean isUserExist(String udid){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor mCount= db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE users.udid ='" + udid + "'", null);
        mCount.moveToFirst();
        int count= mCount.getInt(0);
        mCount.close();
        return count > 0;
    }

    public ArrayList<User> getUsers(String number,String me) {
        ArrayList<User> products = new ArrayList<User>();
        String selectQuery = "SELECT  * FROM " + TABLE_USERS;

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                User r = new User();
                r.setUdid(c.getString(c.getColumnIndex(KEY_USER_UDID)));
                r.setFullname(c.getString(c.getColumnIndex(KEY_USER_NAME)));
                r.setLastMessage(c.getString(c.getColumnIndex(KEY_USER_LASTMESSAGE)));
                r.setLastMessageDate(c.getString(c.getColumnIndex(KEY_USER_DATE)));
                products.add(r);
            } while (c.moveToNext());
        }
        return products;
    }
}


