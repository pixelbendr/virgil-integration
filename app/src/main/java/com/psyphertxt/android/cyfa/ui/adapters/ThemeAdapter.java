package com.psyphertxt.android.cyfa.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.psyphertxt.android.cyfa.R;
import com.psyphertxt.android.cyfa.ui.widget.Shape;
import com.psyphertxt.android.cyfa.model.Themes;

import java.util.List;

public class ThemeAdapter extends ArrayAdapter<Themes> {

    private Context context;
    private List<Themes> themes;

    public ThemeAdapter(Context context, List<Themes> themes) {
        super(context, R.layout.theme_color_item, themes);
        this.context = context;
        this.themes = themes;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.theme_color_item, parent,false);
            holder = new ViewHolder();
            holder.themeColor = (FrameLayout) convertView.findViewById(R.id.themeColor);
            holder.selectedColor = (FrameLayout) convertView.findViewById(R.id.themeSelectedColor);
            holder.imageChecked = (ImageView) convertView.findViewById(R.id.icon_check_image);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Themes themesPosition = themes.get(position);

        Shape.background(holder.themeColor,Shape.oval(3, themesPosition.getColorPrimary(), themesPosition.getColorAccent()));

       // holder.themeColor.setBackground(Shape.oval(3, themesPosition.getColorPrimary(), themesPosition.getColorAccent()));

        if (position == 4) {
            holder.selectedColor.setVisibility(View.VISIBLE);
            holder.imageChecked.setVisibility(View.VISIBLE);
        }

        return convertView;
    }

    private static class ViewHolder {
        FrameLayout themeColor;
        FrameLayout selectedColor;
        ImageView imageChecked;
    }

    public void refill(List<Themes> themes) {
        this.themes.clear();
        this.themes.addAll(themes);
        notifyDataSetChanged();

    }
}
