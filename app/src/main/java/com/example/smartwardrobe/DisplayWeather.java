package com.example.smartwardrobe;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;

public class DisplayWeather extends AppCompatActivity {

    String j1 = null;
    String j2 = null;
    String key;
    String locationName;
    TextView weatherReport;
    TextView cityName;
    LocationManager locationManager;
    ListView viewOutfit;

    public  String[] SHOE = {"Boots", "Shoes"};
    public  String[] BOTTOM = {"Chinos", "Culottes", "Cutoffs", "Flannel", "Jeans", "Jeggings", "Joggers",
            "Leggings", "Shorts", "Skirt", "Sweatpants", "Sweatshorts", "Trunks"};
    public String[] TOP = {"Blouse", "Button-Down", "Cardigan", "henley", "Jersey", "Sweater", "Tank", "Tee",
            "Top", "Hoodie"};
    public  String[] TOPPER = {"Anarak", "Blazer", "Bomber", "Coat", "Jacket", "Parka"};

    public ArrayList<String> availableCategories = new ArrayList<>();
    public ArrayList<String> availableShoes = new ArrayList<>();
    public ArrayList<String> availableBottoms = new ArrayList<>();
    public ArrayList<String> availableTops = new ArrayList<>();
    public ArrayList<String> availableToppers = new ArrayList<>();
    public ArrayList<ClothingItem> outfit = new ArrayList<>();
    public ArrayList<String> outfitNames = new ArrayList<>();
    public ArrayList<Bitmap> outfitBitmaps = new ArrayList<>();


    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };
    Location location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_weather);
        weatherReport = findViewById(R.id.weatherDetails);
        viewOutfit = findViewById(R.id.OutfitView);
        cityName = findViewById(R.id.cityName);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        } else {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if (location != null) {
                onLocationChanged(location);
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 20000, 10, locationListener);
            checkWeather(location);
        }

    }

    public void onLocationChanged(Location location) {
        this.location = location;
    }

    public void checkWeather(Location location) {


        DownloadTask task = new DownloadTask();

        try {
            String lat = String.valueOf(location.getLatitude());
            String lon = String.valueOf(location.getLongitude());
            String loc = lat + "," + lon;
            j1 = task.execute("https://dataservice.accuweather.com/locations/v1/cities/geoposition/search?q=" + loc + "&apikey=P0smdqeg9oN1aiPYjt8KiukuMioFHEtX").get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }

    public class DownloadTask extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... urls) {

            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try {

                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while (data != -1) {

                    char current = (char) data;
                    result += current;
                    data = reader.read();

                }

                return result;

            } catch (Exception e) {
                e.printStackTrace();
                return "FAILED";

            }

        }

        @Override
        protected void onPostExecute(String j1) {
            super.onPostExecute(j1);

            // print key value

            try {
                JSONObject result = new JSONObject(j1);
                key = result.getString("Key");
                locationName = result.getString("EnglishName");
                cityName.setText("Todays weather for " + locationName);

                DownloadTask weather = new DownloadTask();
                j2 = weather.execute("https://dataservice.accuweather.com/forecasts/v1/daily/1day/" + key + ".json?language=en&apikey=P0smdqeg9oN1aiPYjt8KiukuMioFHEtX&metric=true").get();

                try {
                    JSONObject rootObject = new JSONObject(j2);
                    JSONArray results = rootObject.getJSONArray("DailyForecasts");

                    for (int i = 0; i < results.length(); i++) {

                        JSONObject resultsObj = results.getJSONObject(i);

                        String date = resultsObj.getString("Date");
                        weatherReport.append("Date: " + date + "\n");

                        JSONObject temperatureObj = resultsObj.getJSONObject("Temperature");
                        String minTemperature = temperatureObj.getJSONObject("Minimum").getString("Value");
                        weatherReport.append("Min Temp: " + minTemperature + " C \n");

                        String maxTemperature = temperatureObj.getJSONObject("Maximum").getString("Value");
                        weatherReport.append("Max Temp: " + maxTemperature + " C \n");

                        Log.d("info","getting here");

                        JSONObject precipitaionObj = resultsObj.getJSONObject("Day");
                        Boolean precipitation = precipitaionObj.getBoolean("HasPrecipitation");
                        weatherReport.append("Rain Forcast? : " + precipitation + "\n");

                        weatherReport.append("(Source: AccuWeather.com)\n");

                        Log.d("info","getting here now ");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void generateOutfit(View view) {

        //get categories from wardrobe
        availableCategories = (ArrayList) MainActivity.clothingCollection.clothingItemDao().getAvailableCategories();
        String color = "Blue";

        //for each outfit part get the available categories within each part as we dont
        //want the outfit generation to return an empty category
        for (int i = 0; i < SHOE.length; i++) {
            if (availableCategories.contains(SHOE[i])) {
                availableShoes.add(SHOE[i]);
            }
        }
        for (int i = 0; i < BOTTOM.length; i++) {
            if (availableCategories.contains(BOTTOM[i])) {
                availableShoes.add(BOTTOM[i]);
            }
        }
        for (int i = 0; i < TOP.length; i++) {
            if (availableCategories.contains(TOP[i])) {
                availableShoes.add(TOP[i]);
            }
        }
        for (int i = 0; i < TOPPER.length; i++) {
            if (availableCategories.contains(TOPPER[i])) {
                availableShoes.add(TOPPER[i]);
            }
        }


            if (availableShoes.size()!=0) {
                //pick a random category from the available ones
                int rnd = new Random().nextInt(availableShoes.size());
                String shoeCat = availableShoes.get(rnd);
                //retrieve an item within that category with a similar color
                ClothingItem shoeItem = MainActivity.clothingCollection.clothingItemDao().getItemByCategoryColor(shoeCat, color);
                //add item to the outfit
                outfit.add(shoeItem);

            } else {
                //no shoes available
            }
            if (availableBottoms.size()!=0) {
                int rnd = new Random().nextInt(availableBottoms.size());
                String bottomCat = availableShoes.get(rnd);
                ClothingItem bottomItem = MainActivity.clothingCollection.clothingItemDao().getItemByCategoryColor(bottomCat, color);
                outfit.add(bottomItem);
            } else {
                //no bottoms available
            }
            if (availableTops.size()!=0) {
                int rnd = new Random().nextInt(availableTops.size());
                String topCat = availableTops.get(rnd);
                ClothingItem topItem = MainActivity.clothingCollection.clothingItemDao().getItemByCategoryColor(topCat, color);
                outfit.add(topItem);
            } else {
                //no tops available
            }

            if (availableToppers.size()!=0) {
                int rnd = new Random().nextInt(availableToppers.size());
                String topperCat = availableToppers.get(rnd);
                ClothingItem topperItem = MainActivity.clothingCollection.clothingItemDao().getItemByCategoryColor(topperCat, color);
                outfit.add(topperItem);
            } else {
                //no Toppers available
            }

        for (int i = 0; i < outfit.size(); i++) {
            String path = outfit.get(i).image;
            String label = outfit.get(i).itemName;
            try {
                File f = new File(path + "/" + label + ".jpg");
                outfitNames.add(label);
                outfitBitmaps.add(BitmapFactory.decodeStream(new FileInputStream(f)));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }
        final ClothingItemAdapter adapter = new ClothingItemAdapter(this, outfitNames, outfitBitmaps);
        viewOutfit.setAdapter(adapter);

        }
    }
