package com.example.smartwardrobe;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;


public class ClothingItemAdapter extends BaseAdapter{
    ArrayList<String> result;
    Context context;
    ArrayList<Bitmap> imageId;
    private static LayoutInflater inflater=null;

    public ClothingItemAdapter(Activity activity, ArrayList<String> prgmNameList, ArrayList<Bitmap> prgmImages) {
        // TODO Auto-generated constructor stub
        result=prgmNameList;
        context=activity;
        imageId=prgmImages;
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return result.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public class Holder {
        TextView tv;
        ImageView img;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        Holder holder=new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.row_listview_item, null);
        holder.tv=(TextView) rowView.findViewById(R.id.textView1);
        holder.tv.setText(result.get(position));
        holder.img=(ImageView) rowView.findViewById(R.id.imageView1);
        holder.img.setImageBitmap(imageId.get(position));
        return rowView;
    }

} 