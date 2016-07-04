package msr.msrlibrary.base.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.zhy.http.okhttp.OkHttpUtils;

import butterknife.ButterKnife;
import msr.msrlibrary.R;
import msr.msrlibrary.base.application.MSRBaseApplication;
import msr.msrlibrary.callback.LoadFailListenter;
import msr.msrlibrary.utils.LogUtils;
import msr.msrlibrary.utils.MSRUtils;
import msr.msrlibrary.view.SpinnerLoader;
import msr.msrlibrary.view.SystemBarTintManager;

/**
 * 基类
 * Created by MSR on 2016/6/27.
 */
@SuppressLint("InflateParams")
@TargetApi(Build.VERSION_CODES.KITKAT)
public abstract class MSRBaseActivity extends FragmentActivity {

    protected LinearLayout mViewContent;
    protected View actionbar;
    private SystemBarTintManager tintManager;
    private boolean statusBarTintEnabled = true;
    private boolean statusBarEnabled;
    protected View loadView;
    private ViewGroup loadViewParent;
    private LinearLayout loading;
    private LinearLayout loadfail;
    private int offset;
    private LoadFailListenter loadFailListenter;
    protected final int NULL_OFFSET = 0;
    protected final int NULL_CONTENT_VIEWID = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        mViewContent = (LinearLayout) getLayoutInflater().inflate(
                R.layout.layout_base_content, null);
        setStatusBarEnable(true);
        actionbar = getLayoutInflater()
                .inflate(R.layout.layout_actionbar, null);
//        actionbar.layout(0,tintManager.getStatusBarTintView().getHeight(),tintManager.getStatusBarTintView().getWidth(),getActionBarSize(this));
        actionbar.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                getActionBarSize(this)));
        mViewContent.addView(actionbar);
        if (returnContentViewID() != NULL_CONTENT_VIEWID)
            mViewContent.addView(getLayoutInflater().inflate(returnContentViewID(), null), new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        //set status bar transparent
        tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setNavigationBarTintEnabled(true);
        tintManager.setStatusBarTintResource(android.R.color.transparent);

        //优化在前一个activity全屏后到下个界面title会有一个向下跳的不友好的动作
        smoothSwitchScreen();

        //set Content
        setContentView(mViewContent);
        ButterKnife.bind(this);
        ((MSRBaseApplication) getApplication()).addActivity(this);
        initActionbarAndStatusBar();
        TODO(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        OkHttpUtils.getInstance().cancelTag(this);
        ((MSRBaseApplication) getApplication()).removeActivity(this);
    }

    abstract protected int returnContentViewID();

    abstract protected void initActionbarAndStatusBar();

    abstract protected void TODO(Bundle savedInstanceState);

    /**
     * 设置加载等待布局
     *
     * @param loadViewID 需要加载等待的布局ID
     * @param offset     等待进度条偏差值
     */
    protected void setupLoadView(int loadViewID, int offset) {
        loadView = mViewContent.findViewById(loadViewID);
        loadViewParent = (ViewGroup) loadView.getParent();
        if (loadView != null) {
            loading = (LinearLayout) getLayoutInflater().inflate(R.layout.base_content_loader, null);
            loadfail = (LinearLayout) getLayoutInflater().inflate(R.layout.base_content_loadfail, null);
        }
        this.offset = offset;
    }

    /**
     * 设置actionbar可用，需要在
     *
     * @param enable
     */
    protected void setActionbarEnable(boolean enable) {
        if (enable) {
            actionbar.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    getActionBarSize(this)));
        } else {
            actionbar.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    0));
        }

    }

    protected void setStatusBarEnable(boolean enable) {
        statusBarEnabled = enable;
        if (mViewContent != null) {
            mViewContent.setClipToPadding(false);
            mViewContent.setFitsSystemWindows(statusBarEnabled);
        }
    }


    /**
     * 得到包含actionbar的布局
     *
     * @return mViewGroup
     */
    protected ViewGroup getViewGroup() {
        return mViewContent;
    }


    /**
     * 设置状态栏透明颜色
     *
     * @param resourceId
     */
    protected void setStatusBarTintResource(int resourceId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
                && statusBarTintEnabled) {
            tintManager.setStatusBarTintResource(resourceId);
        }
    }

    /**
     * 设置状态栏颜色
     *
     * @param color
     */
    protected void setStatusBarTintColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
                && statusBarTintEnabled) {
            tintManager.setStatusBarTintColor(color);
        }
    }


    public int getStatusBarHeight() {
        return tintManager.getConfig().getStatusBarHeight();
    }

    public int getActionBarSize(Context context) {
        int[] attrs = {android.R.attr.actionBarSize};
        TypedArray values = context.getTheme().obtainStyledAttributes(attrs);
        try {
            int v = values.getDimensionPixelSize(0, 0);
            TypedArray styleAttrs = obtainStyledAttrsFromThemeAttr(this,
                    R.attr.ptrHeaderStyle, R.styleable.PullToRefreshHeader);
            int height = styleAttrs.getDimensionPixelSize(
                    R.styleable.PullToRefreshHeader_ptrHeaderHeight, v);
            return height;
        } finally {
            values.recycle();
        }
    }

    protected TypedArray obtainStyledAttrsFromThemeAttr(Context context,
                                                        int themeAttr, int[] styleAttrs) {
        // Need to get resource id of style pointed to from the theme attr
        TypedValue outValue = new TypedValue();
        context.getTheme().resolveAttribute(themeAttr, outValue, true);
        final int styleResId = outValue.resourceId;

        // Now return the values (from styleAttrs) from the style
        return context.obtainStyledAttributes(styleResId, styleAttrs);
    }

    /**
     * 设置actionbar标题
     *
     * @param title
     */
    public void setActionBarTitle(String title) {
        LinearLayout titleLayout = (LinearLayout) actionbar
                .findViewById(R.id.actionbar_title);
        TextView maintitle = (TextView) titleLayout
                .findViewById(R.id.actionbar_title_main);
        maintitle.setText(title);
    }

    /**
     * 设置actionbar小标题
     * tip 默认带向下箭头
     */
    public void setActionBarSub(String subtext, OnClickListener listener) {
        LinearLayout titleLayout = (LinearLayout) actionbar
                .findViewById(R.id.actionbar_title);
        TextView sub = (TextView) titleLayout
                .findViewById(R.id.actionbar_title_img);
        sub.setText(subtext);
        sub.setVisibility(View.VISIBLE);
        sub.setOnClickListener(listener);
    }

    /**
     * 设置actionbar标题颜色
     *
     * @param color
     */
    protected void setActionBarTitleColor(int color) {
        TextView title_main = (TextView) actionbar.findViewById(R.id.actionbar_title_main);
        TextView right_text = (TextView) actionbar.findViewById(R.id.actionbar_right_button_text);
        title_main.setTextColor(color);
        right_text.setTextColor(color);

    }

    /**
     * 设置actionbartitle的文字并带有图片
     *
     * @param title
     * @param listener
     */
    protected void setActionBarTitleImg(String title, OnClickListener listener) {
        LinearLayout titleLayout = (LinearLayout) actionbar
                .findViewById(R.id.actionbar_title);
        TextView actionbar_title_img = (TextView) actionbar
                .findViewById(R.id.actionbar_title_img);
        TextView maintitle = (TextView) titleLayout
                .findViewById(R.id.actionbar_title_main);
        actionbar_title_img.setVisibility(View.VISIBLE);
        maintitle.setText(title);
        titleLayout.setOnClickListener(listener);
    }

    /**
     * 设置actionbar右边的文字及点击事件
     *
     * @param title
     * @param listener
     */
    protected void setActionbarRightText(String title, OnClickListener listener) {

        LinearLayout titleLayout = (LinearLayout) actionbar
                .findViewById(R.id.actionbar_right_button);
        TextView right_button_text = (TextView) actionbar.findViewById(R.id.actionbar_right_button_text);
        ImageView right_button_img = (ImageView) actionbar.findViewById(R.id.actionbar_right_button_img);
        right_button_text.setVisibility(View.VISIBLE);
        right_button_img.setVisibility(View.GONE);
        right_button_text.setText(title);
        if (listener != null)
            titleLayout.setOnClickListener(listener);
    }

    /**
     * set之actionbar右边文字
     *
     * @param title
     */
    protected void setActionbarRightText(String title) {
        TextView right_button_text = (TextView) actionbar.findViewById(R.id.actionbar_right_button_text);
        right_button_text.setText(title);
    }

    /**
     * 设置actionbar右边的文字并设置文字颜色
     *
     * @param title
     * @param listener
     * @param color
     */
    protected void setActionbarRightTextWithColor(String title, OnClickListener listener, int color) {

        LinearLayout titleLayout = (LinearLayout) actionbar
                .findViewById(R.id.actionbar_right_button);
        TextView right_button_text = (TextView) actionbar.findViewById(R.id.actionbar_right_button_text);
        ImageView right_button_img = (ImageView) actionbar.findViewById(R.id.actionbar_right_button_img);
        right_button_text.setVisibility(View.VISIBLE);
        right_button_img.setVisibility(View.GONE);
        right_button_text.setText(title);
        right_button_text.setTextColor(getResources().getColorStateList(color));
        if (listener != null)
            titleLayout.setOnClickListener(listener);
    }

    /**
     * 设置右边文字的背景及onclick事件
     *
     * @param resId
     * @param listener
     */
    protected void setActionbarRightTextAndBackground(int resId, String title, OnClickListener listener) {
        TextView right_button_text = (TextView) actionbar.findViewById(R.id.actionbar_right_button_text);
        right_button_text.setVisibility(View.VISIBLE);
        right_button_text.setBackgroundResource(resId);
        right_button_text.setText(title);
        if (listener != null)
            right_button_text.setOnClickListener(listener);
    }

    /**
     * 设置actionbar右边的图片
     *
     * @param resid
     * @param listener
     */
    protected void setActionbarRightImg(int resid, OnClickListener listener) {

        LinearLayout titleLayout = (LinearLayout) actionbar
                .findViewById(R.id.actionbar_right_button);
        ImageView right_button_img = (ImageView) actionbar.findViewById(R.id.actionbar_right_button_img);
        right_button_img.setVisibility(View.VISIBLE);
        right_button_img.setBackgroundResource(resid);
        if (listener != null)
            titleLayout.setOnClickListener(listener);
    }

    /**
     * 设置actionBar的icon
     *
     * @param DrawableId
     * @param listener
     */
    protected void setActionBarIcon(int DrawableId, OnClickListener listener) {
        ImageView image = (ImageView) actionbar
                .findViewById(R.id.actionbar_left_icon_image);
        image.setImageResource(DrawableId);
        if (listener != null)
            ((LinearLayout) image.getParent()).setOnClickListener(listener);
    }

    /**
     * 设置actionbar左边的文字
     *
     * @param text
     * @param listener
     */
    protected void setActionBarLeftText(String text, OnClickListener listener) {
        TextView textView = (TextView) actionbar.findViewById(R.id.actionbar_left_text);
        textView.setText(text);
        if (listener != null) {
            textView.setOnClickListener(listener);
        }
    }

    /**
     * 设置actionbar右边的文字
     *
     * @param text
     * @param listener
     */
    protected void setActionBarRightText(String text, OnClickListener listener) {
        TextView textView = (TextView) actionbar.findViewById(R.id.actionbar_right_button_text);
        textView.setVisibility(View.VISIBLE);
        textView.setText(text);
        if (listener != null) {
            textView.setOnClickListener(listener);
        }
    }

    /**
     * 设置actionbar右边文字的大小
     *
     * @param size
     */
    protected void setActionBarRightTextSize(int size) {
        TextView textView = (TextView) actionbar.findViewById(R.id.actionbar_right_button_text);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
    }

    /**
     * 设置actionbar的背景
     *
     * @param drawable
     */
    protected void setActionBarBgDrawable(Drawable drawable) {
        actionbar.setBackground(drawable);
    }

    protected void setActionBarBgColor(int color) {
        actionbar.setBackgroundColor(color);
    }

    protected void setActionBarBgRes(int resID) {
        actionbar.setBackgroundResource(resID);
    }


    protected void setActionBarAndStatusBgRes(int resID) {
        actionbar.setBackgroundResource(resID);
        if (statusBarEnabled)
            tintManager.setStatusBarTintResource(resID);
    }

    protected void setActionBarAndStatusBgColor(int color) {
        actionbar.setBackgroundColor(color);
        if (statusBarEnabled)
            tintManager.setStatusBarTintColor(color);
    }

    /**
     * 设置状态栏透明是否启用
     *
     * @param enable
     */
    protected void setStatusBarTintEnabled(boolean enable) {
        statusBarTintEnabled = enable;

    }

    /**
     * 设置actionbar icon的点击事件
     *
     * @param listener
     */
    protected void setActionBarIconOnClick(OnClickListener listener) {
        actionbar.findViewById(R.id.actionbar_left_icon).setOnClickListener(
                listener);
    }

    /**
     * 简化设置ActionBar,包含关闭当前Activity功能
     *
     * @param DrawableId
     */
    protected void setActionBarIconEasy(int DrawableId) {
        // TODO Auto-generated method stub
        setActionBarIcon(DrawableId, new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                finish();
            }
        });
    }

    /**
     * 隐藏actionbar 左边图标
     */
    protected void hideActionBarLeftIcon() {
        // TODO Auto-generated method stub
        ImageView image = (ImageView) actionbar
                .findViewById(R.id.actionbar_left_icon_image);
        image.setVisibility(View.GONE);
    }

    protected SystemBarTintManager getTintManager() {
        return tintManager;
    }

    /**
     * 返回对象本身，在内部匿名类中很有用
     *
     * @return
     */
    protected Context This() {
        return this;
    }

    protected void setActionbarRightTextColor(int ResID) {
        TextView textView = (TextView) actionbar.findViewById(R.id.actionbar_right_button_text);
        textView.setTextColor(getResources().getColor(ResID));
    }


    /**
     * loading
     */
    protected void loadWait() {
        if (loadView == null) return;
        loading.setLayoutParams(loadView.getLayoutParams());
        SpinnerLoader progress = (SpinnerLoader) loading.findViewById(R.id.base_content_loader_progress);
        if (offset != NULL_OFFSET) {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) progress.getLayoutParams();
            params.bottomMargin = MSRUtils.dp2px(offset, This());
            progress.setLayoutParams(params);
        }
        loadViewParent.removeView(loadView);
        loadViewParent.removeView(loadfail);
        if (loading.getParent() == null)
            loadViewParent.addView(loading);
    }

    /**
     * load fail
     */
    protected void loadFail() {
        if (loadView == null) return;
        loadViewParent.removeView(loadView);
        loadViewParent.removeView(loading);
        if (loadfail.getParent() == null)
            loadViewParent.addView(loadfail);
        loadfail.setLayoutParams(loadView.getLayoutParams());
        LinearLayout error = (LinearLayout) loadfail.findViewById(R.id.base_content_loadfial_lin);
        if (offset != NULL_OFFSET) {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) error.getLayoutParams();
            LogUtils.e("params.bottomMargin--->" + params.bottomMargin);
            params.bottomMargin = MSRUtils.dp2px(offset, This());
            LogUtils.e("params.bottomMargin--->" + params.bottomMargin);
            error.setLayoutParams(params);
        }
        error.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (loadFailListenter != null) {
                    loadFailListenter.loadfialTODO(v);
                }
            }
        });
    }

    /**
     * load success
     */
    protected void loadSuccess() {
        if (loadView == null) return;
        loadViewParent.removeView(loadfail);
        loadViewParent.removeView(loading);
        if (loadView.getParent() == null)
            loadViewParent.addView(loadView);
    }

    /**
     * 设置加载背景
     *
     * @param resID
     */
    protected void setLoadingBackgroundColor(int resID) {
        if (loading != null)
            loading.setBackgroundColor(resID);

    }

    /**
     * 设置加载失败的文字
     *
     * @param text
     */
    protected void setLoadFailText(String text) {
        if (loadfail != null) {
            ((TextView) loadfail.findViewById(R.id.base_content_loadfail_tv)).setText(text);
        }
    }

    /**
     * 设置等待Progress颜色
     *
     * @param resID
     */
    protected void setloadWaitColor(int resID) {
        if (loadView == null) return;
        SpinnerLoader progress = (SpinnerLoader) loading.findViewById(R.id.base_content_loader_progress);
        progress.setPointcolor(resID);
    }

    /**
     * 设置加载失败的图片
     *
     * @param resID
     */
    protected void setLoadFailImg(int resID) {
        if (loadfail != null) {
            ((ImageView) loadfail.findViewById(R.id.base_content_loadfail_img)).setBackgroundResource(resID);
        }
    }

    public LoadFailListenter getLoadFailListenter() {
        return loadFailListenter;
    }

    public void setLoadFailListenter(LoadFailListenter loadFailListenter) {
        this.loadFailListenter = loadFailListenter;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                finish();
                break;
            default:
                break;
        }
        return false;
    }

    /**
     * 优化在前一个activity全屏后到下个界面title会有一个向下跳的不友好的动作
     */
    private void smoothSwitchScreen() {
        // 5.0以上修复了此bug
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            ViewGroup rootView = ((ViewGroup) this.findViewById(android.R.id.content));
            int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
            int statusBarHeight = getResources().getDimensionPixelSize(resourceId);
            rootView.setPadding(0, statusBarHeight, 0, 0);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }

}
