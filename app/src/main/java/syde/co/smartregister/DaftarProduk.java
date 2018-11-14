package syde.co.smartregister;

import android.app.Activity;
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
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class DaftarProduk extends AppCompatActivity {

    private Button tambahBelanja, cekHarga, tambahProduk, screenShot;
    private TextView textTotal;
    private RecyclerView.Adapter adapter;
    private RecyclerView rvView;
    private ArrayList<Kasir> listKasir;
    private DatabaseHandler dbHandler;
    private Intent intent;
    private LinearLayoutManager layoutManager;
    TextView noData,textView;
    TableLayout tableLayout;
    private SwipeRefreshLayout swipp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.daftar_produk);

        tambahBelanja = (Button) findViewById(R.id.btnTambahbel);
        swipp = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);

        rvView = (RecyclerView) findViewById(R.id.rV_main);
        noData = (TextView) findViewById(R.id.noData);
        textView = (TextView) findViewById(R.id.textView);
        tableLayout = (TableLayout) findViewById(R.id.tableLayout);
        rvView.setHasFixedSize(true);

        dbHandler = new DatabaseHandler(this);

        setData();

        swipp.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                setData();
                swipp.setRefreshing(false);
            }
        });
        swipp.setRefreshing(false);
    }

//    public void home (View view){
//        Utils.saveSharedSetting(DaftarProduk.this, "backback", "1");
//        onBackPressed();
//    }

    protected void setData(){
        listKasir= new ArrayList<>();
        listKasir = (ArrayList<Kasir>) dbHandler.getAllKasirs();
        Log.e("list Kasir daf ", listKasir+"");
        if (dbHandler.getAllKasirs().get(0).getHargaProduk() == 0){
            textView.setVisibility(View.GONE);
            rvView.setVisibility(View.GONE);
            tableLayout.setVisibility(View.GONE);
            noData.setVisibility(View.VISIBLE);
//            new AlertDialog.Builder(DaftarProduk.this).setTitle("Smart Register")
//                    .setCancelable(false)
//                    .setMessage("Data kosong. Silahkan tambah daftar produk.")
//                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//
//                        }
//                    }).show();
        } else {
            layoutManager = new LinearLayoutManager(this);
            rvView.setLayoutManager(layoutManager);
            adapter = new RecyclerKasirAdapter(this, listKasir);
            rvView.setAdapter(adapter);
        }
    }

//    @Override
//    public void onBackPressed() {
//        intent = new Intent(DaftarProduk.this, MainActivity.class);
//        finish();
//        startActivity(intent);
//    }

}

