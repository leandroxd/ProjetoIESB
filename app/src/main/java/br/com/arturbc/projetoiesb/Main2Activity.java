package br.com.arturbc.projetoiesb;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class Main2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        final Button btnSair = findViewById(R.id.btnSair);

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
    }
}
