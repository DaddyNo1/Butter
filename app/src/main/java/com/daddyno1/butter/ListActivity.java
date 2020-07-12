package com.daddyno1.butter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ListActivity extends AppCompatActivity {

    ListView listView;
    List data = new ArrayList();

    {
        data.add("1");
        data.add("2");
        data.add("3");
        data.add("4");
        data.add("5");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        listView = findViewById(R.id.lst);
        listView.setAdapter(new MyAdapter(data, this));
    }

    public class MyAdapter extends BaseAdapter{

        List data;
        Context context;

        public MyAdapter(List data, Context context){
            this.data = data;
            this.context = context;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            String str = (String) data.get(position);
            View view = LayoutInflater.from(context).inflate(android.R.layout.activity_list_item, null);
            TextView textView = view.findViewById(android.R.id.text1);
            textView.setText(str);
            return view;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            this.finish();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
}
