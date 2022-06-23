package com.xobyx.satfinder;


import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SearchableListDialog extends DialogFragment implements
        SearchView.OnQueryTextListener, SearchView.OnCloseListener {

    private static final String ITEMS = "items";
    private boolean BaseAdapter =false;

    public void setBaseAdapter(boolean baseAdapter) {
        BaseAdapter = baseAdapter;
    }

    class CustomAdapter extends SatSpinnerAdapter implements CompoundButton.OnCheckedChangeListener
{
    private  List<Satellite> itemsx;
    private ArrayList<Satellite> mOriginalValues;
    private ArrayList<Satellite> Fav;
    private final Object mLock;
    private mArrayFilter xFilter;
    private ArrayList<Satellite> mOriginalValues2;

    void SelectFav()
    {
        mOriginalValues2= new ArrayList<>(itemsx);
        Fav =new ArrayList<>();
        for (Satellite o : itemsx) {
            if(o.isFav)
            {
                Fav.add(o);
            }
        }
    }
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        itemsx = isChecked ? Fav : mOriginalValues2;
        notifyDataSetChanged();
        _searchView.setVisibility(isChecked?View.INVISIBLE:View.VISIBLE);


    }

    private class mArrayFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            final FilterResults results = new FilterResults();

            if (mOriginalValues == null) {
                synchronized (mLock) {
                    mOriginalValues = new ArrayList<>(itemsx);
                }
            }

            if (prefix == null || prefix.length() == 0) {
                final ArrayList<Satellite> list;
                synchronized (mLock) {
                    list = new ArrayList<>(itemsx);
                }
                results.values = list;
                results.count = list.size();
            } else {
                final String prefixString = prefix.toString().toLowerCase();

                final ArrayList<Satellite>  values;
                synchronized (mLock) {
                    values = new ArrayList<>(mOriginalValues);
                }

                final int count = values.size();
                final ArrayList<Satellite> newValues = new ArrayList<>();

                for (Satellite o : values) {
                    final String valueText = o.name.toLowerCase();

                    // First match against the whole, non-splitted value
                    if (valueText.contains(prefixString)) {
                        newValues.add(o);
                    } else {
                        final String[] words = valueText.split(" ");
                        for (String word : words) {
                            if (word.startsWith(prefixString)) {
                                newValues.add(o);
                                break;
                            }
                        }
                    }
                }

                results.values = newValues;
                results.count = newValues.size();
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            //noinspection unchecked
            itemsx = (List<Satellite>) results.values;
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }

    }
    public CustomAdapter(@NonNull Context context, @NonNull List<Satellite> objects) {
        super(context,R.layout.sat_spinner_item, R.id.sat_spinner_name_text,objects);
        this.itemsx =objects;
        mLock=new Object();
        SelectFav();

    }

    @Nullable
    @Override
    public Satellite getItem(int position) {
        return itemsx.get(position);
    }

    @Override
    public int getCount() {
        return itemsx.size();
    }

    @NonNull
    @Override
    public Filter getFilter() {
        if(xFilter==null)
            xFilter=new mArrayFilter();

        return xFilter;
    }
}
    private ArrayAdapter listAdapter;

    private Switch _switch;

    private ListView _listViewItems;

    private SearchableItem _searchableItem;

    private OnSearchTextChanged _onSearchTextChanged;

    private SearchView _searchView;

    private String _strTitle;

    private String _strPositiveButtonText;

    private DialogInterface.OnClickListener _onClickListener;

    public SearchableListDialog() {

    }

    public static SearchableListDialog newInstance(List items) {
        SearchableListDialog multiSelectExpandableFragment = new
                SearchableListDialog();

        Bundle args = new Bundle();
        args.putSerializable(ITEMS, (Serializable) items);

        multiSelectExpandableFragment.setArguments(args);

        return multiSelectExpandableFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams
                .SOFT_INPUT_STATE_HIDDEN);
        return super.onCreateView(inflater, container, savedInstanceState);
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Getting the layout inflater to inflate the view in an alert dialog.
        LayoutInflater inflater = LayoutInflater.from(getActivity());

        // Crash on orientation change #7
        // Change Start
        // Description: As the instance was re initializing to null on rotating the device,
        // getting the instance from the saved instance
        if (null != savedInstanceState) {
            _searchableItem = (SearchableItem) savedInstanceState.getSerializable("item");
        }
        // Change End

        View rootView = inflater.inflate(R.layout.searchable_list_dialog, null);
        setData(rootView);

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setView(rootView);

        String strPositiveButton = _strPositiveButtonText == null ? "CLOSE" : _strPositiveButtonText;
        alertDialog.setPositiveButton(strPositiveButton, _onClickListener);

        String strTitle = _strTitle == null ? "Select Item" : _strTitle;
        alertDialog.setTitle(strTitle);

        final AlertDialog dialog = alertDialog.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams
                .SOFT_INPUT_STATE_HIDDEN);
        return dialog;
    }

    // Crash on orientation change #7
    // Change Start
    // Description: Saving the instance of searchable item instance.
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("item", _searchableItem);
        super.onSaveInstanceState(outState);
    }
    // Change End

    public void setTitle(String strTitle) {
        _strTitle = strTitle;
    }

    public void setPositiveButton(String strPositiveButtonText) {
        _strPositiveButtonText = strPositiveButtonText;
    }

    public void setPositiveButton(String strPositiveButtonText, DialogInterface.OnClickListener onClickListener) {
        _strPositiveButtonText = strPositiveButtonText;
        _onClickListener = onClickListener;
    }

    public void setOnSearchableItemClickListener(SearchableItem searchableItem) {
        this._searchableItem = searchableItem;
    }

    public void setOnSearchTextChangedListener(OnSearchTextChanged onSearchTextChanged) {
        this._onSearchTextChanged = onSearchTextChanged;
    }

    private void setData(View rootView) {
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context
                .SEARCH_SERVICE);

        _searchView = rootView.findViewById(R.id.search);
        _searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName
                ()));
        _searchView.setIconifiedByDefault(false);
        _searchView.setOnQueryTextListener(this);
        _searchView.setOnCloseListener(this);
        _searchView.clearFocus();

        InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(Context
                .INPUT_METHOD_SERVICE);
      //  mgr.hideSoftInputFromWindow(_searchView.getWindowToken(), 0);




        _listViewItems = rootView.findViewById(R.id.listItems);
        _switch=rootView.findViewById(R.id.switch1);


        if(!BaseAdapter) {
            List<Satellite> items  = (List<Satellite>) getArguments().getSerializable(ITEMS);
            //create the adapter by passing your ArrayList data
            listAdapter = new CustomAdapter(getActivity(),items);
            //attach the adapter to the list

        }
        else
        {
            List<Transponder> items  = (List<Transponder>) getArguments().getSerializable(ITEMS);
            listAdapter = new CustomAdapter2(getActivity(), items);

        }
        _switch.setOnCheckedChangeListener((CompoundButton.OnCheckedChangeListener) listAdapter);
        _listViewItems.setAdapter(listAdapter);
        _listViewItems.setTextFilterEnabled(true);

        _listViewItems.setOnItemClickListener((parent, view, position, id) -> {
            _searchableItem.onSearchableItemClicked(listAdapter.getItem(position), position);
            getDialog().dismiss();
        });
    }

    @Override
    public boolean onClose() {
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        _searchView.clearFocus();
        return true;
    }

    @Override
    public boolean onQueryTextChange(String s) {
//        listAdapter.filterData(s);
        if (TextUtils.isEmpty(s)) {
//                _listViewItems.clearTextFilter();
            ((ArrayAdapter) _listViewItems.getAdapter()).getFilter().filter(null);
        } else {
            ((ArrayAdapter) _listViewItems.getAdapter()).getFilter().filter(s);
        }
        if (null != _onSearchTextChanged) {
            _onSearchTextChanged.onSearchTextChanged(s);
        }
        return true;
    }

    public interface SearchableItem<T> extends Serializable {
        void onSearchableItemClicked(T item, int position);
    }

    public interface OnSearchTextChanged {
        void onSearchTextChanged(String strText);
    }

    private class CustomAdapter2 extends TPAdapter implements CompoundButton.OnCheckedChangeListener {
        private List<Transponder> itemsx;
        private ArrayList<Transponder> mOriginalValues2;
        private ArrayList<Transponder> Fav;

        public CustomAdapter2(FragmentActivity activity, List<Transponder> items) {
            super((MainActivity) activity,R.layout.simple_tp,items);
            this.itemsx=items;
            SelectFav();
        }

        @Nullable
        @Override
        public Transponder getItem(int position) {
            return itemsx.get(position);
        }

        @Override
        public int getCount() {
            return itemsx.size();
        }

        void SelectFav()
        {
            mOriginalValues2= new ArrayList<>(itemsx);
            Fav =new ArrayList<>();
            for (Transponder o : itemsx) {
                if(o.fav==1)
                {
                    Fav.add(o);
                }
            }
        }
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            itemsx = isChecked ? Fav : mOriginalValues2;
            notifyDataSetChanged();
            _searchView.setVisibility(isChecked?View.INVISIBLE:View.VISIBLE);
        }

    }
}
