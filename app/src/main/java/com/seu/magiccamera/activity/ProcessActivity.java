package com.seu.magiccamera.activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.hankcs.hanlp.dictionary.CoreSynonymDictionary;
import com.seu.magiccamera.activity.CameraActivity;
import com.seu.magiccamera.R;
import com.seu.magiccamera.adapter.App;
import com.seu.magiccamera.adapter.FilterAdapter;
import com.seu.magiccamera.adapter.PoemAdapter;
import com.seu.magiccamera.view.edit.text.ColorTagImageView;
import com.seu.magiccamera.view.edit.text.MyRelativeLayout;
import com.seu.magicfilter.MagicEngine;
import com.seu.magicfilter.filter.advanced.MagicImageAdjustFilter;
import com.seu.magicfilter.filter.helper.MagicFilterType;
import com.seu.magicfilter.widget.MagicCameraView;
import com.seu.magicfilter.widget.MagicImageView;
import com.squareup.picasso.Picasso;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.model.AspectRatio;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import clarifai2.api.ClarifaiBuilder;
import clarifai2.api.ClarifaiClient;
import clarifai2.api.ClarifaiResponse;
import clarifai2.dto.input.ClarifaiInput;
import clarifai2.dto.input.image.ClarifaiImage;
import clarifai2.dto.model.ConceptModel;
import clarifai2.dto.model.output.ClarifaiOutput;
import clarifai2.dto.prediction.Concept;

import static android.R.attr.mode;
import static android.R.attr.path;
import static android.R.attr.width;


/**
 * Created by luoyin on 2017/5/7.
 */

public class ProcessActivity extends Activity implements PoemAdapter.ListItemClickListener,MyRelativeLayout.textClick {
    //    private ImageView imageView;
    private Bitmap bmp;                          //载入图片
    private RelativeLayout Imagelayout;
    private LinearLayout mFilterLayout;
    private RecyclerView mFilterListView;
    private LinearLayout mPoemLayout;
    private RecyclerView mPoemListView;
    private FilterAdapter mAdapter;
    private MagicEngine magicEngine;
    private File img;
    private TextView poemtext;
    private JSONObject testJson;
    private ArrayList<String> poem_list = new ArrayList<>();
    private static final String SAMPLE_CROPPED_IMAGE_NAME = "CropImage";
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

    //文字修改
    private LinearLayout textCustomLayout;
    private LinearLayout mTextFontLayout;
    private LinearLayout mTextFontMoreLayout;
    private ImageView textFontImageView;
    private ImageView textColorImageView;
    private ImageView textFontMoreImageView;
    private ImageView textDirectIV;
    private ImageView textBorderIV;
    private ImageView textTiltIV;

    @NonNull private final PoemAdapter adapter = new PoemAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process);
        Intent intent = getIntent();
        String path = intent.getStringExtra("path");
        Uri photouri=Uri.parse(path);
        img=new File(photouri.getPath());



//        imageShow = (ImageView) findViewById(R.id.imageView1);
        Imagelayout = (RelativeLayout) findViewById(R.id.Content_Layout);
