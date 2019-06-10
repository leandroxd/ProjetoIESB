package br.com.arturbc.projetoiesb.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;

import br.com.arturbc.projetoiesb.R;

public class MessagesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        verifyAuthentication();
    }

    private void verifyAuthentication() {
        if(FirebaseAuth.getInstance().getUid() == null){
           Intent intent = new Intent(MessagesActivity.this, MainActivity.class);
           intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
           startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.contats:
                break;

            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                verifyAuthentication();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
