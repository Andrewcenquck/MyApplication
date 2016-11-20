package com.example.chenkui.myapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import java.util.ArrayList;
import java.util.List;

/**
 * 弯曲滑竿滑动选择器
 * Created by chenkui on 2016/11/20.
 */
public class IndicatorTabView extends View {
    private String TAG = IndicatorTabView.class.getSimpleName();
    private static final float DEFAULT_SWEEP_ANGLE = 48.0f;
    private float mSweepAngle = DEFAULT_SWEEP_ANGLE;
    private float mStartAngle = (180.0f - mSweepAngle) / 2;
    private List<IndicatorTabItem> mTabItems = new ArrayList<>();
    private int mSelectTabIndex = -1;
    private Paint mTabBackColorPaint;
    private Paint mTabPaint;
    private Paint mTabTtileTextPaint;//滑竿或点击标题

    private Paint mTabWheelPaint;
    private Paint mTabTextPaint;
    private Paint mTabPointerPaint;

    private float mWheelCenterX;
    private float mWheelCenterY;
    private float mWheelRadius;
    private RectF mWheelArcRect;

    private float mPointerAngle;
    private float mPointerRadius;
    private boolean mIsMovingPointer = false;
    private OnTabChangeListener mTabChangeListener = null;

    private List<IndicatorTabRectItem> mTabRectItem = new ArrayList<IndicatorTabRectItem>();
    private Paint textPaint = new Paint();
    private float mMinRectRadius;//文字区域，
    private float mMaxRectRadius;//文字区域，


    public IndicatorTabView(Context context) {
        this(context, null);
    }

