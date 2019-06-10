package br.com.arturbc.projetoiesb;

//Version Controll - Version 2.1
//1.0 - Login
//2.0 - Maps
//2.1 - Login and Maps functional

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private static final int ERROR_DIALOG_REQUEST = 9001;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText txtEmail = findViewById(R.id.txtEmail);
        final EditText pwSenha = findViewById(R.id.pwSenha);

        final Button btnLogin = findViewById(R.id.btnLogin);
        final Button btnCadastrar = findViewById(R.id.btnCadastrar);

        btnLogin.setClickable(false);
        btnCadastrar.setClickable(false);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = txtEmail.getText().toString();
                String senha = pwSenha.getText().toString();

                if(!email.equals("") && !senha.equals("")) {
                    btnLogin.setClickable(false);
                    btnCadastrar.setClickable(false);

                    login(email, senha, MainActivity.this);
                }
            }
        });

        btnCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cadastro(MainActivity.this);

            }

        });
        if(isServicesOK()){
            init();
        }

        tentarAcessar(MainActivity.this, null);

    }


    private void login(@NonNull String email, @NonNull String senha, final Activity atividade) {
        final FirebaseAuth autenticado = FirebaseAuth.getInstance();
        Task<AuthResult> tarefa = autenticado.signInWithEmailAndPassword(email, senha);
        tarefa.addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> tarefa) {
                if(tarefa.isSuccessful()) {
                    tentarAcessar(MainActivity.this, autenticado);
                } else {
                    Toast.makeText(atividade, "Falha na autenticação", Toast.LENGTH_LONG).show();
                    findViewById(R.id.btnLogin).setClickable(true);
                    findViewById(R.id.btnCadastrar).setClickable(true);
                }
            }
        });
    }


    private void cadastro( final Activity atividade) {
        Intent novaAtividade = new Intent(atividade, Main3Activity.class);
        startActivity(novaAtividade);
    }

    private void tentarAcessar (final Activity atividade, FirebaseAuth autenticado) {
        if(autenticado == null)
            autenticado = FirebaseAuth.getInstance();

        FirebaseUser usuario = autenticado.getCurrentUser();

        if(usuario != null) {
            Toast.makeText(atividade, usuario.getUid() + " / " + usuario.getEmail(), Toast.LENGTH_LONG).show();
            Intent novaAtividade = new Intent(atividade, Main2Activity.class);
            startActivity(novaAtividade);
            finish();
        } else {
            Toast.makeText(atividade, "Não logado", Toast.LENGTH_LONG).show();
            findViewById(R.id.btnLogin).setClickable(true);
            findViewById(R.id.btnCadastrar).setClickable(true);
        }
    }

    private void init(){
        Button btnMapa = (Button) findViewById(R.id.btnMapa);
        btnMapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MapActivity.class);
                startActivity(intent);

            }
        });


    }
    public boolean isServicesOK(){
        Log.d(TAG, "isServicesOK: Checando a versão do Google Services");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);

        if(available == ConnectionResult.SUCCESS){
            //Tudo bem e o usuario pode fazer requisicoes de mapa
            Log.d(TAG, "isServicesOK: Google Play Services esta funcionando");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //Tem erro mas e resolvivel
            Log.d(TAG, "isServicesOK: Um erro ocorreu mas podemos arruma-lo");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            Toast.makeText(this, "Voce nao pode fazer requisicoes de mapa", Toast.LENGTH_SHORT).show();
        }
        return false;
    }
}
