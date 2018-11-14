package syde.co.smartregister;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import net.sourceforge.zbar.Config;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;

import java.util.List;

/**
 * Created by erdearik on 12/19/15.
 */
public class TambahProduk extends AppCompatActivity {

    private Camera mCamera;
    private CameraPreview mPreview;
    private Handler autoFocusHandler;
    private String scanResult, txBarcode, txName, txHarga, statusback;

    private Button tambahProduk, daftarProduk;
    private ImageScanner scanner;
    private EditText textBarcode, textNama, textHarga;

    private List<Kasir> listKasir;
    private List<Cart> listCart;
    private DatabaseHandler dbHandler;
    private ArrayAdapter<Kasir> adapter;
    private Intent intent;
    private Kasir kasir;

    private boolean barcodeScanned = false;
    private boolean previewing = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tambah_produk);

        tambahProduk = (Button) findViewById(R.id.btnTambahproduk);
        daftarProduk = (Button) findViewById(R.id.btnDaftarProduk);
        textBarcode = (EditText) findViewById(R.id.textBarcode);
        textNama = (EditText) findViewById(R.id.textNama);
        textHarga = (EditText) findViewById(R.id.textHarga);

        dbHandler = new DatabaseHandler(this);
        dbHandler.getWritableDatabase();

        initControls();

//        statusback = Utils.readSharedSetting(this, "backback", null);
//        Log.e("stat back ", statusback);
//        if (statusback.equals("1")){
//            onBackPressed();
//        } else if (statusback.equals("2")) {
//
//        }
    }

//    @Override
//    public void onResume(){
//        super.onResume();
//        statusback = Utils.readSharedSetting(this, "backback", null);
//        Log.e("stat back ", statusback);
//        if (statusback.equals("1")){
//            onBackPressed();
//        } else if (statusback.equals("2")) {
//
//        }
//    }

    private void initControls() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        autoFocusHandler = new Handler();
        mCamera = getCameraInstance();

        // Instance barcode scanner
        scanner = new ImageScanner();
        scanner.setConfig(0, Config.X_DENSITY, 3);
        scanner.setConfig(0, Config.Y_DENSITY, 3);

        mPreview = new CameraPreview(TambahProduk.this, mCamera, previewCb,
                autoFocusCB);
        FrameLayout preview = (FrameLayout) findViewById(R.id.cameraPreview);
        preview.addView(mPreview);

        daftarProduk.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                intent = new Intent(TambahProduk.this, DaftarProduk.class);
                startActivity(intent);
            }
        });


        tambahProduk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (barcodeScanned) {
                    barcodeScanned = false;
                    mCamera.setPreviewCallback(previewCb);
                    mCamera.startPreview();
                    previewing = true;
                    mCamera.autoFocus(autoFocusCB);
                }
                txBarcode = textBarcode.getText().toString().trim();
                txName = textNama.getText().toString().trim();
                txHarga = textHarga.getText().toString().trim();
                if (txBarcode.isEmpty() || txName.isEmpty() || txHarga.isEmpty()) {
                    Toast.makeText(TambahProduk.this, "Silahkan isi semua data.", Toast.LENGTH_LONG).show();
                } else {
                    kasir = dbHandler.getKasir(txBarcode);
                    Log.e("kasir ", kasir+"");
                    if (kasir.getHargaProduk() == 0) {
                        kasir = new Kasir(txBarcode, txName, Integer.parseInt(txHarga));
                        dbHandler.addKasir(kasir);
                        Log.e("tambah kasir >> ", kasir + "");

                        new AlertDialog.Builder(TambahProduk.this).setTitle(getResources().getString(R.string.app_name))
                                .setCancelable(false)
                                .setMessage("Sukses menambah " + txBarcode + "," + txName + "," + txHarga)
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        textBarcode.setText("");
                                        textNama.setText("");
                                        textHarga.setText("");
                                    }
                                }).show();
                    } else {
                        new AlertDialog.Builder(TambahProduk.this).setTitle(getResources().getString(R.string.app_name))
                                .setCancelable(false)
                                .setMessage("Barang sudah terdaftar.")
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                }).show();
                    }
                }
            }
        });
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

                    Log.e("<<<<<<Asset Code>>>>> ",
                            "<<<<Bar Code>>> " + sym.getData());
                    scanResult = sym.getData().trim();

                    textBarcode.setText(scanResult);

//                    sync();

                  /*  Toast.makeText(BarcodeScanner.this, scanResult,
                            Toast.LENGTH_SHORT).show();*/

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

    @Override
    public void onBackPressed() {
        back(null);
    }

    public void back(View view) {
        intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}
