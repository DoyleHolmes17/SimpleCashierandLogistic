package syde.co.smartregister;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by programmer on 5/18/2017.
 */

public class RecyclerKasirAdapter extends RecyclerView.Adapter<RecyclerKasirAdapter.ViewHolder> {
    private List<Kasir> rvData;
    private RecyclerView mRecyclerView;
    private Context mContext;
    private int hitunganin, harga;
    private String hitunganstr, barcode, hargaBarang, name;

    public RecyclerKasirAdapter(Context mContext, List<Kasir> inputData) {
        this.mContext = mContext;
        rvData = inputData;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {


        // di tutorial ini kita hanya menggunakan data String untuk tiap item
        public TextView nomor, namaBarang, harga, subtotal, idProduct;
        public Button btnEdit, btnDelete;

        public ViewHolder(View v) {
            super(v);
            idProduct = (TextView) v.findViewById(R.id.text_id_produk);
            namaBarang = (TextView) v.findViewById(R.id.text_nama_produk);
            harga = (TextView) v.findViewById(R.id.text_harga);
            subtotal = (TextView) v.findViewById(R.id.text_total);
            btnEdit = (Button) v.findViewById(R.id.edit_produk);
            btnDelete = (Button) v.findViewById(R.id.delete_produk);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        // membuat view baru
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_kasir, parent, false);
        // mengeset ukuran view, margin, padding, dan parameter layout lainnya
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder,final int position) {
        // - mengambil elemen dari dataset (ArrayList) pada posisi tertentu
        // - mengeset isi view dengan elemen dari dataset tersebut
        final DatabaseHandler dbHandler;
        name = rvData.get(position).getNamaProduk();
        barcode = rvData.get(position).getIdBarcode();
        harga = rvData.get(position).getHargaProduk();
        hargaBarang = ("Rp " + rvData.get(position).getHargaProduk());
        holder.namaBarang.setText(name);
        holder.harga.setText(hargaBarang);
        holder.idProduct.setText(barcode);
        dbHandler = new DatabaseHandler(mContext);
        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, EditKasir.class);
                intent.putExtra("idBarcode", rvData.get(position).getIdBarcode());
                intent.putExtra("namaBarang", rvData.get(position).getNamaProduk());
                intent.putExtra("hargaBarang",rvData.get(position).getHargaProduk()+"");
                ((Activity)mContext).finish();
                mContext.startActivity(intent);
            }
        });
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbHandler.deleteKasir1(rvData.get(position).getIdBarcode());
                notifyDataSetChanged();
                new AlertDialog.Builder(mContext).setTitle("Smart Register")
                        .setCancelable(false)
                        .setMessage("Delete barang berhasil. Silahkan swipe layar kebawah untuk refresh.")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(mContext, HalamanKosong.class);
                                Utils.saveSharedSetting(mContext, "statHalkosong", "delkasir");
                                ((Activity)mContext).finish();
                                mContext.startActivity(intent);
                            }
                        }).show();
            }
        });

//        final SearchResto find = rvData.get(position);

//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(mContext, DetailRestoActivity.class);
//                intent.putExtra(FieldName.RESTAURANTNAME, find.getRestaurantname());
//                intent.putExtra(FieldName.ADDRESS, find.getAddress());
//                intent.putExtra(FieldName.OPENTIME, find.getOpentime());
//                intent.putExtra(FieldName.CLOSETIME, find.getClosetime());
//                intent.putExtra(FieldName.DISTANCE, find.getDistance());
//                intent.putExtra(FieldName.ID, find.getId());
//
//                mContext.startActivity(intent);
//            }
//        });
    }


    @Override
    public int getItemCount() {
        // menghitung ukuran dataset / jumlah data yang ditampilkan di RecyclerView
        return rvData.size();
    }
}
