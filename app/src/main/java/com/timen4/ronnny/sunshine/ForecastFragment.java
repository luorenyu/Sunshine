package com.timen4.ronnny.sunshine;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecastfragment,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if(itemId==R.id.action_refresh){
            FetchWeatherTask weatherTask=new FetchWeatherTask();
            weatherTask.execute("shanghai");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.fragment_main,null);
        listView_forecast = (ListView) rootView.findViewById(R.id.listview_forecast);
        initData();
        mForecastAdapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item_forecast, R.id.list_item_forecast_textview, mWeekForecast);
        listView_forecast.setAdapter(mForecastAdapter);
        return rootView;
    }


    private void initData() {
        mWeekForecast=new ArrayList<String>();
        mWeekForecast.add("Today--Sunny--88/63");
        mWeekForecast.add("Tomorrow--Sunny--88/63");
        mWeekForecast.add("Weds--Cloudy--88/63");
        mWeekForecast.add("Thur--Rainny--88/63");
        mWeekForecast.add("Fri--Forggy--88/63");
        mWeekForecast.add("Sat--Sunny--88/63");
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
//                JSONObject weathTemp = (JSONObject) weatherArray.get(i);
                weatherData.setDate(weatherobj.optJSONObject(i).getString("date"));
                weatherData.setHigh(weatherobj.optJSONObject(i).getString("high"));
                weatherData.setLow(weatherobj.optJSONObject(i).getString("low"));
                weatherData.setText_day(weatherobj.optJSONObject(i).getString("text_day"));
                weatherDatas.add(weatherData);
                resultStrs[i]=weatherData.toString();
            }
            return resultStrs;
        }
    }

}
