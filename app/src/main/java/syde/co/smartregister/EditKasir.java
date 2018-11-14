package syde.co.smartregister;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by programmer on 28-Jul-17.
 */

public class EditKasir extends AppCompatActivity {

    private String idBarcode, namaBarang, hargaBarang;
    private EditText edIdBarcode, edNamaProduk, edHargaProduk;
    private Button btnConfirm, btnCancel;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_kasir);
        intent = getIntent();
        idBarcode = intent.getStringExtra("idBarcode");
        namaBarang = intent.getStringExtra("namaBarang");
        hargaBarang = intent.getStringExtra("hargaBarang");

        edIdBarcode = (EditText) findViewById(R.id.txtIdBarcode);
        edNamaProduk = (EditText) findViewById(R.id.txtNamaProduk);
        edHargaProduk = (EditText) findViewById(R.id.txtHargaProduk);
        btnConfirm = (Button) findViewById(R.id.btnConfirm);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        edIdBarcode.setText(idBarcode);
        edNamaProduk.setText(namaBarang);
        edHargaProduk.setText(hargaBarang);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(EditKasir.this, DaftarProduk.class);
                finish();
                startActivity(intent);
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String idBarcodes = edIdBarcode.getText().toString();
                String namaProduks = edNamaProduk.getText().toString();
                String hargaProduks = edHargaProduk.getText().toString();
                int hargaProduki = Integer.parseInt(hargaProduks);
                DatabaseHandler dbHandler;
                dbHandler = new DatabaseHandler(EditKasir.this);
                dbHandler.updateKasir(new Kasir(idBarcodes, namaProduks, hargaProduki));
                Toast.makeText(EditKasir.this, "Sukses Mengubah data", Toast.LENGTH_LONG).show();
                intent = new Intent(EditKasir.this, DaftarProduk.class);
                finish();
                startActivity(intent);
            }
        });
    }
}
