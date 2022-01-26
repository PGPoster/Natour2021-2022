package View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.library.BuildConfig;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;
import org.osmdroid.views.overlay.Marker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ViewUtil.GPSUtil;

public class NuovoPercorso_1 extends AppCompatActivity implements MapEventsReceiver {

    private Toolbar mToolbar;
    private IMapController mapController;
    private Geocoder geocoder;
    private MapView mMapView;
    private ScaleBarOverlay mScaleBarOverlay;
    private LocationManager locationMangaer;
    private MyLocationNewOverlay myLocationoverlay;
    private LayoutInflater inflater;
    private LinearLayout tappe;
    private char ntappe = 'A';
    private static final String MY_USER_AGENT = BuildConfig.APPLICATION_ID+"/"+BuildConfig.VERSION_NAME;
    private RoadManager roadManager;
    private ArrayList<GeoPoint> percorso = new ArrayList<>(10);
    private FloatingActionButton myLocationButton;
    private float[] lastTouchXY = new float[2];
    private MapEventsOverlay mapEventsOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        //configurazione mappa
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        setContentView(R.layout.activity_nuovo_percorso1);

        //configurazione RoadManager
        roadManager = new OSRMRoadManager(this, MY_USER_AGENT);
        ((OSRMRoadManager)roadManager).setMean(OSRMRoadManager.MEAN_BY_FOOT);

        //configurazione partenza
        /*inflater = getLayoutInflater();
        tappe = findViewById(R.id.layout_nuovopercorso_tappe);
        LinearLayout v = (LinearLayout) inflater.inflate(R.layout.linearlayout_tappa, null);
        Log.d("TIPO", v.getClass().toString());
        TextInputEditText partenza = findViewById(R.id.edit_nuovopercorso_iniziopercorso);
        ((MaterialTextView)v.findViewById(R.id.tappa_letter)).setText(ntappe++ + ":");
        aggiungiLogicaTappa(v);
        ((TextInputLayout)v.findViewById(R.id.edit_nuovopercorso_iniziopercorsolayout)).setEndIconVisible(false);
        tappe.addView(v,tappe.getChildCount()-2);*/

        geocoder = new Geocoder(this);


