package syde.co.smartregister;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by programmer on 25-Jul-17.
 */


public class DatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "KasirManager";

    // Kasir table name
    private static final String TABLE_KASIR = "Kasir";
    private static final String TABLE_CART = "Cart";

    // Kasir Table Columns names
    private static final String KEY_ID = "idBarcode";
    private static final String KEY_NAME = "namaProduk";
    private static final String KEY_PRICE = "hargaProduk";
    private static final String KEY_STOCK = "jumlahProduk";


    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_KASIR_TABLE = "CREATE TABLE " + TABLE_KASIR + "("
                + KEY_ID + " INTEGER UNIQUE," + KEY_NAME + " TEXT,"
                + KEY_PRICE + " INTEGER" + ")";
        String CREATE_CART_TABLE = "CREATE TABLE " + TABLE_CART + "("
                + KEY_ID + " INTEGER UNIQUE," + KEY_NAME + " TEXT,"
                + KEY_PRICE + " INTEGER," + KEY_STOCK + " INTEGER" + ")";
        db.execSQL(CREATE_KASIR_TABLE);
        db.execSQL(CREATE_CART_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_KASIR);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CART);

        // Create tables again
        onCreate(db);
    }

    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */

    // Adding new Kasir
    void addKasir(Kasir kasir) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, kasir.getIdBarcode()); // id barcode
        values.put(KEY_NAME, kasir.getNamaProduk()); // Product name
        values.put(KEY_PRICE, kasir.getHargaProduk()); // Product price

        // Inserting Row
        db.insert(TABLE_KASIR, null, values);
        db.close(); // Closing database connection
    }

    // Adding new Cart
    void addCart(Cart cart) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, cart.getIdBarcode()); // id barcode
        values.put(KEY_NAME, cart.getNamaProduk()); // Product name
        values.put(KEY_PRICE, cart.getHargaProduk()); // Product price
        values.put(KEY_STOCK, cart.getJumlahProduk()); // Product stock

        // Inserting Row
        db.insertWithOnConflict(TABLE_CART, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close(); // Closing database connection
    }

    // Getting single kasir
    Kasir getKasir(String id) {
        Cursor cursor;
        Kasir kasir;

        SQLiteDatabase db = this.getReadableDatabase();

        cursor = db.query(TABLE_KASIR, new String[]{KEY_ID,
                        KEY_NAME, KEY_PRICE}, KEY_ID + "=?",
                new String[]{id}, null, null, null);
        cursor.moveToFirst();

        if (cursor != null && cursor.moveToFirst()) {
            kasir = new Kasir(cursor.getString(0),
                    cursor.getString(1), Integer.parseInt(cursor.getString(2)));
            cursor.close();

        } else {
            kasir = new Kasir("barcode kosong", "tidak ditemukan", 0);
            cursor.close();
        }

        // return kasir
        return kasir;

    }

    Cart getCart(String id) {
        Cursor cursor;
        Cart cart;
        SQLiteDatabase db = this.getReadableDatabase();

        cursor = db.query(TABLE_CART, new String[]{KEY_ID,
                        KEY_NAME, KEY_PRICE, KEY_STOCK}, KEY_ID + "=?",
                new String[]{id}, null, null, null);
        cursor.moveToFirst();
        if (cursor != null && cursor.moveToFirst()) {

            cart = new Cart(cursor.getString(0), cursor.getString(1),
                    Integer.parseInt(cursor.getString(2)), Integer.parseInt(cursor.getString(3)));
            cursor.close();
        } else {
            cart = new Cart("barcode kosong", "tidak ditemukan", 0, 0);
            cursor.close();
        }

        // return cart
        return cart;
    }

    // Getting All Kasirs
    public List<Kasir> getAllKasirs() {
        List<Kasir> kasirList = new ArrayList<Kasir>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_KASIR;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst() && cursor.moveToFirst()) {
            do {
                Kasir kasir = new Kasir();
                kasir.setIdBarcode(cursor.getString(0));
                kasir.setNamaProduk(cursor.getString(1));
                kasir.setHargaProduk(Integer.parseInt(cursor.getString(2)));
                // Adding kasir to list
                kasirList.add(kasir);
            } while (cursor.moveToNext());
            cursor.close();
        } else {
            Kasir kasir = new Kasir();
            kasir.setIdBarcode("barcode kosong");
            kasir.setNamaProduk("barang kosong");
            kasir.setHargaProduk(0);
            // Adding kasir to list
            kasirList.add(kasir);
        }

        // return Kasir list
        return kasirList;
    }

    // Getting All Carts
    public List<Cart> getAllCarts() {
        List<Cart> cartList = new ArrayList<Cart>();
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_CART;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Cart cart = new Cart();
                cart.setIdBarcode(cursor.getString(0));
                cart.setNamaProduk(cursor.getString(1));
                cart.setHargaProduk(Integer.parseInt(cursor.getString(2)));
                cart.setJumlahProduk(Integer.parseInt(cursor.getString(3)));
                // Adding kasir to list
                cartList.add(cart);
            } while (cursor.moveToNext());
            cursor.close();
        } else {
            Cart cart = new Cart();
            cart.setIdBarcode("barcode kosong");
            cart.setNamaProduk("barang kosong");
            cart.setHargaProduk(0);
            cart.setJumlahProduk(0);
            // Adding kasir to list
            cartList.add(cart);
        }

        // return Cart list
        return cartList;
    }

    // Getting All Carts
    public List<Kasir> getBarcode() {
        List<Kasir> kasirList = new ArrayList<Kasir>();
        // Select All Query
        String selectQuery = "SELECT idBarcode,namaProduk FROM " + TABLE_KASIR;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Kasir kasir = new Kasir();
                kasir.setIdBarcode(cursor.getString(0));
                // Adding kasir to list
                kasirList.add(kasir);
                kasir.setNamaProduk(cursor.getString(1));
                kasirList.add(kasir);
            } while (cursor.moveToNext());
            cursor.close();
        } else {
            Kasir kasir = new Kasir();
            kasir.setIdBarcode("barcode kosong");
            kasir.setNamaProduk("barang kosong");
            // Adding kasir to list
            kasirList.add(kasir);
        }


        // return Cart list
        return kasirList;
    }

    // Updating single Kasir
    public int updateKasir(Kasir kasir) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, kasir.getIdBarcode());
        values.put(KEY_NAME, kasir.getNamaProduk());
        values.put(KEY_PRICE, kasir.getHargaProduk());

        // updating row
        return db.update(TABLE_KASIR, values, KEY_ID + " = ?",
                new String[]{String.valueOf(kasir.getIdBarcode())});
    }

    // Updating single Cart
    public int updateCart(Cart cart) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, cart.getIdBarcode());
        values.put(KEY_NAME, cart.getNamaProduk());
        values.put(KEY_PRICE, cart.getHargaProduk());
        values.put(KEY_STOCK, cart.getHargaProduk());

        // updating row
        return db.update(TABLE_CART, values, KEY_ID + " = ?",
                new String[]{String.valueOf(cart.getIdBarcode())});
    }

    // Deleting single Kasir
    public void deleteKasir(Kasir kasir) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_KASIR, KEY_ID + " = ?",
                new String[]{String.valueOf(kasir.getIdBarcode())});
        db.close();
    }

    public void deleteKasir1(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_KASIR, KEY_ID + " = ?", new String[]{
                "" + id,});
        db.close();
    }

    public void deleteCart1(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CART, KEY_ID + " = ?", new String[]{
                "" + id,});
        db.close();
    }

    // Deleting single Cart
    public void deleteCart(Cart cart) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CART, KEY_ID + " = ?",
                new String[]{String.valueOf(cart.getIdBarcode())});
        db.close();
    }

    // Getting Kasir Count
    public int getKasirCount() {
        String countQuery = "SELECT  * FROM " + TABLE_KASIR;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }

    // Getting Cart Count
    public int getCartCount() {
        String countQuery = "SELECT  * FROM " + TABLE_CART;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }

    public void deleteAll() {

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CART, null, null);
        db.close();
    }

    public void tambahData() {

        addKasir(new Kasir("1", "test", 1000));
        addKasir(new Kasir("2", "test1", 1000));
        addKasir(new Kasir("3", "test2", 1000));
        addKasir(new Kasir("4", "test3", 1000));
        addCart(new Cart("1", "test", 1000, 1));
        addCart(new Cart("2", "test1", 2000, 3));
        addCart(new Cart("3", "test2", 3000, 1));
        addCart(new Cart("4", "test3", 4000, 2));
        addCart(new Cart("5", "test4", 5000, 1));
    }

}
