package br.com.arturbc.projetoiesb.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.auth.FirebaseAuth;

import br.com.arturbc.projetoiesb.R;
import br.com.arturbc.projetoiesb.fragments.MapActivity;

public class Main2Activity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private static final int ERROR_DIALOG_REQUEST = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        final Button btnSair = findViewById(R.id.btnSair);
        final Button btnMapa = findViewById(R.id.btnMapa);

        btnSair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final FirebaseAuth autenticado = FirebaseAuth.getInstance();
                final Activity atividade = Main2Activity.this;

                autenticado.signOut();

                Intent novaAtividade = new Intent(atividade, MainActivity.class);
                startActivity(novaAtividade);
                finish();
            }
        });

        if(isServicesOK()){
            btnMapa.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Main2Activity.this, MapActivity.class);
                    startActivity(intent);
                }
            });
        }
    }

    public boolean isServicesOK(){
        Log.d(TAG, "isServicesOK: Checando a versão do Google Services");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(Main2Activity.this);

        if(available == ConnectionResult.SUCCESS){
            //Tudo bem e o usuario pode fazer requisicoes de mapa
            Log.d(TAG, "isServicesOK: Google Play Services está funcionando");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //Tem erro mas e resolvivel
            Log.d(TAG, "isServicesOK: Um erro ocorreu, mas podemos arruma-lo");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(Main2Activity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else{
            Toast.makeText(this, "Você não pode fazer requisicoes de mapa", Toast.LENGTH_SHORT).show();
        }

        return false;
    }
}
