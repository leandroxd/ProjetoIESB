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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

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

        //carregar();

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
                gravar("Teste Dono","Teste Destinatario");
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
    }

    private void carregar() {
        FirebaseFirestore.getInstance().collection("conversa")
                .orderBy("destinatario", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        List<DocumentChange> documentChanges = queryDocumentSnapshots.getDocumentChanges();

                        if(documentChanges != null){
                            for (DocumentChange doc: documentChanges) {
                                if(doc.getType() == DocumentChange.Type.ADDED){
                                    Conversa c = doc.getDocument().toObject(Conversa.class);
                                    listaConversa.add(c);
                                }
                            }
                        }
                    }
                });
    }

    private void gravar(String dono, String destinatario) {
        Conversa c = new Conversa();

        c.setDono(dono);
        c.setDestinatario(destinatario);

        FirebaseFirestore.getInstance().collection("conversa")
                .add(c)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("Sucesso enviar msg:", documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Falha enviar msg", e.getMessage(), e);
                    }
                });
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
