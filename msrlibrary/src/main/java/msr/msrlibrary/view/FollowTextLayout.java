package msr.msrlibrary.view;

import android.content.Context;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * 在TextView文字后添加控件 这个控件里面只能放两个子控件
 * Created by MSR on 2016/7/4.
 */
public class FollowTextLayout extends ViewGroup {
    //单行显示
    private static final int SINGLE_LINE = 0x01;
    //多行显示
    private static final int MULTI_LINE = 0x02;
    //两个子控件<=父控件 并且三个子控件大于父控件的时候显示到下一行
    private static final int NEXT_LINE2 = 0x03;
    //两个子控件>父控件
    private static final int NEXT_LINE1 = 0x04;
    //显示样式
    private int type;
    //绘制文字最后一行的顶部坐标
    private int lastLineTop;
    //绘制文字最后一行的右边坐标
    private float lastLineRight;

    public FollowTextLayout(Context context) {
        super(context);
    }

    public FollowTextLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FollowTextLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int childCount = getChildCount();
        int w = MeasureSpec.getSize(widthMeasureSpec);
        if (childCount == 3) {
            TextView tv = null;
            if (getChildAt(0) instanceof TextView) {
                tv = (TextView) getChildAt(0);
                initTextParams(tv.getText(), tv.getMeasuredWidth(), tv.getPaint());
            } else {
                throw new RuntimeException("CustomLayout first child view not a TextView");
            }

            View secondView = getChildAt(1);
            View thirdView = getChildAt(2);

            //测量子view的宽高
            measureChildren(widthMeasureSpec, heightMeasureSpec);

            //两个子view宽度相加小于该控件宽度的时候
            if (tv.getMeasuredWidth() + secondView.getMeasuredWidth() + thirdView.getMeasuredWidth() <= w) {
                int width = tv.getMeasuredWidth() + secondView.getMeasuredWidth() + thirdView.getMeasuredWidth();
                //计算高度
                int heightTemp = Math.max(tv.getMeasuredHeight(), secondView.getMeasuredHeight());
                int height = Math.max(heightTemp, thirdView.getMeasuredHeight());
                //设置该viewgroup的宽高
                setMeasuredDimension(width, height);
                type = SINGLE_LINE;
                return;
            }
            if (getChildAt(0) instanceof TextView) {
                //最后一行文字的宽度加上第二个view的宽度大于viewgroup宽度时第二个控件换行显示
                if (lastLineRight + secondView.getMeasuredWidth() > w) {
                    setMeasuredDimension(tv.getMeasuredWidth(), tv.getMeasuredHeight() + secondView.getMeasuredHeight());
                    type = NEXT_LINE2;
                    return;
                }
                if (lastLineRight + secondView.getMeasuredWidth() <= w && lastLineRight + secondView.getMeasuredWidth() + thirdView.getMeasuredWidth() > w) {
                    setMeasuredDimension(tv.getMeasuredWidth() + secondView.getMeasuredWidth(), tv.getMeasuredHeight() + thirdView.getMeasuredHeight());
                    type = NEXT_LINE1;
                    return;
                }
                int height = Math.max(tv.getMeasuredHeight(), lastLineTop + thirdView.getMeasuredHeight());
                setMeasuredDimension(tv.getMeasuredWidth(), height);
                type = MULTI_LINE;
            }
        } else if (childCount == 2) {
            TextView tv = null;
            if (getChildAt(0) instanceof TextView) {
                tv = (TextView) getChildAt(0);
                initTextParams(tv.getText(), tv.getMeasuredWidth(), tv.getPaint());
            } else {
                throw new RuntimeException("CustomLayout first child view not a TextView");
            }

            View sencodView = getChildAt(1);

            //测量子view的宽高
            measureChildren(widthMeasureSpec, heightMeasureSpec);

            //两个子view宽度相加小于该控件宽度的时候
            if (tv.getMeasuredWidth() + sencodView.getMeasuredWidth() <= w) {
                int width = tv.getMeasuredWidth() + sencodView.getMeasuredWidth();
                //计算高度
                int height = Math.max(tv.getMeasuredHeight(), sencodView.getMeasuredHeight());
                //设置该viewgroup的宽高
                setMeasuredDimension(width, height);
                type = SINGLE_LINE;
                return;
            }
            if (getChildAt(0) instanceof TextView) {
                //最后一行文字的宽度加上第二个view的宽度大于viewgroup宽度时第二个控件换行显示
                if (lastLineRight + sencodView.getMeasuredWidth() > w) {
                    setMeasuredDimension(tv.getMeasuredWidth(), tv.getMeasuredHeight() + sencodView.getMeasuredHeight());
                    type = NEXT_LINE2;
                    return;
                }

                int height = Math.max(tv.getMeasuredHeight(), lastLineTop + sencodView.getMeasuredHeight());
                setMeasuredDimension(tv.getMeasuredWidth(), height);
                type = MULTI_LINE;
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = getChildCount();
        if (childCount == 3) {
            if (type == SINGLE_LINE || type == MULTI_LINE) {
                TextView tv = (TextView) getChildAt(0);
                View v1 = getChildAt(1);
                View v2 = getChildAt(2);
                //设置第二个view在Textview文字末尾位置
                tv.layout(0, 0, tv.getMeasuredWidth(), tv.getMeasuredHeight());
                int left = (int) lastLineRight;
                int top = lastLineTop;
                //最后一行的高度 注:通过staticLayout得到的行高不准确故采用这种方式
                int lastLineHeight = tv.getBottom() - tv.getPaddingBottom() - lastLineTop;
                //当第二view高度小于单行文字高度时竖直居中显示
                if (v1.getMeasuredHeight() < lastLineHeight) {
                    top = lastLineTop + (lastLineHeight - v1.getMeasuredHeight()) / 2;
                }
                v1.layout(left, top, left + v1.getMeasuredWidth(), top + v1.getMeasuredHeight());
                v2.layout(left + v1.getMeasuredWidth(), top, left + v2.getMeasuredWidth(), top + v2.getMeasuredHeight());
            } else if (type == NEXT_LINE1) {
                View v0 = getChildAt(0);
                View v1 = getChildAt(1);
                View v2 = getChildAt(2);
                //设置第二个view换行显示
                v0.layout(0, 0, v0.getMeasuredWidth(), v0.getMeasuredHeight());
                v1.layout(0, v0.getMeasuredHeight(), v1.getMeasuredWidth(), v0.getMeasuredHeight() + v1.getMeasuredHeight());
                v2.layout(v1.getMeasuredWidth(), v0.getMeasuredHeight(), v2.getMeasuredWidth(), v0.getMeasuredHeight() + v1.getMeasuredHeight());
            } else if (type == NEXT_LINE2) {
                TextView v0 = (TextView) getChildAt(0);
                View v1 = getChildAt(1);
                View v2 = getChildAt(2);
                //最后一行的高度 注:通过staticLayout得到的行高不准确故采用这种方式
                int lastLineHeight = v0.getBottom() - v0.getPaddingBottom() - lastLineTop;
                int top = lastLineTop;
                //当第二view高度小于单行文字高度时竖直居中显示
                if (v1.getMeasuredHeight() < lastLineHeight) {
                    top = lastLineTop + (lastLineHeight - v1.getMeasuredHeight()) / 2;
                }
                //设置第三个view换行显示
                v0.layout(0, 0, v0.getMeasuredWidth(), v0.getMeasuredHeight());
                v1.layout(v0.getMeasuredWidth(), top, v1.getMeasuredWidth(), v0.getMeasuredHeight() + v1.getMeasuredHeight());
                v2.layout(0, v0.getMeasuredHeight(), v2.getMeasuredWidth(), v0.getMeasuredHeight() + v2.getMeasuredHeight());
            }
        }
        if (childCount == 2) {
            if (type == SINGLE_LINE || type == MULTI_LINE) {
                TextView tv = (TextView) getChildAt(0);
                View v1 = getChildAt(1);
                //设置第二个view在Textview文字末尾位置
                tv.layout(0, 0, tv.getMeasuredWidth(), tv.getMeasuredHeight());
                int left = (int) lastLineRight;
                int top = lastLineTop;
                //最后一行的高度 注:通过staticLayout得到的行高不准确故采用这种方式
                int lastLineHeight = tv.getBottom() - tv.getPaddingBottom() - lastLineTop;
                //当第二view高度小于单行文字高度时竖直居中显示
                if (v1.getMeasuredHeight() < lastLineHeight) {
                    top = lastLineTop + (lastLineHeight - v1.getMeasuredHeight()) / 2;
                }
                v1.layout(left, top, left + v1.getMeasuredWidth(), top + v1.getMeasuredHeight());
            } else if (type == NEXT_LINE2) {
                View v0 = getChildAt(0);
                View v1 = getChildAt(1);
                //设置第二个view换行显示
                v0.layout(0, 0, v0.getMeasuredWidth(), v0.getMeasuredHeight());
                v1.layout(0, v0.getMeasuredHeight(), v1.getMeasuredWidth(), v0.getMeasuredHeight() + v1.getMeasuredHeight());
            }
        }
    }


    /**
     * 得到Textview绘制文字的基本信息
     *
     * @param text     Textview的文字内容
     * @param maxWidth Textview的宽度
     * @param paint    绘制文字的paint
     */
    private void initTextParams(CharSequence text, int maxWidth, TextPaint paint) {
        StaticLayout staticLayout = new StaticLayout(text, paint, maxWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        int lineCount = staticLayout.getLineCount();
        lastLineTop = staticLayout.getLineTop(lineCount - 1);
        lastLineRight = staticLayout.getLineRight(lineCount - 1);
    }

}