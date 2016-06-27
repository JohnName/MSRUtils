package msr.msrlibrary.base.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * adapter基类
 * Created by MSR on 2016/6/27.
 */
public abstract class MSRBaseAdapter<Item> extends BaseAdapter {

    private List<Item> data;
    private Context context;
    private LayoutInflater inflater;

    public MSRBaseAdapter(Context context, List<Item> data) {
        this.context = context;
        this.data = data;
        if (getContext() != null) {
            this.inflater = LayoutInflater.from(getContext());
        }
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return data == null ? 0 : data.size();
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return data.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return arg0;
    }

    @Override
    public abstract View getView(int position, View view, ViewGroup viewGroup);

    public List<Item> getData() {
        return data;
    }

    public Context getContext() {
        return context;
    }

    /**
     * 设置data,覆盖原有数据
     *
     * @param data
     */
    public void setData(List<Item> data) {
        this.data = data;
    }

    /**
     * 添加 data,添加到原有的data
     *
     * @param data
     */
    public void addData(List<Item> data) {
        this.data.addAll(data);
    }

    /**
     * 添加data
     *
     * @param item
     */
    public void addData(Item item) {
        this.data.add(item);
    }

    public LayoutInflater getInflater() {
        return inflater;
    }

    /**
     * 清楚数据
     */
    public void clearData() {
        if (this.data != null) {
            this.data.clear();
            this.notifyDataSetChanged();
        }
    }

}
