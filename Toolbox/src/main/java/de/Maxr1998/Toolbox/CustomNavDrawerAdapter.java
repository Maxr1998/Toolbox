package de.Maxr1998.Toolbox;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CustomNavDrawerAdapter extends ArrayAdapter<String> {

    private int selectedItem;

    public CustomNavDrawerAdapter(Context context, int resourceID, String[] objects) {
        super(context, resourceID, objects);
    }

    public void selectedItem(int selectedItem) {
        this.selectedItem = selectedItem;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = super.getView(position, convertView, parent);
        Typeface RobotoBold = Typeface.createFromAsset(getContext().getAssets(), "fonts/Roboto-Bold.ttf");
        Typeface RobotoLight = Typeface.createFromAsset(getContext().getAssets(), "fonts/Roboto-Light.ttf");
        ((TextView) convertView).setTypeface(position == selectedItem ? RobotoBold : RobotoLight);

        return convertView;
    }
}