//        imageView = (ImageView) findViewById(R.id.imageView1);
        findViewById(R.id.btn_camera_filter).setOnClickListener(btn_listener);

        mFilterLayout = (LinearLayout)findViewById(R.id.layout_filter);
        mFilterListView = (RecyclerView) findViewById(R.id.filter_listView);//滤镜菜单
        mPoemLayout=(LinearLayout) findViewById(R.id.resultsList);
        mPoemListView = (RecyclerView) findViewById(R.id.poem_listView);//古诗菜单

        findViewById(R.id.btn_camera_filter).setOnClickListener(btn_listener);
        findViewById(R.id.btn_camera_closefilter).setOnClickListener(btn_listener);
        findViewById(R.id.btn_camera_beauty).setOnClickListener(btn_listener);
        findViewById(R.id.btn_closepoems).setOnClickListener(btn_listener);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mFilterListView.setLayoutManager(linearLayoutManager);

        mAdapter = new FilterAdapter(this, types);
        mFilterListView.setAdapter(mAdapter);
        mAdapter.setOnFilterChangeListener(onFilterChangeListener);

        MagicImageView imageView = (MagicImageView)findViewById(R.id.imageView1);

        MagicEngine.Builder builder = new MagicEngine.Builder();
        magicEngine = builder
                .build((MagicImageView)findViewById(R.id.imageView1));



        Point screenSize = new Point();
        getWindowManager().getDefaultDisplay().getSize(screenSize);
        android.view.ViewGroup.LayoutParams pp = Imagelayout.getLayoutParams();
        pp.width=screenSize.x;
        pp.height=screenSize.x * 5 / 4;
        Imagelayout.setLayoutParams(pp);

        bmp = getBitmapFromUri(photouri);
        android.view.ViewGroup.LayoutParams pp1 = imageView.getLayoutParams();
        int bmpwidth=bmp.getWidth();
        int bmpheight=bmp.getHeight();
        if(bmpwidth*2/3<=pp.width/2){
            pp1.width=bmpwidth*3/2;
            pp1.height=bmpheight*3/2;
        }
        else{
            pp1.width=bmpwidth;
            pp1.height=bmpheight;
        }
        imageView.setLayoutParams(pp1);
        System.out.println(pp1.width);
        System.out.println(pp1.height);
        imageView.setImageBitmap(bmp); //显示照片



        //按钮的监听
        findViewById(R.id.btn_camera_filter).setOnClickListener(btn_listener);
        findViewById(R.id.btn_camera_filter).setOnClickListener(btn_listener);
        findViewById(R.id.btn_camera_closefilter).setOnClickListener(btn_listener);
        findViewById(R.id.btn_camera_beauty).setOnClickListener(btn_listener);
        findViewById(R.id.btn_camera_save).setOnClickListener(btn_listener);  //保存
        findViewById(R.id.btn_closepoems).setOnClickListener(btn_listener);
        findViewById(R.id.btn_text_color_close).setOnClickListener(btn_listener);

        //诗词文字
        poemtext=(TextView) findViewById(R.id.poemtext1);
        load_json();
        initTextCustomUI();
        onImagePicked(img);

        //文字修改
        textCustomLayout = (LinearLayout) findViewById(R.id.textModify);
        final int[] colors = new int[1];
        colors[0] = Color.BLACK;
        ColorTagImageView colorTagImageView = (ColorTagImageView) findViewById(R.id.color_tag);
        colorTagImageView.setListener(new ColorTagImageView.OnColorTagChanges() {
            @Override
            public void onColorChange(int color) {
                poemtext.setTextColor(color);
                colors[0] = color;
            }
        });
        //文字修改的监听
        textFontImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("1");
                mTextFontLayout.setVisibility(View.VISIBLE);
                mTextFontMoreLayout.setVisibility(View.INVISIBLE);

            }
        });
        textColorImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("2");
                mTextFontLayout.setVisibility(View.INVISIBLE);
                mTextFontMoreLayout.setVisibility(View.INVISIBLE);
            }
        });
        textFontMoreImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("3");
                mTextFontMoreLayout.setVisibility(View.VISIBLE);
                mTextFontLayout.setVisibility(View.INVISIBLE);
            }
        });

        textFontMore();



    }

    private void initTextCustomUI() {

        mPoemLayout=(LinearLayout) findViewById(R.id.resultsList);
        mPoemListView = (RecyclerView) findViewById(R.id.poem_listView);//古诗菜单
        mPoemListView.setLayoutManager(new LinearLayoutManager(this));
        mPoemListView.setAdapter(adapter);

        textFontImageView = (ImageView) findViewById(R.id.text_font_btn);
        textFontMoreImageView = (ImageView) findViewById(R.id.text_font_more_btn);
        textColorImageView = (ImageView) findViewById(R.id.text_color_btn);

        mTextFontLayout = (LinearLayout) findViewById(R.id.text_font_layout);
        mTextFontMoreLayout = (LinearLayout) findViewById(R.id.text_font_more_layout);

    }


    /* uri转化为bitmap */
    private Bitmap getBitmapFromUri(Uri uri) {
        try {
// 读取uri所在的图片
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(
                    this.getContentResolver(), uri);
            return bitmap;
        } catch (Exception e) {
//            Log.e("[Android]", e.getMessage());
//            Log.e("[Android]", "目录为：" + uri);
            e.printStackTrace();
            return null;
        }
    }

    //下载诗词
    private void load_json() {
        try {
            InputStreamReader isr = new InputStreamReader(getAssets().open("poem.json"), "UTF-8");
            BufferedReader br = new BufferedReader(isr);
            String line;
            StringBuilder builder = new StringBuilder();
            while ((line = br.readLine()) != null) {
                builder.append(line);
            }
            br.close();
            isr.close();
            testJson = new JSONObject(builder.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //匹配
    private void match_poem(List<ClarifaiOutput<Concept>> predictions) {
        poem_list = new ArrayList<>();
        try {
            List<Concept> results = predictions.get(0).data();
            //the list of the title of poems
            List title_list = new ArrayList();
            for (Iterator<String> iterator = testJson.keys(); iterator.hasNext(); ) {
                String key = iterator.next();
                title_list.add(key);
            }
            //使用hanlp计算语义距离
            double[] numarray = new double[title_list.size()];
            for (int i = 0; i < results.size(); i++) {
                for (int j = 0; j < title_list.size(); j++) {
                    numarray[j] += CoreSynonymDictionary.similarity(results.get(i).name().toString(), title_list.get(j).toString());
                }
            }
            //返回最符合的诗句title下标
            int[] index = SearchMaxWithIndex(numarray);
            //通过数组返回诗句
            poem_list = GetResuleFromJson(index, title_list);

        } catch(Exception e){
            e.printStackTrace();
        }

    }

    private static int[] SearchMaxWithIndex(double[] arr) {
        int[] pos = new int[arr.length];
        int position = 0;
        int j = 1;
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] > arr[position]) {
                position = i;
                j = 1;
            }
            else if (arr[i] == arr[position])
                pos[j++] = i;
        }
        pos[0] = position;

        if (j < arr.length) pos[j] = -1;
        return pos;
    }

    private ArrayList GetResuleFromJson(int[] pos, List titlelist) {
        ArrayList resultlist = new ArrayList();
        try {
            for (int i = 0; i < pos.length; i++) {
                if (pos[i] == -1) break;
                JSONArray jsonArray = new JSONArray();
                jsonArray = testJson.getJSONArray(titlelist.get(pos[i]).toString());
                for (int j = 0; j < jsonArray.length(); j++) {
                    resultlist.add(jsonArray.getString(j));
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return resultlist;
    }

    //监听
    @Override
    public void onListItemClick(int clickedItemIndex) {
        poemtext.setText(poem_list.get(clickedItemIndex));
    }
    @Override
    public void onTextClick() {

        showTextColor();

    }
    //匹配的线程
    private void onImagePicked(@NonNull final File image) {
        // Now we will upload our image to the Clarifai API
//        setBusy(true);

        // Make sure we don't show a list of old concepts while the image is being uploaded
//        adapter.setData(Collections.<Concept>emptyList());

        new AsyncTask<Void, Void, ClarifaiResponse<List<ClarifaiOutput<Concept>>>>() {
            @Override protected ClarifaiResponse<List<ClarifaiOutput<Concept>>> doInBackground(Void... params) {
                // The default Clarifai model that identifies concepts in images
                final ConceptModel generalModel = App.get().clarifaiClient().getDefaultModels().generalModel();

                // Use this model to predict, with the image that the user just selected as the input
                return generalModel.predict()
                        .withInputs(ClarifaiInput.forImage(ClarifaiImage.of(image)))
                        .executeSync();
            }

            @Override protected void onPostExecute(ClarifaiResponse<List<ClarifaiOutput<Concept>>> response) {

                final List<ClarifaiOutput<Concept>> predictions = response.get();
                runOnUiThread(new Runnable() {
                    public void run() {
                        // your code to update the UI thread here
                        match_poem(predictions);
                        adapter.setData(poem_list,ProcessActivity.this);
                    }
                });
            }


        }.execute();
    }


    private View.OnClickListener btn_listener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btn_camera_beauty:
                    showPoems();
                    break;
                case R.id.btn_camera_filter:
                    showFilters();
                    break;
                case R.id.btn_camera_closefilter:
                    hideFilters();
                    break;
                case R.id.btn_closepoems:
                    hidePoems();
                    break;
                case R.id.btn_text_color_close:
                    hideTextColor();
                    break;
            }
        }
    };
    private void textFontMore(){
        textDirectIV = (ImageView) findViewById(R.id.text_direct_image);
        textBorderIV = (ImageView) findViewById(R.id.text_border_image);
        textTiltIV = (ImageView) findViewById(R.id.text_tile_image);
        poemtext.setTypeface(Typeface.DEFAULT);
        textBorderIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (poemtext.getTypeface().getStyle()) {
                    case 0:
                        poemtext.setTypeface(null, Typeface.BOLD);
                        break;
                    case 1:
                        poemtext.setTypeface(Typeface.DEFAULT);
                        Log.d("HHHH", "onClick: " + poemtext.getTypeface().getStyle());
                        break;
                    case 2:
                        poemtext.setTypeface(null, Typeface.BOLD_ITALIC);
                        break;
                    case 3:
                        poemtext.setTypeface(null, Typeface.ITALIC);
                        break;
                }

            }
        });
        textTiltIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (poemtext.getTypeface().getStyle()) {
                    case 0:
                        poemtext.setTypeface(null, Typeface.ITALIC);
                        break;
                    case 1:
                        poemtext.setTypeface(null, Typeface.BOLD_ITALIC);
                        break;
                    case 2:
                        poemtext.setTypeface(Typeface.DEFAULT);
                        break;
                    case 3:
                        poemtext.setTypeface(null, Typeface.BOLD);
                        break;
                }
            }
        });
    }
    private void showFilters(){
        ObjectAnimator animator = ObjectAnimator.ofFloat(mFilterLayout, "translationY", mFilterLayout.getHeight(), 0);
        animator.setDuration(200);
        animator.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {
//                findViewById(R.id.btn_camera_shutter).setClickable(false);
                mFilterLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }
        });
        animator.start();
    }
    private void hideFilters(){
        ObjectAnimator animator = ObjectAnimator.ofFloat(mFilterLayout, "translationY", 0 ,  mFilterLayout.getHeight());
        animator.setDuration(200);
        animator.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                // TODO Auto-generated method stub
                mFilterLayout.setVisibility(View.INVISIBLE);
//                findViewById(R.id.btn_camera_shutter).setClickable(true);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                // TODO Auto-generated method stub
                mFilterLayout.setVisibility(View.INVISIBLE);
//                findViewById(R.id.btn_camera_shutter).setClickable(true);
            }
        });
        animator.start();
    }
    private void showPoems(){
        ObjectAnimator animator = ObjectAnimator.ofFloat(mPoemLayout, "translationY", mPoemLayout.getHeight(), 0);
        animator.setDuration(200);
        animator.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {
                findViewById(R.id.btn_camera_beauty).setClickable(false);
                findViewById(R.id.btn_camera_filter).setClickable(false);
                mPoemLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }
        });
        animator.start();
    }
    private void hidePoems(){
        ObjectAnimator animator = ObjectAnimator.ofFloat(mPoemLayout, "translationY", 0 , mPoemLayout.getHeight());
        animator.setDuration(200);
        animator.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                // TODO Auto-generated method stub
                mPoemLayout.setVisibility(View.INVISIBLE);
                findViewById(R.id.btn_camera_beauty).setClickable(true);
                findViewById(R.id.btn_camera_filter).setClickable(true);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                // TODO Auto-generated method stub
                mPoemLayout.setVisibility(View.INVISIBLE);
                findViewById(R.id.btn_camera_beauty).setClickable(true);
                findViewById(R.id.btn_camera_filter).setClickable(true);
            }
        });
        animator.start();
    }
    private void showTextColor(){
        ObjectAnimator animator = ObjectAnimator.ofFloat(textCustomLayout, "translationY", textCustomLayout.getHeight(), 0);
        animator.setDuration(200);
        animator.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {
                findViewById(R.id.btn_camera_beauty).setClickable(false);
                findViewById(R.id.btn_camera_filter).setClickable(false);
                textCustomLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }
        });
        animator.start();
    }
    private void hideTextColor(){
        ObjectAnimator animator = ObjectAnimator.ofFloat(textCustomLayout, "translationY", 0 , textCustomLayout.getHeight());
        animator.setDuration(200);
        animator.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                // TODO Auto-generated method stub
                textCustomLayout.setVisibility(View.INVISIBLE);
                findViewById(R.id.btn_camera_beauty).setClickable(true);
                findViewById(R.id.btn_camera_filter).setClickable(true);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                // TODO Auto-generated method stub
                textCustomLayout.setVisibility(View.INVISIBLE);
                findViewById(R.id.btn_camera_beauty).setClickable(true);
                findViewById(R.id.btn_camera_filter).setClickable(true);
            }
        });
        animator.start();
    }

    private FilterAdapter.onFilterChangeListener onFilterChangeListener = new FilterAdapter.onFilterChangeListener(){

        @Override
        public void onFilterChanged(MagicFilterType filterType) {
            magicEngine.setFilter(filterType);
        }
    };




}
