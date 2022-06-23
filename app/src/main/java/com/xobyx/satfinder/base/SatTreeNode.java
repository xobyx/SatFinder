package com.xobyx.satfinder.base;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import com.unnamed.b.atv.model.TreeNode;
import com.xobyx.satfinder.*;

public class SatTreeNode extends TreeNode.BaseNodeViewHolder<Satellite> {

    public SatTreeNode(Context context) {
        super(context);

    }

    @Override
    public View createNodeView(TreeNode node, Satellite sat) {

        final LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(R.layout.sat_spinner_item, null, false);

        TextView nameText = view.findViewById(R.id.sat_spinner_name_text);   // id:satellite_list_name
        TextView posText = view.findViewById(R.id.sat_spinner_angle_text);   // id:satellite_list_position
        CheckBox fav = view.findViewById(R.id.fav);
        fav.setTag(sat);

        fav.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Satellite tag = (Satellite) buttonView.getTag();
            tag.isFav= buttonView.isChecked();
            DBManager.getInctance(context).update_satellite_fav(tag);

        });
        if(sat.isCapable(getLat(), getLng())) nameText.setTextColor(Color.WHITE);
        else
            nameText.setTextColor(Color.RED);
        nameText.setText(sat.name);
        posText.setText(sat.position <= 0.0f ? -sat.position / 10.0f + " " + this.context.getResources().getString(R.string.strE) : sat.position / 10.0f + " " + this.context.getResources().getString(R.string.strW));  // string:strW "W"
        fav.setChecked(sat.isFav);

        return view;

    }

    private double getLng() {
        return ((SatelliteListActivity2) context).lng;
    }

    private double getLat() {
        return ((SatelliteListActivity2) context).lat;
    }


}
