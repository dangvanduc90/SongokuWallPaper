package com.songoku.songokuwallpaperpro;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.songoku.songokuwallpaperpro.Utils.ParseImageHome;
import com.songoku.songokuwallpaperpro.broadcast.ConnectivityReceiver;
import com.songoku.songokuwallpaperpro.broadcast.MyApplication;
import com.songoku.songokuwallpaperpro.object.ObjectImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import static com.songoku.songokuwallpaperpro.Utils.CommonVL.ASSEST_URI;
import static com.songoku.songokuwallpaperpro.Utils.CommonVL.FILE_FOLDE;
import static com.songoku.songokuwallpaperpro.Utils.CommonVL.FOLDE_NAME;
import static com.songoku.songokuwallpaperpro.Utils.CommonVL.JPG;
import static com.songoku.songokuwallpaperpro.Utils.CommonVL.OBJECT_IMAGE;
import static com.songoku.songokuwallpaperpro.Utils.CommonVL.WALL_PAPER_URL;

public class PreviewPictureActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener  {

    private ObjectImage imageObject;
    ImageView ivPreviewPicture;
    private String path;
    private String linkOnline;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_picture);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        imageObject = (ObjectImage) getIntent().getExtras().getSerializable(OBJECT_IMAGE);
        final String imageSource = imageObject.getImgSourc();
        ivPreviewPicture = findViewById(R.id.ivPreviewPicture);

        if (imageObject.isInternet()) {
            new ParseImageHome(this, new ParseImageHome.LoadImage() {
                @Override
                public void loadDataImg(String httpImg) {
                    linkOnline = httpImg;
                    Glide.with(PreviewPictureActivity.this).load(httpImg).into(ivPreviewPicture);
                }
            }).execute(WALL_PAPER_URL + imageObject.getImgSourc());
        } else {
            path = imageSource;
            Glide.with(this).load(ASSEST_URI + imageSource).into(ivPreviewPicture);
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 0, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private void setWallpaperImg(String pathImg, boolean isOnline) {
        try {
            Bitmap bitmap = null;
            if (isOnline) {
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                bitmap = BitmapFactory.decodeFile(pathImg, bmOptions);
            } else {
                bitmap = BitmapFactory.decodeStream(getAssets().open(pathImg));
            }
            Uri uri = getImageUri(PreviewPictureActivity.this, bitmap);
            setBackgroundFromAssetsFolder(PreviewPictureActivity.this, uri);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setBackgroundFromAssetsFolder(Context mContext, Uri uri) {
        Log.d("uri", uri.toString());
        Intent intent = new Intent(Intent.ACTION_ATTACH_DATA);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setDataAndType(uri, "image/jpeg");
        intent.putExtra("mimeType", "image/jpeg");
        mContext.startActivity(Intent.createChooser(intent, "Set as:"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_preview_picture, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.menuShare:
                Bitmap bitmap = null;
                if (imageObject.isInternet()) {
                    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                    bitmap = BitmapFactory.decodeFile(linkOnline, bmOptions);
                } else {
                    try {
                        bitmap = BitmapFactory.decodeStream(getAssets().open(path));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                Uri uri = getImageUri(PreviewPictureActivity.this, bitmap);
                Intent sendIntent = new Intent(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_STREAM, uri);
                sendIntent.setType("image/jpeg");
                startActivity(Intent.createChooser(sendIntent, "Share image"));
                break;
            case R.id.menuSetPictureAs:
                if (!TextUtils.isEmpty(path)) {
                    setWallpaperImg(path, imageObject.isInternet());
                } else {
                    if (checkConnection()) {
                        isDownloadFile();
                    }
                }
                break;
            case R.id.menuAbout:
                startActivity(new Intent(PreviewPictureActivity.this, AboutActivity.class));
                break;
            default:
                break;
        }
        return true;
    }


    private void isDownloadFile() {
        dialog = new ProgressDialog(this); // this = YourActivity
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("Loading. Please wait...");
        dialog.setIndeterminate(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        File mFile = new File(FILE_FOLDE + FOLDE_NAME +
                File.separator + System.currentTimeMillis() + FOLDE_NAME + JPG);
        Ion.with(PreviewPictureActivity.this)
                .load(linkOnline)
                .progressDialog(dialog)
                .write(mFile)
                .setCallback(new FutureCallback<File>() {
                    @Override
                    public void onCompleted(Exception e, File file) {
                        if (e == null) {
                            path = file.getAbsolutePath();
                            setWallpaperImg(path, imageObject.isInternet());
                        } else {
                            Toast.makeText(PreviewPictureActivity.this,
                                    "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                        dialog.dismiss();
                    }
                });
    }

    // Method to manually check connection status
    private boolean checkConnection() {
        return ConnectivityReceiver.isConnected();
    }


    // Showing the status in Snackbar
    private void showSnack(boolean isConnected) {
        String message;
        int color;
        if (isConnected) {
            message = "Good! Connected to Internet";
        } else {
            message = "Sorry! Not connected to internet";
        }

        Toast.makeText(PreviewPictureActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // register connection status listener
        MyApplication.getInstance().setConnectivityListener(PreviewPictureActivity.this);
    }


    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);
    }
}
