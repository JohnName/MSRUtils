package msr.msrutils.test_temp;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import msr.msrlibrary.base.adapter.MSRBaseAdapter;
import msr.msrutils.R;

/**
 * Created by MSR on 2016/6/27.
 */

public class Test_GirdDropDownAdapter extends MSRBaseAdapter<String> {
    private int checkItemPosition = 0;

    public void setCheckItem(int position) {
        checkItemPosition = position;
        notifyDataSetChanged();
    }

    public Test_GirdDropDownAdapter(Context context, List<String> data) {
        super(context, data);
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view != null) {
            viewHolder = (ViewHolder) view.getTag();
        } else {
            view = getInflater().from(getContext()).inflate(R.layout.test_item_list_drop_down, null);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        }
        fillValue(position, viewHolder);
        return view;
    }

    private void fillValue(int position, ViewHolder viewHolder) {
        viewHolder.mText.setText(getData().get(position));
        if (checkItemPosition != -1) {
            if (checkItemPosition == position) {
                viewHolder.mText.setTextColor(getContext().getResources().getColor(R.color.drop_down_selected));
                viewHolder.mText.setCompoundDrawablesWithIntrinsicBounds(null, null, getContext().getResources().getDrawable(R.mipmap.drop_down_checked), null);
            } else {
                viewHolder.mText.setTextColor(getContext().getResources().getColor(R.color.drop_down_unselected));
                viewHolder.mText.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
            }
        }
    }

    static class ViewHolder {
        @Bind(R.id.text)
        TextView mText;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
