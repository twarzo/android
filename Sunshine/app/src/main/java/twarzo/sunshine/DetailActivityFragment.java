package twarzo.sunshine;

import android.app.LoaderManager;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.ShareActionProvider;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.w3c.dom.Text;


/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment implements android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = DetailActivityFragment.class.getSimpleName();
    private static final String FORECAST_SHARE_HASHTAG = "#SunshineApp";
    private static final int DETAIL_LOADER = 0;
    public static DETAIL_URI;
    public String mLocation;
    public ImageView mIconView;
    public TextView mDateView;
    public TextView mFriendlyDateView;
    public TextView mDescriptionView;
    public TextView mHighTempView;
    public TextView mLowTempView;
    public TextView mHumidityView;
    public TextView mWindView;
    public TextView mPressureView;

    private static final String[] DETAIL_COLUMNS = {
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
            WeatherContract.WeatherEntry.COLUMN_PRESSURE,
            WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
            WeatherContract.WeatherEntry.COLUMN_DEGREES,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
            WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING
    };
    private static final int COL_WEATHER_ID = 0;
    private static final int COL_WEATHER_DATE = 1;
    private static final int COL_WEATHER_DESC = 2;
    private static final int COL_WEATHER_MAX_TEMP = 3;
    private static final int COL_WEATHER_MIN_TEMP = 4;
    private static final int COL_WEATHER_HUMIDITY = 5;
    private static final int COL_WEATHER_PRESSURE = 6;
    private static final int COL_WEATHER_WIND_SPEED = 7;
    private static final int COL_WEATHER_DEGREES = 8;
    private static final int COL_WEATHER_WEATHER_ID = 9;
    private static final int COL_LOCATION_SETTING = 10;
    private String mForecast;
    private ShareActionProvider mShareActionProvider;
    public DetailActivityFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        if(savedInstanceState == null){
            Bundle bundle = new Bundle();
            bundle.putParcelable(DetailActivityFragment.DETAIL_URI, getActivity().getIntent().getData());
            DetailActivityFragment detailActivityFragment = new DetailActivityFragment();
            detailActivityFragment.setArguments(bundle);
            getFragmentManager().beginTransaction().add(R.id.weather_detail_container, detailActivityFragment).commit();
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if(arguments != null){
            mUri = arguments.getParcelable(DetailActivityFragment.DETAIL_URI);
        }
        Intent intent = getActivity().getIntent();
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        mIconView = (ImageView) rootView.findViewById(R.id.detail__icon);
        mDateView = (TextView) rootView.findViewById(R.id.detail_date_textview);
        mFriendlyDateView = (TextView) rootView.findViewById(R.id.detail_day_textview);
        mDescriptionView = (TextView) rootView.findViewById(R.id.detail_forecast_textview);
        mHighTempView = (TextView) rootView.findViewById(R.id.detail_high_textview);
        mLowTempView = (TextView) rootView.findViewById(R.id.detail_low_textview);
        mHumidityView = (TextView) rootView.findViewById(R.id.detail_humidity_textview);
        mWindView = (TextView) rootView.findViewById(R.id.detail_wind_textview);
        mPressureView = (TextView) rootView.findViewById(R.id.detail_pressure_textview);

        return rootView;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, savedInstanceState, this);
        super.onActivityCreated(savedInstanceState);
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater){
        menuInflater.inflate(R.menu.detailfragment, menu);
        MenuItem menuItem = menu.findItem(R.id.action_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
        if(mShareActionProvider != null){
            mShareActionProvider.setShareIntent(createShareForecastIntent());
        }
        else{
            Log.d(LOG_TAG, "Share Action provider is null?");
        }
    }
    private Intent createShareForecastIntent(){
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT,mForecast+FORECAST_SHARE_HASHTAG);
        return shareIntent;

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args){
        Log.v(LOG_TAG, "In onCreateLoader");
        Intent intent = getActivity().getIntent();
        Bundle extra = intent.getExtras();
        String date = extra.getString("forecast_date");
        mLocation = Utility.getPreferredLocation(getActivity());
        Uri weatherForLocationUri = WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(
                mLocation, date);
        //Log.v("here", uri.toString());
        if( intent == null){
            return null;
        }
        Log.v(LOG_TAG, "here2");
        return new CursorLoader(getActivity(), weatherForLocationUri,DETAIL_COLUMNS, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.v(LOG_TAG, "In onLoadFinished");
        if (!data.moveToFirst()) {
            return;
        }
        int weatherId = data.getInt(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_WEATHER_ID));
        long date = data.getLong(COL_WEATHER_DATE);
        String friendlyDateText = Utility.getDayName(getActivity(), date);
        String dateText = Utility.getFormattedMonthDay(getActivity(), date);
        String dateString = Utility.formatDate(
                data.getLong(COL_WEATHER_DATE));

        String weatherDescription =
                data.getString(COL_WEATHER_DESC);

        boolean isMetric = Utility.isMetric(getActivity());

        String high = Utility.formatTemperature(getActivity().getBaseContext(),
                data.getDouble(COL_WEATHER_MAX_TEMP), isMetric);

        String low = Utility.formatTemperature(getActivity().getBaseContext(),
                data.getDouble(COL_WEATHER_MIN_TEMP), isMetric);

        float humidity = data.getFloat(COL_WEATHER_HUMIDITY);
        float windStr = data.getFloat(COL_WEATHER_WIND_SPEED);
        float windDirStr = data.getFloat(COL_WEATHER_DEGREES);
        float pressure = data.getFloat(COL_WEATHER_PRESSURE);
        int imageResource = Utility.getIconResourceForWeatherCondition(weatherId);
        mWindView.setText(Utility.getFormattedWind(getActivity(),windStr, windDirStr));
        mPressureView.setText(String.format("Pressure %s: hPa",pressure));

        mFriendlyDateView.setText(friendlyDateText);
        mDateView.setText(dateText);
        mIconView.setImageResource(imageResource);
        mHighTempView.setText(high);
        mLowTempView.setText(low);
        mHumidityView.setText(String.format("Humidity : %s %% ", humidity));

        mForecast = String.format("%s - %s - %s/%s", dateString, weatherDescription, high, low);

        //TextView detailTextView = (TextView)getView().findViewById(R.id.detail_text);
        //detailTextView.setText(mForecast);

        // If onCreateOptionsMenu has already happened, we need to update the share intent now.
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(createShareForecastIntent());
        }
    }
    @Override
    public void onLoaderReset(Loader<Cursor> loader){

    }
    void onLocationChanged(String location ) {
        FetchWeatherTask weatherTask = new FetchWeatherTask(getActivity().getBaseContext());
        weatherTask.execute(location);
        getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
    }
}
