package br.com.arturbc.projetoiesb.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.nfc.Tag;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.UUID;

import br.com.arturbc.projetoiesb.R;
import br.com.arturbc.projetoiesb.User;

public class Main3Activity extends AppCompatActivity {


    private Uri mSelectedUri;
    private ImageView mImgPhoto;
    private Button mBtnSelectPhoto;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        final EditText txtEmail = findViewById(R.id.txtEmail);
        final EditText pwSenha = findViewById(R.id.pwSenha);
        final EditText pwSenhaOk = findViewById(R.id.pwSenhaOk);

        final Button btnConfirmar = findViewById(R.id.btnConfirmar);
        final Button btnCancelar = findViewById(R.id.btnCancelar);
        mBtnSelectPhoto = findViewById(R.id.btn_selected_photo);
        mImgPhoto = findViewById(R.id.image_photo);

        mBtnSelectPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPhoto();
            }
        });

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final String TAG = "OnActivityResult";

        if(requestCode == 0){
            mSelectedUri = data.getData();

            Bitmap bitmap = null;


            try{
                bitmap =  MediaStore.Images.Media.getBitmap(getContentResolver(), mSelectedUri);
                mImgPhoto.setImageDrawable(new BitmapDrawable(this.getResources(),bitmap));
                mBtnSelectPhoto.setAlpha(0);


            }catch(IOException e){
                Log.d(TAG, "onActivityResult: Falhou em carregar a imagem");
            }
        }
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
                    saveUserInFirebase();
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

    private void saveUserInFirebase() {
        String filename = UUID.randomUUID().toString();
        final StorageReference ref = FirebaseStorage.getInstance().getReference("/images" + filename);
        StorageTask<UploadTask.TaskSnapshot> taskSnapshotStorageTask = ref.putFile(mSelectedUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Log.i("onSuccess: ", uri.toString());

                                String uid = FirebaseAuth.getInstance().getUid();
                                String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                                String profileUrl = uri.toString();

                                User user = new User(uid,email,profileUrl);

                                FirebaseFirestore.getInstance().collection("users")
                                        .document(uid)
                                        .set(user)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Intent intent = new Intent(Main3Activity.this, Main2Activity.class);

                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent);
                                            }


                                        });
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Falhou em salvar: ", e.getMessage(), e);
                    }
                });
    }


    private void selectPhoto(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 0);
    }

}
