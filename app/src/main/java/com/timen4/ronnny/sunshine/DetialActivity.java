package com.timen4.ronnny.sunshine;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by luore on 2016/6/28.
 */
public class DetialActivity extends ActionBarActivity {

    private String detailTitle;
    private static final String LOG_TAG = DetailFragment.class.getSimpleName();

    private static final String FORECAST_SHARE_HASHTAG = " #SunshineApp";
    private String mForecastStr;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Intent intent = getIntent();
        detailTitle = (String) intent.getExtras().get("forecast");
        if (savedInstanceState==null){
            getSupportFragmentManager().beginTransaction().add(R.id.detial_container,new DetailFragment()).commit();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        if(id==R.id.action_setting){
            Intent intent=new Intent(this,SettingActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public static class DetailFragment extends Fragment{
        public DetailFragment() {
            setHasOptionsMenu(true);
        }

        @Override
        public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {

            View rootview=inflater.inflate(R.layout.fragment_detail,container,false);
            Intent intent = getActivity().getIntent();
            if(intent!=null&&intent.hasExtra("forecast")){
                String forecast = (String) intent.getExtras().get("forecast");
                TextView DetailText = (TextView) rootview.findViewById(R.id.detial_text);
                DetailText.setText(forecast);
            }
            return rootview;
        }
    }
}
