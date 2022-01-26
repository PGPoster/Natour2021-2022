package ViewUtil;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.provider.Settings;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import View.Esplora;

public class GPSUtil {
    public static final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;

    public static void getCurrentLocation(Activity callingActivity, LocationManager locationMangaer, MyLocationNewOverlay myLocationoverlay) {
        if(ContextCompat.checkSelfPermission(callingActivity,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            Log.d("GPS", "Permesso non ancora concesso");
            //Permesso non concesso
            if(ActivityCompat.shouldShowRequestPermissionRationale(callingActivity,
                    Manifest.permission.ACCESS_COARSE_LOCATION)){
                //Spiega a cosa serve concedere il permesso se non viene concesso la prima volta
                Log.d("GPS", "Spiegazione richiesta permesso");
                new AlertDialog.Builder(callingActivity)
                        .setTitle("Permesso richiesto")
                        .setMessage("Per accedere a questa funzionalità è necessario avere accesso alla posizione del dispositivo")
                        .setPositiveButton("OK", (dialog, which) -> ActivityCompat.requestPermissions(callingActivity,
                                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION))
                        .setNegativeButton("Annulla", (dialog, which) -> dialog.dismiss())
                        .create()
                        .show();

            } else{
                //Prima richiesta di permesso
                Log.d("GPS", "Richiesta primo permesso");
                ActivityCompat.requestPermissions(callingActivity,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
            }
        }else{
            //Permesso già concesso, posso accedere alla posizione
            Log.d("GPS","Permesso GPS già concesso");
            ActivityCompat.requestPermissions(callingActivity,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
            //Verifico se GPS è attivo
            if(locationMangaer.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                Log.d("GPS", "GPS attivo");

                myLocationoverlay.enableMyLocation();
                myLocationoverlay.enableFollowLocation();
            }else mostraGPSDisabilitatoAlert(callingActivity);

        }
    }
    private static void mostraGPSDisabilitatoAlert(Activity callingActivity) {
        new AlertDialog.Builder(callingActivity)
                .setTitle("GPS non attivo")
                .setMessage("Abilitare il GPS sul proprio dispositivo")
                .setCancelable(false)
                .setPositiveButton("Attiva GPS", (dialog, which) -> {
                    callingActivity.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    dialog.dismiss();
                })
                .setNegativeButton("Annulla", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

}
