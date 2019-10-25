package com.user.location.validation;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    Button boton;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;

    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);


        auth = FirebaseAuth.getInstance();
        boton=findViewById(R.id.button);
        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //showLogin();
                Intent intent = new Intent(MainActivity.this, Welcome.class);
                                    startActivity(intent);
                                    finish();
            }
        });

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()!= null){

                }
            }
        };

    }

    private void showLogin() {

        AlertDialog.Builder dialog = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle);
        dialog.setTitle("Registro");
        dialog.setMessage("ingrese sus datos para registrarse");
        LayoutInflater inflater = LayoutInflater.from(this);
        View layout_login = inflater.inflate(R.layout.layout_login, null);
        dialog.setView(layout_login);
        final EditText mNombre = layout_login.findViewById(R.id.nombre);
        final EditText mTelefono = layout_login.findViewById(R.id.telefono);
        dialog.setPositiveButton("Registrar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intetn = new Intent(MainActivity.this, Validation_sms.class);
                intetn.putExtra("Nombre", mNombre.getText().toString());
                intetn.putExtra("Telefono", mTelefono.getText().toString());
                startActivity(intetn);
            }
        });
        dialog.show();
    }

}
