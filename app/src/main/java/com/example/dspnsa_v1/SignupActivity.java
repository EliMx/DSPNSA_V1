package com.example.dspnsa_v1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignupActivity  extends AppCompatActivity {
    //Componentes del registro
    private EditText  EditTextNombre, EditTextEmail, EditTextContrasena;
    private Button buttonRegistrar;
    private Button loginButton;

    //variables del registro
    private String nombre = "";
    private String email = "";
    private String password = "";

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mDatabaseReference = mDatabase.getReference();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();

        EditTextNombre = (EditText) findViewById(R.id.inputNombre);
        EditTextEmail = (EditText) findViewById(R.id.inputEmail);
        EditTextContrasena = (EditText) findViewById(R.id.inputContrasena);
        buttonRegistrar = (Button) findViewById(R.id.registroButton);
        loginButton = (Button) findViewById(R.id.loginButton);

        buttonRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nombre = EditTextNombre.getText().toString();
                email = EditTextEmail.getText().toString().trim();
                password = EditTextContrasena.getText().toString();

                if (!nombre.isEmpty() && !email.isEmpty() && !password.isEmpty()){
                    if (password.length() >= 6){
                        registrarUsuario();
                    }else{
                        Toast.makeText( SignupActivity.this, "La contraseña debe tener por lo menos 6 caracteres", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                finish();
            }
        });
    }
    private void registrarUsuario() {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    onAuthSuccess(task.getResult().getUser());
                }else{
                    Toast.makeText(SignupActivity.this, "No se pudo registrar de momento, intente mas tarde" + task.getException(), Toast.LENGTH_SHORT).show();
                }
            }

            private void onAuthSuccess(FirebaseUser firebaseUser) {
                Usuario user = new Usuario(nombre, email, password);
                mDatabaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        mDatabaseReference.child("Users").child(firebaseUser.getUid()).setValue(user);
                        Toast.makeText(SignupActivity.this, "informacion guardada", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(SignupActivity.this, MenuActivity.class));
                        finish();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(SignupActivity.this, "Lo sentimos, su informacion no pudo ser almacenada. Error: " + error, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
