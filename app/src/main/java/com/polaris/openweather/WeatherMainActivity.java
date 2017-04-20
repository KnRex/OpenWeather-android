package com.polaris.openweather;

import android.Manifest;
import android.app.SearchManager;
import android.content.Context;
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
import android.support.v7.widget.SearchView;
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
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.polaris.model.WeatherDetail;
import com.polaris.model.WeatherList;
import com.polaris.service.ImageLoaderService;
import com.polaris.service.WeatherService;
import com.polaris.utils.WeatherAdapter;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class WeatherMainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, LocationListener, GoogleApiClient.OnConnectionFailedListener {

    private ListView weatherDetailListView;

    private ArrayList<WeatherList> weatherListData;

    private TextView cityLbl;

    private TextView tempLbl;

    private TextView descriptionLbl;

    private ImageView weatherIconView;

    private static final String WEATHER_ICON_URL = "http://openweathermap.org/img/w/";

    WeatherAdapter weatherAdapter;

    GoogleApiClient mGoogleApiClient;

    LocationRequest mLocationRequest;


    /**
     * Activity view gets loaded,
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_main);
        weatherDetailListView = (ListView) findViewById(R.id.weatherDetailList);
        LayoutInflater myinflater = getLayoutInflater();
        ViewGroup myHeader = (ViewGroup) myinflater.inflate(R.layout.weather_detail_header, null);
        weatherDetailListView.addHeaderView(myHeader);
        weatherListData = new ArrayList<>();

        cityLbl = (TextView) myHeader.findViewById(R.id.cityLbl);
        tempLbl = (TextView) myHeader.findViewById(R.id.weatherDegree);
        descriptionLbl = (TextView) myHeader.findViewById(R.id.weatherDescription);
        weatherIconView = (ImageView) myHeader.findViewById(R.id.weatherIconView);


        // Weather api call
        WeatherService weatherService = new WeatherService();
        weatherService.getWeatherDetailForCity(this, "london", new WeatherService.WeatherServiceCallbacks() {
            @Override
            public void onSuccessResponse(WeatherDetail weatherDetail) {

                loadWeatherDetail(weatherDetail);
            }

            @Override
            public void onFailure() {


            }
        });
    }


    /**
     * loads weather detail into the listview
     *
     * @param weatherDetail
     */
    private void loadWeatherDetail(WeatherDetail weatherDetail) {

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

            setTitle(weatherDetail.getName() + ", " + weatherDetail.getSys().getCountry());
            descriptionLbl.setText(weatherDetail.getWeather().get(0).getDescription());

            String iconUrl = WEATHER_ICON_URL + weatherDetail.getWeather().get(0).getIcon() + ".png";

            ImageLoaderService imageLoaderService = new ImageLoaderService();
            imageLoaderService.loadImage(this, iconUrl, weatherIconView);

            if (weatherAdapter == null) {
                weatherAdapter = new WeatherAdapter(this, R.layout.weather_list_row, weatherListData);
                weatherDetailListView.setAdapter(weatherAdapter);
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
        MenuItem searchMenuItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) searchMenuItem.getActionView();

        searchView.setSearchableInfo(searchManager.
                getSearchableInfo(getComponentName()));
        searchView.setSubmitButtonEnabled(true);
        // searchView.setOnQueryTextListener(this);
        return true;
    }


    protected void onStart() {
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }


    /**
     * Get the user location which acquired ny wifi/cell tower
     */
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
    }


    /**
     * Requesting Location Updates
     */
    protected void startLocationUpdates() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            askForPermission(Manifest.permission.ACCESS_COARSE_LOCATION,100);

        } else {
            createLocationRequest();
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
        }

    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        startLocationUpdates();

    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        mGoogleApiClient.connect();

    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    protected void stopLocationUpdates() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected())
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);
    }


    /**
     * Location callback for receiving lat & long
     *
     * @param location
     */
    @Override
    public void onLocationChanged(Location location) {

        Geocoder gcd = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = gcd.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addresses.size() > 0)
            System.out.println(addresses.get(0).getLocality());

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i("aafas", "GoogleApiClient connection failed: " + connectionResult.toString());
        if (!connectionResult.hasResolution()) {
            // show the localized error dialog.
            GoogleApiAvailability.getInstance().getErrorDialog(this, connectionResult.getErrorCode(), 0).show();
            return;
        }

    }

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

}

