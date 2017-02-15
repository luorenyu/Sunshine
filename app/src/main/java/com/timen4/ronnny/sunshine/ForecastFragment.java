package com.timen4.ronnny.sunshine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.timen4.ronnny.sunshine.bean.WeatherData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by luore on 2016/6/26.
 */
public class ForecastFragment extends Fragment {

    private ArrayList<String> mWeekForecast;
    private ListView listView_forecast;
    private ArrayAdapter<String> mForecastAdapter;
    private HttpURLConnection urlConnection;
    private BufferedReader reader=null;
    private String forecastJsonStr;


    public ForecastFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);


    }

    @Override
    public void onResume() {
        super.onResume();
        updateWeather();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if(itemId==R.id.action_refresh){
            updateWeather();
            return true;
        }else if (itemId==R.id.action_setting){
            Intent intent=new Intent(getActivity(),SettingActivity.class);
            startActivity(intent);
            return true;
        }else if(itemId==R.id.action_map){
            openPreferredLocationInMap();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void openPreferredLocationInMap() {
        SharedPreferences sharedPrefs =
                PreferenceManager.getDefaultSharedPreferences(getActivity());
        String location = sharedPrefs.getString(
                getString(R.string.pref_location_key),
                getString(R.string.pref_location_default));

        // Using the URI scheme for showing a location found on a map.  This super-handy
        // intent can is detailed in the "Common Intents" page of Android's developer site:
        // http://developer.android.com/guide/components/intents-common.html#Maps
        Uri geoLocation = Uri.parse("geo:0,0?").buildUpon()
                .appendQueryParameter("q", location)
                .build();

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(geoLocation);

        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(getContext(),"no mapApp on your phone",Toast.LENGTH_SHORT);
            Log.d("location", "Couldn't call " + location + ", no receiving apps installed!");
        }
    }

    private void updateWeather() {
        FetchWeatherTask weatherTask=new FetchWeatherTask();
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String location= sharedPrefs.getString(getString(R.string.pref_location_key),getString(R.string.pref_location_default));
        weatherTask.execute(location);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.fragment_main,null);
        listView_forecast = (ListView) rootView.findViewById(R.id.listview_forecast);

        mForecastAdapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item_forecast, R.id.list_item_forecast_textview, new ArrayList<String>());
        listView_forecast.setAdapter(mForecastAdapter);
        listView_forecast.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String forecast=mForecastAdapter.getItem(position);
                Intent intent=new Intent(getContext(),DetialActivity.class);
                intent.putExtra("forecast",forecast);
                startActivity(intent);
            }
        });
        return rootView;
    }


    public class FetchWeatherTask extends AsyncTask<String, Void, String[]> {
        private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();
        String KEY="uhsg3eukcex5qrgr";
        String language="zh-Hans";
        String unit="c";
        String start="0";
        String days="7";

        final String FORECAST_BASE_URL="https://api.thinkpage.cn/v3/weather/daily.json?";
        final String KEY_PARAM="key";
        final String LOCATION="location";
        final String LANGUAGE_PARAM="language";
        final String UNIT_PARAM="unit";
        final String START_PARAM="start";
        final String DAYS_PARAM="days";


        // Data is fetched in Celsius by default.
        // If user prefers to see in Fahrenheit, convert the values here.
        // We do this rather than fetching in Fahrenheit so that the user can
        // change this option without us having to re-fetch the data once
        // we start storing the values in a database.
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String unitType = sharedPrefs.getString(
                getString(R.string.pref_units_key),
                getString(R.string.pref_units_metric));



        @Override
        protected String[] doInBackground(String... params) {
            if (params==null){
                return null;
            }
            try {
                Uri uribuild=Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendQueryParameter(KEY_PARAM,KEY)
                        .appendQueryParameter(LOCATION,params[0])
                        .appendQueryParameter(LANGUAGE_PARAM,language)
                        .appendQueryParameter(UNIT_PARAM,unit)
                        .appendQueryParameter(START_PARAM,start)
                        .appendQueryParameter(DAYS_PARAM,days).build();

                URL url=new URL(uribuild.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setConnectTimeout(3000);
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();

                if (inputStream==null){
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                StringBuffer buffer=new StringBuffer();
                while ((line=reader.readLine())!=null){
                    buffer.append(line+"\n");
                }

                if (buffer.length()==0){
                    return null;
                }
                forecastJsonStr = buffer.toString();
                return getWeatherDataFromJson(forecastJsonStr);

            } catch (IOException e) {
                Log.e(LOG_TAG,"Error",e);
            } catch (JSONException e) {
                Log.e(LOG_TAG,"Error",e);
            } finally {
                if (urlConnection!=null){
                    urlConnection.disconnect();
                }
                if (reader!=null){
                    try {
                        reader.close();
                    } catch (IOException e) {
                        Log.e(LOG_TAG,"Error",e);
                    }
                }

            }
            return null;

        }

        @Override
        protected void onPostExecute(String[] results) {
            if (results!=null){
                mForecastAdapter.clear();
                for (String result:results) {
                    mForecastAdapter.add(result);
                }

            }
        }

        public String[] getWeatherDataFromJson(String jsondata ) throws JSONException {
            ArrayList<WeatherData> weatherDatas=new ArrayList<WeatherData>();
            JSONObject dataStr=new JSONObject(jsondata);
            JSONArray result=dataStr.getJSONArray("results");
            JSONArray weatherobj= result.getJSONObject(0).getJSONArray("daily");
            String [] resultStrs=new String[3];
            for (int i=0;i<weatherobj.length();i++){
                WeatherData weatherData = new WeatherData();
                weatherData.setDate(weatherobj.optJSONObject(i).getString("date"));
                weatherData.setHigh(Double.parseDouble(weatherobj.optJSONObject(i).getString("high")));
                weatherData.setLow(Double.parseDouble(weatherobj.optJSONObject(i).getString("low")));
                weatherData.setText_day(weatherobj.optJSONObject(i).getString("text_day"));
                weatherDatas.add(weatherData);
                resultStrs[i]=weatherData.toString(getActivity(),unitType);
            }
            return resultStrs;
        }
    }

}
