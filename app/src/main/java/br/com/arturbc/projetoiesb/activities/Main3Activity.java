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

public class Main3Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        final EditText txtEmail = findViewById(R.id.txtEmail);
        final EditText pwSenha = findViewById(R.id.pwSenha);
        final EditText pwSenhaOk = findViewById(R.id.pwSenhaOk);

        final Button btnConfirmar = findViewById(R.id.btnConfirmar);
        final Button btnCancelar = findViewById(R.id.btnCancelar);

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = txtEmail.getText().toString();
                String senha = pwSenha.getText().toString();
                String senhaOk = pwSenhaOk.getText().toString();

                if(!email.equals("") && !senha.equals("")) {
                    btnConfirmar.setClickable(false);
                    btnCancelar.setClickable(false);
                    Activity atividade = Main3Activity.this;

                    if(senha.equals(senhaOk)) {
                        cadastro(email, senha, atividade);
                    } else {
                        Toast.makeText(atividade, "As senhas não são iguais", Toast.LENGTH_LONG).show();
                        btnConfirmar.setClickable(true);
                        btnCancelar.setClickable(true);
                    }
                }
            }
        });
    }

    private void cadastro(@NonNull String email, @NonNull String senha, final Activity atividade) {
        final FirebaseAuth autenticado = FirebaseAuth.getInstance();
        Task<AuthResult> tarefa = autenticado.createUserWithEmailAndPassword(email, senha);
        tarefa.addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> tarefa) {
                if(tarefa.isSuccessful()) {
                    FirebaseUser usuario = autenticado.getCurrentUser();
                    Toast.makeText(atividade, usuario.getUid() + " / " + usuario.getEmail(), Toast.LENGTH_LONG).show();
                    Intent novaAtividade = new Intent(atividade, Main2Activity.class);
                    startActivity(novaAtividade);
                    finish();
                } else {
                    Toast.makeText(atividade, "Falha no cadastro", Toast.LENGTH_LONG).show();
                    findViewById(R.id.btnConfirmar).setClickable(true);
                    findViewById(R.id.btnCancelar).setClickable(true);
                }
            }
        });
    }
}
