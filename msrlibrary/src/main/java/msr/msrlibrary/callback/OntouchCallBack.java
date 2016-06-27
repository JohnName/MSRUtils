package msr.msrlibrary.callback;

/**
 * Pull2FullScreen上拉下滑的状态 0 隐藏 1 一半 2 全屏
 * Created by MSR on 2016/6/27.
 */

public interface OnTouchCallBack {

    //浮层滑动情况...
    void slideState(int state);
}