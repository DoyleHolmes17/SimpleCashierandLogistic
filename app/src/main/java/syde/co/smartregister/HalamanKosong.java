package syde.co.smartregister;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by imac on 8/3/17.
 */

public class HalamanKosong extends AppCompatActivity {
    private Intent intent;
    private String halstat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.halaman_kosong);

        halstat = Utils.readSharedSetting(this, "statHalkosong", null);

        if (halstat.equals("delkasir")) {
            intent = new Intent(this, DaftarProduk.class);
            finish();
            startActivity(intent);
        } else if (halstat.equals("delcart")) {
            intent = new Intent(this, MainActivity.class);
            finish();
            startActivity(intent);
        }
    }
}
