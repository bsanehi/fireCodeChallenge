package com.fire.baz.firecodechallenge;

import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.fire.baz.firecodechallenge.models.WeatherData;
import com.fire.baz.firecodechallenge.network.ServerRequests;
import com.fire.baz.firecodechallenge.utilities.Constants;
import com.fire.baz.firecodechallenge.utilities.SystemUtilSingleton;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.IOException;

import im.delight.android.location.SimpleLocation;
import okhttp3.ResponseBody;
import retrofit2.Response;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class MainActivity extends AppCompatActivity {

    private CompositeSubscription subscriptions;

    private SimpleLocation location;

    private static WeatherData weatherData;

    double latitude, longitude;

    TextView tv_temp, tv_location_name, tv_date, tv_max_temp, tv_min_temp, tv_wind_speed, tv_humidity, tv_sunrise, tv_sunset;

    ImageView weatherIcon;

    ProgressBar pro1, pro2, pro3;

    LinearLayout linearLayout1, linearLayout2, linearLayout3;

    private static final int REQUEST_LOCATION = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // for retrofit
        subscriptions = new CompositeSubscription();

        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        // construct a new instance of SimpleLocation -- update every 10 minutes
        location = new SimpleLocation(this, true, true);

        initializeViews();


        // after screen rotation set data again
        if (weatherData != null) {
            setViewData(weatherData);
        }

        // if we can't access the location yet
        if (!location.hasLocationEnabled()) {
            // ask the user to enable location access
            SimpleLocation.openSettings(this);
        }


        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {

            location.beginUpdates();
        }

        latitude = location.getLatitude();
        longitude = location.getLongitude();

        // get weather data
        getWeatherData(latitude, longitude);

        // update on location change
        location.setListener(() -> {

            // new location data has been received and can be accessed
            latitude = location.getLatitude();
            longitude = location.getLongitude();

            refresh();

            // get weather data
            getWeatherData(latitude, longitude);

        });


        findViewById(R.id.btn_refresh).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                                PackageManager.PERMISSION_GRANTED) {

                    location.beginUpdates();
                } else {
                    ActivityCompat.requestPermissions(getParent(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
                }


                latitude = location.getLatitude();
                longitude = location.getLongitude();

                refresh();

                // get weather data
                getWeatherData(latitude, longitude);

            }

        });

    }




    private void initializeViews() {

        tv_temp = findViewById(R.id.tv_temp_num);
        tv_location_name = findViewById(R.id.tv_locationName);
        tv_date = findViewById(R.id.tv_date);
        tv_max_temp = findViewById(R.id.tv_max_temp);
        tv_min_temp = findViewById(R.id.tv_min_temp);
        tv_wind_speed = findViewById(R.id.tv_wind_speed);
        tv_humidity = findViewById(R.id.tv_humidity);

        weatherIcon = findViewById(R.id.weatherIcon);

        tv_sunrise = findViewById(R.id.tv_sunrise);
        tv_sunset = findViewById(R.id.tv_sunset);

        pro1 = findViewById(R.id.progress1);
        pro2 = findViewById(R.id.progress2);
        pro3 = findViewById(R.id.progress3);

        linearLayout1 = findViewById(R.id.weatherLayout);
        linearLayout2 = findViewById(R.id.weatherDetails);
        linearLayout3 = findViewById(R.id.sunriseSunsetLayout);

    }


    @Override
    protected void onResume() {
        super.onResume();

        // make the device update its location
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {

            location.beginUpdates();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();

        // stop location updates (saves battery)
        location.endUpdates();
    }

    public void getWeatherData(double lat, double lon) {

        subscriptions.add(ServerRequests.getRetrofit().getWeather("metric", lat, lon, Constants.API_TOKEN)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleGetResponse, this::handleError));

    }


    private void handleGetResponse(Response<ResponseBody> responseBodyResponse) {
        // save weather data
        try {

            String jsonString = responseBodyResponse.body().string();

            JsonParser parser = new JsonParser();
            JsonElement mJson = parser.parse(jsonString);

          //  Log.e("error", jsonString);

            Gson gson = new Gson();

            weatherData = gson.fromJson(mJson, WeatherData.class);

        } catch (IOException e) {
            e.printStackTrace();
        }

        setViewData(weatherData);

    }

    private void handleError(Throwable throwable) {
        Toast.makeText(getApplicationContext(), throwable.getMessage(),
                Toast.LENGTH_LONG).show();
    }


    // method shows progress dialog
    private void refresh() {

        linearLayout1.setVisibility(View.GONE);
        linearLayout2.setVisibility(View.GONE);
        linearLayout3.setVisibility(View.GONE);

        pro1.setVisibility(View.VISIBLE);
        pro2.setVisibility(View.VISIBLE);
        pro3.setVisibility(View.VISIBLE);

    }


    private void setViewData(WeatherData weatherData) {

        // Delay end of progress
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                pro1.setVisibility(View.GONE);
                pro2.setVisibility(View.GONE);
                pro3.setVisibility(View.GONE);

                linearLayout1.setVisibility(View.VISIBLE);
                linearLayout2.setVisibility(View.VISIBLE);
                linearLayout3.setVisibility(View.VISIBLE);
            }
        }, 500);


        // method to set weather icon
        setWeatherIcon(weatherData.getWeather().get(0).getIcon());

        // set weather temp
        tv_temp.setText(String.format("%.0f", weatherData.getMain().getTemp()));

        // set location name
        tv_location_name.setText(weatherData.getName());

        // grab the String values from strings.xml file
        String max_temp = getResources().getString(R.string.max_temp);
        String min_temp = getResources().getString(R.string.min_temp);
        String wind = getResources().getString(R.string.wind);
        String humidity = getResources().getString(R.string.humidity);

        // set weather details
        tv_max_temp.setText(max_temp + ": " + String.format("%d", weatherData.getMain().getTempMax()) + "°C");
        tv_min_temp.setText(min_temp + ": " + String.format("%d", weatherData.getMain().getTempMin()) + "°C");
        tv_wind_speed.setText(wind + ": " + weatherData.getWind().getSpeed() + " km/h");
        tv_humidity.setText(humidity + ": " + weatherData.getMain().getHumidity() + "%");

        // set current date
        tv_date.setText(SystemUtilSingleton.getInstance().getDate());

        String sunrise_text = getResources().getString(R.string.sunrise);
        String sunset_text = getResources().getString(R.string.sunset);

        String sunrise = SystemUtilSingleton.getInstance().getDate(weatherData.getSys().getSunrise());
        String sunset = SystemUtilSingleton.getInstance().getDate(weatherData.getSys().getSunset());

        tv_sunrise.setText(sunrise_text + ": " + sunrise);
        tv_sunset.setText(sunset_text + ": " + sunset);


    }


    private void setWeatherIcon(String icon) {

        switch (icon) {

            case "01d": {
                weatherIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_wi_day_sunny));
                Animation startRotateAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_animation);
                weatherIcon.startAnimation(startRotateAnimation);
                break;
            }

            case "02d": {
                weatherIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_wi_day_cloudy));
                Animation startFadeIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in_animation);
                weatherIcon.startAnimation(startFadeIn);
                break;
            }

            case "03d": {
                weatherIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_wi_cloud));
                Animation startFadeIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in_animation);
                weatherIcon.startAnimation(startFadeIn);
                break;
            }

            case "04d": {
                weatherIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_wi_cloudy));
                Animation startFadeIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in_animation);
                weatherIcon.startAnimation(startFadeIn);

                //  final Animatable animatable = (Animatable) weatherIcon.getDrawable();
                // animatable.start();
                break;
            }

            case "09d": {
                weatherIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_wi_day_showers));
                Animation startFadeIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in_animation);
                weatherIcon.startAnimation(startFadeIn);
                break;
            }

            case "10d": {
                weatherIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_wi_day_rain));
                Animation startFadeIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in_animation);
                weatherIcon.startAnimation(startFadeIn);
                break;
            }

            case "11d": {
                weatherIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_wi_day_thunderstorm));
                Animation startFadeIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in_animation);
                weatherIcon.startAnimation(startFadeIn);
                break;
            }

            case "13d": {
                weatherIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_wi_day_snow));
                Animation startFadeIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in_animation);
                weatherIcon.startAnimation(startFadeIn);
                break;
            }

            case "50d": {
                weatherIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_wi_mist));
                Animation startFadeIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in_animation);
                weatherIcon.startAnimation(startFadeIn);
                break;
            }

            case "01n": {

                //weatherIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_wi_day_sunny));
                // final Animatable animatable = (Animatable) weatherIcon.getDrawable();

                // animatable.start();

               /* Drawable d = weatherIcon.getDrawable();

                if(d instanceof Animatable){
                    Toast.makeText(this, "This is  animatorable",
                            Toast.LENGTH_LONG).show();
                    ((Animatable) d).start();
                } else {
                    Toast.makeText(this, "This is not animatorable",
                            Toast.LENGTH_LONG).show();
                }*/

                weatherIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_wi_night_clear));
                Animation startFadeIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in_animation);
                weatherIcon.startAnimation(startFadeIn);
                break;
            }

            case "02n": {
                weatherIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_wi_night_cloudy));
                Animation startFadeIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in_animation);
                weatherIcon.startAnimation(startFadeIn);
                break;
            }

            case "03n": {
                weatherIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_wi_cloud));
                Animation startFadeIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in_animation);
                weatherIcon.startAnimation(startFadeIn);
                break;
            }

            case "04n": {
                weatherIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_wi_cloudy));
                Animation startFadeIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in_animation);
                weatherIcon.startAnimation(startFadeIn);
                break;
            }

            case "09n": {
                weatherIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_wi_night_showers));
                Animation startFadeIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in_animation);
                weatherIcon.startAnimation(startFadeIn);
                break;
            }

            case "10n": {
                weatherIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_wi_night_rain));
                Animation startFadeIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in_animation);
                weatherIcon.startAnimation(startFadeIn);
                break;
            }

            case "11n": {
                weatherIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_wi_night_thunderstorm));
                Animation startFadeIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in_animation);
                weatherIcon.startAnimation(startFadeIn);
                break;
            }

            case "13n": {
                weatherIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_wi_night_snow));
                Animation startFadeIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in_animation);
                weatherIcon.startAnimation(startFadeIn);
                break;
            }

            case "50n": {
                weatherIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_wi_mist));
                Animation startFadeIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in_animation);
                weatherIcon.startAnimation(startFadeIn);
                break;
            }

            default: {

                break;
            }

        }

    }

}