    public IndicatorTabView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IndicatorTabView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        mTabBackColorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);//绘制最大的里边图层背景
        mTabBackColorPaint.setStyle(Paint.Style.FILL);
        mTabBackColorPaint.setColor(Color.argb(255, 35, 47, 62));

        mTabPaint = new Paint(Paint.ANTI_ALIAS_FLAG);//绘制里面最小图层背景。
        mTabPaint.setStyle(Paint.Style.FILL);
        mTabPaint.setColor(Color.argb(255, 253,253, 254));

        mTabTtileTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);//绘制里面最小图层背景。
        mTabTtileTextPaint.setStyle(Paint.Style.FILL);
        mTabTtileTextPaint.setColor(Color.WHITE);

        mTabWheelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTabWheelPaint.setStyle(Paint.Style.STROKE);
        mTabWheelPaint.setStrokeWidth(getResources().getDimension(R.dimen.tab_wheel_width));
        mTabWheelPaint.setColor(Color.argb(200, 253, 250, 245));
        mTabWheelPaint.setStrokeCap(Paint.Cap.ROUND);//这个是设置绘制弧是，两端圆滑;

        mTabTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);//绘制文字
        mTabTextPaint.setColor(Color.argb(255, 253, 253, 254));
        mTabTextPaint.setTextSize(getResources().getDimension(R.dimen.tab_text));
        mTabTextPaint.setStrokeWidth(5);

        mTabPointerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);//绘制小点
        mTabPointerPaint.setStyle(Paint.Style.FILL);
        mTabPointerPaint.setColor(Color.argb(255, 255, 255, 255));
        mPointerRadius = getResources().getDimension(R.dimen.tab_pointer_radius);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //绘制深蓝背景。
        canvas.drawCircle(mWheelCenterX, mWheelCenterY - getResources().getDimension(R.dimen.tab_wheel_padding_inner), mWheelRadius*1.2f, mTabBackColorPaint);

        //绘制里面第一条弧
        canvas.drawCircle(mWheelCenterX, mWheelCenterY - getResources().getDimension(R.dimen.tab_wheel_padding_inner), mWheelRadius, mTabPaint);

        canvas.drawArc(mWheelArcRect, mStartAngle, mSweepAngle, false, mTabWheelPaint);//绘制滑杆

        for (int i = 0; i < mTabItems.size(); i++) {
            IndicatorTabItem tempItem = mTabItems.get(i);
            float angle = (tempItem.getStartAngle() + tempItem.getEndAngle()) / 2 - 90.0f;
            canvas.save();//将之前绘制图片保存起来，
            canvas.rotate(angle, mWheelCenterX, mWheelCenterY);
            canvas.drawText(tempItem.getName(), mWheelCenterX - tempItem.getMesureWidth() / 2,
                    getResources().getDimension(R.dimen.tab_wheel_width) + mWheelCenterY + mWheelRadius + getResources().getDimension(R.dimen.tab_wheel_padding_inner),
                    mTabTextPaint);
            /***********************************************************************************************************/
            Log.d(TAG, "-----------onDraw()-----------" + "X===[" + (mWheelCenterX - tempItem.getMesureWidth() / 2) + "]-------Y==={" + (getResources().getDimension(R.dimen.tab_wheel_width) + mWheelCenterY + mWheelRadius + getResources().getDimension(R.dimen.tab_wheel_padding_inner)) + "}");

            float rectWidth = mTabTextPaint.measureText(tempItem.getName());
            Paint.FontMetrics fm = mTabTextPaint.getFontMetrics();

            float offsetAscent = fm.ascent;
            float offsetBottom = fm.bottom;
            float startX = mWheelCenterX - tempItem.getMesureWidth() / 2;
            float startY = getResources().getDimension(R.dimen.tab_wheel_width) + mWheelCenterY + mWheelRadius + getResources().getDimension(R.dimen.tab_wheel_padding_inner);
            Log.d(TAG, "TEXT-offsetAscent=" + offsetAscent);
//            RectF testRect = new RectF(
//                    startX,
//                    (float) (startY + offsetAscent),
//                    (float) (startX + rectWidth),
//                    (float) (startY + offsetBottom)
//            );
//            textPaint.setColor(Color.argb(100, 233, 233, 0));
//            canvas.drawRect(testRect, textPaint);
/*************************************************************************************************************************/
            canvas.restore();//他的作用为，将之前的绘制保存的图片save(),进行合并.

            mMinRectRadius = distance(mWheelCenterX, startY + offsetAscent);//计算
            mMaxRectRadius = distance(mWheelCenterX, startY + offsetBottom);


        }

        float[] pointerPosition = calculatePointerPosition(mPointerAngle);
        canvas.drawCircle(mWheelCenterX + pointerPosition[0], mWheelCenterY + pointerPosition[1], mPointerRadius, mTabPointerPaint);//绘制小球
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        mWheelRadius = (float) (widthSize / (2.0f * Math.sin(Math.toRadians(32))));

        int offset = (int) (heightSize - getResources().getDimension(R.dimen.tab_wheel_padding_bottom));

        mWheelCenterY = offset - (int) Math.sqrt(Math.pow((double) mWheelRadius, 2) - Math.pow((double) (widthSize / 2), 2));

        mWheelCenterX = widthSize / 2.0f;

        mWheelArcRect = new RectF(mWheelCenterX - mWheelRadius, mWheelCenterY - mWheelRadius,
                mWheelCenterX + mWheelRadius, mWheelCenterY + mWheelRadius);

    }

    private float[] calculatePointerPosition(float angle) {
        float x = (float) (mWheelRadius * Math.cos(Math.toRadians(angle)));
        float y = (float) (mWheelRadius * Math.sin(Math.toRadians(angle)));

        return new float[]{x, y};
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX() - mWheelCenterX;
        float y = event.getY() - mWheelCenterY;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                float[] pointerPosition = calculatePointerPosition(mPointerAngle);
                if (x >= (pointerPosition[0] - mPointerRadius * 2)
                        && x <= (pointerPosition[0] + mPointerRadius * 2)
                        && y >= (pointerPosition[1] - mPointerRadius * 2)
                        && y <= (pointerPosition[1] + mPointerRadius * 2)) {
                    mIsMovingPointer = true;
                    return true;
                }
                float pointerLength = distanceRelative(x, y);//计算触摸点距离圆心的坐标：
                //计算文本触摸区域的顶部距离圆心的坐标的距离：
                //计算文本触摸区域的底部距离圆心的坐标的距离；
                //计算触摸点的角度，

                float tempPointerRectFAngle = (float) Math.toDegrees(Math.atan2(y, x));//文本触摸区域的的角度
                if (pointerLength >= mMinRectRadius - mPointerRadius && pointerLength <= mMaxRectRadius + mPointerRadius) {
                    int willSelectedIndex = -1;
                    for (int i = 0; i < mTabRectItem.size(); i++) {
                        IndicatorTabRectItem item = mTabRectItem.get(i);
                        if (tempPointerRectFAngle >= item.getStartAngle() && tempPointerRectFAngle <= item.getEndAngle()) {
                            willSelectedIndex = i;
                            break;
                        }
                    }
                    if (mSelectTabIndex != willSelectedIndex) {
                        if (mTabChangeListener != null) {
                            mTabChangeListener.onTabSelected(this, willSelectedIndex);
                        }
                    }
                    setSelection(willSelectedIndex);
                    invalidate();
                    return true;
                }


                break;
            case MotionEvent.ACTION_MOVE:
                if (mIsMovingPointer) {
                    float tempPointerAngle = (float) Math.toDegrees(Math.atan2(y, x));
                    if (tempPointerAngle >= mTabItems.get(0).getStartAngle()
                            && tempPointerAngle <= mTabItems.get(mTabItems.size() - 1).getEndAngle()) {
                        mPointerAngle = tempPointerAngle;
                        invalidate();
                    }

                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (mIsMovingPointer) {
                    mIsMovingPointer = false;
                    smoothMove();
                    return true;
                }
                break;
        }

        return false;
    }

    /**
     * 根据触摸点距离圆心的距离，与每一块的活动角度，确定惟一的文本触摸块。
     */
