package com.xobyx.satfinder;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import androidx.fragment.app.FragmentActivity;
import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;
import com.xobyx.satfinder.base.ChannelBase;
import com.xobyx.satfinder.base.SatTreeNode;

import java.util.ArrayList;
import java.util.List;

public class SatelliteListActivity2 extends FragmentActivity implements PopupMenu.OnMenuItemClickListener, TreeNode.TreeNodeClickListener, TreeNode.TreeNodeLongClickListener {
    private final String name;
    private SatelliteListAdapter satelliteListAdapter;


    private int mGroup;
    private int mChild;
    private List<Satellite> SatelliteList;
    private int mid;
    public double lat;
    public double lng;
    private int lastTP;

    public SatelliteListActivity2() {
        this.name = "Satellite";
    }

    @Override  // android.app.Activity
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 16) {

            Intent v1 = new Intent();
            v1.putExtra("sat_pos", -1);
            v1.putExtra("tp_pos", -1);
            this.setResult(3, v1);
        }
    }


    @Override
    public boolean onLongClick(TreeNode node, Object value) {
        PopupMenu popupMenu = new PopupMenu(this, node.getViewHolder().getView());
        popupMenu.getMenuInflater().inflate(R.menu.menu, popupMenu.getMenu());  // menu:menu
        popupMenu.setOnMenuItemClickListener(this);
        mid = ((Satellite) value).satelite_id;
        popupMenu.show();
        return true;
    }

    @Override  // android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.vb);   // layout:satellite_main_list
        ViewGroup f = findViewById(R.id.mmmm);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if(extras!=null&&intent.hasExtra("lat")) {

            lat = extras.getDouble("lat", 0);
            lng = extras.getDouble("lng");
        }
        TreeNode root = TreeNode.root();

        TreeNode satellites = new TreeNode(new ML.vb("Satellites")).setViewHolder(new ML(this));
        root.addChild(satellites);
        AndroidTreeView tView = new AndroidTreeView(this, root);

        tView.setDefaultContainerStyle(R.style.TreeNodeStyleCustom);
        f.addView(tView.getView());


        new loaderm(this, tView, satellites).execute();
        //TreeNode computerRoot = new TreeNode("Satellites");


        //this.imageView = this.findViewById(R.id.sat_add_button);   // id:sat_add_button
        // this.satelliteListAdapter = new SatelliteListAdapter(this);

        // this.imageView.setOnClickListener(this);
    }

    @Override  // android.app.Activity
    protected void onDestroy() {
        super.onDestroy();
        this.satelliteListAdapter = null;
    }

    public void onTransponderClick(Transponder n, TreeNode transNode) {

        Intent intent = new Intent();
        int i1 = SatelliteList.indexOf(transNode.getParent().getValue());
        int i = SatelliteList.get(i1).mTransponders.indexOf(n);
        intent.putExtra("sat_pos", i1);
        intent.putExtra("tp_pos", i);
      /*  for (int i = 0; i < SatelliteList.size(); i++) {
            if(SatelliteList.get(i).satelite_id==n.satellite_id)
            {

                break;
            }

        }*/


        this.setResult(3, intent);
        this.finish();


    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.menu_delete: {  // id:menu_delete
                break;
            }
            case R.id.menu_edit: {  // id:menu_edit

                Intent intent = new Intent(this, SatEditActivity.class);
                intent.putExtra("Satellite", mid);
                this.startActivityForResult(intent, 16);
                return false;
            }
            default: {
                return false;
            }
        }

        Satellite satellite = this.satelliteListAdapter.SatelliteList.get(this.mGroup);
        if (this.mChild == -1) {
            this.satelliteListAdapter.SatelliteList.remove(this.mGroup);
            DBManager.getInctance(this).DeleteSatellite_db(satellite);
            this.satelliteListAdapter.notifyDataSetChanged();

            Intent intent = new Intent();
            intent.putExtra("sat_pos", -1);
            intent.putExtra("tp_pos", -1);
            this.setResult(3, intent);

        } else {
            if (satellite.mTransponders.size() <= 1) {
                return false;
            }

            Transponder transponder = satellite.mTransponders.get(this.mChild);
            satellite.mTransponders.remove(this.mChild);
            DBManager.getInctance(this).DeleteTransponder_Channels(transponder);
            this.satelliteListAdapter.notifyDataSetChanged();

            Intent intent = new Intent();
            intent.putExtra("sat_pos", -1);
            intent.putExtra("tp_pos", -1);
            this.setResult(3, intent);
        }

        return false;
    }

    @Override
    public void onClick(TreeNode satNode, Object sat) {
        if (satNode.size() == 0 ) {
            Satellite v = (Satellite) sat;
            v.mTransponders = new ArrayList<>();
            for (Transponder tran : DBManager.getInctance(this).GetTransponderList(v.satelite_id)) {
                v.mTransponders.add(tran);
                TreeNode childNode1 = new TreeNode(tran).setViewHolder(new TransponderTreeNode(this))
                        .setClickListener((transNode, trans) -> {
                            if (transNode.size() == 0) {
                                Transponder b = (Transponder) trans;
                                List<ChannelBase> channels = DBManager.getInctance(SatelliteListActivity2.this).getTransponderChannels(b.tpId);
                                if ((channels != null && !channels.isEmpty()) && lastTP!=transNode.getId()) {
                                    lastTP=transNode.getId();
                                    for (ChannelBase channel : channels) {
                                        transNode.addChild(new TreeNode(channel).setViewHolder(new ChannelTreeNode(this)));
                                    }
                                } else {
                                    onTransponderClick(((Transponder) trans), transNode);
                                }
                            }
                        });

                satNode.addChild(childNode1);
            }
        }
    }

    private static class TransponderTreeNode extends TreeNode.BaseNodeViewHolder<Transponder> {
        public TransponderTreeNode(Context context) {
            super(context);
        }

        @Override
        public View createNodeView(TreeNode node, Transponder value) {
            final LayoutInflater inflater = LayoutInflater.from(context);
            final View view = inflater.inflate(R.layout.simple_tp, null, false);

            TextView nameText = view.findViewById(R.id.tp_text);   // id:satellite_list_name

            CheckBox fav = view.findViewById(R.id.fav2);
            fav.setTag(value);

            fav.setOnCheckedChangeListener((buttonView, isChecked) -> {
                Transponder tag = (Transponder) buttonView.getTag();
                tag.fav = buttonView.isChecked() ? 1 : 0;
                DBManager.getInctance(context).UpdateTransponder_fav(tag);

            });
            nameText.setText(value.toString());

            fav.setChecked(value.fav == 1);

            return view;
        }
    }

    private static class ChannelTreeNode extends TreeNode.BaseNodeViewHolder<ChannelBase> {
        public ChannelTreeNode(Context context) {
            super(context);
        }

        @Override
        public View createNodeView(TreeNode node, ChannelBase obj) {
            final LayoutInflater inflater = LayoutInflater.from(context);
            final View view = inflater.inflate(R.layout.transponder_channel, null, false);

            TextView txt_channel_name = view.findViewById(R.id.transponder_list_channel);// id:transponder_list_channel
            ImageView img_iv_channel_tv = view.findViewById(R.id.iv_channel_tv1);   // id:iv_channel_tv
            ImageView img_channel_type = view.findViewById(R.id.iv_channel_type1);   // id:iv_channel_type

            txt_channel_name.setText(obj.getChannelName());
            img_channel_type.setVisibility(0);
            if (obj.isPaidChannel()) {
                img_channel_type.setVisibility(View.VISIBLE);
                img_channel_type.setImageResource(R.drawable.ic_channel_dollers);   // drawable:ic_channel_dollers
            } else {
                img_channel_type.setVisibility(View.INVISIBLE);
            }

            if (obj.isTvChannel()) {
                img_iv_channel_tv.setImageResource(R.drawable.ic_channel_tv);   // drawable:ic_channel_tv
                img_iv_channel_tv.setVisibility(View.VISIBLE);
                return view;
            }

            img_iv_channel_tv.setImageResource(R.drawable.ic_channel_radio);   // drawable:ic_channel_radio
            return view;
        }
    }

    private static class ML extends TreeNode.BaseNodeViewHolder<ML.vb> {
        public ML(Context activity2) {
            super(activity2);
        }

        @Override
        public View createNodeView(TreeNode node, vb value) {
            final LayoutInflater inflater = LayoutInflater.from(context);
            final TextView view = (TextView) inflater.inflate(R.layout.simple_center, null, false);
            view.setText(value.name);
            return view;
        }

        static class vb {
            String name;

            public vb(String s) {
                name = s;
            }
        }
    }

    class loaderm extends AsyncTask<Object, TreeNode, ArrayList<TreeNode>> {
        private final AndroidTreeView view;
        SatelliteListActivity2 context;
        private TreeNode holder;

        public loaderm(SatelliteListActivity2 pcontext, AndroidTreeView view, TreeNode x) {
            context = pcontext;
            this.view = view;
            holder = x;
            view.setDefaultAnimation(true);
            view.expandNode(this.holder);

        }

        @Override
        protected ArrayList<TreeNode> doInBackground(Object... objects) {
            ArrayList<TreeNode> nodes = new ArrayList<>();
            context.SatelliteList = DBManager.getInctance(context).GetAllSatellites();
            for (Satellite sat : SatelliteList) {
                TreeNode childNode = new TreeNode(sat)
                        .setViewHolder(new SatTreeNode(context))
                        .setClickListener(context).setLongClickListener(context);

                nodes.add(childNode);
                this.publishProgress(childNode);

            }
            return nodes;
        }

        @Override
        protected void onProgressUpdate(TreeNode[] values) {

            holder.addChild(values[0]);


        }

        @Override
        protected void onPostExecute(ArrayList<TreeNode> treeNodes) {
            //root.addChildren(treeNodes);
            view.expandNode(this.holder);
        }
    }
}

