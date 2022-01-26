
package DB;

import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBInstance {

    private static DBInstance instance;
    private Connection connection=null;
    private final String USERNAME = "postgres";
    private final String PASSWORD = "password";
    private final String url = "jdbc:postgresql://natour-db.cfwkm0wydepu.eu-west-3.rds.amazonaws.com:5432/postgres";



    private DBInstance() throws SQLException{

        try {
            Class<?> driver = Class.forName("org.postgresql.Driver");
            Log.d("Driver", driver.getName());
            Log.d("Driver", driver.getDeclaredFields().toString());
        }
        catch (Exception e) {
            System.out.println("Errore di connesione ad DB");
        }

    }

    public Connection getConnection() throws SQLException {
        connection = DriverManager.getConnection(url, USERNAME, PASSWORD);
        return connection;
    }

    public static DBInstance getInstance() throws SQLException {
        if (instance == null)
        {
            instance = new DBInstance();
        }
        else
        if (instance.getConnection().isClosed())
        {
            instance = new DBInstance();
        }

        return instance;
    }


}
