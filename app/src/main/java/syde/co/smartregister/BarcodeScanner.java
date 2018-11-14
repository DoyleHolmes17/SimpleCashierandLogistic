package syde.co.smartregister;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

import net.sourceforge.zbar.Config;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;

import java.util.HashMap;
import java.util.List;

/**
 * Created by erdearik on 12/19/15.
 */
public class BarcodeScanner extends AppCompatActivity implements TextWatcher, AdapterView.OnItemClickListener {

    private Camera mCamera;
    private CameraPreview mPreview;
    private Handler autoFocusHandler;
    private String scanResult, statustombol, txKode;

    private Button scanButton, cariKodebarang, homee;
    private ImageScanner scanner;
    private EditText textKode;
    private Intent intent;
    private int a, b;
    private EditText quantity;
    private FrameLayout camerapreview;

    private List<Kasir> listKasir;
    private List<Cart> listCart;
    private Kasir kasir;
    private Cart cart;
    private DatabaseHandler dbHelper;
    private ArrayAdapter<Kasir> adapter;

    private ListView lv;

    private boolean barcodeScanned = false;
    private boolean previewing = true;

    private void persiapanData() {
        if (listKasir.get(0).getIdBarcode().equals("barcode kosong")) {

        } else {
            adapter = new ArrayAdapter<Kasir>(this,
                    android.R.layout.simple_expandable_list_item_1,
                    listKasir);
            lv.setAdapter(adapter);

            Log.e("listkasir ", adapter+"");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.barcode_scanner);

        scanButton = (Button) findViewById(R.id.ScanButton);
        homee = (Button) findViewById(R.id.home);
        cariKodebarang = (Button) findViewById(R.id.cari_kode_barang);
        textKode = (EditText) findViewById(R.id.textKode);
        camerapreview = (FrameLayout) findViewById(R.id.cameraPreview);
        lv = (ListView) findViewById(R.id.lv_data);
//        camerapreview = (FrameLayout) findViewById(R.id.cameraPreview);

        Utils.saveSharedSetting(this, "statushalaman", "tengah");

        statustombol = Utils.readSharedSetting(this, "cekatautambah", null);
        scanButton.setText("Scan Barcode");

        dbHelper = new DatabaseHandler(this);
        dbHelper.getReadableDatabase();
        camerapreview.setVisibility(View.GONE);

        listKasir = dbHelper.getAllKasirs();

        Log.e("listkasir ", listKasir+"");
        if (listKasir == null) {
            //do nothing
        } else {
            persiapanData();
        }

        if (statustombol.equals("tambahbelanja")){
            cariKodebarang.setText("Tambah Belanja");
        } else {
            //do nothing
        }

        textKode.addTextChangedListener(this);
        lv.setOnItemClickListener(this);

        initControls();
    }


    private void initControls() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        autoFocusHandler = new Handler();
        mCamera = getCameraInstance();

        // Instance barcode scanner
        scanner = new ImageScanner();
        scanner.setConfig(0, Config.X_DENSITY, 3);
        scanner.setConfig(0, Config.Y_DENSITY, 3);

        mPreview = new CameraPreview(BarcodeScanner.this, mCamera, previewCb,
                autoFocusCB);
        FrameLayout preview = (FrameLayout) findViewById(R.id.cameraPreview);
        preview.addView(mPreview);

        statustombol = Utils.readSharedSetting(this, "cekatautambah", null);

        Log.e("cekatautambah ", statustombol);

        scanButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                scanButton.setText("Scan Barcode Lagi");
                if (camerapreview.getVisibility() == View.GONE){
                    camerapreview.setVisibility(View.VISIBLE);
                } else if (camerapreview.getVisibility() == View.VISIBLE) {
//                    camerapreview.setVisibility(View.GONE);
                }
                if (barcodeScanned) {
                    barcodeScanned = false;
                    mCamera.setPreviewCallback(previewCb);
                    mCamera.startPreview();
                    previewing = true;
                    mCamera.autoFocus(autoFocusCB);
                }
            }
        });

        cariKodebarang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txKode = textKode.getText().toString().trim();
                textKode.setText("");
                if (statustombol.equals("cek")) {
                    Log.e("barcode >> ", txKode);
                    if (txKode.isEmpty()) {
                        Toast.makeText(BarcodeScanner.this, "Kode barang tidak boleh kosong", Toast.LENGTH_LONG).show();
                    } else if (txKode != null) {
                        kasir = dbHelper.getKasir(txKode);
                        Log.e("hasil kasir >> ", kasir + "");

                        if (kasir.getHargaProduk() != 0) {
                            alretsukses();
                        } else if (kasir.getHargaProduk() == 0) {
                            alretgagal();
                        }
                    }
                } else if (statustombol.equals("tambahbelanja")) {
                    Log.e("barcode >> ", txKode);
                    if (txKode.isEmpty()) {
                        Toast.makeText(BarcodeScanner.this, "Barcode tidak boleh kosong.", Toast.LENGTH_LONG).show();
                    } else if (txKode != null) {
                        kasir = dbHelper.getKasir(txKode);
                        Log.e("hasil kasir >> ", kasir + "");

                        if (kasir.getHargaProduk() == 0) {
                            alretgagal();
                        } else {
                            showDialogFloat();
                        }
                    }
                }
            }
        });
    }

    @Override
    public void afterTextChanged(Editable arg0) {

    }

    @Override
    public void beforeTextChanged(CharSequence arg0,
                                  int arg1, int arg2,
                                  int arg3) {
        lv.setVisibility(View.GONE);
//        camerapreview.setVisibility(View.VISIBLE);
    }

    @Override
    public void onTextChanged(CharSequence s, int arg1,
                              int arg2, int arg3) {
        String kolom = textKode.getText().toString().trim();
        if (kolom.equals("")) {
            lv.setVisibility(View.GONE);
//            camerapreview.setVisibility(View.VISIBLE);
        } else {
            lv.setVisibility(View.VISIBLE);
            adapter.getFilter().filter(s.toString());
            Log.e("adapterwathcer ", adapter+"");
        }
    }

    @Override
    public void onItemClick(AdapterView<?> arg0,
                            View arg1, int position,
                            long id) {
        //TODO Auto-generated method stub
        textKode.setText(adapter.getItem(position).getIdBarcode());
        lv.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        kembali(null);
    }

    public void kembali(View view) {
        intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void back() {
//        cart = dbHelper.getCart(kasir.getIdBarcode());
//        Log.e("cart scan ", cart + "");
//        if (cart.getJumlahProduk() == 0) {
//            a = 1;
//        } else if (cart.getJumlahProduk() != 0) {
//            a = cart.getJumlahProduk() + 1;
//        }
//        cart = new Cart(kasir.getIdBarcode(), kasir.getNamaProduk(), kasir.getHargaProduk(), a); //jumlahproduk a
//        dbHelper.addCart(cart);
        new AlertDialog.Builder(BarcodeScanner.this).setTitle(getResources().getString(R.string.app_name))
                .setCancelable(false)
                .setMessage("Barang berhasil ditambahkan ke keranjang. dengan jumlah " + b)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
        Log.e("tambah cart >> ", cart + "");

    }

    private void alretgagal() {
        new AlertDialog.Builder(BarcodeScanner.this).setTitle(getResources().getString(R.string.app_name))
                .setCancelable(false)
                .setMessage("Maaf barang " + kasir.getNamaProduk() + " (" + kasir.getIdBarcode() + "), Rp " + kasir.getHargaProduk() + "")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
    }

    private void alretsukses() {
        new AlertDialog.Builder(BarcodeScanner.this).setTitle(getResources().getString(R.string.app_name))
                .setCancelable(false)
                .setMessage("Harga " + kasir.getNamaProduk() + " (" + kasir.getIdBarcode() + ") adalah .. Rp " + kasir.getHargaProduk() + "")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
    }

    private void showDialogFloat() {
        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(BarcodeScanner.this);
        View promptView = layoutInflater.inflate(R.layout.activity_dialog_qty, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(BarcodeScanner.this);
        alertDialogBuilder.setView(promptView);
        quantity = (EditText) promptView.findViewById(R.id.quantity);
        final TextInputLayout Jlayout = (TextInputLayout) promptView.findViewById(R.id.Jlayout);

        alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        }).setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        final AlertDialog alert = alertDialogBuilder.create();

        alert.show();

        cart = dbHelper.getCart(kasir.getIdBarcode());
        Log.e("cart scan ", cart + "");
        if (cart.getJumlahProduk() == 0) {
            a = 1;
        } else if (cart.getJumlahProduk() != 0) {
            a = cart.getJumlahProduk() + 1;
        }

        quantity.setText(a + "");

        Button nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
        nbutton.setTextColor(Color.BLUE);
        nbutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                alert.dismiss();
            }
        });
        Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
        pbutton.setTextColor(Color.BLUE);
        pbutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String qty = quantity.getText().toString().trim();

                if (qty.isEmpty()) {
                    Jlayout.setError("Silahkan isi quantity. ");
                    quantity.requestFocus();
                } else {
                    try {
                        b = Integer.parseInt(qty);

                    } catch (NumberFormatException e) {
                        b = 0;
                    }
                    cart = new Cart(kasir.getIdBarcode(), kasir.getNamaProduk(), kasir.getHargaProduk(), b); //jumlahproduk a
                    dbHelper.addCart(cart);
                    Log.e("quantity str ", quantity + " , " + (b + ""));

                    alert.dismiss();
                    back();
                }
            }
        });

        if (quantity.requestFocus()) {
            BarcodeScanner.this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            releaseCamera();
        }
        return super.onKeyDown(keyCode, event);
    }


    /**
     * A safe way to get an instance of the Camera object.
     */
    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open();
        } catch (Exception e) {
        }
        return c;
    }

    private void releaseCamera() {
        if (mCamera != null) {
            previewing = false;
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }
    }

    private Runnable doAutoFocus = new Runnable() {
        public void run() {
            if (previewing)
                mCamera.autoFocus(autoFocusCB);
        }
    };

    Camera.PreviewCallback previewCb = new Camera.PreviewCallback() {
        public void onPreviewFrame(byte[] data, Camera camera) {
            Camera.Parameters parameters = camera.getParameters();
            Camera.Size size = parameters.getPreviewSize();

            Image barcode = new Image(size.width, size.height, "Y800");
            barcode.setData(data);

            int result = scanner.scanImage(barcode);

            if (result != 0) {
                previewing = false;
                mCamera.setPreviewCallback(null);
                mCamera.stopPreview();

                SymbolSet syms = scanner.getResults();
                for (Symbol sym : syms) {

                    Log.i("<<<<<<Asset Code>>>>> ",
                            "<<<<Bar Code>>> " + sym.getData());
                    scanResult = sym.getData().trim();
                    Log.e("barcode >> ", scanResult);
                    kasir = dbHelper.getKasir(scanResult);
                    Log.e("hasil kasir >> ", String.valueOf(kasir) + " " + scanResult);


                    if (statustombol.equals("cek")) {
                        if (kasir.getHargaProduk() != 0) {
                            alretsukses();
                        } else if (kasir.getHargaProduk() == 0) {
                            alretgagal();
                        }
                    } else if (statustombol.equals("tambahbelanja")) {
                        if (kasir.getHargaProduk() == 0) {
                            alretgagal();
                        } else {

                            showDialogFloat();

                        }

                    }

                    barcodeScanned = true;
                    break;
                }
            }
        }
    };

    // Mimic continuous auto-focusing
    Camera.AutoFocusCallback autoFocusCB = new Camera.AutoFocusCallback() {
        public void onAutoFocus(boolean success, Camera camera) {
            autoFocusHandler.postDelayed(doAutoFocus, 1000);
        }
    };
}
