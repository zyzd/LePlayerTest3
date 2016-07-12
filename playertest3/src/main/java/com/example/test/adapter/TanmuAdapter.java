package com.example.test.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.test.bean.ChatMessage;
import com.test.yiya.R;

import java.util.ArrayList;

/**
 * Created by 韩瑞峰 on 2016/6/23.
 */
public class TanmuAdapter extends BaseAdapter {
    private Context context;
    ArrayList<ChatMessage> danmaList = new ArrayList<ChatMessage>();
    public TanmuAdapter(ArrayList<ChatMessage> danmaList, Context context) {
        this.danmaList = danmaList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return danmaList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View itemView = View.inflate(context, R.layout.item_danma,null);
        TextView tvContent = (TextView) itemView.findViewById(R.id.tv_item_content);

        tvContent.setText(danmaList.get(i).getMsg());
        return itemView;
    }
}
