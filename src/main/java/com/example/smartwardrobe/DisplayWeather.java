package com.example.smartwardrobe;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
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
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;

public class DisplayWeather extends AppCompatActivity {

    String j1 = null;
    String j2 = null;
    float averageTemp;
    boolean isRain;
    String key;
    String locationName;
    TextView weatherReport;
    TextView cityName;
    LocationManager locationManager;
    ListView viewOutfit;
    String color;
    Button generateOutfit;
    Button saveOutfit;

    public  String[] SHOE = {"Boots", "Shoes"};
    public  String[] BOTTOM = {"Chinos", "Culottes", "Cutoffs", "Flannel", "Jeans", "Jeggings", "Joggers",
            "Leggings", "Shorts", "Skirt", "Sweatpants", "Sweatshorts", "Trunks"};
    public String[] TOP = {"Blouse", "Button-Down", "henley", "Jersey", "Tank", "Tee",
            "Top",};
    public  String[] TOPPER = {"Blazer", "Jacket","Hoodie","Sweater","Cardigan"};
    public String[] JACKET = {"Anarak","Coat", "Parka","Bomber"};

    public ArrayList<String> availableCategories = new ArrayList<>();
    public ArrayList<String> availableShoes = new ArrayList<>();
    public ArrayList<String> availableBottoms = new ArrayList<>();
    public ArrayList<String> availableTops = new ArrayList<>();
    public ArrayList<String> availableToppers = new ArrayList<>();
    public ArrayList<String> availableJackets = new ArrayList<>();
    public ArrayList<ClothingItem> outfit = new ArrayList<>();
    public ArrayList<String> outfitNames = new ArrayList<>();
    public ArrayList<Bitmap> outfitBitmaps = new ArrayList<>();
    Outfit newOutfit = new Outfit();


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
        generateOutfit = findViewById(R.id.generateOutfit);
        saveOutfit = findViewById(R.id.saveOutfit);
        saveOutfit.setVisibility(View.GONE);

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

