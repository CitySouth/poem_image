package com.seu.magiccamera.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.seu.magiccamera.R;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.model.AspectRatio;

import java.io.File;

/**
 * Created by luoyin on 2017/6/5.
 */

public class CropActivity extends Activity {

    private final int GET_DATA = 2;
    private static final String SAMPLE_CROPPED_IMAGE_NAME = "CropImage";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String path = intent.getStringExtra("path");
        File img= new File(path);
        startCropActivity(getImageContentUri(CropActivity.this,img));

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == UCrop.REQUEST_CROP){
            handleCropResult(data);

        }
    }

    private void handleCropResult(@NonNull Intent result) {
        final Uri resultUri = UCrop.getOutput(result);
        if (resultUri != null) {
            Intent photo1 = new Intent(this, ProcessActivity.class);
            photo1.putExtra("path", resultUri.toString());
            startActivityForResult(photo1, GET_DATA);
        } else {
            Toast.makeText(CropActivity.this, R.string.toast_cannot_retrieve_cropped_image, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Gets the content:// URI from the given corresponding path to a file
     *
     * @param context
     * @param imageFile
     * @return content Uri
     */
    public static Uri getImageContentUri(Context context, java.io.File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Images.Media._ID }, MediaStore.Images.Media.DATA + "=? ",
                new String[] { filePath }, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }

    private void startCropActivity(@NonNull Uri uri) {
        String destinationFileName = SAMPLE_CROPPED_IMAGE_NAME;
        destinationFileName += ".jpg";
        UCrop uCrop = UCrop.of(uri, Uri.fromFile(new File(getCacheDir(), destinationFileName)));
//        uCrop = basisConfig(uCrop);
        uCrop = advancedConfig(uCrop);
        uCrop.start(CropActivity.this);
    }

    /**
     * Sometimes you want to adjust more options, it's done via {@link com.yalantis.ucrop.UCrop.Options} class.
     *
     * @param uCrop - ucrop builder instance
     * @return - ucrop builder instance
     */
    private UCrop advancedConfig(@NonNull UCrop uCrop) {
        UCrop.Options options = new UCrop.Options();
//        options.setCompressionQuality(mSeekBarQuality.getProgress());
//
//        options.setHideBottomControls(mCheckBoxHideBottomControls.isChecked());
//        options.setFreeStyleCropEnabled(mCheckBoxFreeStyleCrop.isChecked());

        /*
        If you want to configure how gestures work for all UCropActivity tabs

        options.setAllowedGestures(UCropActivity.SCALE, UCropActivity.ROTATE, UCropActivity.ALL);
        * */

        /*
        This sets max size for bitmap that will be decoded from source Uri.
        More size - more memory allocation, default implementation uses screen diagonal.

        options.setMaxBitmapSize(640);
        * */

//        Tune everything (ﾉ◕ヮ◕)ﾉ*:･ﾟ✧

//        options.setMaxScaleMultiplier(5);
//        options.setImageToCropBoundsAnimDuration(666);
//        options.setDimmedLayerColor(Color.CYAN);
//        options.setCircleDimmedLayer(true);
//        options.setShowCropFrame(false);
//        options.setCropGridStrokeWidth(20);
//        options.setCropGridColor(Color.GREEN);
//        options.setCropGridColumnCount(2);
//        options.setCropGridRowCount(1);
//        options.setToolbarCropDrawable(R.drawable.your_crop_icon);
//        options.setToolbarCancelDrawable(R.drawable.your_cancel_icon);

        // Color palette
//        options.setToolbarColor(ContextCompat.getColor(this, R.color.your_color_res));
//        options.setStatusBarColor(ContextCompat.getColor(this, R.color.your_color_res));
//        options.setActiveWidgetColor(ContextCompat.getColor(this, R.color.your_color_res));
//        options.setToolbarWidgetColor(ContextCompat.getColor(this, R.color.your_color_res));
//        options.setRootViewBackgroundColor(ContextCompat.getColor(this, R.color.your_color_res));

        // Aspect ratio options
        options.setAspectRatioOptions(1,
                new AspectRatio("4:3", 4, 3),
//            new AspectRatio("MUCH", 3, 4),
//                new AspectRatio("RATIO", CropImageView.DEFAULT_ASPECT_RATIO, CropImageView.DEFAULT_ASPECT_RATIO),
                new AspectRatio("16:9", 16, 9),
                new AspectRatio("1:1", 1, 1));



        return uCrop.withOptions(options);
    }

}
