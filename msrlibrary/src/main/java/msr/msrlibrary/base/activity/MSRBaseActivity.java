package msr.msrlibrary.base.activity;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import msr.msrlibrary.R;
import msr.msrlibrary.view.SystemBarTintManager;

/**
 * activity基类
 * Created by MSR on 2016/6/15.
 */

public abstract class MSRBaseActivity extends FragmentActivity {
    protected LinearLayout viewContent;
    private boolean statusBarEnabled = false;
    protected View actionbar;
    protected final int NULL_CONTENT_VIEWID = 0;
    private SystemBarTintManager tintManager;

    abstract protected int returnContentViewID();

    abstract protected void initActionbarAndStatusBar();

    abstract protected void TODO(Bundle savedInstanceState);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        viewContent = (LinearLayout) getLayoutInflater().inflate(R.layout.layout_base_content, null);
        setStatusBarEnable(true);
        actionbar = getLayoutInflater().inflate(R.layout.layout_actionbar, null);
        actionbar.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, getActionBarSize(this)));
        viewContent.addView(actionbar);
        if (returnContentViewID() != NULL_CONTENT_VIEWID)
            viewContent.addView(getLayoutInflater().inflate(returnContentViewID(), null), new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        //设置状态栏透明
        tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setNavigationBarTintEnabled(true);
        tintManager.setStatusBarTintResource(android.R.color.transparent);

        //优化在前一个activity全屏后到下个界面title会有一个向下跳的不友好的动作
        smoothSwitchScreen();

        //set Content
        setContentView(viewContent);
        ButterKnife.bind(this);
        ((MSRBaseApplication) getApplication()).addActivity(this);
        initActionbarAndStatusBar();
        TODO(savedInstanceState);
    }

    /**
     * 设置状态栏是否可用
     *
     * @param enable
     */
    protected void setStatusBarEnable(boolean enable) {
        statusBarEnabled = enable;
        if (viewContent != null) {
            viewContent.setClipToPadding(false);
            viewContent.setFitsSystemWindows(statusBarEnabled);
        }
    }

    /**
     * 获取actionbar的尺寸
     *
     * @param context
     * @return
     */
    public int getActionBarSize(Context context) {
        int[] attrs = {android.R.attr.actionBarSize};
        TypedArray values = context.getTheme().obtainStyledAttributes(attrs);
        try {
            int v = values.getDimensionPixelSize(0, 0);
            TypedArray styleAttrs = obtainStyledAttrsFromThemeAttr(this, R.attr.ptrHeaderStyle, R.styleable.PullToRefreshHeader);
            int height = styleAttrs.getDimensionPixelSize(R.styleable.PullToRefreshHeader_ptrHeaderHeight, v);
            return height;
        } finally {
            values.recycle();
        }
    }

    /**
     * 根据主题获取风格
     *
     * @param context
     * @param themeAttr
     * @param styleAttrs
     * @return
     */
    protected TypedArray obtainStyledAttrsFromThemeAttr(Context context, int themeAttr, int[] styleAttrs) {
        // 需要在attr中得到资源id
        TypedValue outValue = new TypedValue();
        context.getTheme().resolveAttribute(themeAttr, outValue, true);
        final int styleResId = outValue.resourceId;
        // 现在从attr中返回值
        return context.obtainStyledAttributes(styleResId, styleAttrs);
    }
}
