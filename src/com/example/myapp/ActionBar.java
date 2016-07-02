package com.example.myapp;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.GradientDrawable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.*;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ActionBar extends LinearLayout {
    //最小容器宽度
    final static int CONTAINER_MIN_WIDTH = 44;
    //点击颜色
    final static String PRESS_COLOR = "#05000000";
    //文字大小
    final static int DEFAULT_TEXT_SIZE = 50;
    //默认文字颜色
    final static String DEFAULT_NORMAL_TEXT_COLOR = "#000000";
    //禁用后，文字颜色
    final static String DEFAULT_DISABLE_TEXT_COLOR = "#CCCCCC";
    //底部虚线开始颜色
    final static String DEFAULT_BOTTOM_LINE_START_COLOR = "#CCCCCC";
    //底部虚线结束颜色
    final static String DEFAULT_BOTTOM_LINE_END_COLOR = "#FFFFFF";
    //底部虚线高度
    final static int DEFAULT_BOTTOM_LINE_HEIGHT_DP = 5;

    //左边容器
    FrameLayout fl_left_container;
    //左边文字/图片
    TextView tv_left;
    //中间标题
    TextView tv_middle;
    //右边容器
    FrameLayout fl_right_container;
    //右边文字/图片
    TextView tv_right;
    //底部虚线
    View v_bottom_line;

    //点击监听器
    OnClickListener listener;
    //左边控件是否禁用
    boolean leftDisable;
    //右边控件是否禁用
    boolean rightDisable;

    //左边文字颜色
    int leftTextColor = 0;
    //左边文字禁用颜色
    int leftTextDisableColor = 0;
    //标题文字颜色
    int titleColor = 0;
    //右边文字颜色
    int rightTextColor = 0;
    //右边文字禁用颜色
    int rightTextDisableColor = 0;

    public ActionBar(Context context) {
        this(context, null);
    }


    public ActionBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    /**
     * dp转px工具
     */
    static int dp2px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    private void init(Context context, AttributeSet attrs) {
        //初始化视图
        initView(context);
        //初始化配置
        initProfile(context, attrs);
        //初始化事件
        initEvent(context);
    }

    /**
     * 初始化试图
     */
    private void initView(Context context) {
        //设置本ActionBar的样式
        {
            setOrientation(LinearLayout.VERTICAL);
        }
        //主要容器
        {
            LinearLayout ll_main_container = new LinearLayout(context);
            ll_main_container.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1));
            //设置左边容器
            {
                fl_left_container = new FrameLayout(context);
                fl_left_container.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
                fl_left_container.setMinimumWidth(dp2px(context, CONTAINER_MIN_WIDTH));
                fl_left_container.setPadding(dp2px(context, 10), 0, dp2px(context, 10), 0);
                fl_left_container.setOnTouchListener(new OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (!isLeftDisable()) {
                            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                                fl_left_container.setBackgroundColor(Color.parseColor(PRESS_COLOR));
                            } else if (event.getAction() == MotionEvent.ACTION_UP
                                    || event.getAction() == MotionEvent.ACTION_CANCEL) {
                                fl_left_container.setBackgroundDrawable(null);
                            }
                        }
                        return false;
                    }
                });
                //包裹内容
                {
                    tv_left = new TextView(context);
                    tv_left.setSingleLine(true);
                    fl_left_container.addView(tv_left);
                }
                ll_main_container.addView(fl_left_container);
            }
            //设置中间标题
            {
                tv_middle = new TextView(context);
                tv_middle.setLayoutParams(new LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1));
                tv_middle.setGravity(Gravity.CENTER);
                tv_middle.setEllipsize(TextUtils.TruncateAt.END);
                tv_middle.setSingleLine(true);
                ll_main_container.addView(tv_middle);
            }
            //设置右边内容
            {
                fl_right_container = new FrameLayout(context);
                fl_right_container.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
                fl_right_container.setMinimumWidth(dp2px(context, CONTAINER_MIN_WIDTH));
                fl_right_container.setPadding(dp2px(context, 10), 0, dp2px(context, 10), 0);
                fl_right_container.setOnTouchListener(new OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (!isRightDisable()) {
                            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                                fl_right_container.setBackgroundColor(Color.parseColor(PRESS_COLOR));
                            } else if (event.getAction() == MotionEvent.ACTION_UP
                                    || event.getAction() == MotionEvent.ACTION_CANCEL) {
                                fl_right_container.setBackgroundDrawable(null);
                            }
                        }
                        return false;
                    }
                });
                //包裹内容
                {
                    tv_right = new TextView(context);
                    tv_right.setSingleLine(true);
                    fl_right_container.addView(tv_right);
                }
                ll_main_container.addView(fl_right_container);
            }
            this.addView(ll_main_container);
        }
        //脚线
        {
            v_bottom_line = new View(context);
            v_bottom_line.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1, 0));
            this.addView(v_bottom_line);
        }
    }

    /**
     * 初始化配置
     */
    void initProfile(Context context, AttributeSet attrs) {
        //左边文字
        String leftText = null;
        //左边图片ID
        int leftImg = 0;
        //是否隐藏左边的控件
        boolean leftHide = false;
        //中部标题
        String title = null;
        //右边文本
        String rightText = null;
        //右边图片ID
        int rightImg = 0;
        //右边控件是否隐藏
        boolean rightHide = false;
        //底部脚线是否隐藏
        boolean bottomLineHide = false;
        //左边是否禁用
        boolean leftDisable = false;
        //右边是否禁用
        boolean rightDisable = false;
        //文字大小
        int textSize = DEFAULT_TEXT_SIZE;
        //文字颜色
        int leftTextColor = Color.parseColor(DEFAULT_NORMAL_TEXT_COLOR);
        int leftTextDisableColor = Color.parseColor(DEFAULT_DISABLE_TEXT_COLOR);
        int titleColor = Color.parseColor(DEFAULT_NORMAL_TEXT_COLOR);
        int rightTextColor = Color.parseColor(DEFAULT_NORMAL_TEXT_COLOR);
        int rightTextDisableColor = Color.parseColor(DEFAULT_DISABLE_TEXT_COLOR);
        //底部虚线
        int bottomLineStartColor = Color.parseColor(DEFAULT_BOTTOM_LINE_START_COLOR);
        int bottomLineEndColor = Color.parseColor(DEFAULT_BOTTOM_LINE_END_COLOR);
        int bottomLineHeight = dp2px(context, DEFAULT_BOTTOM_LINE_HEIGHT_DP);


        if (attrs != null) {
            //读取属性
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.action_bar_styleable);
            final int indexCount = typedArray.getIndexCount();
            for (int i = 0; i < indexCount; ++i) {
                int index = typedArray.getIndex(i);
                switch (index) {
                    case R.styleable.action_bar_styleable_left_text:
                        leftText = typedArray.getString(index);
                        break;
                    case R.styleable.action_bar_styleable_left_img:
                        leftImg = typedArray.getResourceId(index, 0);
                        break;
                    case R.styleable.action_bar_styleable_left_hide:
                        leftHide = typedArray.getBoolean(index, false);
                        break;
                    case R.styleable.action_bar_styleable_left_disable:
                        leftDisable = typedArray.getBoolean(index, false);
                        break;
                    case R.styleable.action_bar_styleable_left_text_color:
                        leftTextColor = typedArray.getColor(index, Color.parseColor(DEFAULT_NORMAL_TEXT_COLOR));
                        break;
                    case R.styleable.action_bar_styleable_left_text_disable_color:
                        leftTextDisableColor = typedArray.getColor(index, Color.parseColor(DEFAULT_DISABLE_TEXT_COLOR));
                        break;
                    case R.styleable.action_bar_styleable_title:
                        title = typedArray.getString(index);
                        break;
                    case R.styleable.action_bar_styleable_title_color:
                        titleColor = typedArray.getColor(index, Color.parseColor(DEFAULT_NORMAL_TEXT_COLOR));
                        break;
                    case R.styleable.action_bar_styleable_right_text:
                        rightText = typedArray.getString(index);
                        break;
                    case R.styleable.action_bar_styleable_right_img:
                        rightImg = typedArray.getResourceId(index, 0);
                        break;
                    case R.styleable.action_bar_styleable_right_hide:
                        rightHide = typedArray.getBoolean(index, false);
                        break;
                    case R.styleable.action_bar_styleable_right_disable:
                        rightDisable = typedArray.getBoolean(index, false);
                        break;
                    case R.styleable.action_bar_styleable_right_text_color:
                        rightTextColor = typedArray.getColor(index, Color.parseColor(DEFAULT_NORMAL_TEXT_COLOR));
                        break;
                    case R.styleable.action_bar_styleable_right_text_disable_color:
                        rightTextDisableColor = typedArray.getColor(index, Color.parseColor(DEFAULT_DISABLE_TEXT_COLOR));
                        break;
                    case R.styleable.action_bar_styleable_text_size:
                        textSize = typedArray.getDimensionPixelSize(index, DEFAULT_TEXT_SIZE);
                        break;
                    case R.styleable.action_bar_styleable_bottom_line_hide:
                        bottomLineHide = typedArray.getBoolean(index, false);
                        break;
                    case R.styleable.action_bar_styleable_bottom_line_start_color:
                        bottomLineStartColor = typedArray.getColor(index, Color.parseColor(DEFAULT_BOTTOM_LINE_START_COLOR));
                        break;
                    case R.styleable.action_bar_styleable_bottom_line_end_color:
                        bottomLineEndColor = typedArray.getColor(index, Color.parseColor(DEFAULT_BOTTOM_LINE_END_COLOR));
                        break;
                    case R.styleable.action_bar_styleable_bottom_line_height:
                        bottomLineHeight = typedArray.getDimensionPixelSize(index, dp2px(context, DEFAULT_BOTTOM_LINE_HEIGHT_DP));
                        break;
                }
            }
            typedArray.recycle();
        }
        //左边
        {
            if (leftText != null) {
                setLeftText(leftText);
            }
            if (leftImg != 0) {
                setLeftImg(leftImg);
            }
            setLeftHide(leftHide);
            setLeftDisable(leftDisable);
            setLeftTextColor(leftTextColor);
            setLeftTextDisableColor(leftTextDisableColor);
        }
        //中间
        {
            if (title != null) {
                setTitle(title);
            }
            setTitleColor(titleColor);
        }
        //右边
        {
            if (rightText != null) {
                setRightText(rightText);
            }
            if (rightImg != 0) {
                setRightImg(rightImg);
            }
            setRightHide(rightHide);
            setRightDisable(rightDisable);
            setRightTextColor(rightTextColor);
            setRightTextDisableColor(rightTextDisableColor);
        }
        //设置文字大小
        {
            setTextSize(textSize);
        }
        //底部
        {
            //设置基本样式
            setBottomLineStyle(bottomLineStartColor, bottomLineEndColor, bottomLineHeight);
            //设置是否影藏
            setBottomLineHide(bottomLineHide);
        }
    }

    /**
     * 初始化事件
     */
    private void initEvent(Context context) {
        fl_left_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null && !isLeftDisable()) {
                    listener.onLeftClick(tv_left);
                }
            }
        });
        tv_middle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onTitleClick(tv_middle);
                }
            }
        });
        fl_right_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null && !isRightDisable()) {
                    listener.onRightClick(tv_right);
                }
            }
        });
    }

    /**
     * 设置右边文字
     */
    public void setLeftText(CharSequence leftText) {
        tv_left.setText(leftText);
        tv_left.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER));
        tv_left.setBackgroundDrawable(null);
    }

    /**
     * 设置右边图片
     */
    public void setLeftImg(int leftImg) {
        tv_left.setText(null);
        if (leftImg != 0) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), leftImg);
            if (bitmap != null) {
                tv_left.setLayoutParams(new FrameLayout.LayoutParams(bitmap.getWidth(), bitmap.getHeight(), Gravity.CENTER));
                tv_left.setBackgroundDrawable(new BitmapDrawable(bitmap));
            }
        } else {
            tv_left.setBackgroundDrawable(null);
        }
    }

    /**
     * 设置右边是否隐藏
     */
    public void setLeftHide(boolean leftHide) {
        if (leftHide) {
            tv_left.setVisibility(GONE);
        } else {
            tv_left.setVisibility(VISIBLE);
        }
    }

    /**
     * 设置左边控件是否禁用
     */
    public void setLeftDisable(boolean disable) {
        leftDisable = disable;
        refreshTextColor();
    }

    /**
     * 设置左边文字颜色
     */
    public void setLeftTextColor(int leftTextColor) {
        this.leftTextColor = leftTextColor;
        refreshTextColor();
    }

    /**
     * 设置左边文字禁用颜色
     */
    public void setLeftTextDisableColor(int leftTextDisableColor) {
        this.leftTextDisableColor = leftTextDisableColor;
        refreshTextColor();
    }


    /**
     * 设置标题
     */
    public void setTitle(CharSequence title) {
        tv_middle.setText(title);
    }

    /**
     * 设置标题
     *
     * @param title    标题，这部分会被缩略
     * @param decorate 修饰，这部分尽量显示
     */
    public void setTitle(final CharSequence title, final CharSequence decorate) {
        if (title == null || decorate == null) {
            if (title != null) {
                tv_middle.setText(title);
            } else if (decorate != null) {
                tv_middle.setText(decorate);
            } else {
                tv_middle.setText(null);
            }
        } else {
            tv_middle.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                static final int LINE_COUNT = 1;

                @Override
                public void onGlobalLayout() {
                    tv_middle.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    TextPaint paint = tv_middle.getPaint();
                    int paddingLeft = tv_middle.getPaddingLeft();
                    int paddingRight = tv_middle.getPaddingRight();
                    //缓冲区长度，空出两个字符的长度来给最后的省略号及添加的修饰文字
                    int bufferWidth = (int) paint.getTextSize() * decorate.length() + 2;
                    // 计算出2行文字所能显示的长度
                    int availableTextWidth = (tv_middle.getWidth() - paddingLeft - paddingRight) * LINE_COUNT - bufferWidth;
                    // 根据长度截取出剪裁后的文字
                    CharSequence ellipsizeStr = TextUtils.ellipsize(title, (TextPaint) paint, availableTextWidth, TextUtils.TruncateAt.END);
                    //合并文字
                    tv_middle.setText(TextUtils.concat(ellipsizeStr, decorate));
                }
            });
            //重绘
            tv_middle.invalidate();
        }
    }

    /**
     * 设置标题文字颜色
     */
    public void setTitleColor(int titleColor) {
        this.titleColor = titleColor;
        refreshTextColor();
    }

    /**
     * 设置右边的文字
     */
    public void setRightText(CharSequence rightText) {
        tv_right.setText(rightText);
        tv_right.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER));
        tv_right.setBackgroundDrawable(null);
    }


    /**
     * 设置右边图片
     */
    public void setRightImg(int rightImg) {
        tv_right.setText(null);
        if (rightImg != 0) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), rightImg);
            if (bitmap != null) {
                tv_right.setLayoutParams(new FrameLayout.LayoutParams(bitmap.getWidth(), bitmap.getHeight(), Gravity.CENTER));
                tv_right.setBackgroundDrawable(new BitmapDrawable(bitmap));
            }
        } else {
            tv_right.setBackgroundDrawable(null);
        }
    }

    /**
     * 设置右边是否隐藏
     */
    public void setRightHide(boolean rightHide) {
        if (rightHide) {
            tv_right.setVisibility(GONE);
        } else {
            tv_right.setVisibility(VISIBLE);
        }
    }

    /**
     * 设置右边文字是否为灰色
     */
    public void setRightDisable(boolean disable) {
        rightDisable = disable;
        refreshTextColor();
    }

    /**
     * 设置右边文字颜色
     */
    public void setRightTextColor(int rightTextColor) {
        this.rightTextColor = rightTextColor;
        refreshTextColor();
    }

    /**
     * 设置右边文字禁用颜色
     */
    public void setRightTextDisableColor(int rightTextDisableColor) {
        this.rightTextDisableColor = rightTextDisableColor;
        refreshTextColor();
    }


    /**
     * 设置底部线条是否隐藏
     */
    public void setBottomLineHide(boolean hide) {
        if (hide) {
            v_bottom_line.setVisibility(GONE);
        } else {
            v_bottom_line.setVisibility(VISIBLE);
        }
    }

    /**
     * 设置底部虚线样式
     */
    public void setBottomLineStyle(int startColor, int endColor, int height) {
        v_bottom_line.setBackgroundDrawable(new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{startColor, endColor}));
        v_bottom_line.getLayoutParams().height = height;
        v_bottom_line.requestLayout();
    }

    /**
     * 重置颜色后，需要刷新颜色
     */
    void refreshTextColor() {
        //右边
        if (leftDisable) {
            tv_left.setTextColor(leftTextDisableColor);
        } else {
            tv_left.setTextColor(leftTextColor);
        }
        //中间
        tv_middle.setTextColor(titleColor);
        //左边
        if (rightDisable) {
            tv_right.setTextColor(rightTextDisableColor);
        } else {
            tv_right.setTextColor(rightTextColor);
        }
    }


    /**
     * 左边控件是否禁用
     */
    public boolean isLeftDisable() {
        return leftDisable || tv_left.getVisibility() != VISIBLE;
    }

    /**
     * 右边控件是否禁用
     */
    public boolean isRightDisable() {
        return rightDisable || tv_right.getVisibility() != VISIBLE;
    }

    public CharSequence getLeftText() {
        return tv_left.getText();
    }

    public boolean isLeftHide() {
        return tv_left.getVisibility() == GONE;
    }

    public CharSequence getTitle() {
        return tv_middle.getText();
    }

    public CharSequence getRightText() {
        return tv_right.getText();
    }

    public boolean isRightHide() {
        return tv_right.getVisibility() == GONE;
    }

    public boolean isBottomLineHide() {
        return v_bottom_line.getVisibility() == GONE;
    }

    /**
     * 设置监听器
     */
    public void setOnClickListener(OnClickListener l) {
        this.listener = l;
    }

    public void setTextSize(int textSize) {
        tv_left.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        tv_right.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        tv_middle.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
    }

    public interface OnClickListener {
        /**
         * 左边点击
         */
        void onLeftClick(TextView textView);

        /**
         * 点击了中间标题
         */
        void onTitleClick(TextView textView);

        /**
         * 右边点击
         */
        void onRightClick(TextView textView);
    }
}
