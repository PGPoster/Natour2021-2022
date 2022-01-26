package View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;

import android.content.Context;
import android.content.res.loader.ResourcesProvider;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.example.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;

import Logic.Controller;

public class NuovoProfilo_1 extends AppCompatActivity {
    private Toolbar mToolbar;
    private EditText password, confirmpass, email;
    private TextInputLayout passlayout, confirmpasslayout, emaillayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuovo_profilo1);
        mToolbar = (Toolbar) findViewById(R.id.nuovoprofilo_app_bar);
        setSupportActionBar(mToolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setTitle("Registrazione");
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setDisplayShowHomeEnabled(true);

        // Validation mail e password
        password = findViewById(R.id.edit_nuovoprofilo_password);
        passlayout = findViewById(R.id.layout_nuovoprofilo_password);

        confirmpass = findViewById(R.id.edit_nuovoprofilo_confirmpassword);
        confirmpasslayout = findViewById(R.id.layout_nuovoprofilo_confirmpassword);

        email = findViewById(R.id.edit_nuovoprofilo_email);
        emaillayout = findViewById(R.id.layout_nuovoprofilo_email);

        passlayout.setHelperTextColor(getColorStateList(R.color.myGreen_200));
        confirmpasslayout.setHelperTextColor(getColorStateList(R.color.myGreen_200));
        emaillayout.setHelperTextColor(getColorStateList(R.color.myGreen_200));

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkValidMail(s);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() == 0){
                    emaillayout.setHelperText(null);
                    emaillayout.setError(null);
                }
            }

            private void checkValidMail(CharSequence s) {

                if(!Patterns.EMAIL_ADDRESS.matcher(s.toString()).matches()) emaillayout.setError("Inserire una mail valida");
                else {
                    emaillayout.setHelperText("Email valida");
                    emaillayout.setError(null);
                }
            }
        });
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {checkValidPassword(s);}

            private void checkValidPassword(Editable s) {
                if(s.length() == 0) {
                    passlayout.setError(null);
                    passlayout.setHelperText(null);
                }
                else if(s.length() < 8) passlayout.setError("Deve contenere almeno 8 caratteri");
                else {
                    passlayout.setError(null);
                    passlayout.setHelperText("Password valida");
                }
            }
        });
        confirmpass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {checkValidPassword(s);}

            private void checkValidPassword(Editable s) {
                if(s.length() == 0){
                    confirmpasslayout.setError(null);
                    confirmpasslayout.setHelperText(null);
                }
                else if(password.length() < 8) confirmpasslayout.setError("Inserire prima una password valida");
                else if(!s.toString().equals(password.getText().toString())) confirmpasslayout.setError("Le password non corrispondono");
                else {
                    confirmpasslayout.setError(null);
                    confirmpasslayout.setHelperText("Le password corrispondono");
                }
            }
        });
        confirmpass.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                InputMethodManager imm = (InputMethodManager) getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(confirmpass.getWindowToken(),0);
                return true;
            }
            return false;
        });

    }

    public void registratiPremuto(View view) {
        Controller.registratiPremuto(this);
    }
}