package com.seu.magiccamera.activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.seu.magiccamera.R;
import com.seu.magiccamera.activity.ProcessActivity;
import com.seu.magiccamera.adapter.FilterAdapter;
import com.seu.magicfilter.MagicEngine;
import com.seu.magicfilter.filter.helper.MagicFilterType;
import com.seu.magicfilter.helper.SavePictureTask;
import com.seu.magicfilter.utils.MagicParams;
import com.seu.magicfilter.widget.MagicCameraView;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.model.AspectRatio;
import com.yalantis.ucrop.view.CropImageView;


import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.R.attr.path;


/**
 * Created by why8222 on 2016/3/17.
 */
public class CameraActivity extends Activity{
    private LinearLayout mFilterLayout;
    private RecyclerView mFilterListView;
    private FilterAdapter mAdapter;
    private MagicEngine magicEngine;
    private boolean isRecording = false;
    private final int MODE_PIC = 1;
    private final int MODE_VIDEO = 2;
    private int mode = MODE_PIC;
    private final int GET_DATA = 2;
    private final int TAKE_PHOTO = 3;
    private String path;
    ArrayList<Uri> path_album;
    private static final int REQUEST_CODE_CHOOSE = 23;
    private static final int REQUEST_SELECT_PICTURE = 0x01;
    private static final String SAMPLE_CROPPED_IMAGE_NAME = "CropImage";
    private ImageView btn_shutter;
    private ImageView btn_mode;

    private ObjectAnimator animator;

    private final MagicFilterType[] types = new MagicFilterType[]{
            MagicFilterType.NONE,
            MagicFilterType.FAIRYTALE,
            MagicFilterType.SUNRISE,
            MagicFilterType.SUNSET,
            MagicFilterType.WHITECAT,
            MagicFilterType.BLACKCAT,
            MagicFilterType.SKINWHITEN,
            MagicFilterType.HEALTHY,
            MagicFilterType.SWEETS,
            MagicFilterType.ROMANCE,
            MagicFilterType.SAKURA,
            MagicFilterType.WARM,
            MagicFilterType.ANTIQUE,
            MagicFilterType.NOSTALGIA,
            MagicFilterType.CALM,
            MagicFilterType.LATTE,
            MagicFilterType.TENDER,
            MagicFilterType.COOL,
            MagicFilterType.EMERALD,
            MagicFilterType.EVERGREEN,
            MagicFilterType.CRAYON,
            MagicFilterType.SKETCH,
            MagicFilterType.AMARO,
            MagicFilterType.BRANNAN,
            MagicFilterType.BROOKLYN,
            MagicFilterType.EARLYBIRD,
            MagicFilterType.FREUD,
            MagicFilterType.HEFE,
            MagicFilterType.HUDSON,
            MagicFilterType.INKWELL,
            MagicFilterType.KEVIN,
            MagicFilterType.LOMO,
            MagicFilterType.N1977,
            MagicFilterType.NASHVILLE,
            MagicFilterType.PIXAR,
            MagicFilterType.RISE,
            MagicFilterType.SIERRA,
            MagicFilterType.SUTRO,
            MagicFilterType.TOASTER2,
            MagicFilterType.VALENCIA,
            MagicFilterType.WALDEN,
            MagicFilterType.XPROII
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        MagicEngine.Builder builder = new MagicEngine.Builder();
        magicEngine = builder
                .build((MagicCameraView)findViewById(R.id.glsurfaceview_camera));
        initView();
    }

