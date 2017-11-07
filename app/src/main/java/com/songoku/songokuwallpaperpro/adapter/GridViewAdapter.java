package com.songoku.songokuwallpaperpro.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.songoku.songokuwallpaperpro.R;
import com.songoku.songokuwallpaperpro.object.ObjectImage;

import java.util.List;

import static com.songoku.songokuwallpaperpro.Utils.CommonVL.ASSEST_URI;


public class GridViewAdapter extends BaseAdapter {

    private Context context;
    private int layout;
    private List<ObjectImage> objectImageList;

    public GridViewAdapter(Context context, int layout, List<ObjectImage> objectImageList) {
        this.context = context;
        this.layout = layout;
        this.objectImageList = objectImageList;
    }

    @Override
    public int getCount() {
        return objectImageList.size();
    }

    @Override
    public Object getItem(int i) {
        return objectImageList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.row_gridview, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.gvRow = convertView.findViewById(R.id.gvRow);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        try {
            if (objectImageList.get(i).isInternet()) {
                Glide.with(context).load(objectImageList.get(i).getImgThumb()).into(viewHolder.gvRow);
            } else {
                Glide.with(context).load(ASSEST_URI + objectImageList.get(i).getImgThumb()).into(viewHolder.gvRow);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return convertView;
    }

    class ViewHolder {
        ImageView gvRow;
    }
}
