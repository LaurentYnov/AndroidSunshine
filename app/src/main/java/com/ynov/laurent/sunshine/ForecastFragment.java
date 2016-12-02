package com.ynov.laurent.sunshine;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.ynov.laurent.sunshine.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ForecastFragment extends Fragment implements WeatherFetcherListener {


    ArrayAdapter<String> mForecastAdapter;

    public ForecastFragment() {

    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

       View rootView = inflater.inflate(R.layout.forecast_fragment, container, false);

        mForecastAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.list_item_forecast,
                R.id.list_item_forecast_textview,
                new ArrayList<String>());


        ListView listView = (ListView) rootView.findViewById(R.id.list_view_forecast);
        listView.setAdapter(mForecastAdapter);

        downloadDatas();
        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void didGetData(String[] datas) {
        if(datas != null) {
            mForecastAdapter.clear();
            mForecastAdapter.addAll(datas);
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_menu,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.action_refresh) {
            downloadDatas();
        }

        return super.onOptionsItemSelected(item);
    }


    private void downloadDatas () {
        new WeatherFetcher(this).execute("Bordeaux");
    }
}
