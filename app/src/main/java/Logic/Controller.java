package Logic;

import android.content.Intent;

import View.Accedi;
import View.Esplora;
import View.NuovoPercorso_1;
import View.NuovoProfilo_1;
import View.NuovoProfilo_2;
import View.SplashPage;

public class Controller {
    private NuovoProfilo_1 nuovoProfilo_1;
    private SplashPage splashPage;


    public static void nuovoAccountPremuto(SplashPage splashPage) {
        Intent tInt = new Intent(splashPage, NuovoProfilo_1.class);
        splashPage.startActivity(tInt);
    }

    public static void accediConEmailPremuto(SplashPage splashPage) {
        Intent tInt = new Intent(splashPage, Accedi.class);
        splashPage.startActivity(tInt);
    }

    public static void iniziaPremuto(NuovoProfilo_2 nuovoProfilo_2) {
        Intent tInt = new Intent(nuovoProfilo_2, Esplora.class);
        nuovoProfilo_2.startActivity(tInt);
    }

    public static void registratiPremuto(NuovoProfilo_1 nuovoProfilo_1) {
        Intent tInt = new Intent(nuovoProfilo_1, NuovoProfilo_2.class);
        nuovoProfilo_1.startActivity(tInt);
    }

    public static void logoutPremuto(Esplora esplora) {
        Intent tInt = new Intent(esplora, SplashPage.class);
        esplora.startActivity(tInt);
    }
    public static void nuovoPercorsoPremuto(Esplora esplora){
        Intent tInt = new Intent(esplora, NuovoPercorso_1.class);
        esplora.startActivity(tInt);
    }
}
