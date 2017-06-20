
package com.example.zwr.hookviewlistenerdemo.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.example.zwr.hookviewlistenerdemo.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhongwr
 * @Function: TODO ADD FUNCTION.
 */
public class ContentAdapter extends BaseAdapter {
    private static final String TAG = "ContentAdapter";
    private Context context;
    private List<String> strList;


    public ContentAdapter(Context context, List<String> strList) {
        super();
        this.context = context;
        this.strList = strList;
    }

    public void addAll(List<String> strList) {
        if (null != strList && !strList.isEmpty()) {
            this.strList.addAll(strList);
            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        return strList.size();
    }

    @Override
    public Object getItem(int position) {
        return strList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.listview_item, null);
            holder = new ViewHolder();
            holder.tvContent = (TextView) convertView.findViewById(R.id.tv_content_item);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Log.d(TAG, "position = " + position);
        holder.tvContent.setText(strList.get(position) + " position : " + position);
        holder.tvContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "position = " + position);
            }
        });
        return convertView;
    }

    class ViewHolder {
        TextView tvContent;
    }

}