    private void initView(){
//        mFilterLayout = (LinearLayout)findViewById(R.id.layout_filter);
//        mFilterListView = (RecyclerView) findViewById(R.id.filter_listView);

        btn_shutter = (ImageView)findViewById(R.id.btn_camera_shutter);
//        btn_mode = (ImageView)findViewById(R.id.btn_camera_mode);

//        findViewById(R.id.btn_camera_filter).setOnClickListener(btn_listener);
        findViewById(R.id.btn_camera_closefilter).setOnClickListener(btn_listener);
        findViewById(R.id.btn_camera_shutter).setOnClickListener(btn_listener);
//        findViewById(R.id.btn_camera_switch).setOnClickListener(btn_listener);
//        findViewById(R.id.btn_camera_mode).setOnClickListener(btn_listener);
        findViewById(R.id.btn_camera_beauty).setOnClickListener(btn_listener);

//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
//       linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
//        mFilterListView.setLayoutManager(linearLayoutManager);
//
//        mAdapter = new FilterAdapter(this, types);
//        mFilterListView.setAdapter(mAdapter);
//        mAdapter.setOnFilterChangeListener(onFilterChangeListener);

        animator = ObjectAnimator.ofFloat(btn_shutter,"rotation",0,360);
        animator.setDuration(500);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        Point screenSize = new Point();
        getWindowManager().getDefaultDisplay().getSize(screenSize);
        MagicCameraView cameraView = (MagicCameraView)findViewById(R.id.glsurfaceview_camera);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) cameraView.getLayoutParams();
        params.width = screenSize.x;
        params.height = screenSize.x * 5 / 4;
        cameraView.setLayoutParams(params);
    }

    private FilterAdapter.onFilterChangeListener onFilterChangeListener = new FilterAdapter.onFilterChangeListener(){

        @Override
        public void onFilterChanged(MagicFilterType filterType) {
            magicEngine.setFilter(filterType);
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (grantResults.length != 1 || grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if(mode == MODE_PIC)
                takePhoto();
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SELECT_PICTURE  && resultCode == RESULT_OK) {
//            path_album=Matisse.obtainResult(data);
//            Intent photo1 = new Intent(this, ProcessalbumActivity.class);
//            photo1.putParcelableArrayListExtra("path_album", path_album);
//            startActivityForResult(photo1, GET_DATA);

            final Uri selectedUri = data.getData();
            System.out.println(selectedUri);
            if (selectedUri != null) {
                startCropActivity(data.getData());
            } else {
                Toast.makeText(CameraActivity.this, R.string.toast_cannot_retrieve_selected_image, Toast.LENGTH_SHORT).show();
            }

        }
        else if(requestCode == UCrop.REQUEST_CROP){
            handleCropResult(data);

        }
    }

    private void handleCropResult(@NonNull Intent result) {
        final Uri resultUri = UCrop.getOutput(result);
        if (resultUri != null) {
            Intent photo1 = new Intent(this, ProcessalbumActivity.class);
            photo1.putExtra("path_album", resultUri.toString());
            startActivityForResult(photo1, GET_DATA);
        } else {
            Toast.makeText(CameraActivity.this, R.string.toast_cannot_retrieve_cropped_image, Toast.LENGTH_SHORT).show();
        }
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
        options.setToolbarColor(ContextCompat.getColor(this, R.color.white));
        options.setStatusBarColor(ContextCompat.getColor(this, R.color.white));
        options.setActiveWidgetColor(ContextCompat.getColor(this, R.color.zhihu_primary));
        options.setToolbarWidgetColor(ContextCompat.getColor(this, R.color.black));
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

    private View.OnClickListener btn_listener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btn_camera_shutter:
                    if (PermissionChecker.checkSelfPermission(CameraActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_DENIED) {
                        ActivityCompat.requestPermissions(CameraActivity.this, new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE },
                                v.getId());
                    } else {
                        if(mode == MODE_PIC)
                            takePhoto();
                    }
                    break;
//                case R.id.btn_camera_filter:
//                    showFilters();
//                    break;
//                case R.id.btn_camera_switch:
//                    magicEngine.switchCamera();
//                    break;
                case R.id.btn_camera_beauty:
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    startActivityForResult(Intent.createChooser(intent, getString(R.string.label_select_picture)), REQUEST_SELECT_PICTURE);
                    break;
            }
        }
    };
    private void startCropActivity(@NonNull Uri uri) {
        String destinationFileName = SAMPLE_CROPPED_IMAGE_NAME;
        destinationFileName += ".jpg";
        UCrop uCrop = UCrop.of(uri, Uri.fromFile(new File(getCacheDir(), destinationFileName)));
//        uCrop = basisConfig(uCrop);
        uCrop = advancedConfig(uCrop);

        uCrop.start(CameraActivity.this);
    }

    private void takePhoto(){
        savePicture(getOutputMediaFile(),null);
        Intent photo = new Intent(this, CropActivity.class);
        photo.putExtra("path", path);
        startActivityForResult(photo, TAKE_PHOTO);
    }
    public void savePicture(File file, SavePictureTask.OnPictureSaveListener listener){
        SavePictureTask savePictureTask = new SavePictureTask(file, listener);
        MagicParams.magicBaseView.savePicture(savePictureTask);
        path=file.getPath();
    }

//    private void showFilters(){
//        ObjectAnimator animator = ObjectAnimator.ofFloat(mFilterLayout, "translationY", mFilterLayout.getHeight(), 0);
//        animator.setDuration(200);
//        animator.addListener(new Animator.AnimatorListener() {
//
//            @Override
//            public void onAnimationStart(Animator animation) {
//                findViewById(R.id.btn_camera_shutter).setClickable(false);
//                mFilterLayout.setVisibility(View.VISIBLE);
//            }
//
//            @Override
//            public void onAnimationRepeat(Animator animation) {
//
//            }
//
//            @Override
//            public void onAnimationEnd(Animator animation) {
//
//            }
//
//            @Override
//            public void onAnimationCancel(Animator animation) {
//
//            }
//        });
//        animator.start();
//    }
//
//    private void hideFilters(){
//        ObjectAnimator animator = ObjectAnimator.ofFloat(mFilterLayout, "translationY", 0 ,  mFilterLayout.getHeight());
//        animator.setDuration(200);
//        animator.addListener(new Animator.AnimatorListener() {
//
//            @Override
//            public void onAnimationStart(Animator animation) {
//                // TODO Auto-generated method stub
//            }
//
//            @Override
//            public void onAnimationRepeat(Animator animation) {
//                // TODO Auto-generated method stub
//
//            }
//
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                // TODO Auto-generated method stub
//                mFilterLayout.setVisibility(View.INVISIBLE);
//                findViewById(R.id.btn_camera_shutter).setClickable(true);
//            }
//
//            @Override
//            public void onAnimationCancel(Animator animation) {
//                // TODO Auto-generated method stub
//                mFilterLayout.setVisibility(View.INVISIBLE);
//                findViewById(R.id.btn_camera_shutter).setClickable(true);
//            }
//        });
//        animator.start();
//    }

    public File getOutputMediaFile() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmm", Locale.CHINESE).format(new Date());
        String filename=timeStamp+".jpg";

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), filename);
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

//        File mediaFile = new File(mediaStorageDir.getPath() + File.separator +
//                "IMG_" + timeStamp + ".jpg");

        return mediaStorageDir;
    }
}