            try {
                //get location name using location API
                JSONObject result = new JSONObject(j1);
                key = result.getString("Key");
                locationName = result.getString("EnglishName");
                cityName.setText("Todays weather for " + locationName);

                //get forecast using API and location key
                DownloadTask weather = new DownloadTask();
                j2 = weather.execute("https://dataservice.accuweather.com/forecasts/v1/daily/1day/" + key + ".json?language=en&apikey=P0smdqeg9oN1aiPYjt8KiukuMioFHEtX&metric=true").get();

                try {
                    JSONObject rootObject = new JSONObject(j2);
                    JSONArray results = rootObject.getJSONArray("DailyForecasts");

                    for (int i = 0; i < results.length(); i++) {

                        JSONObject resultsObj = results.getJSONObject(i);
                        //get date
                        String date = resultsObj.getString("Date");
                        weatherReport.append("Date: " + date + "\n");

                        //get temperature object from JSON response and display
                        JSONObject temperatureObj = resultsObj.getJSONObject("Temperature");
                        String minTemperature = temperatureObj.getJSONObject("Minimum").getString("Value");
                        weatherReport.append("Min Temp: " + minTemperature + " C \n");

                        String maxTemperature = temperatureObj.getJSONObject("Maximum").getString("Value");
                        weatherReport.append("Max Temp: " + maxTemperature + " C \n");

                        averageTemp = (Float.parseFloat(maxTemperature)+Float.parseFloat(minTemperature))/2;

                        weatherReport.append("Average Temp: "+ averageTemp + "C \n");

                        //get whether precipitation will occur
                        JSONObject precipitaionObj = resultsObj.getJSONObject("Day");
                        isRain = precipitaionObj.getBoolean("HasPrecipitation");
                        weatherReport.append("Rain Forcast? : " + isRain + "\n");

                        weatherReport.append("(Source: AccuWeather.com)\n");

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

    //Generating outfits
    public void generateOutfit(View view) {
        ClothingItemAdapter adapter = new ClothingItemAdapter(this, outfitNames, outfitBitmaps);


        if (!outfit.isEmpty()) {
            outfit.clear();
            outfitNames.clear();
            outfitBitmaps.clear();
            adapter.notifyDataSetChanged();
        }


        //get categories from wardrobe
        availableCategories = (ArrayList) MainActivity.clothingCollection.clothingItemDao().getAvailableCategories();

        //for each outfit part get the available categories within each part as we dont
        //want the outfit generation to return an empty category
        for (int i = 0; i < SHOE.length; i++) {
            if (availableCategories.contains(SHOE[i])) {
                availableShoes.add(SHOE[i]);
            }
        }
        for (int i = 0; i < BOTTOM.length; i++) {
            if (availableCategories.contains(BOTTOM[i])) {
                availableBottoms.add(BOTTOM[i]);
            }
        }
        for (int i = 0; i < TOP.length; i++) {
            if (availableCategories.contains(TOP[i])) {
                availableTops.add(TOP[i]);
            }
        }

        for (int i = 0; i < TOPPER.length; i++) {
            if (availableCategories.contains(TOPPER[i])) {
                availableToppers.add(TOPPER[i]);
            }
        }
        for (int i = 0; i < JACKET.length; i++) {
            if (availableCategories.contains(JACKET[i])) {
                availableJackets.add(JACKET[i]);
            }
        }

        //Color seletion is based of random item selected from tops
        if (availableTops.size() != 0) {
            //get random category from available tops
            int rnd = new Random().nextInt(availableTops.size());
            String topCat = availableTops.get(rnd);
            //get item from that category
            ClothingItem topItem = MainActivity.clothingCollection.clothingItemDao().getRandItemFromCat(topCat);
            //get color of item which outfit will be based on
            color = topItem.color;
            outfit.add(topItem);
            newOutfit.setTop(topItem.itemName);

        } else {
            Context context = getApplicationContext();
            int duration = Toast.LENGTH_LONG;
            String text = "Please add top item to generate outfit";
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            return;
        }

        if (availableToppers.size() != 0 && averageTemp < 15) {
            //get all available topper colors
            List<String> topperColors = MainActivity.clothingCollection.clothingItemDao().loadColorsByCategory(availableToppers);
            //get the nearest matching color to selected topper color
            String nearestColor = GetClothingColor.getSimilarColor(color, topperColors);
            //get item from toppers that has that color
            ClothingItem topperItem = MainActivity.clothingCollection.clothingItemDao().getItemByCatColor(availableToppers, nearestColor);
            outfit.add(topperItem);
            newOutfit.setTopper(topperItem.itemName);
        } else {
            Context context = getApplicationContext();
            int duration = Toast.LENGTH_LONG;
            String text = "No toppers available...Continuing without topper";
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }

        if (availableJackets.size() != 0 && (isRain == true || averageTemp < 10)) {
            //get all available bottom colors
            List jacketColors = MainActivity.clothingCollection.clothingItemDao().loadColorsByCategory(availableBottoms);
            //get the nearest matching color to selected bottom color
            String nearestColor = GetClothingColor.getSimilarColor(color, jacketColors);
            //get item from bottoms that has that color
            ClothingItem jacketItem = MainActivity.clothingCollection.clothingItemDao().getItemByCatColor(availableJackets, nearestColor);
            outfit.add(jacketItem);
            newOutfit.setJacket(jacketItem.itemName);
        } else {
            Context context = getApplicationContext();
            int duration = Toast.LENGTH_LONG;
            String text = "No Jackets available...Continuing without jackets";
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }

        if (availableBottoms.size() != 0) {
            //get all available bottom colors
            List bottomColors = MainActivity.clothingCollection.clothingItemDao().loadColorsByCategory(availableBottoms);
            //get the nearest matching color to selected bottom color
            String nearestColor = GetClothingColor.getSimilarColor(color, bottomColors);
            //get item from bottoms that has that color
            ClothingItem bottomItem = MainActivity.clothingCollection.clothingItemDao().getItemByCatColor(availableBottoms, nearestColor);
            outfit.add(bottomItem);
            newOutfit.setBottom(bottomItem.itemName);
        } else {
            Context context = getApplicationContext();
            int duration = Toast.LENGTH_LONG;
            String text = "No bottoms available...Continuing without bottoms";
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }

        if (availableShoes.size() != 0) {
            //get all available shoe colors
            List<String> shoeColors = MainActivity.clothingCollection.clothingItemDao().loadColorsByCategory(availableShoes);
            //get the nearest matching color to selected top color
            String nearestColor = GetClothingColor.getSimilarColor(color, shoeColors);
            //get item from shoes that has that color
            ClothingItem shoeItem = MainActivity.clothingCollection.clothingItemDao().getItemByCatColor(availableShoes, nearestColor);
            newOutfit.setShoes(shoeItem.itemName);
            outfit.add(shoeItem);
        } else {
            Context context = getApplicationContext();
            int duration = Toast.LENGTH_LONG;
            String text = "No shoes available...Continuing without shoes";
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
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
        viewOutfit.setAdapter(adapter);
        generateOutfit.setText("Generate new outfit");
        saveOutfit.setVisibility(View.VISIBLE);
    }

        public void onSaveOutfit(View view) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Enter Outfit Name");

            final EditText input = new EditText(this);
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);

            builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    newOutfit.setOutfitName(input.getText().toString());
                    MainActivity.outfitCollection.outfitDao().insert(newOutfit);
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();
        }
    }
