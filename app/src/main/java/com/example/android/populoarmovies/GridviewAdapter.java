package com.example.android.populoarmovies;

import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class GridviewAdapter extends BaseAdapter {
    private Context context;
    private String[] items;
    private int numColumn;
    public GridviewAdapter(Context context, String[] items) {
        super();
        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.length;
    }

    //---returns the ID of an item---
    public Object getItem(int position) {
        return items[position];
    }

    //---returns the ID of an item---
    public long getItemId(int position) {
        return position;
    }

    //---returns an ImageView view---
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;

        int imageWidth = (int) (width / numColumn);
        if (convertView == null) {
            imageView = new ImageView(context);
            convertView = imageView;
//                imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
//                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//                imageView.setPadding(5, 5, 5, 5);
        } else {
            imageView = (ImageView) convertView;
        }
        Picasso.with(context)
                .load(items[position])
                .placeholder(R.drawable.placeholder)
                .fit()
                .noFade()
                .into(imageView);

//            imageView.setResource(items[position]);
//            return imageView;
        return convertView;
    }
}