//    private void calculateTouchRect(float x, float y) {
////        =x-mWheelCenterX;
//        float dpow2 = (float) (Math.pow((x - mWheelCenterX), 2) + Math.pow((y - mWheelCenterY), 2));
//        float d = (float) Math.sqrt(dpow2);
//        Log.d(TAG, "TouchRect---d=" + d);
//        Log.d(TAG, "mMinRectRadius====" + mMinRectRadius);
//        Log.d(TAG, "mMAXRectRadius====" + mMaxRectRadius);
//
//
//    }

    /**
     * 相对与（x-mWheelCenterX，y-mWheelCenterY）坐标，
     * 计算触摸点。求两点间距离，此时的圆心为
     */
    public float distanceRelative(float x, float y) {
        float dx = Math.abs(x);
        float dy = Math.abs(y);
        Log.d(TAG, "distance---d=" + (float) Math.hypot(dx, dy));
        Log.d(TAG, "mMinRectRadius====" + mMinRectRadius);
        Log.d(TAG, "mMAXRectRadius====" + mMaxRectRadius);
        return (float) Math.hypot(dx, dy);
    }

    /**
     * 计算触摸点。求两点间距离
     */
    public float distance(float x, float y) {
        float dx = Math.abs(x - mWheelCenterX);
        float dy = Math.abs(y - mWheelCenterY);
        Log.d(TAG, "distance---d=" + (float) Math.hypot(dx, dy));
        Log.d(TAG, "mMinRectRadius====" + mMinRectRadius);
        Log.d(TAG, "mMAXRectRadius====" + mMaxRectRadius);
        return (float) Math.hypot(dx, dy);
    }


    private void smoothMove() {
        int willSelectedIndex = -1;
        for (int i = 0; i < mTabItems.size(); i++) {
            IndicatorTabItem item = mTabItems.get(i);
            if (mPointerAngle >= item.getStartAngle() && mPointerAngle <= item.getEndAngle()) {
                willSelectedIndex = i;
                break;
            }
        }

        if (mSelectTabIndex != willSelectedIndex) {
            if (mTabChangeListener != null) {
                mTabChangeListener.onTabSelected(this, willSelectedIndex);
            }
        }

        setSelection(willSelectedIndex);
    }

    public void setTabInfo(List<String> tabInfo) {
        if (tabInfo == null && (tabInfo.size() == 0 && tabInfo.size() > 4)) {
            return;
        }

        float totalPercent = 0.0f;
        for (int i = 0; i < tabInfo.size(); i++) {
            IndicatorTabItem item = new IndicatorTabItem();
            item.setName(tabInfo.get(i));
            item.setMesureWidth(mTabTextPaint.measureText(item.getName()));

            Log.d(TAG, "--------setTabInfo()-------" + item.getName());
            totalPercent += item.getMesureWidth();
            mTabItems.add(item);
        }

        float startAngle = mStartAngle;
        for (int i = 0; i < mTabItems.size(); i++) {
            IndicatorTabItem tempItem = mTabItems.get(i);
            float itemSweepAngle = mSweepAngle * tempItem.getMesureWidth() / totalPercent;
            tempItem.setStartAngle(startAngle);
            tempItem.setEndAngle(startAngle + itemSweepAngle);
            startAngle += itemSweepAngle;
            Log.d(TAG, "startAngle" + i + "======" + startAngle);
            Log.d(TAG, "EndAngle" + i + "======" + (startAngle + itemSweepAngle));
        }
        setSelection(0);

        initIndicatorTabRectItem(tabInfo);
    }

    /**
     * 初始化(绘制文字点击区域，采用极坐标，来确定位置);
     *
     * @param tabInfo
     */
    private void initIndicatorTabRectItem(List<String> tabInfo) {
        if (tabInfo == null && (tabInfo.size() == 0 && tabInfo.size() > 4)) {
            return;
        }
        float totalPercent = 0.0f;
        for (int i = 0; i < tabInfo.size(); i++) {
            IndicatorTabRectItem rectItem = new IndicatorTabRectItem();
            rectItem.setRectName(tabInfo.get(i));
            rectItem.setRectWidth(mTabTextPaint.measureText(rectItem.getRectName()));
            totalPercent += rectItem.getRectWidth();
            Log.d(TAG, "----initTXWheelTabRectItem--------setRectWidth" + i + "======" + mTabTextPaint.measureText(rectItem.getRectName()));
            mTabRectItem.add(rectItem);
        }
        float startAngle = mStartAngle;

        for (int i = 0; i < mTabRectItem.size(); i++) {
            IndicatorTabRectItem tempItem = mTabRectItem.get(i);
            float itemSweepAngle = mSweepAngle * tempItem.getRectWidth() / totalPercent;

            tempItem.setStartAngle(startAngle);
            tempItem.setEndAngle(startAngle + itemSweepAngle);
            startAngle += itemSweepAngle;
            Log.d(TAG, "----initIndicatorTabRectItem-------startAngle" + i + "======" + startAngle);
            Log.d(TAG, "----initIndicatorTabRectItem-------EndAngle" + i + "======" + (startAngle + itemSweepAngle));
        }
    }

    /**
     * 设置选择小点的每一段中心点角度。
     *
     * @param index
     */
    public void setSelection(int index) {
        if (index < 0 || index > mTabItems.size() - 1) {
            return;
        }

        mSelectTabIndex = index;
        mPointerAngle = (mTabItems.get(mSelectTabIndex).getStartAngle() + mTabItems.get(mSelectTabIndex).getEndAngle()) / 2;
        invalidate();
    }

    public void setTabChangeListener(OnTabChangeListener tabChangeListener) {
        this.mTabChangeListener = tabChangeListener;
    }

    public interface OnTabChangeListener {
        void onTabSelected(View v, int position);
    }
}
