package br.com.arturbc.projetoiesb.activities;

//Version Controll - Version 2.1
//1.0 - Login
//2.0 - Maps
//2.1 - Login and Maps functional

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import br.com.arturbc.projetoiesb.Conversa;
import br.com.arturbc.projetoiesb.R;
import br.com.arturbc.projetoiesb.fragments.MapActivity;

public class Main2Activity extends AppCompatActivity {
    private static final String TAG = "Main2Activity";

    private static final int ERROR_DIALOG_REQUEST = 9001;

    private List<Conversa> listaConversa = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        final Button btnSair = findViewById(R.id.btnSair);
        final Button btnMapa = findViewById(R.id.btnMapa);
        final Button btnChat = findViewById(R.id.btnChat);
        final Button btnAdd = findViewById(R.id.btnAdd);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new MeuAdaptador());

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

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gravar("artur-bc@hotmail.com","teste");
            }
        });

        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Clicou no Chat: ");
                Intent intenet = new Intent(Main2Activity.this,MessagesActivity.class);

                getIntent().setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intenet);
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

        load();
    }

    private void load() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String email = user.getEmail();

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference ref = db.getReference("conversa");
        Query consulta = ref.orderByChild("dono").equalTo(email);

        consulta.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Conversa c = dataSnapshot.getChildren().iterator().next().getValue(Conversa.class);

                listaConversa.add(c);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void gravar(String dono, String destinatario) {
        Conversa c = new Conversa();

        c.setDono(dono);
        c.setDestinatario(destinatario);

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference ref = db.getReference("conversa");

        ref.push().setValue(c);
    }

    private class MeuViewHolder extends RecyclerView.ViewHolder {
        public TextView txtId;

        public MeuViewHolder(View v) {
            super(v);

            txtId = itemView.findViewById(R.id.txtId);
        }
    }

    private class MeuAdaptador extends RecyclerView.Adapter<MeuViewHolder> {

        @Override
        public MeuViewHolder onCreateViewHolder(ViewGroup pai, int viewTipo) {
            View v = LayoutInflater.from(pai.getContext()).inflate(R.layout.item_conversa_lista, pai, false);

            MeuViewHolder viewHolder = new MeuViewHolder(v);

            return viewHolder;
        }

        @Override
        public void onBindViewHolder(MeuViewHolder viewHolder, int position) {
            Conversa c = listaConversa.get(position);

            viewHolder.txtId.setText(c.getDestinatario());
        }

        @Override
        public int getItemCount() { return listaConversa.size(); }
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
