package syde.co.smartregister;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

/**
 * Created by programmer on 5/18/2017.
 */

public class RecyclerCartAdapter extends RecyclerView.Adapter<RecyclerCartAdapter.ViewHolder> {
    private List<Cart> rvData;
    private RecyclerView mRecyclerView;
    private Context mContext;
    private int hitunganin, hargabar, quantity, tothar;
    private String hitunganstr;
    private Cart cart;

    public RecyclerCartAdapter(Context mContext, List<Cart> inputData) {
        this.mContext = mContext;
        rvData = inputData;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // di tutorial ini kita hanya menggunakan data String untuk tiap item
        public TextView nomor, namaBarang, qty, harga, subtotal;
        public Button btnDelete;

        public ViewHolder(View v) {
            super(v);
            nomor = (TextView) v.findViewById(R.id.text_no);
            namaBarang = (TextView) v.findViewById(R.id.text_barang);
            qty = (TextView) v.findViewById(R.id.text_qty);
            harga = (TextView) v.findViewById(R.id.text_harga);
            subtotal = (TextView) v.findViewById(R.id.text_sub_total);
            btnDelete = (Button) v.findViewById(R.id.delete_produk);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        // membuat view baru
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_cart, parent, false);

        // mengeset ukuran view, margin, padding, dan parameter layout lainnya
        ViewHolder vh = new ViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // - mengambil elemen dari dataset (ArrayList) pada posisi tertentu
        // - mengeset isi view dengan elemen dari dataset tersebut
        final String name = rvData.get(position).getNamaProduk();
        final String barcode = rvData.get(position).getIdBarcode();
        final String hargaBarang = (rvData.get(position).getHargaProduk())+"";
        final String qtyy = (rvData.get(position).getJumlahProduk())+"";
        final DatabaseHandler dbHandler;

        final String totalharga = ((rvData.get(position).getJumlahProduk())*(rvData.get(position).getHargaProduk()))+"";
        Log.e("total harga ", (position+"") + " , " + (rvData.get(position).getJumlahProduk()+"") + " , " + (rvData.get(position).getHargaProduk()+"") + " , " + totalharga);
        Utils.saveSharedSetting(mContext, "totalhargabelanja", totalharga);

        Log.e("VH >> ", name + " , " + barcode + " , " + hargaBarang + " , " + qtyy);
        String refresh = Utils.readSharedSetting(mContext, "statusrefresh", null);
        if (refresh.equals("true")){
            hitunganstr = Utils.readSharedSetting(mContext, "hitungan", null);
            hitunganin = (Integer.parseInt(hitunganstr));
        } else if (refresh.equals("false")) {
            hitunganstr = Utils.readSharedSetting(mContext, "hitungan", null);
            hitunganin = (Integer.parseInt(hitunganstr)) + 1;
            Utils.saveSharedSetting(mContext, "hitungan", hitunganin + "");
        }
        holder.nomor.setText(hitunganin+"");
        holder.namaBarang.setText(name);
        holder.harga.setText(hargaBarang);
        holder.qty.setText(qtyy);
        holder.subtotal.setText(totalharga);
        dbHandler = new DatabaseHandler(mContext);

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    hargabar = Integer.parseInt(hargaBarang);

                } catch (NumberFormatException e) {
                    hargabar = 0;
                }

                try {
                    quantity = Integer.parseInt(qtyy);

                } catch (NumberFormatException e) {
                    quantity = 0;
                }

                try {
                    tothar = Integer.parseInt(totalharga);

                } catch (NumberFormatException e) {
                    tothar = 0;
                }

                Log.e("quantity ", quantity + " , " + barcode);
                if (quantity >= 2){
                    int qua = quantity - 1;
                    cart = new Cart(barcode, name, hargabar, qua);
                    dbHandler.addCart(cart);
                    holder.nomor.setText(hitunganin+"");
                    holder.namaBarang.setText(name);
                    holder.harga.setText(hargaBarang);
                    holder.qty.setText(qua+"");
                    holder.subtotal.setText((tothar - hargabar)+"");

                } else if (quantity == 1){
                    dbHandler.deleteCart1(barcode);
                    notifyDataSetChanged();
                    holder.nomor.setText("");
                    holder.namaBarang.setText("");
                    holder.harga.setText("");
                    holder.qty.setText("");
                    holder.subtotal.setText("");
                }
                new AlertDialog.Builder(mContext).setTitle("Smart Register")
                        .setCancelable(false)
                        .setMessage("Delete barang berhasil. Silahkan swipe layar kebawah untuk refresh.")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(mContext, HalamanKosong.class);
                                Utils.saveSharedSetting(mContext, "statHalkosong", "delcart");
                                ((Activity)mContext).finish();
                                mContext.startActivity(intent);
                            }
                        }).show();

            }
        });
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