        //crea mappa
        mMapView = (MapView) findViewById(R.id.map);
        mMapView.setTileSource(TileSourceFactory.MAPNIK);
        mMapView.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.NEVER);
        mMapView.setMultiTouchControls(true);
        mMapView.setScrollableAreaLimitLatitude(MapView.getTileSystem().getMaxLatitude(), MapView.getTileSystem().getMinLatitude(), 0);
        mMapView.setMinZoomLevel(10.0);
        mMapView.setMaxZoomLevel(19.0);


        //Opzioni Mappa
        mapController = mMapView.getController();
        mapController.setZoom(16.0);
        GeoPoint startPoint = new GeoPoint(40.85,14.12);
        mMapView.getTileProvider().clearTileCache();
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
            GPSUtil.getCurrentLocation(this, locationMangaer,myLocationoverlay);
        });
        mMapView.getOverlays().add(myLocationoverlay);
        mapEventsOverlay = new MapEventsOverlay(this);
        mMapView.getOverlays().add(0, mapEventsOverlay);
        /*mMapView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN){

            }
            return true;
        });
        mMapView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                return false;
            }
        });*/


        //Appbar
        mToolbar = (Toolbar) findViewById(R.id.nuovoprofilo_app_bar);
        setSupportActionBar(mToolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setTitle("Nuovo percorso");
        actionbar.setDisplayHomeAsUpEnabled(true);

        //Searchbox
        EditText searchbox = findViewById(R.id.edit_nuovopercorso_iniziopercorso);
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
                        marker.setOnMarkerClickListener((marker1, mapView) -> {
                            mapController.animateTo(marker1.getPosition());
                            mapController.setZoom(17.5);
                            return false;
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
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
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

/*    public void aggiungiTappa(View view) {
        View v = inflater.inflate(R.layout.linearlayout_tappa, null);
        ((MaterialTextView)v.findViewById(R.id.tappa_letter)).setText(ntappe++ + ":");
        aggiungiLogicaTappa((LinearLayout) v);
        tappe.addView(v,tappe.getChildCount()-2
        );
    }*/
    private void aggiungiLogicaTappa(LinearLayout l){
        TextInputLayout textlayout = l.findViewById(R.id.edit_nuovopercorso_iniziopercorsolayout);
        TextInputEditText text = l.findViewById(R.id.edit_nuovopercorso_iniziopercorso);

        text.setOnEditorActionListener((view, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                //TODO Cambia posizione partenza se trova risultato
                try {
                    List<Address> liAddress = geocoder.getFromLocationName(text.getText().toString(), 1);
                    if(liAddress.size() > 0){
                        List<Overlay> overlays = mMapView.getOverlays();
                        for(Overlay o : overlays){
                            if(o instanceof Marker && ((Marker) o).getId() == "Ultima ricerca"){
                                overlays.remove(o);
                                break;
                            }
                        }
                        Address address = liAddress.get(0);
                        GeoPoint centerPoint = new GeoPoint(address.getLatitude(), address.getLongitude());
                        //TODO Modifica percorso quando cambi la tappa
                        percorso.add(centerPoint);
                        Log.d("Percorso", "Aggiunto nuovo punto al percorso");
                        mapController.setCenter(centerPoint);
                        text.setText(address.getAddressLine(0));
                        Marker marker = new Marker(mMapView);
                        //TODO serve id per identificare le posizioni
                        marker.setPosition(centerPoint);
                        marker.setOnMarkerClickListener((marker1, mapView) -> {
                            mapController.animateTo(marker1.getPosition());
                            mapController.setZoom(15.5);
                            return false;
                        });
                        marker.setIcon(ResourcesCompat.getDrawable(getResources(),R.drawable.ic_baseline_location_on_24, null));
                        marker.setAnchor(Marker.ANCHOR_CENTER,Marker.ANCHOR_CENTER);
                        // Creo strada su mappa
                        if (percorso.size() > 1) {
                            Log.d("Percorso", "Generazione percorso");
                            Road road = roadManager.getRoad(percorso);
                            Polyline roadOverlay = RoadManager.buildRoadOverlay(road);
                            roadOverlay.setTitle("Percorso");
                            mMapView.getOverlays().add(roadOverlay);
                        }
                        mMapView.getOverlays().add(marker);
                        mMapView.invalidate();
                    }
                    else{
                        Toast.makeText(this,"Luogo non trovato", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return true;
            }
            else{
                text.clearFocus();
                //TODO Ripristina valore precedente dal Marker
                return false;
            }
        });
        textlayout.setEndIconOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(NuovoPercorso_1.this);
            builder.setMessage("Eliminare tappa?");
            builder.setPositiveButton("Si",(dialog,which)->{
                //TODO Rimuovi tappa dal percorso e aggiorna le altre tappe
                dialog.dismiss();
            });
            builder.setNegativeButton("No",(dialog,which)->{
                dialog.dismiss();
            });
            builder.create().show();
        Log.d("Bottone", "Premuto");
        });
    }

    public void importaGPX(View view) {
        //TODO Importa file GPX
    }

    public void nuovoPercorsoDettagliPremuto(View view) {
        //TODO
        Log.d("FAB", "Dettagli percorso");
    }

    @Override
    public boolean singleTapConfirmedHelper(GeoPoint p) {
        return false;
    }

    @Override
    public boolean longPressHelper(GeoPoint p) {
        Marker marker = new Marker(mMapView);
        marker.setPosition(p);
        marker.setId(String.valueOf(ntappe++));
        marker.setInfoWindow(null);
        Log.d("Percorso", "Tappa " + marker.getId() + " aggiunta.");
        marker.setAnchor(Marker.ANCHOR_CENTER,Marker.ANCHOR_CENTER);
        percorso.add(p);
        // setto icona tappa
        if(percorso.size() > 1) {
            marker.setIcon(ResourcesCompat.getDrawable(getResources(),R.drawable.ic_baseline_tappa_24, null));
            Road road = roadManager.getRoad(percorso);
            Polyline route = new Polyline(mMapView);
            route.setColor(ResourcesCompat.getColor(getResources(),R.color.myGreen_100_50,null));
            route.setPoints(percorso);
            route.setTitle("Percorso");
            mMapView.getOverlays().add(route);
        }
        else marker.setIcon(ResourcesCompat.getDrawable(getResources(),R.drawable.ic_baseline_flag_circle_24, null));
        mMapView.getOverlays().add(marker);
        mMapView.invalidate();
        return false;
    }

    public void nuovoTracciatoPremuto(View view) {
        if(percorso.size() > 0){
            AlertDialog.Builder builder = new AlertDialog.Builder(NuovoPercorso_1.this);
            builder.setMessage("Il percorso precedentemente tracciato sarà eliminato\nProcedere?");
            builder.setPositiveButton("Si",(dialog,which)->{
                percorso.clear();
                List<Overlay> overlays = mMapView.getOverlays();
/*                ntappe--;
                for(Overlay o : overlays){
                    if(o instanceof Marker && ((Marker) o).getId() == String.valueOf(ntappe)){
                        ntappe--;
                        overlays.remove(o);
                        ((Marker) o).remove(mMapView);
                        Log.d("Percorso", "Marker cancellato");
                    }
                }*/
                resetOverlays(overlays);
                mMapView.invalidate();

                dialog.dismiss();
            });
            builder.setNegativeButton("No",(dialog,which)->{
                dialog.dismiss();
            });
            builder.create().show();
            Log.d("Bottone", "Premuto");
        }
    }

    private void resetOverlays(List<Overlay> overlays) {
        overlays.clear();
        overlays.add(0, mapEventsOverlay);
        overlays.add(mScaleBarOverlay);
        overlays.add(myLocationoverlay);
        ntappe = 'A';

    }
}