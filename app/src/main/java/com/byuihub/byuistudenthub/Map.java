package com.byuihub.byuistudenthub;

import android.*;
import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.Iterator;
import java.util.List;

public class Map extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {

    private static final String TAG = "Map";

    /** Needed to access map fragment in xml layout file. */
    private MapFragment mapFragment;

    /** Needed to access to access the Google Maps API. */
    private GoogleMap mGoogleMap;

    /** Needed to store and access custom markers used throughout map. */
    private ArrayList <Marker> buildings, northParkingMarkers, southParkingMarkers, visitorParkingParkers,
            accessibleParkingMarkers, motorcycleParkingMarkers, freeParkingMarkers,
            longTermParkingMarkers, facultyParkingMarkers, childLabMarkers, campusHousingParkingMarkers,
            fourthWardMarkers, busStopsSLE, busStopsWalMart, bikeRacks, limitedParkingMarkers;

    /** Needed to store and access custom overlays used throughout map. */
    private ArrayList <Polygon> northParkingOverlays, facultyParkingOverlays, southParkingOverlays,
            cityParkingOverlays, busRouteWalMart;

    /** String constants needed to access a user's location. */
    private static final String[] LOCATION_PERMISSIONS = {
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
    };

    /** Integer constant needed to access a user's location. */
    private static final int LOCATION_PERMISSIONS_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* Google Maps needs access to Google Play services, so we check */
        if (googleServicesAvailable()) {
            Log.i(TAG, "Creating Map activity");
            Toast.makeText(this, "Successfully connected to Google Play services",
                    Toast.LENGTH_LONG).show();

            setContentView(R.layout.activity_map);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    removeEverything();
                }
            });

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.setDrawerListener(toggle);
            toggle.syncState();

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);

            Log.i(TAG, "Initializing map fragment");
            /* Initialize map fragment */
            mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
            Log.i(TAG, "Initialized map fragment");
        }
    }

    /**
     * Clears all custom markers and overlays from the map fragment.
     */
    private void removeEverything() {
        Log.i(TAG, "About to delete all markers.");
        boolean cleared = false;

        if(buildings.get(0).isVisible()) {
            for(Marker item : buildings) {
                item.setVisible(false);
            }
            cleared = true;
        }

        if(northParkingMarkers.get(0).isVisible()) {
            for(Marker item : northParkingMarkers) {
                item.setVisible(false);
            }
            if(!cleared) {
                cleared = true;
            }
        }

        if(southParkingMarkers.get(0).isVisible()) {
            for(Marker item : southParkingMarkers) {
                item.setVisible(false);
            }
            if(!cleared) {
                cleared = true;
            }
        }

        if(visitorParkingParkers.get(0).isVisible()) {
            for(Marker item : visitorParkingParkers) {
                item.setVisible(false);
            }
            if(!cleared) {
                cleared = true;
            }
        }

//        if(accessibleParkingMarkers.get(0).isVisible()) {
//            for(Marker item : accessibleParkingMarkers) {
//                item.setVisible(false);
//            }
//        }

//        if(limitedParkingMarkers.get(0).isVisible()) {
//            for(Marker item : limitedParkingMarkers) {
//                item.setVisible(false);
//            }
//        }

        if(motorcycleParkingMarkers.get(0).isVisible()) {
            for(Marker item : motorcycleParkingMarkers) {
                item.setVisible(false);
            }
        }

        if(bikeRacks.get(0).isVisible()) {
            for(Marker item : bikeRacks) {
                item.setVisible(false);
            }
        }

        if(freeParkingMarkers.get(0).isVisible()) {
            for(Marker item : freeParkingMarkers) {
                item.setVisible(false);
            }
            if(!cleared) {
                cleared = true;
            }
        }

        if(longTermParkingMarkers.get(0).isVisible()) {
            for(Marker item : longTermParkingMarkers) {
                item.setVisible(false);
            }
            if(!cleared) {
                cleared = true;
            }
        }

        if(facultyParkingMarkers.get(0).isVisible()) {
            for(Marker item : facultyParkingMarkers) {
                item.setVisible(false);
            }
            if(!cleared) {
                cleared = true;
            }
        }

        if(childLabMarkers.get(0).isVisible()) {
            for(Marker item : childLabMarkers) {
                item.setVisible(false);
            }
            if(!cleared) {
                cleared = true;
            }
        }

        if(campusHousingParkingMarkers.get(0).isVisible()) {
            for(Marker item : campusHousingParkingMarkers) {
                item.setVisible(false);
            }
            if(!cleared) {
                cleared = true;
            }
        }

        if(fourthWardMarkers.get(0).isVisible()) {
            for(Marker item : fourthWardMarkers) {
                item.setVisible(false);
            }
            if(!cleared) {
                cleared = true;
            }
        }

        if(northParkingOverlays.get(0).isVisible()) {
            for (Polygon item : northParkingOverlays) {
                item.setVisible(false);
            }
            if(!cleared) {
                cleared = true;
            }
        }

        if(southParkingOverlays.get(0).isVisible()) {
            for (Polygon item : southParkingOverlays) {
                item.setVisible(false);
            }
            if(!cleared) {
                cleared = true;
            }
        }

        if(facultyParkingOverlays.get(0).isVisible()) {
            for (Polygon item : facultyParkingOverlays) {
                item.setVisible(false);
            }
            if(!cleared) {
                cleared = true;
            }
        }

        if(cityParkingOverlays.get(0).isVisible()) {
            for (Polygon item : cityParkingOverlays) {
                item.setVisible(false);
            }
            if(!cleared) {
                cleared = true;
            }
        }

        if(busStopsSLE.get(0).isVisible()) {
            for(Marker item : busStopsSLE) {
                item.setVisible(false);
            }
            if(!cleared) {
                cleared = true;
            }
        }

        if(busStopsWalMart.get(0).isVisible()) {
            for(Marker item : busStopsWalMart) {
                item.setVisible(false);
            }
            if(!cleared) {
                cleared = true;
            }
        }

        if(busRouteWalMart.get(0).isVisible()) {
            for (Polygon item : busRouteWalMart) {
                item.setVisible(false);
            }
            if(!cleared) {
                cleared = true;
            }
        }

        Log.i(TAG, "About to display Toast.");
        if(cleared) {
            Toast.makeText(this, "Cleared everything",
                    Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Checks for Google Play services.
     *
     * @return
     */
    public boolean googleServicesAvailable() {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int isAvailable = api.isGooglePlayServicesAvailable(this);
        if (isAvailable == ConnectionResult.SUCCESS) {
            return true;
        } else if (api.isUserResolvableError(isAvailable)) {
            Dialog dialog = api.getErrorDialog(this, isAvailable, 0);
            dialog.show();
        } else {
            Toast.makeText(this, "Unable to connect to Google Play services.", Toast.LENGTH_LONG).show();
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Needed to access user's location and to access Google Play services.
     *
     * @param requestCode
     * @param permissions permission from user needed
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(final int requestCode, final @NonNull String[] permissions,
                                           final @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length == LOCATION_PERMISSIONS.length) {
                for (final int grantResult : grantResults) {
                    if (PackageManager.PERMISSION_GRANTED != grantResult) {
                        return;
                    }
                }
                showCurrentLocationMapControl();
            }
        }
    }

    /**
     * To access user's location.
     *
     * @return
     */
    private boolean hasGrantedLocationPermissions() {
        return ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * To access user's location.
     */
    private void requestForLocationPermissions() {
        ActivityCompat.requestPermissions(this, LOCATION_PERMISSIONS, LOCATION_PERMISSIONS_REQUEST_CODE);
    }

    /**
     * To access user's location.
     */
    private void showCurrentLocationMapControl() {
        Log.i(TAG, "Redirected to showCurrentLocationMapControl().");
        if (null != this.mGoogleMap) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
            }
            mGoogleMap.setMyLocationEnabled(true);
        }
    }

    /**
     * Allows user's to view options for changing map types.
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Reacts to users changing map types.
     *
     * @param item the map type chosen
     * @return
     */
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mapTypeNormal:
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
            case R.id.mapTypeSatellite:
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;
            case R.id.mapTypeTerrain:
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                break;
            case R.id.mapTypeHybrid:
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;
            default:
                break;
        }
        return false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;

        if(mGoogleMap != null) {
            /* NOTE: This updates current location, manually, with current location button */
            if (hasGrantedLocationPermissions()) {
                showCurrentLocationMapControl();
            } else {
                requestForLocationPermissions();
            }

            mGoogleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                @Override
                public View getInfoWindow(Marker marker) {
                    return null;
                }

                @Override
                public View getInfoContents(Marker marker) {
                    View v = getLayoutInflater().inflate(R.layout.info_window, null);

                    TextView tvLocality = (TextView) v.findViewById(R.id.tv_locality);
                    TextView tvSnippet = (TextView) v.findViewById(R.id.tv_snippet);

                    LatLng ll = marker.getPosition();

                    tvLocality.setText(marker.getTitle());
                    tvSnippet.setText(marker.getSnippet());

                    return v;
                }
            });

            mGoogleMap.setMapType(3);

            setupMarkers();

            /* We initialize the map activity by zooming in to BYU-Idaho's campus */
            goToLocationZoom(43.814961, -111.783005, 15.0000f);
        }
    }

    /**
     * Initializes markers and overlays lists and calls set-up
     * methods.
     */
    private void setupMarkers() {
        buildings = new ArrayList <Marker> ();
        northParkingMarkers = new ArrayList <Marker> ();
        northParkingOverlays = new ArrayList <Polygon> ();
        southParkingMarkers = new ArrayList <Marker> ();
        southParkingOverlays = new ArrayList <Polygon> ();
        visitorParkingParkers = new ArrayList <Marker> ();
        cityParkingOverlays = new ArrayList <Polygon> ();
        accessibleParkingMarkers = new ArrayList <Marker> ();
        limitedParkingMarkers = new ArrayList <Marker> ();
        motorcycleParkingMarkers = new ArrayList <Marker> ();
        freeParkingMarkers = new ArrayList <Marker> ();
        longTermParkingMarkers = new ArrayList <Marker> ();
        facultyParkingMarkers = new ArrayList <Marker> ();
        facultyParkingOverlays = new ArrayList <Polygon> ();
        childLabMarkers = new ArrayList <Marker> ();
        campusHousingParkingMarkers = new ArrayList <Marker> ();
        fourthWardMarkers = new ArrayList <Marker> ();
        busStopsSLE = new ArrayList <Marker> ();
        busStopsWalMart = new ArrayList <Marker> ();
        busRouteWalMart = new ArrayList <Polygon> ();
        bikeRacks = new ArrayList <Marker> ();

        setupBuildings();
        setupParking();
        setupBikeRacks();
        setupBusStops();
        setupMotoATVParking();
    }

    /**
     * Setup the marker and overlay lists for parking information
     */
    private void setupParking() {
        MarkerOptions optionsS2ndLot = new MarkerOptions()
                .title("2nd South Lot")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .position(new LatLng(43.822544, -111.7833466))
                .visible(false);

        MarkerOptions optionsStadiumLot = new MarkerOptions()
                .title("Stadium Lot")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .position(new LatLng(43.820922, -111.786385))
                .visible(false);

        MarkerOptions optionsBYUICenterLot = new MarkerOptions()
                .title("BYU-Idaho Center Lot")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .position(new LatLng(43.817687, -111.784887))
                .visible(false);

        MarkerOptions optionsTaylorLot = new MarkerOptions()
                .title("Taylor Lot")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .position(new LatLng(43.816958, -111.783378))
                .visible(false);

        MarkerOptions optionsPionerrLot = new MarkerOptions()
                .title("Pioneer Lot")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .position(new LatLng(43.818788, -111.779639))
                .visible(false);

        northParkingMarkers.add(mGoogleMap.addMarker(optionsS2ndLot));
        northParkingMarkers.add(mGoogleMap.addMarker(optionsStadiumLot));
        northParkingMarkers.add(mGoogleMap.addMarker(optionsBYUICenterLot));
        northParkingMarkers.add(mGoogleMap.addMarker(optionsTaylorLot));
        northParkingMarkers.add(mGoogleMap.addMarker(optionsPionerrLot));

        PolygonOptions S1stWOverlay = new PolygonOptions()
                .add(
                        new LatLng(43.817915, -111.786583),
                        new LatLng(43.817920, -111.786662),
                        new LatLng(43.818928, -111.786663),
                        new LatLng(43.818928, -111.786593),

                        new LatLng(43.817915, -111.786583))
                .fillColor(Color.argb(100, 77, 136, 255))
                .strokeWidth(0)
                .visible(false);

        MarkerOptions optionsS1W = new MarkerOptions()
                .title("S. 1st W. On-Street Parking")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .position(new LatLng(43.818654, -111.786621))
                .visible(false);

        northParkingOverlays.add(mGoogleMap.addPolygon(S1stWOverlay));
        northParkingMarkers.add(mGoogleMap.addMarker(optionsS1W));

        PolygonOptions EVikingStOverlay = new PolygonOptions()
                .add(
                        new LatLng(43.817817, -111.780585),
                        new LatLng(43.817821, -111.778375),
                        new LatLng(43.817612, -111.778371),
                        new LatLng(43.817603, -111.780566),

                        new LatLng(43.817815, -111.780585))
                .fillColor(Color.argb(100, 77, 136, 255))
                .strokeWidth(0)
                .visible(false);

        MarkerOptions optionsEVikingSt = new MarkerOptions()
                .title("E. Viking St. On-Street Parking")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .position(new LatLng(43.817711, -111.779573))
                .visible(false);

        northParkingOverlays.add(mGoogleMap.addPolygon(EVikingStOverlay));
        northParkingMarkers.add(mGoogleMap.addMarker(optionsEVikingSt));

        PolygonOptions SCenterStOverlay = new PolygonOptions()
                .add(
                        new LatLng(43.817378, -111.783678),
                        new LatLng(43.816617, -111.783689),
                        new LatLng(43.816613, -111.783972),
                        new LatLng(43.817357, -111.783970),

                        new LatLng(43.817378, -111.783678))
                .fillColor(Color.argb(100, 77, 136, 255))
                .strokeWidth(0)
                .visible(false);

        MarkerOptions optionsSCenterSt = new MarkerOptions()
                .title("S. Center St. On-Street Parking")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .position(new LatLng(43.817047, -111.783830))
                .visible(false);

        northParkingOverlays.add(mGoogleMap.addPolygon(SCenterStOverlay));
        northParkingMarkers.add(mGoogleMap.addMarker(optionsSCenterSt));

        MarkerOptions optionsRicksLot = new MarkerOptions()
                .title("Ricks Lot")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
                .position(new LatLng(43.814748, -111.780043))
                .visible(false);

        MarkerOptions optionsHinckleyLot = new MarkerOptions()
                .title("Hinckley Lot")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
                .position(new LatLng(43.815534, -111.778914))
                .visible(false);

        MarkerOptions optionsSTCLot = new MarkerOptions()
                .title("STC Lot")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
                .position(new LatLng(43.813768, -111.785164))
                .visible(false);

        southParkingMarkers.add(mGoogleMap.addMarker(optionsRicksLot));
        southParkingMarkers.add(mGoogleMap.addMarker(optionsHinckleyLot));
        southParkingMarkers.add(mGoogleMap.addMarker(optionsSTCLot));

        PolygonOptions S1stWSouthOverlay = new PolygonOptions()
                .add(
                        new LatLng(43.815532, -111.786793),
                        new LatLng(43.815535, -111.786597),
                        new LatLng(43.814106, -111.786545),
                        new LatLng(43.814112, -111.786805),

                        new LatLng(43.815532, -111.786793))
                .fillColor(Color.argb(100, 255, 255, 0))
                .strokeWidth(0)
                .visible(false);

        MarkerOptions optionsS1stW = new MarkerOptions()
                .title("S. 1st W. On-Street Parking")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
                .position(new LatLng(43.814842, -111.786690))
                .visible(false);

        southParkingOverlays.add(mGoogleMap.addPolygon(S1stWSouthOverlay));
        southParkingMarkers.add(mGoogleMap.addMarker(optionsS1stW));

        PolygonOptions SageStOverlay = new PolygonOptions()
                .add(
                        new LatLng(43.815133, -111.784034),
                        new LatLng(43.814973, -111.784049),
                        new LatLng(43.814956, -111.786489),
                        new LatLng(43.815126, -111.786499),

                        new LatLng(43.815133, -111.784034))
                .fillColor(Color.argb(100, 255, 255, 0))
                .strokeWidth(0)
                .visible(false);

        MarkerOptions optionsSageSt = new MarkerOptions()
                .title("Sage St. On-Street Parking")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
                .position(new LatLng(43.815051, -111.785187))
                .visible(false);

        southParkingOverlays.add(mGoogleMap.addPolygon(SageStOverlay));
        southParkingMarkers.add(mGoogleMap.addMarker(optionsSageSt));

        PolygonOptions SCenterStSouthOverlay = new PolygonOptions()
                .add(
                        new LatLng(43.816373, -111.783685),
                        new LatLng(43.816373, -111.783970),
                        new LatLng(43.811739, -111.783972),
                        new LatLng(43.811743, -111.783679),

                        new LatLng(43.816373, -111.783685))
                .fillColor(Color.argb(100, 255, 255, 0))
                .strokeWidth(0)
                .visible(false);

        MarkerOptions optionsSCenterStSouth = new MarkerOptions()
                .title("S. Center St. On-Street Parking")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
                .position(new LatLng(43.814269, -111.783876))
                .visible(false);

        southParkingOverlays.add(mGoogleMap.addPolygon(SCenterStSouthOverlay));
        southParkingMarkers.add(mGoogleMap.addMarker(optionsSCenterStSouth));

        MarkerOptions optionsSnowLot = new MarkerOptions()
                .title("Snow Lot")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                .position(new LatLng(43.820350, -111.784006))
                .visible(false);

        MarkerOptions optionsManwaringLoadingLot = new MarkerOptions()
                .title("Manwaring Loading Lot")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                .position(new LatLng(43.818072, -111.781321))
                .visible(false);

        MarkerOptions optionsBYIUCenterVisitorLot = new MarkerOptions()
                .title("BYU-Idaho Center Lot")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                .position(new LatLng(43.817730, -111.784203))
                .visible(false);

        MarkerOptions optionsKimballLot = new MarkerOptions()
                .title("Kimball Lot")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                .position(new LatLng(43.816946, -111.780583))
                .visible(false);

        visitorParkingParkers.add(mGoogleMap.addMarker(optionsSnowLot));
        visitorParkingParkers.add(mGoogleMap.addMarker(optionsManwaringLoadingLot));
        visitorParkingParkers.add(mGoogleMap.addMarker(optionsBYIUCenterVisitorLot));
        visitorParkingParkers.add(mGoogleMap.addMarker(optionsKimballLot));

        MarkerOptions optionsCollegeAveLot = new MarkerOptions()
                .title("College Ave. Lot")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))
                .position(new LatLng(43.824339, -111.782703))
                .visible(false);

        MarkerOptions options7thSLot = new MarkerOptions()
                .title("7th S. Lot")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))
                .position(new LatLng(43.812470, -111.781700))
                .visible(false);

        MarkerOptions options2ndELot = new MarkerOptions()
                .title("2nd E. Lot")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))
                .position(new LatLng(43.814092, -111.778578))
                .visible(false);

        MarkerOptions optionsSportsLot = new MarkerOptions()
                .title("Sports Complex Lot")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))
                .position(new LatLng(43.810313, -111.783818))
                .visible(false);

        MarkerOptions optionsEStakeLot = new MarkerOptions()
                .title("East Stake Lot")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))
                .position(new LatLng(43.809963, -111.781686))
                .visible(false);

        freeParkingMarkers.add(mGoogleMap.addMarker(optionsCollegeAveLot));
        freeParkingMarkers.add(mGoogleMap.addMarker(options7thSLot));
        freeParkingMarkers.add(mGoogleMap.addMarker(options2ndELot));
        freeParkingMarkers.add(mGoogleMap.addMarker(optionsSportsLot));
        freeParkingMarkers.add(mGoogleMap.addMarker(optionsEStakeLot));

        MarkerOptions options7thSLongTermLot = new MarkerOptions()
                .title("7th S. Lot")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET))
                .position(new LatLng(43.812205, -111.781531))
                .visible(false);

        longTermParkingMarkers.add(mGoogleMap.addMarker(options7thSLongTermLot));

        MarkerOptions optionsSnowFacultyLot = new MarkerOptions()
                .title("Snow Lot")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                .position(new LatLng(43.820426, -111.784300))
                .visible(false);

        MarkerOptions optionsClarkeLot = new MarkerOptions()
                .title("Clarke Lot")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                .position(new LatLng(43.820246, -111.781222))
                .visible(false);

        MarkerOptions options3rdSLot = new MarkerOptions()
                .title("3rd S. Lot")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                .position(new LatLng(43.819998, -111.779847))
                .visible(false);

        MarkerOptions options332S1stW = new MarkerOptions()
                .title("332 S. 1st W.")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                .position(new LatLng(43.819237, -111.786336))
                .visible(false);

        MarkerOptions optionsBYUICenterWestLot = new MarkerOptions()
                .title("BYU-Idaho Center West Lot")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                .position(new LatLng(43.818636, -111.786338))
                .visible(false);

        MarkerOptions optionsManwaringLoadingFacultyLot = new MarkerOptions()
                .title("Manwaring Loading Lot")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                .position(new LatLng(43.818529, -111.781339))
                .visible(false);

        MarkerOptions optionsManwaringEDrLot = new MarkerOptions()
                .title("Manwaring E. Dr. Lot")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                .position(new LatLng(43.818307, -111.780964))
                .visible(false);

        MarkerOptions options456S2ndE = new MarkerOptions()
                .title("456 S. 2nd E.")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                .position(new LatLng(43.817037, -111.780163))
                .visible(false);

        MarkerOptions optionsTaylorFacultyLot = new MarkerOptions()
                .title("Taylor Lot")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                .position(new LatLng(43.816297, -111.783066))
                .visible(false);

        MarkerOptions optionsFacilitiesLot = new MarkerOptions()
                .title("Facilities Management Lot")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                .position(new LatLng(43.815590, -111.785465))
                .visible(false);

        MarkerOptions optionsAustinLot = new MarkerOptions()
                .title("Austin Faculty Lot")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                .position(new LatLng(43.815272, -111.784806))
                .visible(false);

        MarkerOptions optionsSageLot = new MarkerOptions()
                .title("Sage Lot")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                .position(new LatLng(43.814434, -111.785827))
                .visible(false);

        facultyParkingMarkers.add(mGoogleMap.addMarker(optionsSnowFacultyLot));
        facultyParkingMarkers.add(mGoogleMap.addMarker(optionsClarkeLot));
        facultyParkingMarkers.add(mGoogleMap.addMarker(options3rdSLot));
        facultyParkingMarkers.add(mGoogleMap.addMarker(options332S1stW));
        facultyParkingMarkers.add(mGoogleMap.addMarker(optionsBYUICenterWestLot));
        facultyParkingMarkers.add(mGoogleMap.addMarker(optionsManwaringLoadingFacultyLot));
        facultyParkingMarkers.add(mGoogleMap.addMarker(optionsManwaringEDrLot));
        facultyParkingMarkers.add(mGoogleMap.addMarker(options456S2ndE));
        facultyParkingMarkers.add(mGoogleMap.addMarker(optionsTaylorFacultyLot));
        facultyParkingMarkers.add(mGoogleMap.addMarker(optionsFacilitiesLot));
        facultyParkingMarkers.add(mGoogleMap.addMarker(optionsAustinLot));
        facultyParkingMarkers.add(mGoogleMap.addMarker(optionsSageLot));

        PolygonOptions KirkhamLotOverlay = new PolygonOptions()
                .add(
                        new LatLng(43.821637, -111.781118),
                        new LatLng(43.821520, -111.781117),
                        new LatLng(43.821532, -111.781310),
                        new LatLng(43.821637, -111.781318),

                        new LatLng(43.821637, -111.781118))
                .fillColor(Color.argb(100, 255, 0, 0))
                .strokeWidth(0)
                .visible(false);

        MarkerOptions optionsKirkhamLot = new MarkerOptions()
                .title("Kirkham Auditorium Lot")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                .position(new LatLng(43.821578, -111.781206))
                .visible(false);

        facultyParkingOverlays.add(mGoogleMap.addPolygon(KirkhamLotOverlay));
        facultyParkingMarkers.add(mGoogleMap.addMarker(optionsKirkhamLot));

        PolygonOptions WCampusDrOverlay = new PolygonOptions()
                .add(
                        new LatLng(43.816563, -111.786542),
                        new LatLng(43.816409, -111.786543),
                        new LatLng(43.816409, -111.784078),
                        new LatLng(43.816559, -111.784081),

                        new LatLng(43.816563, -111.786542))
                .fillColor(Color.argb(100, 255, 0, 0))
                .strokeWidth(0)
                .visible(false);

        MarkerOptions optionsWCampusDr = new MarkerOptions()
                .title("W. Campus Dr. On-Street Parking")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                .position(new LatLng(43.816484, -111.784816))
                .visible(false);

        facultyParkingOverlays.add(mGoogleMap.addPolygon(WCampusDrOverlay));
        facultyParkingMarkers.add(mGoogleMap.addMarker(optionsWCampusDr));

        PolygonOptions PhysicalPlantWayOverlay = new PolygonOptions()
                .add(
                        new LatLng(43.815181, -111.785333),
                        new LatLng(43.815186, -111.785170),
                        new LatLng(43.816339, -111.785186),
                        new LatLng(43.816349, -111.785360),

                        new LatLng(43.815181, -111.785333))
                .fillColor(Color.argb(100, 255, 0, 0))
                .strokeWidth(0)
                .visible(false);

        MarkerOptions optionsPhysicalPlantWay = new MarkerOptions()
                .title("Physical Plant Way On-Street Parking")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                .position(new LatLng(43.815769, -111.785243))
                .visible(false);

        facultyParkingOverlays.add(mGoogleMap.addPolygon(PhysicalPlantWayOverlay));
        facultyParkingMarkers.add(mGoogleMap.addMarker(optionsPhysicalPlantWay));

        PolygonOptions BensonLotOverlay = new PolygonOptions()
                .add(
                        new LatLng(43.814704, -111.783511),
                        new LatLng(43.814700, -111.783176),
                        new LatLng(43.814970, -111.783189),
                        new LatLng(43.814978, -111.783254),
                        new LatLng(43.814815, -111.783257),
                        new LatLng(43.814811, -111.783512),

                        new LatLng(43.814704, -111.783511))
                .fillColor(Color.argb(100, 255, 0, 0))
                .strokeWidth(0)
                .visible(false);

        MarkerOptions optionsBensonLot = new MarkerOptions()
                .title("Benson Lot")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                .position(new LatLng(43.814757, -111.783356))
                .visible(false);

        facultyParkingOverlays.add(mGoogleMap.addPolygon(BensonLotOverlay));
        facultyParkingMarkers.add(mGoogleMap.addMarker(optionsBensonLot));

        PolygonOptions AuxLotOverlay = new PolygonOptions()
                .add(
                        new LatLng(43.814600, -111.783452),
                        new LatLng(43.814596, -111.782690),
                        new LatLng(43.813712, -111.782695),
                        new LatLng(43.813713, -111.783456),
                        new LatLng(43.813875, -111.783455),
                        new LatLng(43.813881, -111.783285),
                        new LatLng(43.813837, -111.783282),
                        new LatLng(43.813839, -111.782785),
                        new LatLng(43.814176, -111.782775),
                        new LatLng(43.814178, -111.782864),
                        new LatLng(43.814415, -111.782857),
                        new LatLng(43.814420, -111.783277),
                        new LatLng(43.814484, -111.783275),
                        new LatLng(43.814492, -111.783456),

                        new LatLng(43.814600, -111.783452))
                .fillColor(Color.argb(100, 255, 0, 0))
                .strokeWidth(0)
                .visible(false);

        MarkerOptions optionsAuxLot = new MarkerOptions()
                .title("Auxiliary Services Lot")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                .position(new LatLng(43.813809, -111.783140))
                .visible(false);

        facultyParkingOverlays.add(mGoogleMap.addPolygon(AuxLotOverlay));
        facultyParkingMarkers.add(mGoogleMap.addMarker(optionsAuxLot));

        PolygonOptions RicksLotOverlay = new PolygonOptions()
                .add(
                        new LatLng(43.814135, -111.780235),
                        new LatLng(43.814137, -111.780168),
                        new LatLng(43.815322, -111.780164),
                        new LatLng(43.815326, -111.780227),

                        new LatLng(43.814135, -111.780235))
                .fillColor(Color.argb(100, 255, 0, 0))
                .strokeWidth(0)
                .visible(false);

        MarkerOptions optionsRicksLotFaculty = new MarkerOptions()
                .title("Ricks Lot")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                .position(new LatLng(43.814620, -111.780224))
                .visible(false);

        facultyParkingOverlays.add(mGoogleMap.addPolygon(RicksLotOverlay));
        facultyParkingMarkers.add(mGoogleMap.addMarker(optionsRicksLotFaculty));

        PolygonOptions HinckleyLotOverlay = new PolygonOptions()
                .add(
                        new LatLng(43.815964, -111.779361),
                        new LatLng(43.816290, -111.778947),
                        new LatLng(43.816154, -111.778819),
                        new LatLng(43.815860, -111.779192),

                        new LatLng(43.815964, -111.779361))
                .fillColor(Color.argb(100, 255, 0, 0))
                .strokeWidth(0)
                .visible(false);

        MarkerOptions optionsHinckleyLotFaculty = new MarkerOptions()
                .title("Hinckley Lot")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                .position(new LatLng(43.815952, -111.779231))
                .visible(false);

        facultyParkingOverlays.add(mGoogleMap.addPolygon(HinckleyLotOverlay));
        facultyParkingMarkers.add(mGoogleMap.addMarker(optionsHinckleyLotFaculty));

        MarkerOptions optionsClarkeChildLabLot = new MarkerOptions()
                .title("Clarke Lot")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))
                .position(new LatLng(43.820243, -111.781301))
                .visible(false);

        childLabMarkers.add(mGoogleMap.addMarker(optionsClarkeChildLabLot));

        MarkerOptions optionsCenterSquareLot = new MarkerOptions()
                .title("Center Square Apartments Lot")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                .position(new LatLng(43.813696, -111.787869))
                .visible(false);

        campusHousingParkingMarkers.add(mGoogleMap.addMarker(optionsCenterSquareLot));

        MarkerOptions options4thWardLot = new MarkerOptions()
                .title("4th Ward Lot")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                .position(new LatLng(43.822864, -111.781400))
                .visible(false);

        fourthWardMarkers.add(mGoogleMap.addMarker(options4thWardLot));

        PolygonOptions cityOverlay1 = new PolygonOptions()
                .add(
                        new LatLng(43.823887, -111.787599),
                        new LatLng(43.824022, -111.787609),
                        new LatLng(43.824026, -111.777273),
                        new LatLng(43.823888, -111.777276),

                        new LatLng(43.823887, -111.787599))
                .fillColor(Color.argb(100, 0, 255, 0))
                .strokeWidth(0)
                .visible(false);

        cityParkingOverlays.add(mGoogleMap.addPolygon(cityOverlay1));

        PolygonOptions cityOverlay2 = new PolygonOptions()
                .add(
                        new LatLng(43.821833, -111.787814),
                        new LatLng(43.821937, -111.787820),
                        new LatLng(43.821925, -111.777102),
                        new LatLng(43.821823, -111.777102),

                        new LatLng(43.821833, -111.787814))
                .fillColor(Color.argb(100, 0, 255, 0))
                .strokeWidth(0)
                .visible(false);

        cityParkingOverlays.add(mGoogleMap.addPolygon(cityOverlay2));

        PolygonOptions cityOverlay3 = new PolygonOptions()
                .add(
                        new LatLng(43.819868, -111.788381),
                        new LatLng(43.819865, -111.786600),
                        new LatLng(43.819756, -111.786598),
                        new LatLng(43.819737, -111.788774),

                        new LatLng(43.819868, -111.788381))
                .fillColor(Color.argb(100, 0, 255, 0))
                .strokeWidth(0)
                .visible(false);

        cityParkingOverlays.add(mGoogleMap.addPolygon(cityOverlay3));

        PolygonOptions cityOverlay4 = new PolygonOptions()
                .add(
                        new LatLng(43.817812, -111.788466),
                        new LatLng(43.817796, -111.786581),
                        new LatLng(43.817610, -111.786581),
                        new LatLng(43.817617, -111.788590),

                        new LatLng(43.817812, -111.788466))
                .fillColor(Color.argb(100, 0, 255, 0))
                .strokeWidth(0)
                .visible(false);

        cityParkingOverlays.add(mGoogleMap.addPolygon(cityOverlay4));

        PolygonOptions cityOverlay5 = new PolygonOptions()
                .add(
                        new LatLng(43.815706, -111.788796),
                        new LatLng(43.815706, -111.786590),
                        new LatLng(43.815547, -111.786597),
                        new LatLng(43.815552, -111.788823),

                        new LatLng(43.815706, -111.788796))
                .fillColor(Color.argb(100, 0, 255, 0))
                .strokeWidth(0)
                .visible(false);

        cityParkingOverlays.add(mGoogleMap.addPolygon(cityOverlay5));

        PolygonOptions cityOverlay6 = new PolygonOptions()
                .add(
                        new LatLng(43.819853, -111.777344),
                        new LatLng(43.819736, -111.777329),
                        new LatLng(43.819699, -111.780873),
                        new LatLng(43.819743, -111.781001),
                        new LatLng(43.819864, -111.780812),

                        new LatLng(43.819853, -111.777344))
                .fillColor(Color.argb(100, 0, 255, 0))
                .strokeWidth(0)
                .visible(false);

        cityParkingOverlays.add(mGoogleMap.addPolygon(cityOverlay6));

        PolygonOptions cityOverlay7 = new PolygonOptions()
                .add(
                        new LatLng(43.815658, -111.777951),
                        new LatLng(43.815656, -111.776206),
                        new LatLng(43.815539, -111.776206),
                        new LatLng(43.815524, -111.777964),

                        new LatLng(43.815658, -111.777951))
                .fillColor(Color.argb(100, 0, 255, 0))
                .strokeWidth(0)
                .visible(false);

        cityParkingOverlays.add(mGoogleMap.addPolygon(cityOverlay7));

        PolygonOptions cityOverlay8 = new PolygonOptions()
                .add(
                        new LatLng(43.824935, -111.786789),
                        new LatLng(43.824925, -111.786603),
                        new LatLng(43.815540, -111.786592),
                        new LatLng(43.815540, -111.786809),

                        new LatLng(43.824935, -111.786789))
                .fillColor(Color.argb(100, 0, 255, 0))
                .strokeWidth(0)
                .visible(false);

        cityParkingOverlays.add(mGoogleMap.addPolygon(cityOverlay8));

        PolygonOptions cityOverlay9 = new PolygonOptions()
                .add(
                        new LatLng(43.824949, -111.783938),
                        new LatLng(43.824949, -111.783686),
                        new LatLng(43.821936, -111.783720),
                        new LatLng(43.821926, -111.783932),

                        new LatLng(43.824949, -111.783938))
                .fillColor(Color.argb(100, 0, 255, 0))
                .strokeWidth(0)
                .visible(false);

        cityParkingOverlays.add(mGoogleMap.addPolygon(cityOverlay9));

        PolygonOptions cityOverlay10 = new PolygonOptions()
                .add(
                        new LatLng(43.824860, -111.782474),
                        new LatLng(43.824860, -111.782316),
                        new LatLng(43.821929, -111.782327),
                        new LatLng(43.821929, -111.782469),

                        new LatLng(43.824860, -111.782474))
                .fillColor(Color.argb(100, 0, 255, 0))
                .strokeWidth(0)
                .visible(false);

        cityParkingOverlays.add(mGoogleMap.addPolygon(cityOverlay10));

        PolygonOptions cityOverlay11 = new PolygonOptions()
                .add(
                        new LatLng(43.824885, -111.781069),
                        new LatLng(43.824885, -111.780835),
                        new LatLng(43.819860, -111.780825),
                        new LatLng(43.819710, -111.780943),
                        new LatLng(43.819841, -111.781062),

                        new LatLng(43.824885, -111.781069))
                .fillColor(Color.argb(100, 0, 255, 0))
                .strokeWidth(0)
                .visible(false);

        cityParkingOverlays.add(mGoogleMap.addPolygon(cityOverlay11));

        PolygonOptions cityOverlay12 = new PolygonOptions()
                .add(
                        new LatLng(43.819796, -111.779577),
                        new LatLng(43.821841, -111.779585),
                        new LatLng(43.821831, -111.779465),
                        new LatLng(43.819842, -111.779459),

                        new LatLng(43.819796, -111.779577))
                .fillColor(Color.argb(100, 0, 255, 0))
                .strokeWidth(0)
                .visible(false);

        cityParkingOverlays.add(mGoogleMap.addPolygon(cityOverlay12));

        PolygonOptions cityOverlay13 = new PolygonOptions()
                .add(
                        new LatLng(43.824848, -111.778172),
                        new LatLng(43.819851, -111.778168),
                        new LatLng(43.819851, -111.778002),
                        new LatLng(43.824828, -111.777960),

                        new LatLng(43.824848, -111.778172))
                .fillColor(Color.argb(100, 0, 255, 0))
                .strokeWidth(0)
                .visible(false);

        cityParkingOverlays.add(mGoogleMap.addPolygon(cityOverlay13));
    }

    /**
     * Setup the marker and overlay lists for bus stop information
     */
    private void setupBusStops() {

        MarkerOptions optionsSLEBYUI = new MarkerOptions()
                .title("Address: BYU-Idaho: 310 S. 1st W.")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_shuttle))
                .position(new LatLng(43.819562, -111.786720))
                .snippet("Location: Load West Side of the BYU-Idaho's Hart Building (Curbside) by Green Benches")
                .visible(false);

        MarkerOptions optionsSLEAmer = new MarkerOptions()
                .title("Address: Americ Inn:1098 Golden Beauty Dr")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_shuttle))
                .position(new LatLng(43.803378, -111.811678))
                .snippet("Location: Load in Front of AmericInn's Main Door, near the Canopy")
                .visible(false);

        MarkerOptions optionsSLEDepot = new MarkerOptions()
                .title("Address: Transit Depot: 1276 North Hwy 33")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_shuttle))
                .position(new LatLng(43.826070, -111.805766))
                .snippet("Location: Load near front door of Transit Depot building")
                .visible(false);

        busStopsSLE.add(mGoogleMap.addMarker(optionsSLEBYUI));
        busStopsSLE.add(mGoogleMap.addMarker(optionsSLEAmer));
        busStopsSLE.add(mGoogleMap.addMarker(optionsSLEDepot));

        MarkerOptions optionsWM1 = new MarkerOptions()
                .title("Wal-Mart Stop")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_bus))
                .position(new LatLng(43.855088, -111.775761))
                .snippet("Times: 10:00am, 10:45am, 11:30am, 12:15pm, 1:00pm, 1:45pm, 2:30pm, " +
                        "3:15pm, 4:00pm, 4:45pm, 5:30pm, 6:15pm, 7:00pm, 7:45pm, 8:30pm, 9:15pm")
                .visible(false);

        MarkerOptions optionsWM2 = new MarkerOptions()
                .title("Rexburg Floral Stop")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_bus))
                .position(new LatLng(43.830246, -111.784227))
                .snippet("Times: 10:30am, 11:15am, 12:00pm, 12:45pm, 1:30pm, 2:15pm, 3:00pm, " +
                        "3:45pm, 4:30pm, 5:15pm, 6:00pm, 6:45pm, 7:30pm, 8:15pm, 9:00pm, 9:45pm")
                .visible(false);

        MarkerOptions optionsWM3 = new MarkerOptions()
                .title("BYU-Idaho Parking Lot Stop (Across from Snow Building)")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_bus))
                .position(new LatLng(43.822385, -111.783905))
                .snippet("Times: 10:27am, 11:12am, 11:57am, 12:42pm, 1:27pm, 2:12pm, 2:57pm, " +
                        "3:42pm, 4:27pm, 5:12pm, 5:57pm, 6:42pm, 7:27pm, 8:12pm, 8:57pm, 9:42pm")
                .visible(false);

        MarkerOptions optionsWM4 = new MarkerOptions()
                .title("BYU-Idaho Hart Building Stop")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_bus))
                .position(new LatLng(43.819528, -111.786707))
                .snippet("Times: 10:24am, 11:09am, 11:54am, 12:39pm, 1:24pm, 2:09pm, 2:54pm, " +
                        "3:39pm, 4:24pm, 5:09pm, 5:54pm, 6:39pm, 7:24pm, 8:09pm, 8:54pm, 9:39pm")
                .visible(false);

        MarkerOptions optionsWM5 = new MarkerOptions()
                .title("Colonial House Stop")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_bus))
                .position(new LatLng(43.817658, -111.788767))
                .snippet("Times: 10:22am, 11:07am, 11:52am, 12:37pm, 1:22pm, 2:07pm, 2:52pm, " +
                        "3:37pm, 4:22pm, 5:07pm, 5:52pm, 6:37pm, 7:22pm, 8:07pm, 8:52pm, 9:37pm")
                .visible(false);

        MarkerOptions optionsWM6 = new MarkerOptions()
                .title("East M.C. Circle Stop")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_bus))
                .position(new LatLng(43.817702, -111.780977))
                .snippet("Times: 10:09am, 10:54am, 11:39am, 12:24pm, 1:09pm, 1:54pm, 2:39pm, " +
                        "3:24pm, 4:09pm, 4:54pm, 5:39pm, 6:24pm, 7:09pm, 7:54pm, 8:39pm, 9:24pm")
                .visible(false);

        MarkerOptions optionsWM7 = new MarkerOptions()
                .title("Camden Apartments Stop")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_bus))
                .position(new LatLng(43.815582, -111.790468))
                .snippet("Times: 10:20am, 11:05am, 11:50am, 12:35pm, 1:20pm, 2:05pm, 2:50pm, " +
                        "3:35pm, 4:20pm, 5:05pm, 5:50pm, 6:35pm, 7:20pm, 8:05pm, 8:50pm, 9:35pm")
                .visible(false);

        MarkerOptions optionsWM8 = new MarkerOptions()
                .title("Aspen Village Apartments Stop")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_bus))
                .position(new LatLng(43.814001, -111.776890))
                .snippet("Times: 10:12am, 10:57am, 11:42am, 12:27pm, 1:12pm, 1:57pm, 2:42pm, " +
                        "3:27pm, 4:12pm, 4:57pm, 5:42pm, 6:27pm, 7:12pm, 7:57pm, 8:42pm, 9:27pm")
                .visible(false);

        MarkerOptions optionsWM9 = new MarkerOptions()
                .title("The Gates Apartments Stop")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_bus))
                .position(new LatLng(43.813533, -111.794237))
                .snippet("Times: 10:19am, 11:04am, 11:49am, 12:34pm, 1:19pm, 2:04pm, 2:49pm, " +
                        "3:34pm, 4:19pm, 5:04pm, 5:49pm, 6:34pm, 7:19pm, 8:04pm, 8:49pm, 9:34pm")
                .visible(false);

        MarkerOptions optionsWM10 = new MarkerOptions()
                .title("Centre Square Apartments Stop")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_bus))
                .position(new LatLng(43.811524, -111.786688))
                .snippet("Times: 10:15am, 11:00am, 11:45am, 12:30pm, 1:15pm, 2:00pm, 2:45pm, " +
                        "3:30pm, 4:15pm, 5:00pm, 5:45pm, 6:30pm, 7:15pm, 8:00pm, 8:45pm, 9:30pm")
                .visible(false);

        busStopsWalMart.add(mGoogleMap.addMarker(optionsWM1));
        busStopsWalMart.add(mGoogleMap.addMarker(optionsWM2));
        busStopsWalMart.add(mGoogleMap.addMarker(optionsWM3));
        busStopsWalMart.add(mGoogleMap.addMarker(optionsWM4));
        busStopsWalMart.add(mGoogleMap.addMarker(optionsWM5));
        busStopsWalMart.add(mGoogleMap.addMarker(optionsWM6));
        busStopsWalMart.add(mGoogleMap.addMarker(optionsWM7));
        busStopsWalMart.add(mGoogleMap.addMarker(optionsWM8));
        busStopsWalMart.add(mGoogleMap.addMarker(optionsWM9));
        busStopsWalMart.add(mGoogleMap.addMarker(optionsWM10));

        PolygonOptions WMRoute1 = new PolygonOptions()
                .add(
                        new LatLng(43.854889, -111.775625),
                        new LatLng(43.854902, -111.777941),
                        new LatLng(43.845048, -111.778108),
                        new LatLng(43.845030, -111.777897),
                        new LatLng(43.854832, -111.777791),
                        new LatLng(43.854817, -111.775559),

                        new LatLng(43.854889, -111.775625))
                .fillColor(Color.argb(100, 0, 0, 0))
                .strokeWidth(0)
                .visible(false);

        busRouteWalMart.add(mGoogleMap.addPolygon(WMRoute1));
        busRouteWalMart.add(mGoogleMap.addPolygon(WMRoute1));

        PolygonOptions WMRoute2 = new PolygonOptions()
                .add(
                        new LatLng(43.845030, -111.777897),
                        new LatLng(43.845035, -111.778099),
                        new LatLng(43.843134, -111.778412),
                        new LatLng(43.843072, -111.778240),

                        new LatLng(43.845030, -111.777897))
                .fillColor(Color.argb(100, 0, 0, 0))
                .strokeWidth(0)
                .visible(false);

        busRouteWalMart.add(mGoogleMap.addPolygon(WMRoute2));
        busRouteWalMart.add(mGoogleMap.addPolygon(WMRoute2));

        PolygonOptions WMRoute3 = new PolygonOptions()
                .add(
                        new LatLng(43.843072, -111.778240),
                        new LatLng(43.840418, -111.777983),
                        new LatLng(43.840418, -111.778139),
                        new LatLng(43.843095, -111.778433),

                        new LatLng(43.843072, -111.778240))
                .fillColor(Color.argb(100, 0, 0, 0))
                .strokeWidth(0)
                .visible(false);

        busRouteWalMart.add(mGoogleMap.addPolygon(WMRoute3));
        busRouteWalMart.add(mGoogleMap.addPolygon(WMRoute3));

        PolygonOptions WMRoute4 = new PolygonOptions()
                .add(
                        new LatLng(43.840431, -111.777966),
                        new LatLng(43.817740, -111.778032),
                        new LatLng(43.817740, -111.780881),
                        new LatLng(43.817678, -111.780876),
                        new LatLng(43.817674, -111.780959),
                        new LatLng(43.817792, -111.780959),
                        new LatLng(43.817799, -111.778109),
                        new LatLng(43.840434, -111.778133),

                        new LatLng(43.840431, -111.777966))
                .fillColor(Color.argb(100, 0, 0, 0))
                .strokeWidth(0)
                .visible(false);

        busRouteWalMart.add(mGoogleMap.addPolygon(WMRoute4));
        busRouteWalMart.add(mGoogleMap.addPolygon(WMRoute4));

        PolygonOptions WMRoute5 = new PolygonOptions()
                .add(
                        new LatLng(43.817686, -111.780969),
                        new LatLng(43.817674, -111.779557),
                        new LatLng(43.817686, -111.778034),
                        new LatLng(43.814121, -111.778057),
                        new LatLng(43.814156, -111.777451),
                        new LatLng(43.814137, -111.777231),
                        new LatLng(43.814091, -111.777016),
                        new LatLng(43.814087, -111.776903),
                        new LatLng(43.814002, -111.776839),
                        new LatLng(43.813913, -111.776898),
                        new LatLng(43.813901, -111.777048),
                        new LatLng(43.813955, -111.777139),
                        new LatLng(43.814017, -111.777305),
                        new LatLng(43.814032, -111.777520),
                        new LatLng(43.813974, -111.778158),
                        new LatLng(43.817611, -111.778125),
                        new LatLng(43.817623, -111.780965),

                        new LatLng(43.817686, -111.780969))
                .fillColor(Color.argb(100, 0, 0, 0))
                .strokeWidth(0)
                .visible(false);

        busRouteWalMart.add(mGoogleMap.addPolygon(WMRoute5));
        busRouteWalMart.add(mGoogleMap.addPolygon(WMRoute5));

        PolygonOptions WMRoute6 = new PolygonOptions()
                .add(
                        new LatLng(43.811603, -111.778048),
                        new LatLng(43.811540, -111.788035),
                        new LatLng(43.811467, -111.788032),
                        new LatLng(43.811533, -111.778037),

                        new LatLng(43.811603, -111.778048))
                .fillColor(Color.argb(100, 0, 0, 0))
                .strokeWidth(0)
                .visible(false);

        busRouteWalMart.add(mGoogleMap.addPolygon(WMRoute6));
        busRouteWalMart.add(mGoogleMap.addPolygon(WMRoute6));

        PolygonOptions WMRoute7 = new PolygonOptions()
                .add(
                        new LatLng(43.813981, -111.778068),
                        new LatLng(43.813989, -111.778148),
                        new LatLng(43.811595, -111.778132),
                        new LatLng(43.811587, -111.778046),

                        new LatLng(43.813981, -111.778068))
                .fillColor(Color.argb(100, 0, 0, 0))
                .strokeWidth(0)
                .visible(false);

        busRouteWalMart.add(mGoogleMap.addPolygon(WMRoute7));
        busRouteWalMart.add(mGoogleMap.addPolygon(WMRoute7));

        PolygonOptions WMRoute9 = new PolygonOptions()
                .add(
                        new LatLng(43.811528, -111.788037),
                        new LatLng(43.811724, -111.794206),
                        new LatLng(43.811639, -111.794190),
                        new LatLng(43.811466, -111.788026),

                        new LatLng(43.811528, -111.788037))
                .fillColor(Color.argb(100, 0, 0, 0))
                .strokeWidth(0)
                .visible(false);

        busRouteWalMart.add(mGoogleMap.addPolygon(WMRoute9));
        busRouteWalMart.add(mGoogleMap.addPolygon(WMRoute9));

        PolygonOptions WMRoute10 = new PolygonOptions()
                .add(
                        new LatLng(43.811639, -111.794190),
                        new LatLng(43.813515, -111.794177),
                        new LatLng(43.813511, -111.793037),
                        new LatLng(43.813403, -111.792468),
                        new LatLng(43.813577, -111.792366),
                        new LatLng(43.815580, -111.792388),
                        new LatLng(43.815602, -111.789478),
                        new LatLng(43.817110, -111.789498),
                        new LatLng(43.817114, -111.789600),
                        new LatLng(43.815669, -111.789585),
                        new LatLng(43.815647, -111.792473),
                        new LatLng(43.813589, -111.792470),
                        new LatLng(43.813488, -111.792518),
                        new LatLng(43.813573, -111.793038),
                        new LatLng(43.813577, -111.794286),
                        new LatLng(43.811638, -111.794313),

                        new LatLng(43.811639, -111.794190))
                .fillColor(Color.argb(100, 0, 0, 0))
                .strokeWidth(0)
                .visible(false);

        busRouteWalMart.add(mGoogleMap.addPolygon(WMRoute10));
        busRouteWalMart.add(mGoogleMap.addPolygon(WMRoute10));

        PolygonOptions WMRoute11 = new PolygonOptions()
                .add(
                        new LatLng(43.817110, -111.789498),
                        new LatLng(43.817555, -111.789439),
                        new LatLng(43.817632, -111.789337),
                        new LatLng(43.817690, -111.788543),
                        new LatLng(43.817675, -111.786649),
                        new LatLng(43.821816, -111.786632),
                        new LatLng(43.821819, -111.783779),
                        new LatLng(43.830304, -111.783770),
                        new LatLng(43.830308, -111.784591),
                        new LatLng(43.830238, -111.784596),
                        new LatLng(43.830238, -111.783861),
                        new LatLng(43.821881, -111.783873),
                        new LatLng(43.821889, -111.786733),
                        new LatLng(43.817738, -111.786737),
                        new LatLng(43.817746, -111.788604),
                        new LatLng(43.817684, -111.789409),
                        new LatLng(43.817605, -111.789525),
                        new LatLng(43.817117, -111.789605),
                        new LatLng(43.817110, -111.789498),

                        new LatLng(43.817110, -111.789498))
                .fillColor(Color.argb(100, 0, 0, 0))
                .strokeWidth(0)
                .visible(false);

        busRouteWalMart.add(mGoogleMap.addPolygon(WMRoute11));
        busRouteWalMart.add(mGoogleMap.addPolygon(WMRoute11));

        PolygonOptions WMRoute12 = new PolygonOptions()
                .add(
                        new LatLng(43.830242, -111.783797),
                        new LatLng(43.830231, -111.778085),
                        new LatLng(43.830301, -111.778090),
                        new LatLng(43.830307, -111.783870),

                        new LatLng(43.830242, -111.783797))
                .fillColor(Color.argb(100, 0, 0, 0))
                .strokeWidth(0)
                .visible(false);

        busRouteWalMart.add(mGoogleMap.addPolygon(WMRoute12));
        busRouteWalMart.add(mGoogleMap.addPolygon(WMRoute12));
    }

    /**
     * Setup the marker and overlay lists for parking information
     */
    private void setupMotoATVParking() {
        MarkerOptions moto1 = new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_moto))
                .position(new LatLng(43.816082, -111.783699))
                .visible(false);

        MarkerOptions moto2 = new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_moto))
                .position(new LatLng(43.816644, -111.783379))
                .visible(false);

        MarkerOptions moto3 = new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_moto))
                .position(new LatLng(43.817286, -111.783529))
                .visible(false);

        MarkerOptions moto4 = new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_moto))
                .position(new LatLng(43.815217, -111.783704))
                .visible(false);

        MarkerOptions moto5 = new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_moto))
                .position(new LatLng(43.813812, -111.783695))
                .visible(false);

        MarkerOptions moto6 = new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_moto))
                .position(new LatLng(43.813609, -111.783700))
                .visible(false);

        MarkerOptions moto7 = new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_moto))
                .position(new LatLng(43.813017, -111.783702))
                .visible(false);

        MarkerOptions moto8 = new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_moto))
                .position(new LatLng(43.812647, -111.783693))
                .visible(false);

        MarkerOptions moto9 = new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_moto))
                .position(new LatLng(43.812497, -111.783705))
                .visible(false);

        motorcycleParkingMarkers.add(mGoogleMap.addMarker(moto1));
        motorcycleParkingMarkers.add(mGoogleMap.addMarker(moto2));
        motorcycleParkingMarkers.add(mGoogleMap.addMarker(moto3));
        motorcycleParkingMarkers.add(mGoogleMap.addMarker(moto4));
        motorcycleParkingMarkers.add(mGoogleMap.addMarker(moto5));
        motorcycleParkingMarkers.add(mGoogleMap.addMarker(moto6));
        motorcycleParkingMarkers.add(mGoogleMap.addMarker(moto7));
        motorcycleParkingMarkers.add(mGoogleMap.addMarker(moto8));
        motorcycleParkingMarkers.add(mGoogleMap.addMarker(moto9));
    }

    /**
     * Setup the marker and overlay lists for bike rack information
     */
    private void setupBikeRacks() {
        MarkerOptions bikes1 = new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_bike))
                .position(new LatLng(43.821518, -111.783595))
                .visible(false);

        MarkerOptions bikes2 = new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_bike))
                .position(new LatLng(43.821046, -111.783772))
                .visible(false);

        MarkerOptions bikes3 = new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_bike))
                .position(new LatLng(43.821340, -111.782849))
                .visible(false);

        MarkerOptions bikes4 = new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_bike))
                .position(new LatLng(43.821092, -111.783106))
                .visible(false);

        MarkerOptions bikes5 = new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_bike))
                .position(new LatLng(43.820689, -111.784082))
                .visible(false);

        MarkerOptions bikes6 = new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_bike))
                .position(new LatLng(43.820449, -111.783519))
                .visible(false);

        MarkerOptions bikes7 = new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_bike))
                .position(new LatLng(43.820197, -111.783573))
                .visible(false);

        MarkerOptions bikes8 = new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_bike))
                .position(new LatLng(43.820801, -111.782715))
                .visible(false);

        MarkerOptions bikes9 = new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_bike))
                .position(new LatLng(43.820933, -111.782103))
                .visible(false);

        MarkerOptions bikes10 = new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_bike))
                .position(new LatLng(43.821138, -111.781819))
                .visible(false);

        MarkerOptions bikes11 = new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_bike))
                .position(new LatLng(43.820557, -111.782076))
                .visible(false);

        MarkerOptions bikes12 = new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_bike))
                .position(new LatLng(43.820274, -111.782757))
                .visible(false);

        MarkerOptions bikes13 = new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_bike))
                .position(new LatLng(43.819980, -111.781979))
                .visible(false);

        MarkerOptions bikes14 = new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_bike))
                .position(new LatLng(43.819790, -111.781963))
                .visible(false);

        MarkerOptions bikes15 = new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_bike))
                .position(new LatLng(43.819747, -111.782564))
                .visible(false);

        MarkerOptions bikes16 = new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_bike))
                .position(new LatLng(43.819654, -111.782832))
                .visible(false);

        MarkerOptions bikes17 = new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_bike))
                .position(new LatLng(43.819925, -111.783208))
                .visible(false);

        MarkerOptions bikes18 = new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_bike))
                .position(new LatLng(43.819321, -111.781727))
                .visible(false);

        MarkerOptions bikes19 = new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_bike))
                .position(new LatLng(43.819693, -111.780134))
                .visible(false);

        MarkerOptions bikes20 = new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_bike))
                .position(new LatLng(43.819662, -111.778621))
                .visible(false);

        MarkerOptions bikes21 = new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_bike))
                .position(new LatLng(43.819384, -111.782648))
                .visible(false);

        MarkerOptions bikes22 = new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_bike))
                .position(new LatLng(43.819028, -111.782844))
                .visible(false);

        MarkerOptions bikes23 = new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_bike))
                .position(new LatLng(43.818803, -111.781793))
                .visible(false);

        MarkerOptions bikes24 = new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_bike))
                .position(new LatLng(43.819407, -111.783971))
                .visible(false);

        MarkerOptions bikes25 = new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_bike))
                .position(new LatLng(43.818989, -111.784025))
                .visible(false);

        MarkerOptions bikes26 = new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_bike))
                .position(new LatLng(43.819825, -111.784776))
                .visible(false);

        MarkerOptions bikes27 = new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_bike))
                .position(new LatLng(43.819833, -111.785924))
                .visible(false);

        MarkerOptions bikes28 = new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_bike))
                .position(new LatLng(43.819175, -111.785345))
                .visible(false);

        MarkerOptions bikes29 = new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_bike))
                .position(new LatLng(43.818509, -111.783532))
                .visible(false);

        MarkerOptions bikes30 = new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_bike))
                .position(new LatLng(43.818075, -111.783897))
                .visible(false);

        MarkerOptions bikes31 = new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_bike))
                .position(new LatLng(43.817897, -111.784637))
                .visible(false);

        MarkerOptions bikes32 = new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_bike))
                .position(new LatLng(43.817502, -111.784143))
                .visible(false);

        MarkerOptions bikes33 = new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_bike))
                .position(new LatLng(43.817223, -111.785216))
                .visible(false);

        MarkerOptions bikes34 = new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_bike))
                .position(new LatLng(43.816333, -111.786096))
                .visible(false);

        MarkerOptions bikes35 = new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_bike))
                .position(new LatLng(43.816126, -111.785070))
                .visible(false);

        MarkerOptions bikes36 = new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_bike))
                .position(new LatLng(43.815963, -111.785363))
                .visible(false);

        MarkerOptions bikes37 = new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_bike))
                .position(new LatLng(43.816087, -111.784129))
                .visible(false);

        MarkerOptions bikes38 = new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_bike))
                .position(new LatLng(43.815630, -111.784022))
                .visible(false);

        MarkerOptions bikes39 = new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_bike))
                .position(new LatLng(43.814931, -111.785168))
                .visible(false);

        MarkerOptions bikes40 = new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_bike))
                .position(new LatLng(43.814334, -111.785128))
                .visible(false);

        MarkerOptions bikes41 = new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_bike))
                .position(new LatLng(43.814663, -111.784204))
                .visible(false);

        MarkerOptions bikes42 = new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_bike))
                .position(new LatLng(43.813843, -111.783433))
                .visible(false);

        MarkerOptions bikes43 = new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_bike))
                .position(new LatLng(43.815609, -111.783503))
                .visible(false);

        MarkerOptions bikes44 = new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_bike))
                .position(new LatLng(43.816029, -111.783128))
                .visible(false);

        MarkerOptions bikes45 = new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_bike))
                .position(new LatLng(43.814838, -111.782174))
                .visible(false);

        MarkerOptions bikes46 = new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_bike))
                .position(new LatLng(43.815081, -111.780693))
                .visible(false);

        MarkerOptions bikes47 = new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_bike))
                .position(new LatLng(43.816610, -111.782751))
                .visible(false);

        MarkerOptions bikes48 = new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_bike))
                .position(new LatLng(43.815870, -111.780283))
                .visible(false);

        MarkerOptions bikes49 = new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_bike))
                .position(new LatLng(43.816862, -111.781182))
                .visible(false);

        MarkerOptions bikes50 = new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_bike))
                .position(new LatLng(43.817346, -111.781218))
                .visible(false);

        MarkerOptions bikes51 = new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_bike))
                .position(new LatLng(43.817215, -111.782148))
                .visible(false);

        MarkerOptions bikes52 = new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_bike))
                .position(new LatLng(43.817130, -111.782929))
                .visible(false);

        MarkerOptions bikes53 = new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_bike))
                .position(new LatLng(43.818074, -111.782915))
                .visible(false);

        MarkerOptions bikes54 = new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_bike))
                .position(new LatLng(43.818066, -111.783360))
                .visible(false);

        MarkerOptions bikes55 = new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_bike))
                .position(new LatLng(43.818104, -111.781697))
                .visible(false);

        bikeRacks.add(mGoogleMap.addMarker(bikes1));
        bikeRacks.add(mGoogleMap.addMarker(bikes2));
        bikeRacks.add(mGoogleMap.addMarker(bikes3));
        bikeRacks.add(mGoogleMap.addMarker(bikes4));
        bikeRacks.add(mGoogleMap.addMarker(bikes5));
        bikeRacks.add(mGoogleMap.addMarker(bikes6));
        bikeRacks.add(mGoogleMap.addMarker(bikes7));
        bikeRacks.add(mGoogleMap.addMarker(bikes8));
        bikeRacks.add(mGoogleMap.addMarker(bikes9));
        bikeRacks.add(mGoogleMap.addMarker(bikes10));
        bikeRacks.add(mGoogleMap.addMarker(bikes11));
        bikeRacks.add(mGoogleMap.addMarker(bikes12));
        bikeRacks.add(mGoogleMap.addMarker(bikes13));
        bikeRacks.add(mGoogleMap.addMarker(bikes14));
        bikeRacks.add(mGoogleMap.addMarker(bikes15));
        bikeRacks.add(mGoogleMap.addMarker(bikes16));
        bikeRacks.add(mGoogleMap.addMarker(bikes17));
        bikeRacks.add(mGoogleMap.addMarker(bikes18));
        bikeRacks.add(mGoogleMap.addMarker(bikes19));
        bikeRacks.add(mGoogleMap.addMarker(bikes20));
        bikeRacks.add(mGoogleMap.addMarker(bikes21));
        bikeRacks.add(mGoogleMap.addMarker(bikes22));
        bikeRacks.add(mGoogleMap.addMarker(bikes23));
        bikeRacks.add(mGoogleMap.addMarker(bikes24));
        bikeRacks.add(mGoogleMap.addMarker(bikes25));
        bikeRacks.add(mGoogleMap.addMarker(bikes26));
        bikeRacks.add(mGoogleMap.addMarker(bikes27));
        bikeRacks.add(mGoogleMap.addMarker(bikes28));
        bikeRacks.add(mGoogleMap.addMarker(bikes29));
        bikeRacks.add(mGoogleMap.addMarker(bikes30));
        bikeRacks.add(mGoogleMap.addMarker(bikes31));
        bikeRacks.add(mGoogleMap.addMarker(bikes32));
        bikeRacks.add(mGoogleMap.addMarker(bikes33));
        bikeRacks.add(mGoogleMap.addMarker(bikes34));
        bikeRacks.add(mGoogleMap.addMarker(bikes35));
        bikeRacks.add(mGoogleMap.addMarker(bikes36));
        bikeRacks.add(mGoogleMap.addMarker(bikes37));
        bikeRacks.add(mGoogleMap.addMarker(bikes38));
        bikeRacks.add(mGoogleMap.addMarker(bikes39));
        bikeRacks.add(mGoogleMap.addMarker(bikes40));
        bikeRacks.add(mGoogleMap.addMarker(bikes41));
        bikeRacks.add(mGoogleMap.addMarker(bikes42));
        bikeRacks.add(mGoogleMap.addMarker(bikes43));
        bikeRacks.add(mGoogleMap.addMarker(bikes44));
        bikeRacks.add(mGoogleMap.addMarker(bikes45));
        bikeRacks.add(mGoogleMap.addMarker(bikes46));
        bikeRacks.add(mGoogleMap.addMarker(bikes47));
        bikeRacks.add(mGoogleMap.addMarker(bikes48));
        bikeRacks.add(mGoogleMap.addMarker(bikes49));
        bikeRacks.add(mGoogleMap.addMarker(bikes51));
        bikeRacks.add(mGoogleMap.addMarker(bikes52));
        bikeRacks.add(mGoogleMap.addMarker(bikes53));
        bikeRacks.add(mGoogleMap.addMarker(bikes54));
        bikeRacks.add(mGoogleMap.addMarker(bikes55));

    }

    /**
     * Setup the marker and overlay lists for building information
     */
    private void setupBuildings() {
        MarkerOptions optionsCampHouse = new MarkerOptions()
                .title("Center Square Apartments")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_building))
                .position(new LatLng(43.813190, -111.787116))
                .snippet("A housing complex for male and female students at Brigham Young University-Idaho.")
                .visible(false);

        MarkerOptions optionsBYUICenter = new MarkerOptions()
                .title("BYU-Idaho Center")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_building))
                .position(new LatLng(43.818500, -111.784946))
                .snippet("Dedicated in December 2010 after approximately three years of construction, the BYU-Idaho Center has become the university's central gathering place.")
                .visible(false);

        MarkerOptions optionsMC = new MarkerOptions()
                .title("Hyrum Manwaring Student Center")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_building))
                .position(new LatLng(43.818423, -111.782558))
                .snippet("Recently remodeled, named after president who guided BYU–Idaho through the Great Depression and WWII.")
                .visible(false);

        MarkerOptions optionsHart = new MarkerOptions()
                .title("John W. Hart Building")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_building))
                .position(new LatLng(43.819549, -111.785162))
                .snippet("Named after prominent Idaho state legislator who served on the school’s Board of Education for over 20 years, the last 8 as chairman.")
                .visible(false);

        MarkerOptions optionsLibrary = new MarkerOptions()
                .title("David O. McKay Library")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_building))
                .position(new LatLng(43.819498, -111.782852))
                .snippet("The library was named for the ninth President of the Church, who was also a prominent educator.")
                .visible(false);

        MarkerOptions optionsSmith = new MarkerOptions()
                .title("Joseph Fielding Smith Building")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_building))
                .position(new LatLng(43.819187, -111.781442))
                .snippet("This building was named for the 10th President of the Church.")
                .visible(false);

        MarkerOptions optionsRomney = new MarkerOptions()
                .title("George S. Romney Building")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_building))
                .position(new LatLng(43.820217, -111.783177))
                .snippet("This building was named for George Romney, who become president of Ricks Academy in 1917.")
                .visible(false);

        MarkerOptions optionsSnow = new MarkerOptions()
                .title("Eliza R. Snow Performing Arts Center")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_building))
                .position(new LatLng(43.821320, -111.783607))
                .snippet("Eliza R. Snow was a prolific historian and writer of poetry who served as Relief Society's 2nd president.")
                .visible(false);

        MarkerOptions optionsSpori = new MarkerOptions()
                .title("Jacob Spori Arts & Letters Building")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_building))
                .position(new LatLng(43.820822, -111.782411))
                .snippet("Named for the school's first principal, who served March 26, 1847 – September 27, 1903.")
                .visible(false);

        MarkerOptions optionsClark = new MarkerOptions()
                .title("John L. Clarke Building")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_building))
                .position(new LatLng(43.820222, -111.781732))
                .snippet("This building honors the man who led the institution through an unprecedented 27-year period of expansion in enrollment and campus facilities.")
                .visible(false);

        MarkerOptions optionsKirkham = new MarkerOptions()
                .title("Kirkham Auditorium")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_building))
                .position(new LatLng(43.821078, -111.781556))
                .snippet("The building was named for a member of the Church’s Presidency of the Seventy.")
                .visible(false);

        MarkerOptions optionsBiddulph = new MarkerOptions()
                .title("Lowell G. Biddulph Building")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_building))
                .position(new LatLng(43.817078, -111.785155))
                .snippet("This building was named for the first full-time Dean of Students at Ricks College.")
                .visible(false);

        MarkerOptions optionsRigby = new MarkerOptions()
                .title("William F. Rigby Building")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_building))
                .position(new LatLng(43.817088, -111.784447))
                .snippet("This building was named for William F. Rigby, a prominent settler and Church leader in eastern Idaho.")
                .visible(false);

        MarkerOptions optionsAustin = new MarkerOptions()
                .title("Mark Austin Technical & Engineering Building")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_building))
                .position(new LatLng(43.815796, -111.784451))
                .snippet("Named for a philanthropist and humanitarian who helped establish sugar factories in the western U.S.")
                .visible(false);

        MarkerOptions optionsBenson = new MarkerOptions()
                .title("Ezra Taft Benson Agricultural & Biological Sciences Building")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_building))
                .position(new LatLng(43.815478, -111.783235))
                .snippet("Named for the Church's 13th president.")
                .visible(false);

        MarkerOptions optionsSTC = new MarkerOptions()
                .title("Science & Technology Center")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_building))
                .position(new LatLng(43.814654, -111.784691))
                .snippet("BYU-Idaho's Learning Model inspired the design of the newest building on campus.")
                .visible(false);

        MarkerOptions optionsAux = new MarkerOptions()
                .title("Auxiliary Services Building")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_building))
                .position(new LatLng(43.814122, -111.783065))
                .snippet("This building was originally used as a College Press, central warehouse, and purchasing department. It is one of the few un-dedicated buildings on campus.")
                .visible(false);

        MarkerOptions optionsRicks = new MarkerOptions()
                .title("Thomas E. Ricks Building")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_building))
                .position(new LatLng(43.814845, -111.781206))
                .snippet("Named after the school's founder. BYUI was once named Bannock Stake Academy until Ricks’ death in 1902, when it was renamed Ricks Academy.")
                .visible(false);

        MarkerOptions optionsHinckley = new MarkerOptions()
                .title("Gordon B Hinckley Building")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_building))
                .position(new LatLng(43.815866, -111.779840))
                .snippet("This building was named after Gordon B. Hinckley, the 15th President of the Church.")
                .visible(false);

        MarkerOptions optionsUComm = new MarkerOptions()
                .title("University Communications Building")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_building))
                .position(new LatLng(43.817309, -111.779362))
                .snippet("Houses KBYI FM, the college-owned MPR station and KBYR-FM, a station used by communication majors.")
                .visible(false);

        MarkerOptions optionsHealth = new MarkerOptions()
                .title("Student Health & Counselling Center")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_building))
                .position(new LatLng(43.816833, -111.779251))
                .snippet("Completed in 2004, open to students, spouses or dependents of students, or students a health plan.")
                .visible(false);

        MarkerOptions optionsTaylor = new MarkerOptions()
                .title("John Taylor Building")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_building))
                .position(new LatLng(43.816956, -111.782485))
                .snippet("The Taylor Building was named after the 3rd President of the Church, whom Joseph Smith referred to as the 'Defender of the Faith.'")
                .visible(false);

        MarkerOptions optionsKimball = new MarkerOptions()
                .title("Spencer W. Kimball Building")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_building))
                .position(new LatLng(43.817097, -111.781487))
                .snippet("This Student and Administrative Services building was named for the twelfth president of the Church.")
                .visible(false);

        MarkerOptions optionsChapman = new MarkerOptions()
                .title("Chapman Hall")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_building))
                .position(new LatLng(43.818200, -111.780311))
                .visible(false);

        MarkerOptions optionsLampreght = new MarkerOptions()
                .title("Lampreght Hall")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_building))
                .position(new LatLng(43.818218, -111.779506))
                .visible(false);

        MarkerOptions options4thWard = new MarkerOptions()
                .title("4th Ward Meetinghouse")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_building))
                .position(new LatLng(43.822294, -111.781386))
                .visible(false);

        MarkerOptions optionsStadium = new MarkerOptions()
                .title("BYU-Idaho Stadium")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_building))
                .position(new LatLng(43.820948, -111.785985))
                .snippet("The Stadium Field consists of an outdoor track and an artificial turf used for football, frisbee, and soccer.")
                .visible(false);

        MarkerOptions optionsEnergy = new MarkerOptions()
                .title("BYU-Idaho Central Energy Facility")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_building))
                .position(new LatLng(43.817027, -111.785884))
                .snippet("The new Central Energy Facility was designed and built to support the university for the next 50 years.")
                .visible(false);

        MarkerOptions optionsRecycling = new MarkerOptions()
                .title("BYU-Idaho Recycling Center")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_building))
                .position(new LatLng(43.814898, -111.787806))
                .snippet("Started in 2010, Recycling Center seeks for increasingly innovative ways to create student employment opportunities and promote sustainability.")
                .visible(false);

        MarkerOptions optionsTemple = new MarkerOptions()
                .title("Rexburg, Idaho Temple")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_building))
                .position(new LatLng(43.810919, -111.779099))
                .snippet("The Rexburg Idaho Temple is the 125th operating temple of The Church of Jesus Christ of Latter-day Saints.")
                .visible(false);

        MarkerOptions options1stStake = new MarkerOptions()
                .title("1st Stake Meetinghouse")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_building))
                .position(new LatLng(43.810737, -111.781360))
                .visible(false);

        MarkerOptions optionsUniVillage = new MarkerOptions()
                .title("University Village Housing")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_building))
                .position(new LatLng(43.809873, -111.788530))
                .snippet("University Village is owned and operated by BYU-Idaho and serves as the only on-campus student family housing.")
                .visible(false);

        MarkerOptions options3rdStake = new MarkerOptions()
                .title("3rd Stake Meetinghouse")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_building))
                .position(new LatLng(43.810774, -111.791430))
                .visible(false);

        MarkerOptions optionsOperations = new MarkerOptions()
                .title("University Operations")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_building))
                .position(new LatLng(43.815761, -111.785974))
                .snippet("Facilities Management. Construction Management Services. Security & Safety.")
                .visible(false);

        buildings.add(mGoogleMap.addMarker(optionsBYUICenter));
        buildings.add(mGoogleMap.addMarker(optionsMC));
        buildings.add(mGoogleMap.addMarker(optionsHart));
        buildings.add(mGoogleMap.addMarker(optionsLibrary));
        buildings.add(mGoogleMap.addMarker(optionsSmith));
        buildings.add(mGoogleMap.addMarker(optionsRomney));
        buildings.add(mGoogleMap.addMarker(optionsSpori));
        buildings.add(mGoogleMap.addMarker(optionsClark));
        buildings.add(mGoogleMap.addMarker(optionsKirkham));
        buildings.add(mGoogleMap.addMarker(optionsBiddulph));
        buildings.add(mGoogleMap.addMarker(optionsRigby));
        buildings.add(mGoogleMap.addMarker(optionsAustin));
        buildings.add(mGoogleMap.addMarker(optionsBenson));
        buildings.add(mGoogleMap.addMarker(optionsSTC));
        buildings.add(mGoogleMap.addMarker(optionsAux));
        buildings.add(mGoogleMap.addMarker(optionsRicks));
        buildings.add(mGoogleMap.addMarker(optionsHinckley));
        buildings.add(mGoogleMap.addMarker(optionsUComm));
        buildings.add(mGoogleMap.addMarker(optionsHealth));
        buildings.add(mGoogleMap.addMarker(optionsSnow));
        buildings.add(mGoogleMap.addMarker(optionsTaylor));
        buildings.add(mGoogleMap.addMarker(optionsKimball));
        buildings.add(mGoogleMap.addMarker(optionsChapman));
        buildings.add(mGoogleMap.addMarker(optionsLampreght));
        buildings.add(mGoogleMap.addMarker(options4thWard));
        buildings.add(mGoogleMap.addMarker(optionsStadium));
        buildings.add(mGoogleMap.addMarker(optionsEnergy));
        buildings.add(mGoogleMap.addMarker(optionsRecycling));
        buildings.add(mGoogleMap.addMarker(optionsTemple));
        buildings.add(mGoogleMap.addMarker(options1stStake));
        buildings.add(mGoogleMap.addMarker(optionsUniVillage));
        buildings.add(mGoogleMap.addMarker(optionsCampHouse));
        buildings.add(mGoogleMap.addMarker(optionsUniVillage));
        buildings.add(mGoogleMap.addMarker(options3rdStake));
        buildings.add(mGoogleMap.addMarker(optionsOperations));
    }

    /**
     * Centers navigation angle to cdertain position, including
     * a zoom level.
     *
     * @param lat
     * @param lng
     * @param zoom
     */
    private void goToLocationZoom(double lat, double lng, float zoom) {
        LatLng ll = new LatLng(lat, lng);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, zoom);
        Log.i(TAG, "Updating Googl Map object.");
        mGoogleMap.animateCamera(update);
    }


    /**
     * This method handles navigation view item click.
     *
     * @param item
     * @return
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.markers_buildings) {
            for(Marker building : buildings) {
                if(building.isVisible()) {
                    building.setVisible(false);
                } else {
                    building.setVisible(true);
                }
            }
            if(buildings.get(0).isVisible()) {
                Toast.makeText(this, "Showing Buildings Around Campus", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Hiding Buildings Around Campus", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.markers_north_parking) {
            for(Marker lot : northParkingMarkers) {
                if(lot.isVisible()) {
                    lot.setVisible(false);
                } else {
                    lot.setVisible(true);
                }
            }
            for(Polygon zone : northParkingOverlays) {
                if(zone.isVisible()) {
                    zone.setVisible(false);
                } else {
                    zone.setVisible(true);
                }
            }

            if(northParkingMarkers.get(0).isVisible()) {
                Toast.makeText(this, "Showing North Zone Parking\n             (Color: Azure)", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Hiding North Zone Parking", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.markers_south_parking) {
            for(Marker lot : southParkingMarkers) {
                if(lot.isVisible()) {
                    lot.setVisible(false);
                } else {
                    lot.setVisible(true);
                }
            }

            for(Polygon zone : southParkingOverlays) {
                if(zone.isVisible()) {
                    zone.setVisible(false);
                } else {
                    zone.setVisible(true);
                }
            }

            if(southParkingMarkers.get(0).isVisible()) {
                Toast.makeText(this, "Showing South Zone Parking\n            (Color: Yellow)", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Hiding South Zone Parking", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.markers_vistitor_parking) {
            for(Marker lot : visitorParkingParkers) {
                if(lot.isVisible()) {
                    lot.setVisible(false);
                } else {
                    lot.setVisible(true);
                }
            }
            if(visitorParkingParkers.get(0).isVisible()) {
                Toast.makeText(this, "Showing Visitor Parking\n           (Color: Blue)", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Hiding Visitor Parking", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.markers_city_parking) {
            for(Polygon zone : cityParkingOverlays) {
                if(zone.isVisible()) {
                    zone.setVisible(false);
                } else {
                    zone.setVisible(true);
                }
            }

            if(cityParkingOverlays.get(0).isVisible()) {
                Toast.makeText(this, "Showing City Street Parking\n        (Color: Neon Green)", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Hiding City Street Parking", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.markers_accessible_parking) {
            Toast.makeText(this, "Accessible Parking", Toast.LENGTH_LONG).show();
        } else if (id == R.id.markers_limited_parking) {
            Toast.makeText(this, "Limited Time Parking", Toast.LENGTH_LONG).show();
        } else if (id == R.id.markers_moto_parking) {
            for(Marker lot : motorcycleParkingMarkers) {
                if(lot.isVisible()) {
                    lot.setVisible(false);
                } else {
                    lot.setVisible(true);
                }
            }
            if(motorcycleParkingMarkers.get(0).isVisible()) {
                Toast.makeText(this, "Showing Motorcycles, Scooters & ATVs Parking", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Hiding Motorcycles, Scooters & ATVs Parking", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.markers_free_parking) {
            for(Marker lot : freeParkingMarkers) {
                if(lot.isVisible()) {
                    lot.setVisible(false);
                } else {
                    lot.setVisible(true);
                }
            }
            if(freeParkingMarkers.get(0).isVisible()) {
                Toast.makeText(this, "Showing Free Parking\n     (Color: Magenta)", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Hiding Free Parking", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.markers_long_overnight_parking) {
            for(Marker lot : longTermParkingMarkers) {
                if(lot.isVisible()) {
                    lot.setVisible(false);
                } else {
                    lot.setVisible(true);
                }
            }
            if(longTermParkingMarkers.get(0).isVisible()) {
                Toast.makeText(this, "Showing Long Term & Overnight Parking\n                       (Color: Violet)", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Hiding Long Term & Overnight Parking", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.markers_faculty_parking) {
            for(Marker lot : facultyParkingMarkers) {
                if(lot.isVisible()) {
                    lot.setVisible(false);
                } else {
                    lot.setVisible(true);
                }
            }

            for(Polygon zone : facultyParkingOverlays) {
                if(zone.isVisible()) {
                    zone.setVisible(false);
                } else {
                    zone.setVisible(true);
                }
            }

            if(facultyParkingMarkers.get(0).isVisible()) {
                Toast.makeText(this, "Showing Faculty Parking\n           (Color: Red)", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Hiding Faculty Parking", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.markers_child_lab_parking) {
            for(Marker lot : childLabMarkers) {
                if(lot.isVisible()) {
                    lot.setVisible(false);
                } else {
                    lot.setVisible(true);
                }
            }
            if(childLabMarkers.get(0).isVisible()) {
                Toast.makeText(this, "Showing Child Lab Parking\n            (Color: Cyan)", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Hiding Child Lab Parking", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.markers_campus_housing_parking) {
            for(Marker lot : campusHousingParkingMarkers) {
                if(lot.isVisible()) {
                    lot.setVisible(false);
                } else {
                    lot.setVisible(true);
                }
            }
            if(campusHousingParkingMarkers.get(0).isVisible()) {
                Toast.makeText(this, "Showing On-Campus Housing Parking\n                     (Color: Green)", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Hiding On-Campus Housing Parking", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.markers_meetinghouse_4th_ward_parking) {
            for(Marker lot : fourthWardMarkers) {
                if(lot.isVisible()) {
                    lot.setVisible(false);
                } else {
                    lot.setVisible(true);
                }
            }
            if(fourthWardMarkers.get(0).isVisible()) {
                Toast.makeText(this, "Showing 4th Ward Meetinghouse Parking\n                       (Color: Orange)", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Hiding 4th Ward Meetinghouse Parking", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.markers_bike_racks) {
            for(Marker lot : bikeRacks) {
                if(lot.isVisible()) {
                    lot.setVisible(false);
                } else {
                    lot.setVisible(true);
                }
            }
            if(bikeRacks.get(0).isVisible()) {
                Toast.makeText(this, "Showing Bike Racks Around Campus", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Hiding Bike Racks Around Campus", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.markers_bus_walmart) {
            for(Marker stop : busStopsWalMart) {
                if(stop.isVisible()) {
                    stop.setVisible(false);
                } else {
                    stop.setVisible(true);
                }
            }

            for(Polygon route : busRouteWalMart) {
                if(route.isVisible()) {
                    route.setVisible(false);
                } else {
                    route.setVisible(true);
                }
            }

            if(busStopsWalMart.get(0).isVisible()) {
                Toast.makeText(this, "Showing Wal-Mart Shuttle Stops and Route\n                         (Color: Black)", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Hiding Wal-Mart Shuttle Bus Stops", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.markers_bus_sle) {
            for(Marker stop : busStopsSLE) {
                if(stop.isVisible()) {
                    stop.setVisible(false);
                } else {
                    stop.setVisible(true);
                }
            }
            if(busStopsSLE.get(0).isVisible()) {
                Toast.makeText(this, "Showing Salt Lake Express Bus Stops", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Hiding Salt Lake Express Bus Stops", Toast.LENGTH_SHORT).show();
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}