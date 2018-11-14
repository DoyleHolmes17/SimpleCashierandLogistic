package syde.co.smartregister;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class RegisterActivity extends AppCompatActivity {

    private Button tambahBelanja, cekHarga, tambahProduk, screenShot;
    private TextView textTotal;
    private List<Kasir> listKasir;
    private List<Cart> listCart;
    private DatabaseHandler dbHandler;
    private Intent intent;
    private LinearLayoutManager layoutManager;
    private RecyclerView.Adapter adapter;
    private RecyclerView rvView;
    private String barcode, namabar, totalharga, directmenu;
    private int hargabar, totalhargain, txTotal, i, hitungan;
    private Cart cart;
    boolean doubleBackToExitPressedOnce = false;
    private SwipeRefreshLayout swipere;
    private String firsttime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tambahBelanja = (Button) findViewById(R.id.btnTambahbel);
        cekHarga = (Button) findViewById(R.id.btnCekharga);
        tambahProduk = (Button) findViewById(R.id.btnTambahproduk);
        screenShot = (Button) findViewById(R.id.btnScreenshot);
        textTotal = (TextView) findViewById(R.id.text_total);
        swipere = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);

        rvView = (RecyclerView) findViewById(R.id.rV_main);
        rvView.setHasFixedSize(true);

        hitungan = 0;
        Utils.saveSharedSetting(this, "hitungan", hitungan + "");
//        directmenu = Utils.readSharedSetting(this, "directmenu", "yes");
//
//        if (directmenu.equals("yes")){
//            intent = new Intent(MainActivity.this, MainMenuActivity.class);
//            Utils.saveSharedSetting(MainActivity.this, "directmenu", "no");
//            startActivity(intent);
//            finish();
//        } else if (directmenu.equals("no")){
//            // do nothing
//        }

        dbHandler = new DatabaseHandler(this);
        dbHandler.getWritableDatabase();

        swipere.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Utils.saveSharedSetting(RegisterActivity.this, "statusrefresh", "true");
                dbHandler = new DatabaseHandler(RegisterActivity.this);
                dbHandler.getWritableDatabase();
                listCart = dbHandler.getAllCarts();
                Log.e("size cart ", String.valueOf(listCart.size()));
                setData();

                swipere.setRefreshing(false);
            }
        });

        swipere.setRefreshing(false);
//        rvView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                setData();
//            }
//        });
//        intent = getIntent();
//
//            try {
//                hargabar = Integer.parseInt(intent.getStringExtra("harga"));
//            } catch (NumberFormatException e) {
//                hargabar = 0;
//            }
//
//            if (hargabar != 0) {
//                barcode = intent.getStringExtra("barcode");
//                namabar = intent.getStringExtra("nama");
        listCart = dbHandler.getAllCarts();
        Log.e("size cart ", String.valueOf(listCart.size()));

        setData();

        screenShot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Screenshoot.takeScreenshot(RegisterActivity.this);
            }
        });

        cekHarga.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(view.getContext(), BarcodeScanner.class);
                Utils.saveSharedSetting(RegisterActivity.this, "cekatautambah", "cek");
                startActivity(intent);
                finish();
            }
        });

        tambahBelanja.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(v.getContext(), BarcodeScanner.class);
                Utils.saveSharedSetting(RegisterActivity.this, "cekatautambah", "tambahbelanja");
                Utils.saveSharedSetting(RegisterActivity.this, "statusrefresh", "false");
                startActivity(intent);
                finish();
            }
        });

        tambahProduk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(v.getContext(), TambahProduk.class);
//                Utils.saveSharedSetting(MainActivity.this, "backback", "2");
                startActivity(intent);
                finish();
            }
        });

        firsttime = Utils.readSharedSetting(this, "firsttime", "false");
        if (firsttime.equals("true")){
            intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
            finish();
        }

    }

    public void clear(View view) {
        dbHandler.deleteAll();
        new AlertDialog.Builder(RegisterActivity.this).setTitle("Smart Register")
                .setCancelable(false)
                .setMessage("Delete semua daftar belanja berhasil. Silahkan swipe layar kebawah untuk refresh.")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        intent = new Intent(RegisterActivity.this, HalamanKosong.class);
                        Utils.saveSharedSetting(RegisterActivity.this, "statHalkosong", "delcart");
                        finish();
                        startActivity(intent);
                    }
                }).show();
    }

    protected void setData(){
        Log.e("list cart >> ", listCart + "");
        if (listCart.get(0).getHargaProduk() == 0) {
            rvView.setVisibility(View.GONE);
            textTotal.setText("Total Harga = Rp 0");
        } else if (listCart.get(0).getHargaProduk() != 0) {
//            String hitung = Utils.readSharedSetting(this, "hitungan", null);
//            try {
//                hitungan = Integer.parseInt(hitung);
//            } catch (NumberFormatException e) {
//                hitungan = 99;
//            }
//            Log.e("hitungan main act >> ", hitungan+"");
//            int count = hitungan;
//            for (count = 1, count == hitungan, count++){
//
//            }
            rvView.setVisibility(View.VISIBLE);
            layoutManager = new LinearLayoutManager(this);
            rvView.setLayoutManager(layoutManager);
            adapter = new RecyclerCartAdapter(this, listCart);
            rvView.setAdapter(adapter);

            txTotal = 0;
            for (i = 0; i < listCart.size(); i++){
                totalhargain = (listCart.get(i).getHargaProduk())*(listCart.get(i).getJumlahProduk());
                txTotal = txTotal + totalhargain;
                Log.e("txTotal ", txTotal+"");
            }

            Log.e("mainact txtotal ", txTotal + "");
            textTotal.setText("Total Harga = Rp " + (txTotal+ ""));

        }
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Daftar belanja akan didelete, klik back sekali lagi untuk exit dan hapus semua daftar belanja.", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
                dbHandler.deleteAll();
            }
        }, 2000);
    }


    public void kembali(View view) {
        finish();
        intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }
}
