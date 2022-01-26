package View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.config.Configuration;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.IOException;
import java.util.List;

import Logic.Controller;
import ViewUtil.GPSUtil;

public class Esplora extends AppCompatActivity {

    private Toolbar mToolbar;
    private ActionBarDrawerToggle toggle;
    private DrawerLayout drawerLayout;
    private NavigationView navView;
    private TextView nome, email;
    private MapView mMapView;
    private IMapController mapController;
    private ScaleBarOverlay mScaleBarOverlay;
    private Geocoder geocoder;
    private LocationManager locationMangaer;
    private MyLocationNewOverlay myLocationoverlay;
    private FloatingActionButton myLocationButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //configurazione mappa
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        setContentView(R.layout.activity_esplora);

        geocoder = new Geocoder(this);
        //crea mappa
        mMapView = (MapView) findViewById(R.id.map);
        mMapView.setTileSource(TileSourceFactory.HIKEBIKEMAP);
        mMapView.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.NEVER);
        mMapView.setMultiTouchControls(true);
        mMapView.setScrollableAreaLimitLatitude(MapView.getTileSystem().getMaxLatitude(), MapView.getTileSystem().getMinLatitude(), 0);
        mMapView.setMinZoomLevel(10.0);
        mMapView.setMaxZoomLevel(17.0);


        //Opzioni Mappa
        mapController = mMapView.getController();
        mapController.setZoom(14.0);
        GeoPoint startPoint = new GeoPoint(40.85,14.12);
        mapController.setCenter(startPoint);
        final DisplayMetrics dm = ctx.getResources().getDisplayMetrics();
        mScaleBarOverlay = new ScaleBarOverlay(mMapView);
        mScaleBarOverlay.setAlignBottom(true);
        mScaleBarOverlay.setEnableAdjustLength(true);
        mScaleBarOverlay.setCentred(true);
        mScaleBarOverlay.setScaleBarOffset(dm.widthPixels / 5 + 10, dm.heightPixels / 10 * 9);
        mMapView.getOverlays().add(this.mScaleBarOverlay);

        //GPS Location
        locationMangaer = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //My Location
        myLocationoverlay = new MyLocationNewOverlay(mMapView);
        myLocationoverlay.setDrawAccuracyEnabled(false);
        myLocationButton = (FloatingActionButton)findViewById(R.id.fab_esplora_mylocation);
        myLocationButton.setRippleColor(getColorStateList(R.color.facebook));
        myLocationButton.setOnClickListener((v)->{
            Log.d("FAB", "Fab location premuto");
            GPSUtil.getCurrentLocation(this,locationMangaer,myLocationoverlay);
        });
        mMapView.getOverlays().add(myLocationoverlay);

        EditText searchbox = findViewById(R.id.edit_esplora_search);
        searchbox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(searchbox.length() == 0) searchbox.clearFocus();
            }
        });
        searchbox.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                try {
                    Log.d("Searchbox", "Effettuo ricerca di " + searchbox.getText().toString());
                    List<Address> liAddress = geocoder.getFromLocationName(searchbox.getText().toString(), 1);
                    if(liAddress.size() > 0){
                        List<Overlay> overlays = mMapView.getOverlays();
                        for(Overlay o : overlays){
                            if(o instanceof Marker && ((Marker) o).getTitle() == "Ultima ricerca"){
                                overlays.remove(o);
                                break;
                            }
                        }
                        Address address = liAddress.get(0);
                        GeoPoint centerPoint = new GeoPoint(address.getLatitude(), address.getLongitude());
                        mapController.setCenter(centerPoint);
                        searchbox.setText(address.getAddressLine(0));
                        Marker marker = new Marker(mMapView);
                        marker.setTitle("Ultima ricerca");
                        marker.setPosition(centerPoint);
                        marker.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
                            @Override
                            public boolean onMarkerClick(Marker marker, MapView mapView) {
                                mapController.animateTo(marker.getPosition());
                                mapController.setZoom(15.5);
                                return false;
                            }
                        });
                        marker.setIcon(ResourcesCompat.getDrawable(getResources(),R.drawable.ic_baseline_location_on_24, null));
                        marker.setAnchor(Marker.ANCHOR_CENTER,Marker.ANCHOR_CENTER);
                        mMapView.getOverlays().add(marker);
                        mMapView.invalidate();
                    }
                    else{
                        Toast.makeText(this,"Percorso non trovato", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                InputMethodManager imm = (InputMethodManager) getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchbox.getWindowToken(),0);
                searchbox.clearFocus();
                return true;
            }
            return false;
        });
        navigationInit();
    }

    /*private void mostraGPSDisabilitatoAlert() {
        new AlertDialog.Builder(this)
                .setTitle("GPS non attivo")
                .setMessage("Abilitare il GPS sul proprio dispositivo")
                .setCancelable(false)
                .setPositiveButton("Attiva GPS", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }

    private void getCurrentLocation() {
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            //Permesso non concesso
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)){
                //Spiega a cosa serve concedere il permesso se non viene concesso la prima volta
                new AlertDialog.Builder(this)
                        .setTitle("Permesso richiesto")
                        .setMessage("Per accedere a questa funzionalità è necessario avere accesso alla posizione del dispositivo")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(Esplora.this,
                                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
                            }
                        })
                        .setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create()
                        .show();

            } else{
                //Prima richiesta di permesso
                ActivityCompat.requestPermissions(Esplora.this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
            }
        }else{
            //Permesso già concesso, posso accedere alla posizione
            Log.d("FAB","Permesso GPS già concesso");
            //Verifico se GPS è attivo
            ActivityCompat.requestPermissions(Esplora.this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
                myLocationoverlay.enableMyLocation();
                myLocationoverlay.enableFollowLocation();
            if(locationMangaer.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                Log.d("FAB", "GPS attivo");

            }else mostraGPSDisabilitatoAlert();

        }
    }*/

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,@NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d("GPS permission", "onPermissionResult");
        if (requestCode == GPSUtil.MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("GPS permission", "Granted");
                myLocationoverlay.enableFollowLocation();
                myLocationoverlay.enableMyLocation();
            } else {
                Log.d("GPS permission", "Not granted");
            }
        }
    }
    private void navigationInit() {
        mToolbar = (Toolbar) findViewById(R.id.nuovoprofilo_app_bar);
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.acc_open, R.string.acc_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        setSupportActionBar(mToolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setTitle("Esplora Percorsi");
        actionbar.setDisplayHomeAsUpEnabled(true);

        mToolbar.setNavigationIcon(R.drawable.ic_baseline_menu_24);
        navView = (NavigationView)findViewById(R.id.nav_view);
        nome = navView.getHeaderView(0).findViewById(R.id.text_navheader_nomeprofilo);
        email = navView.getHeaderView(0).findViewById(R.id.text_navheader_email);

        nome.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                if(v.isSelected()) nome.setSelected(true);
                else{
                    nome.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                    nome.setSelected(false);
                }
                return false;
            }
        });
        email.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                if(v.isSelected()) email.setSelected(true);
                else{
                    email.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                    email.setSelected(false);
                }
                return false;
            }
        });
        navView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.item_profilo:
                    Toast.makeText(getApplicationContext(),"Visita profilo", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.item_logout:
                    Controller.logoutPremuto(this);
                    Toast.makeText(getApplicationContext(),"Log out eseguito", Toast.LENGTH_SHORT).show();
                    break;

            }
            return true;
        });
    }

    public void onResume(){
        super.onResume();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        myLocationoverlay.enableMyLocation();
        mMapView.onResume(); //needed for compass, my location overlays, v6.0.0 and up
    }

    public void onPause(){
        super.onPause();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        myLocationoverlay.disableMyLocation();
        mMapView.onPause();  //needed for compass, my location overlays, v6.0.0 and up
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(toggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void nuovoPercorsoPremuto(View view) {
        Controller.nuovoPercorsoPremuto(this);
    }
}