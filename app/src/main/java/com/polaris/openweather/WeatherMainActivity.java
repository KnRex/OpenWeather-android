package com.polaris.openweather;

import android.Manifest;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.SuperscriptSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.polaris.model.Forecast;
import com.polaris.model.WeatherDetail;
import com.polaris.model.WeatherList;
import com.polaris.service.ForecastService;
import com.polaris.service.ImageLoaderService;
import com.polaris.service.WeatherService;
import com.polaris.utils.CommonUtils;
import com.polaris.utils.Constants;
import com.polaris.utils.HorizontalAdapter;
import com.polaris.utils.WeatherAdapter;

import org.w3c.dom.Text;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class WeatherMainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, LocationListener, GoogleApiClient.OnConnectionFailedListener, SearchView.OnQueryTextListener {

    private ListView weatherDetailListView;

    private ArrayList<WeatherList> weatherListData;


    private TextView tempLbl;

    private TextView dateLbl;

    private TextView descriptionLbl;

    private ImageView weatherIconView;


    private static final String TAG = WeatherMainActivity.class.getName();

    WeatherAdapter weatherAdapter;

    GoogleApiClient mGoogleApiClient;

    LocationRequest mLocationRequest;
    private SearchView searchView;
    private MenuItem searchMenuItem;

    private RecyclerView horizontalView;


    String city;

    /**
     * Activity view gets loaded,
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "Weather Activity Initialized");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_main);
        weatherDetailListView = (ListView) findViewById(R.id.weatherDetailList);
        LayoutInflater inflater = getLayoutInflater();
        ViewGroup header = (ViewGroup) inflater.inflate(R.layout.weather_detail_header, null);
        weatherDetailListView.addHeaderView(header);
        weatherListData = new ArrayList<>();

        ViewGroup footer = (ViewGroup) inflater.inflate(R.layout.weather_deatil_footer, null);
        weatherDetailListView.addFooterView(footer);
        horizontalView = (RecyclerView) footer.findViewById(R.id.recycler_view);

        tempLbl = (TextView) header.findViewById(R.id.weatherDegree);
        descriptionLbl = (TextView) header.findViewById(R.id.weatherDescription);
        weatherIconView = (ImageView) header.findViewById(R.id.weatherIconView);
        dateLbl = (TextView) header.findViewById(R.id.dateLbl);

        //Auto load last searched city
        if (CommonUtils.getCity(this) != null) {
            // Weather api call
            makeWeatherApiCall(CommonUtils.getCity(this));
            weatherForeCastService(CommonUtils.getCity(this));
        }
    }


    /**
     * call to fetch current weather info for the given city.
     * Save the current city to the preference.
     *
     * @param city
     */
    private void makeWeatherApiCall(final String city) {
        WeatherService weatherService = new WeatherService();
        weatherService.getWeatherDetailForCity(this, city, new WeatherService.WeatherServiceCallbacks() {
            @Override
            public void onSuccessResponse(WeatherDetail weatherDetail) {
                Log.i(TAG, "Weather info fetched");
                loadWeatherDetail(weatherDetail);
                CommonUtils.saveCity(WeatherMainActivity.this, city);
            }

            @Override
            public void onFailure(String error) {
//Show error to the user
                AlertDialog.Builder builder = new AlertDialog.Builder(WeatherMainActivity.this)
                        .setTitle("Alert!")
                        .setMessage(error)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                AlertDialog alert11 = builder.create();
                alert11.show();
            }
        });
    }


    /**
     * Method for fetching weather forecast for five days
     * Loads forecast details to horizontal view
     *
     * @param city
     */
    private void weatherForeCastService(final String city) {
        ForecastService forecastService = new ForecastService();

        forecastService.getWeatherForecastForCity(this, city, new ForecastService.ForecastServiceCallback() {
            @Override
            public void onSuccessResponse(Forecast forecast) {
                if (forecast != null) {
                    Log.i(TAG, "Weather forecast info fetched");
                    HorizontalAdapter horizontalAdapter = new HorizontalAdapter(WeatherMainActivity.this, forecast.getList());
                    LinearLayoutManager horizontalLayoutManagaer
                            = new LinearLayoutManager(WeatherMainActivity.this, LinearLayoutManager.HORIZONTAL, false);
                    horizontalView.setLayoutManager(horizontalLayoutManagaer);
                    horizontalView.setAdapter(horizontalAdapter);
                }
            }

            @Override
            public void onFailure() {
                Log.e(TAG, "Forecast API Error");
            }
        });
    }


    /**
     * loads weather detail into the listview
     *
     * @param weatherDetail
     */
    private void loadWeatherDetail(WeatherDetail weatherDetail) {

        weatherListData.clear();

        if (weatherDetail != null) {

            if (weatherDetail.getMain().getHumidity() != null) {
                WeatherList humidity = new WeatherList();
                humidity.setWeatherKey("Humidity :");
                humidity.setWeatherInfo(weatherDetail.getMain().getHumidity().toString() + " %");
                weatherListData.add(humidity);
            }

            if (weatherDetail.getMain().getPressure() != null) {
                WeatherList pressure = new WeatherList();
                pressure.setWeatherKey("Pressure :");
                pressure.setWeatherInfo(weatherDetail.getMain().getPressure().toString() + " hPa");
                weatherListData.add(pressure);
            }

            if (weatherDetail.getWind().getSpeed() != null) {
                WeatherList speed = new WeatherList();
                speed.setWeatherKey("Speed :");
                speed.setWeatherInfo(weatherDetail.getWind().getSpeed().toString() + " km/hr");
                weatherListData.add(speed);
            }

            if (weatherDetail.getVisibility() != null) {
                WeatherList visibility = new WeatherList();
                visibility.setWeatherKey("Visibility :");
                visibility.setWeatherInfo(weatherDetail.getVisibility().toString() + " km");
                weatherListData.add(visibility);
            }


            if (weatherDetail.getSys().getSunrise() != null) {
                WeatherList sunRise = new WeatherList();
                sunRise.setWeatherKey("Sunrise :");
                sunRise.setWeatherInfo(CommonUtils.getStandardTime(weatherDetail.getSys().getSunrise()));
                weatherListData.add(sunRise);
            }

            if (weatherDetail.getSys().getSunset() != null) {
                WeatherList sunSet = new WeatherList();
                sunSet.setWeatherKey("Sunset :");
                sunSet.setWeatherInfo(CommonUtils.getStandardTime(weatherDetail.getSys().getSunset()));
                weatherListData.add(sunSet);
            }

            setTitle(weatherDetail.getName() + ", " + weatherDetail.getSys().getCountry());
            descriptionLbl.setText(weatherDetail.getWeather().get(0).getDescription());

            dateLbl.setText("Today at " + CommonUtils.getStandardTime(weatherDetail.getDt()));

            String iconUrl = Constants.WEATHER_ICON_URL + weatherDetail.getWeather().get(0).getIcon() + ".png";


            tempLbl.setText(CommonUtils.convertKelvinToCelsius(weatherDetail.getMain().getTemp()) + (char) 0x00B0 + "C"

                    + " / " + CommonUtils.convertKelvinToFarenheit(weatherDetail.getMain().getTemp()) + (char) 0x00B0 + "F");


            ImageLoaderService imageLoaderService = new ImageLoaderService();
            imageLoaderService.loadImage(this, iconUrl, weatherIconView);

            if (weatherAdapter == null) {
                weatherAdapter = new WeatherAdapter(this, R.layout.weather_list_row, weatherListData);
                weatherDetailListView.setAdapter(weatherAdapter);
            } else {
                weatherAdapter.notifyDataSetChanged();
            }

        }

    }


    /**
     * Inflating action bar search menu
     *
     * @param menu
     * @return
     */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_view, menu);

        SearchManager searchManager = (SearchManager)
                getSystemService(Context.SEARCH_SERVICE);
        searchMenuItem = menu.findItem(R.id.search);
        searchView = (SearchView) searchMenuItem.getActionView();

        searchView.setSearchableInfo(searchManager.
                getSearchableInfo(getComponentName()));
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh:
                if (CommonUtils.getCity(this) != null) {
                    makeWeatherApiCall(CommonUtils.getCity(this));
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Get the user location which acquired ny wifi/cell tower
     */
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient,
                        builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
                final Status status = locationSettingsResult.getStatus();
                //final LocationSettingsStates =locationSettingsResult.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can
                        // initialize location requests here.

                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way
                        // to fix the settings so we won't show the dialog.

                        break;
                }
            }
        });

    }


    /**
     * Requesting Location Updates
     */
    protected void startLocationUpdates() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            Log.i(TAG, "Asking permission from users");
            askForPermission(Manifest.permission.ACCESS_FINE_LOCATION, 100);

        } else {
            createLocationRequest();
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
        }

    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "Google API Client Connected");
        startLocationUpdates();

    }


    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Google API Client disconnected");

    }

    /**
     * Connect to GoogleApiClient for fetching loction.
     */

    @Override
    protected void onStart() {
        super.onStart();
        if (CommonUtils.getCity(this) == null) {
            if (mGoogleApiClient == null) {
                mGoogleApiClient = new GoogleApiClient.Builder(this)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .addApi(LocationServices.API)
                        .build();
            }
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient != null) {
            stopLocationUpdates();
            mGoogleApiClient.disconnect();
        }
    }


    protected void stopLocationUpdates() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);
        }

    }


    /**
     * Location callback for receiving lat & long
     *
     * @param location
     */
    @Override
    public void onLocationChanged(Location location) {
        stopLocationUpdates();
        try {
            Log.i(TAG, "Got Users current location ");
            Geocoder gcd = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = null;
            try {
                addresses = gcd.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (addresses.size() > 0) {
                Log.d(TAG, "Current City is, " + addresses.get(0).getLocality());
                makeWeatherApiCall(addresses.get(0).getLocality());
                weatherForeCastService(addresses.get(0).getLocality());

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * @param connectionResult
     */

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (!connectionResult.hasResolution()) {
            // show the localized error dialog.
            GoogleApiAvailability.getInstance().getErrorDialog(this, connectionResult.getErrorCode(), 0).show();
            return;
        }

    }

    /**
     * ask location permission to the user for android version 6.0 and above
     *
     * @param permission
     * @param requestCode
     */

    private void askForPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {

                //This is called if user has denied the permission before
                //In this case I am just asking the permission again
                ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);

            } else {

                ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
            }
        } else {
            Toast.makeText(this, "" + permission + " is already granted.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Permission callback for android 6.0 and above
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (ActivityCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
                //Location
                case 100:
                    createLocationRequest();
                    LocationServices.FusedLocationApi.requestLocationUpdates(
                            mGoogleApiClient, mLocationRequest, this);
                    break;
            }
            Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * callback for search views. Service call made after user entered text.
     *
     * @param query
     * @return
     */
    @Override
    public boolean onQueryTextSubmit(String query) {
        makeWeatherApiCall(query);
        weatherForeCastService(query);
        if (searchView.isShown()) {
            searchMenuItem.collapseActionView();
            searchView.setQuery("", false);
        }
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }
}

