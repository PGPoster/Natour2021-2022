package View;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.R;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import DB.DBInstance;
import Logic.Controller;

public class SplashPage extends AppCompatActivity {


    private DBInstance dbconnection;
    private Connection connection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_splash_page);
        try {
            dbconnection = DBInstance.getInstance();
            connection = dbconnection.getConnection();
            Log.d("DB", "DB CONNESSO");
            String email = "ftkris@gmail.com";
            String password = "password";
            PreparedStatement ps= connection.prepareStatement("SELECT * FROM \"Utente\" WHERE \"Email\"=? AND \"Password\"=?");
            ps.setString(1, email);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            Log.d("UTENTE", "query eseguita" );
            Log.d("UTENTE-email", rs.getString("Email"));
            Log.d("UTENTE-password", rs.getString("password"));
            ps.close();
            connection.close();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void nuovoAccountPremuto(View view) { Controller.nuovoAccountPremuto(SplashPage.this); }
    public void AccediConEmailPremuto(View view){ Controller.accediConEmailPremuto(SplashPage.this); }
}