package com.example.android.weatherwithpreferences;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.preference.PreferenceFragmentCompat;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.weatherwithpreferences.data.WeatherPreferences;
import com.example.android.weatherwithpreferences.utils.NetworkUtils;
import com.example.android.weatherwithpreferences.utils.OpenWeatherMapUtils;
import com.example.android.weatherwithpreferences.SettingsFragment;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements ForecastAdapter.OnForecastItemClickListener, LoaderManager.LoaderCallbacks<String> {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String FORECAST_URL_KEY = "forecastUrl";
    private static final int FORECAST_LOADER_ID = 0;

    private TextView mForecastLocationTV;
    private RecyclerView mForecastItemsRV;
    private ProgressBar mLoadingIndicatorPB;
    private TextView mLoadingErrorMessageTV;
    private ForecastAdapter mForecastAdapter;
    public String previous_location = "";
    public String previous_units = "";
    boolean changedp = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Remove shadow under action bar.
        getSupportActionBar().setElevation(0);

        mForecastLocationTV = (TextView)findViewById(R.id.tv_forecast_location);
        mLoadingIndicatorPB = (ProgressBar)findViewById(R.id.pb_loading_indicator);
        mLoadingErrorMessageTV = (TextView)findViewById(R.id.tv_loading_error_message);
        mForecastItemsRV = (RecyclerView)findViewById(R.id.rv_forecast_items);

        String location = getLocation();
        previous_location = location;
        previous_units = getUnits();
        mForecastLocationTV.setText(location);
        mForecastAdapter = new ForecastAdapter(this,getBaseContext());

        mForecastLocationTV.setText(location);
//        mForecastAdapter.setAbbr(OpenWeatherMapUtils.getUnit());
        mForecastItemsRV.setAdapter(mForecastAdapter);
        mForecastItemsRV.setLayoutManager(new LinearLayoutManager(this));
        mForecastItemsRV.setHasFixedSize(true);

        getSupportLoaderManager().initLoader(FORECAST_LOADER_ID, null, this);
        doSearch();
    }

    //these don't appear to work
    public String getLocation() {
        Log.d("MAIN", "INSIDE getLocation");
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        return sharedPreferences.getString(getString(R.string.pref_location_key),
                getString(R.string.pref_location_default));
    }
    public String getUnits() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        return sharedPreferences.getString(getString(R.string.pref_sort_key),
                getString(R.string.pref_sort_default));
    }

    public void doSearch(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        String unit = getUnits();
        String location = getLocation();
        if(unit == null || unit == "") {
            Log.d(TAG, "UNITS FAILURE");
            unit = WeatherPreferences.getDefaultTemperatureUnits();
        }

        previous_location = location;
        previous_units = unit;
        mForecastLocationTV.setText(location);

        String search = OpenWeatherMapUtils.buildForecastURL(location, unit);

        Bundle argsBundle = new Bundle();
        argsBundle.putString(FORECAST_URL_KEY, search);
        getSupportLoaderManager().restartLoader(FORECAST_LOADER_ID, argsBundle, this);
    }
    @Override
    public void onForecastItemClick(OpenWeatherMapUtils.ForecastItem forecastItem) {
        Intent intent = new Intent(this, ForecastItemDetailActivity.class);
        intent.putExtra(OpenWeatherMapUtils.ForecastItem.EXTRA_FORECAST_ITEM, forecastItem);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_location:
                showForecastLocation();
                return true;
            case R.id.action_settings:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void showForecastLocation() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        Uri geoUri = Uri.parse("geo:0,0").buildUpon()
                .appendQueryParameter("q", sharedPreferences.getString("pref_location", ""))
                .build();
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, geoUri);
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        }
    }

    @Override
    public Loader<String> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<String>(this) {

            String mForecastJSON;

            @Override
            protected void onStartLoading() {
                System.out.println("In onstartLoading");
                if (mForecastJSON != null) {
                    Log.d(TAG, "AsyncTaskLoader delivering cached forecast");
                    deliverResult(mForecastJSON);
                } else {
                    mLoadingIndicatorPB.setVisibility(View.VISIBLE);
                    changedp = false;
                    forceLoad();
                }
            }

            @Override
            public String loadInBackground() {
                String forecastURL = args.getString(FORECAST_URL_KEY);
                if (forecastURL == null || forecastURL.equals("")) {
                    return null;
                }
                System.out.println("IN loadInBackground");
                Log.d(TAG, "AsyncTaskLoader loading forecast from url: " + forecastURL);

                String forecastJSON = null;
                try {

                    forecastJSON = NetworkUtils.doHTTPGet(forecastURL);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return forecastJSON;
            }

            @Override
            public void deliverResult(String forecastJSON) {
                System.out.println("In deliverResult");
                mForecastJSON = forecastJSON;
                super.deliverResult(forecastJSON);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String forecastJSON) {
        System.out.println("In onLoadFinished");
        Log.d(TAG, "AsyncTaskLoader load finished");
        mLoadingIndicatorPB.setVisibility(View.INVISIBLE);
        if (forecastJSON != null) {
            mLoadingErrorMessageTV.setVisibility(View.INVISIBLE);
            mForecastItemsRV.setVisibility(View.VISIBLE);
            ArrayList<OpenWeatherMapUtils.ForecastItem> forecastItems = OpenWeatherMapUtils.parseForecastJSON(forecastJSON);

            mForecastAdapter.updateForecastItems(forecastItems);
        } else {
            mForecastItemsRV.setVisibility(View.INVISIBLE);
            mLoadingErrorMessageTV.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {
        System.out.println("In restart loader!");
        // Nothing to do here...
    }

    protected void onResume(){
        super.onResume();
        if(previous_location.equals(getLocation()) && previous_units.equals(getUnits())) {
            Log.d(TAG, "NO CHANGE DETECTED");
        } else {
            Log.d(TAG, "CHANGE DETECTED");
            doSearch();
        }

    }
}
