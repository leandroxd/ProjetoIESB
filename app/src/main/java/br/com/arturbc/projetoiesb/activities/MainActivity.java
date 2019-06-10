package br.com.arturbc.projetoiesb.activities;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import br.com.arturbc.projetoiesb.R;

public class MainActivity extends AppCompatActivity {

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
}
