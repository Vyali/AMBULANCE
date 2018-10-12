package com.example.vyali.ambulance;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {
    EditText emailreg, passwordreg,passwordreg2;
    TextView loginText;
    Button register;
    private FirebaseAuth mAuth;
    //public static final String TAG;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        emailreg = findViewById(R.id.emailreg);
        passwordreg =findViewById(R.id.passwordreg);
        passwordreg2 = findViewById(R.id.pass2reg);
        register = findViewById(R.id.reg_button);
        loginText = findViewById(R.id.login_text);
        //TAG= "tag";


        mAuth = FirebaseAuth.getInstance();

        register.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String email = emailreg.getText().toString();
                String password = passwordreg.getText().toString();
                String repassword = passwordreg2.getText().toString();
                if(!email.equals("")){
                    //TODO check password length
                    if(password.equals(repassword)){
                        registerUser(email,password);

                    }
                    else{
                        Toast.makeText(RegisterActivity.this,"password do not match",Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Log.d("NULL","@@@@@@@NULLL");
                }
            }
        });

        loginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(intent);

            }
        });


    }

    public void registerUser(String email,String password){
        try {


            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("TAG", "createUserWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                updateUI(user);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("TAG", "createUserWithEmail:failure", task.getException());
                                Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                                updateUI(null);
                            }

                            // ...
                        }
                    });
        }
        catch (Exception e){
            Log.d("excep","fa"+e);
        }
    }


    public void updateUI(FirebaseUser user){
        if(user != null)
        Toast.makeText(this,"successfully registered",Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this,"unable to register",Toast.LENGTH_SHORT).show();

    }

}
