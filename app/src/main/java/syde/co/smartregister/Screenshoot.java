package syde.co.smartregister;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

/**
 * Created by Syde.co on 04/12/2015.
 */
public class Screenshoot {
    public static void takeScreenshot(Activity activity){
        Bitmap bitmap = screenshot(activity);
        saveBitmap(bitmap, activity);
    }

    public static Bitmap screenshot(Activity activity) {
        View rootView = activity.findViewById(android.R.id.content).getRootView();
        rootView.setDrawingCacheEnabled(true);
        return rootView.getDrawingCache();
    }

    public static void saveBitmap(Bitmap bitmap, Context context) {
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        if(!path.exists()){
            path.mkdir();
        }
        File imagePath = new File(path, File.separator+Dates.toString(new Date(), Dates.DATETIME_LOCAL_FORMAT)+".png");//now gallery can see it

        FileOutputStream fos;
        try {
            ContentValues values = new ContentValues();

            values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            values.put(MediaStore.MediaColumns.DATA, String.valueOf(imagePath));

            context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            fos = new FileOutputStream(imagePath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();

            displayResult(imagePath.getAbsolutePath(), context); // here you display your result after saving
        } catch (FileNotFoundException e) {
            //Log.e("GREC", e.getMessage(), e);
        } catch (IOException e) {
            //Log.e("GREC", e.getMessage(), e);
        }
    }

    public static void displayResult(String imagePath, Context context){
        LinearLayout linearLayout= new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        linearLayout.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        //ImageView Setup
        ImageView imageView = new ImageView(context);//you need control the context of Imageview

        //Diaglog setup
        final Dialog dialog = new Dialog(context);//you need control the context of this dialog
        dialog.setContentView(imageView);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        //Display result
        imageView.setImageBitmap(BitmapFactory.decodeFile(imagePath));
        dialog.show();
    }
}
