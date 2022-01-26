package View;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultCaller;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.R;

import Logic.Controller;
import de.hdodenhof.circleimageview.CircleImageView;

public class NuovoProfilo_2 extends AppCompatActivity {
    private int SELECT_PHOTO = 1;
    private ActivityResultLauncher arl;
    private CircleImageView img;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuovo_profilo2);
        mToolbar = (Toolbar) findViewById(R.id.nuovoprofilo_app_bar);
        setSupportActionBar(mToolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setTitle("Nuovo Profilo");
        actionbar.setDisplayHomeAsUpEnabled(false);
        //actionbar.setDisplayShowHomeEnabled(true);
        img = findViewById(R.id.image_nuovoprofilo_profilo);
        arl = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // Here, no request code
                        /*Intent data = result.getData();
                        Uri selectedImage = data.getData();*/
                        img.setImageURI(result.getData().getData());
                    }
                });
    }

    public void iniziaPremuto(View view) {
        Controller.iniziaPremuto(this);
    }

    public void cambiaFotoProfiloPremuto(View view) {
        Intent tInt = new Intent(Intent.ACTION_GET_CONTENT);
        tInt.setType("image/*");
        arl.launch(tInt);

    }

}