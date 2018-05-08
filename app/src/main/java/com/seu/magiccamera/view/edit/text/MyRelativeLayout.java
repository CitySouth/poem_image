package com.seu.magiccamera.view.edit.text;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.seu.magiccamera.R;

/**
 * Created by nan on 2017/5/27.
 */

public class MyRelativeLayout extends RelativeLayout {

    private TextView mytextview;
    private static LinearLayout textCustomLayout;

    private float startx;// down事件发生时，手指相对于view左上角x轴的距离
    private float starty;// down事件发生时，手指相对于view左上角y轴的距离
    private float endx; // move事件发生时，手指相对于view左上角x轴的距离
    private float endy; // move事件发生时，手指相对于view左上角y轴的距离
    private float left; // DragTV左边缘相对于父控件的距离
    private float top; // DragTV上边缘相对于父控件的距离
    private int right; // DragTV右边缘相对于父控件的距离
    private int bottom; // DragTV底边缘相对于父控件的距离
    private float hor; // 触摸情况下，手指在x轴方向移动的距离
    private float ver; // 触摸情况下，手指在y轴方向移动的距离
    private float mfX, mfY, msX, msY;
    private boolean isMove = true;
    private float mAngle;
    private int ptrID1 = INVALID_POINTER_ID, ptrID2 = INVALID_POINTER_ID;
    private float oldDist = 0;
    private float textSize = 0;
    private float scale;
    private static final int INVALID_POINTER_ID = -1;
    private MotionEvent mEvent;
    private boolean onefingure;

    public MyRelativeLayout(Context context) {
        super(context);

    }

    public MyRelativeLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MyRelativeLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public textClick getTextClick() {
        return mytextClick;
    }
    public void setTextClick(textClick mytextClick) {
        this.mytextClick = mytextClick;
    }
    private textClick mytextClick;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mEvent = event;
        mytextview = (TextView) findViewById(R.id.poemtext);

        mytextview.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                mytextview = (TextView) v;
                if (textSize == 0) {
                    textSize = mytextview.getTextSize();
                }
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        onefingure = true;
                        startx = event.getX();
                        starty = event.getY();
                        Log.d("HHHH", "onTouch: ACTION_DOWM " + startx);
                        ptrID1 = event.getPointerId(event.getActionIndex());
                        break;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        isMove = false;
                        onefingure = false;

                        ptrID2 = event.getPointerId(event.getActionIndex());

                        msX = mEvent.getX(event.findPointerIndex(ptrID2));
//                        Log.d("HHHH", "onTouch: SECOND_POINTER_DOWN "+msX);
                        msY = mEvent.getY(event.findPointerIndex(ptrID2));
                        mfX = mEvent.getX(event.findPointerIndex(ptrID1));
//                        Log.d("HHHH", "onTouch: FIRST_POINTER_DOWN "+mfX);
                        mfY = mEvent.getY(event.findPointerIndex(ptrID1));
                        oldDist = spacing(event);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        Log.d("HHHH", "onTouch: ACTION_MOVE ");
                        left = mytextview.getX();
                        top = mytextview.getY();
                        if (isMove) {
                            Log.d("HHHH", "onTouch: scaleing ");
                            endx = event.getX();
                            endy = event.getY();
                            hor = (endx - startx);
                            ver = (endy - starty);
//                            textView.layout(left + hor, top + ver, right + hor, bottom + ver);

                            mytextview.setX(left + hor);
                            mytextview.setY(top + ver);

                        }
                        else  {

                            //处理旋转模块
                            float nfX, nfY, nsX, nsY, test;
                            test = event.getX(event.findPointerIndex(ptrID1));

                            nsX = mEvent.getX(event.findPointerIndex(ptrID2));
                            nsY = mEvent.getY(event.findPointerIndex(ptrID2));
                            nfX = mEvent.getX(event.findPointerIndex(ptrID1));
                            nfY = mEvent.getY(event.findPointerIndex(ptrID1));
                            mAngle = angleBetweenLines(mfX, mfY, msX, msY, nfX, nfY, nsX, nsY);
                            Log.d("HHHH", "onTouch: " + mytextview.getLeft());
                            mytextview.setRotation(mAngle);
//                                mfY = nfY;
//                                mfX = nfX;
//                                msY = nsY;
//                                mfX = nsX;
                            //缩放
                            float newDist = spacing(event);
                            if (newDist > oldDist + 1) {
                                zoom(newDist / oldDist);
                                oldDist = newDist;
                            }
                            if (newDist < oldDist - 1) {
                                zoom(newDist / oldDist);
                                oldDist = newDist;
                            }

                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        Log.d("HHHH", "onTouch: ACTION_UP");
                        if (onefingure) {
                            Log.d("HHH", "onTouch: oneFigure");
                            mytextClick.onTextClick();
                        }
                        break;
                    case MotionEvent.ACTION_POINTER_UP:
                        Log.d("HHHH", "onTouch: ACTION_POINTER_UP");
                        isMove = true;
                        break;
                }
                return true;
            }
        });
        return true;
    }
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }
    private void zoom(float f) {
        mytextview.setTextSize(textSize *= f);
    }

    /**
     * 计算刚开始触摸的两个点构成的直线和滑动过程中两个点构成直线的角度
     *
     * @param fX  初始点一号x坐标
     * @param fY  初始点一号y坐标
     * @param sX  初始点二号x坐标
     * @param sY  初始点二号y坐标
     * @param nfX 终点一号x坐标
     * @param nfY 终点一号y坐标
     * @param nsX 终点二号x坐标
     * @param nsY 终点二号y坐标
     * @return 构成的角度值
     */
    private float angleBetweenLines(float fX, float fY, float sX, float sY, float nfX, float nfY, float nsX, float nsY) {
        float angle1 = (float) Math.atan2((fY - sY), (fX - sX));
        float angle2 = (float) Math.atan2((nfY - nsY), (nfX - nsX));

        float angle = ((float) Math.toDegrees(angle1 - angle2)) % 360;
        if (angle < -180.f) angle += 360.0f;
        if (angle > 180.f) angle -= 360.0f;
        return -angle;
    }
//    private void showTextCustom(){

//        ObjectAnimator animator = ObjectAnimator.ofFloat(textCustomLayout, "translationY", textCustomLayout.getHeight(), 0);
//        animator.setDuration(200);
//        animator.addListener(new Animator.AnimatorListener() {
//
//            @Override
//            public void onAnimationStart(Animator animation) {
////                findViewById(R.id.btn_camera_shutter).setClickable(false);
//                findViewById(R.id.layout_filter).setVisibility(View.INVISIBLE);

//                textCustomLayout.setVisibility(View.VISIBLE);
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
    public interface textClick {
        void onTextClick();
    }
}

