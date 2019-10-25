package com.user.location.validation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.solver.widgets.Snapshot;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

public class Validation_sms extends AppCompatActivity {

    String  nombre, telefono;
    TextView meNombre, meTelefono;
    EditText validation_code;
    private String mVerificationId;
    Button boton;

    private FirebaseAuth mAuth;
    private FirebaseDatabase db;
    DatabaseReference users;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_validation_sms);

        mAuth = FirebaseAuth.getInstance();


        db = FirebaseDatabase.getInstance();
        users = db.getReference("Riders");

        nombre = getIntent().getStringExtra("Nombre");
        telefono = getIntent().getStringExtra("Telefono");

        meNombre = findViewById(R.id.txNombre);
        meTelefono = findViewById(R.id.txNumero);
        validation_code=findViewById(R.id.validation);

        meNombre.setText(nombre);
        meTelefono.setText(telefono);

        recuestCode(telefono);

        boton = findViewById(R.id.button2);
        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String code = validation_code.getText().toString().trim();
                recuestCode(code);
            }
        });


    }

    ///Enviamos Mensaje de SMS
    private void recuestCode(String telefono) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber("+52"+telefono,60,
                TimeUnit.SECONDS, TaskExecutors.MAIN_THREAD, mCallbacks);
    }

    //obtenemos el codigo automaticamente

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {


        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

            String code = phoneAuthCredential.getSmsCode();
            if(code != null){

                validation_code.setText(code);
                verificationCode(code);

            }



        }

        @Override
        public void onVerificationFailed(FirebaseException e) {

            Toast.makeText(Validation_sms.this, "Error de verificacion" + e.getMessage(), Toast.LENGTH_LONG).show();

        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);

            mVerificationId = s;
        }
    };

    // Verificamos el codigo
    private void verificationCode(String code) {
        PhoneAuthCredential credencial = PhoneAuthProvider.getCredential(mVerificationId, code);
       singInWithPhoneAutCredencial(credencial);
    }

    private void singInWithPhoneAutCredencial(PhoneAuthCredential credencial) {

        mAuth.signInWithCredential(credencial).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){
                    /**Si el codigo es correcto  */

                    //creamos un login con los datos de correo y contraseña




                    Users us = new Users();

                    us.setNombre(nombre);
                    us.setTelefono(telefono);
                    us.setAcces("0");

                    //Envio de datos a la base de datos
                    users.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(us)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(Validation_sms.this,"registro completo",Toast.LENGTH_LONG).show();

                                    Intent intent = new Intent(Validation_sms.this, Welcome.class);
                                    startActivity(intent);

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Validation_sms.this,"Failure:  " + e.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    });

                    /**
                    //creacion de login con Email y Contraseña
                    mAuth.createUserWithEmailAndPassword(correo.toString(),pass.toString())
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                }
                            });*/
                }
            }
        });

    }


}
