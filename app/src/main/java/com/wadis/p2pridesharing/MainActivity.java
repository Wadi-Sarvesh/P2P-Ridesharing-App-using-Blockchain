package com.wadis.p2pridesharing;




import android.Manifest;
import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ActionBarOverlayLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

import java.net.URL;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;

import android.widget.EditText;
import android.widget.ImageButton;


import com.google.android.play.core.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.wadis.p2pridesharing.login.DriverLoginActivity;
import com.wadis.p2pridesharing.login.RegistrationActivity;
import com.wadis.p2pridesharing.managers.BlockChainManager;
import com.wadis.p2pridesharing.map.DirectionsParser;

import com.wadis.p2pridesharing.views.Snackbar;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener , LocationListener, GoogleMap.OnMarkerDragListener, GoogleMap.OnMapLongClickListener
        , OnMapReadyCallback {

    private BlockChainManager blockChain;
    //private ProgressDialog progressDialog;

    // *** MAP CODE ***//
    SearchView Search_bar;
    private static final int REQUEST_CODE = 99;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private Location lastLocation;
    private Marker currentUserLocationMarker;
    private  static  LatLng latLng;
    private GoogleMap map;

    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;

    private Snackbar bookRideSnackbar;
    private Snackbar stateSnackbar;
    View mapView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);


        TextView hash = findViewById(R.id.hash);
        TextView timestamp = findViewById(R.id.timestamp);
        TextView previousHash = findViewById(R.id.previousHash);
        TextView data_message = findViewById(R.id.data);
        TextView index = findViewById(R.id.index);



        //UI of ride req.


        Button book_ride = findViewById(R.id.ht_action);
        View parentLayout = findViewById(android.R.id.content);
        LinearLayout linearLayout=(LinearLayout)findViewById(R.id.LinearLayout);

        stateSnackbar = Snackbar.make(parentLayout, R.layout.snackbar_state, Snackbar.LENGTH_INDEFINITE);
        bookRideSnackbar = Snackbar.make(linearLayout, R.layout.snackbar_book_ride, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.id.ht_action, view -> {


                    EditText mdropoff_text = (EditText)findViewById(R.id.dropoff_text);
                    EditText mpickup_text =(EditText) findViewById(R.id.pickup_text);

                    String pick;
                    if( mpickup_text.getText() != null || mpickup_text.getText().toString() != "")
                    {pick = mpickup_text.getText().toString();}
                    else
                    {pick = null;}
                    LatLng source_latlng = null;
                    if(lastLocation != null)
                    { source_latlng =new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude());}
                    if(pick !=null && !pick.equals(""))
                    {
                        List<Address> Picklist = null;

                        Geocoder geocoder = new Geocoder(MainActivity.this);
                        try {
                            Picklist=geocoder.getFromLocationName(pick,1);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Address pick_address = Picklist.get(0);
                        source_latlng = new LatLng(pick_address.getLatitude(),pick_address.getLongitude());
                    }
                    else
                        Toast.makeText(MainActivity.this,"Enter pickup location",Toast.LENGTH_LONG).show();
                    String drop = mdropoff_text.getText().toString();
                    Log.d("in snackbar ation",drop);


                    if(drop != null && !drop.equals("")){
                        List<Address> Droplist = null;
                        Geocoder geocoder = new Geocoder(MainActivity.this);
                        try {
                            Droplist=geocoder.getFromLocationName(drop,1);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Address drop_address = Droplist.get(0);
                        LatLng search_latlng = new LatLng(drop_address.getLatitude(),drop_address.getLongitude());
                        map.addMarker(new MarkerOptions().position(search_latlng).title(drop).snippet(search_latlng.latitude+","+search_latlng.longitude));
                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(search_latlng,17));
                        String url = getRequestUrl(source_latlng,search_latlng);
                        TaskRequestDirections taskRequestDirections = new TaskRequestDirections();
                        taskRequestDirections.execute(url);
                        stateSnackbar.show();
                        bookRideSnackbar.dismiss();
                    }
                    else
                        Toast.makeText(MainActivity.this,"Enter dropoff location",Toast.LENGTH_LONG).show();


                    ((TextView)stateSnackbar.getView().findViewById(R.id.text)).setText(R.string.finding_drivers);
                    stateSnackbar.setAction(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            stateSnackbar.dismiss();
                            bookRideSnackbar.show();
                        }
                    });

                });
        View view = bookRideSnackbar.getView();
        FrameLayout.LayoutParams params =(FrameLayout.LayoutParams)view.getLayoutParams();
        params.gravity = Gravity.TOP;
        view.setLayoutParams(params);
        bookRideSnackbar.show();

        /*bookRideSnackbar = Snackbar.make(parentLayout,  R.id.ht_action, Snackbar.LENGTH_INDEFINITE)
                .setAction(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bookRideSnackbar.dismiss();
                    }
                });
        ;

        // inflate the custom_snackbar_view created previously
        View customSnackView = getLayoutInflater().inflate(R.layout.snackbar_book_ride, null);

        // set the background of the default snackbar as transparent
        bookRideSnackbar.getView().setBackgroundColor(Color.TRANSPARENT);


        // now change the layout of the snackbar
        Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout)bookRideSnackbar.getView();

        // set padding of the all corners as 0
        snackbarLayout.setPadding(0, 0, 0, 0);
        snackbarLayout.addView(customSnackView, 0);


        bookRideSnackbar.show();

        */
        //ImageButton btn = findViewById(R.id.btn_send_data);
        //showProgressDialog(getResources().getString(R.string.text_creating_blockChain));
       /* new Thread(() -> runOnUiThread(()->{
            blockChain = new BlockChainManager(4);
            hash.setText(blockChain.blocks.get(0).getHash());
            Long time = blockChain.blocks.get(0).getTimestamp();
            timestamp.setText(time.toString() );
            previousHash.setText(blockChain.blocks.get(0).getPreviousHash());
            data_message.setText(blockChain.blocks.get(0).getData());
            index.setText(String.valueOf(blockChain.blocks.get(0).getIndex()));
            Log.d("before progress dialog","cancelling progress");
            //cancelProgressDialog(progressDialog);
        })).start();
        btn.setOnClickListener(this);
        */


        // **** MAP CODE ****//
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            checkUserLocationPermission();
        }
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mapView = mapFragment.getView();



    }
    // 3 dot menu


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.logout_menu, menu);
        return true;

    }
    //To do when menuitem is selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                Intent intent=new Intent(MainActivity.this, DriverLoginActivity.class);
                startActivity(intent);
                finish();
                break;





        }
        return true;

    }

    // **** Run blockchain on UI thread ****//
    private void startBlockChain()
    {
        final EditText data = findViewById(R.id.edit_message);
        TextView hash = findViewById(R.id.hash);
        TextView timestamp = findViewById(R.id.timestamp);
        TextView previousHash = findViewById(R.id.previousHash);
        TextView data_message =findViewById(R.id.data);

        TextView index = findViewById(R.id.index);
        //showProgressDialog(getResources().getString(R.string.text_mining_blocks));
        runOnUiThread(()->{
            if(blockChain !=null && data.getText() != null )
            {
                String message = data.getText().toString();
                if(!message.isEmpty())
                {
                    blockChain.addBlock(blockChain.newBlock(message));
                    if(blockChain.isBlockChainValid())
                    {data.setText("");
                    hash.setText(blockChain.blocks.get(blockChain.blocks.size()-1).getHash());
                    Long time = blockChain.blocks.get(blockChain.blocks.size()-1).getTimestamp();
                    timestamp.setText(time.toString());
                    previousHash.setText(blockChain.blocks.get(blockChain.blocks.size()-1).getPreviousHash());
                    data_message.setText(blockChain.blocks.get(blockChain.blocks.size()-1).getData());
                    index.setText(String.valueOf(blockChain.blocks.get(blockChain.blocks.size()-1).getIndex()));}
                    else
                        Toast.makeText(this,"Blockchain corrupted",Toast.LENGTH_LONG).show();
                }
                else
                    Toast.makeText(this,"Error, empty data",Toast.LENGTH_LONG).show();

                //cancelProgressDialog(progressDialog);
            }
            else
                Toast.makeText(this,"Something wrong happened",Toast.LENGTH_LONG).show();



        });
    }
    // on click listener for send data and start blockchain
    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.btn_send_data)
            startBlockChain();
    }
   /* private void showProgressDialog(@NonNull String loadingMessage)
    {
        ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage(loadingMessage);
        progressDialog.setCancelable(true);
        progressDialog.setMax(100);
        progressDialog.show();

    }
    private void cancelProgressDialog(@NonNull ProgressDialog progressDialog)
    {
        Log.d("in progress dialog","cancel progress");
        if(progressDialog != null)
            progressDialog.dismiss();

    }*/





    // MAP METHODS
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setPadding(0,160,35,0);
        map.setOnMarkerDragListener(this);
        map.setOnMapLongClickListener(this);
        if (mapView != null &&
                mapView.findViewById(Integer.parseInt("1")) != null) {
            // Get the button view
            View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            // and next place it, on bottom right (as Google Maps app)
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                    locationButton.getLayoutParams();
            // position on right bottom
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            layoutParams.setMargins(0, 0, 30, 30);
        }
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {buildGoogleApiClient();
            map.setMyLocationEnabled(true);}



    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest =new LocationRequest();
        locationRequest.setInterval(1100);
        locationRequest.setFastestInterval(1100);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest , this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

        lastLocation = location;
        if(currentUserLocationMarker != null){

            currentUserLocationMarker.remove();
        }
        latLng = new LatLng(location.getLatitude(),location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng).title("Current Location").snippet(location.getLatitude()+","+location.getLongitude())
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        currentUserLocationMarker = map.addMarker(markerOptions);
        map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        map.animateCamera(CameraUpdateFactory.zoomBy(17));


        if(googleApiClient != null){
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        }

    }
    public  boolean checkUserLocationPermission()
    {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION))
            {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            }
            else
            {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            }
            return false;
        }

            return true;

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {


        switch (requestCode) {
            case REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        if (googleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        map.setMyLocationEnabled(true);
                    }
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
                return;
        }
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    protected synchronized void buildGoogleApiClient()
    {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
        googleApiClient.connect();
    }
    // **** GET DIRECTIONS **** //
    private String getRequestUrl(LatLng origin, LatLng dest) {
        String str_org = "origin=" + origin.latitude + "," + origin.longitude;
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        String mode = "mode=driving";
        String param =   str_org +"&" +str_dest  +"&" +mode;
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?&" + param+ "&key=" + getString(R.string.map_key);
        Log.d("URL",url);
        return url;
    }

    private String requestDirection(String reqUrl) throws IOException {
        String responseString ="";
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;
        try {
            URL  url = new URL(reqUrl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();

            inputStream =httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuffer stringBuffer = new StringBuffer();
            String line = "";
            while ((line=bufferedReader.readLine()) != null)
            {
                stringBuffer.append(line);

            }

            responseString = stringBuffer.toString();
            bufferedReader.close();
            inputStreamReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if(inputStream != null)
            {
                inputStream.close();
            }
            httpURLConnection.disconnect();
        }
        return responseString;
    }
    public class TaskRequestDirections extends AsyncTask<String,Void,String>{
        @Override
        protected String doInBackground(String... strings) {
            String responseString = "";
            try {
                responseString = requestDirection(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            TaskParser taskParser = new TaskParser();
            taskParser.execute(s);
        }
    }
    public class TaskParser extends AsyncTask<String, Void, List<List<HashMap<String,String>>>>{

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... strings) {
            JSONObject jsonObject = null;
            List<List<HashMap<String,String>>> routes = null;
            try {
                jsonObject = new JSONObject(strings[0]);
                DirectionsParser directionsParser = new DirectionsParser();
                routes = directionsParser.parse(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return routes;

        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> lists) {
            ArrayList points = null;
            PolylineOptions polylineOptions = null;
            for(List<HashMap<String, String>> path : lists)
            {
                points = new ArrayList();
                polylineOptions = new PolylineOptions();
                for (HashMap<String, String> point : path)
                {
                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    points.add(new LatLng(lat,lng));
                }
                polylineOptions.addAll(points);
                polylineOptions.width(15);
                polylineOptions.color(Color.BLUE);
                polylineOptions.geodesic(true);

            }
            if(polylineOptions != null)
            {map.addPolyline(polylineOptions);}
            else
            {Toast.makeText(getApplicationContext(),"Direction Not Found",Toast.LENGTH_SHORT).show();}
        }
    }

    @Override
    public void onMapLongClick(LatLng latLng) {

    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {

    }
}