package com.example.acme_explorer.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.acme_explorer.auth.ActorService;
import com.example.acme_explorer.R;
import com.example.acme_explorer.auth.SessionManager;
import com.example.acme_explorer.data.Actor;
import com.google.android.gms.auth.api.signin.*;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.*;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Button signinButtonGoogle;
    private Button signinButtonMail;
    private Button loginButtonSignup;
    private ProgressBar progressBar;
    private TextInputLayout loginEmailParent;
    private TextInputLayout loginPassParent;
    private AutoCompleteTextView loginEmail;
    private AutoCompleteTextView loginPass;

    private static final int RC_SIGN_IN = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        progressBar = findViewById(R.id.login_progress);
        loginEmail = findViewById(R.id.login_email_et);
        loginPass = findViewById(R.id.login_pass_et);
        loginEmailParent = findViewById(R.id.login_email);
        loginPassParent = findViewById(R.id.login_pass);
        signinButtonGoogle = findViewById(R.id.login_button_google);
        signinButtonMail = findViewById(R.id.login_button_mail);
        loginButtonSignup = findViewById(R.id.login_button_register);

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_client_id))
                .requestEmail()
                .build();

        signinButtonGoogle.setOnClickListener(l -> attemptLoginGoogle(googleSignInOptions));
        signinButtonMail.setOnClickListener(v -> attemptLoginEmail());
        loginButtonSignup.setOnClickListener(l -> redirectSignUpActivity());
    }

    private void redirectSignUpActivity() {
        Intent intent = new Intent(this, SignupActivity.class);
        intent.putExtra(SignupActivity.EMAIL_PARAM, loginEmail.getText().toString());
        startActivity(intent);
    }

    private void attemptLoginGoogle(GoogleSignInOptions googleSignInOptions) {
        GoogleSignInClient googleSignIn = GoogleSignIn.getClient(this, googleSignInOptions);
        Intent signInIntent = googleSignIn.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> result = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = result.getResult(ApiException.class);
                assert account != null;

                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);

                mAuth.signInWithCredential(credential)
                        .addOnCompleteListener(this, task -> {
                            if (task.isSuccessful()) {
                                FirebaseUser user = task.getResult().getUser();
                                checkUserDatabaseLogin(user);
                            } else {
                                showErrorDialogMail();
                            }
                        });

            } catch (ApiException e) {
                showErrorDialogMail();
            }
        }
    }

    private void attemptLoginEmail() {
        loginEmailParent.setError(null);
        loginPassParent.setError(null);

        if (loginEmail.getText().length() == 0) {
            loginEmailParent.setErrorEnabled(true);
            loginEmailParent.setError(getString(R.string.login_mail_error_1));
        } else if (loginPass.getText().length() == 0) {
            loginPassParent.setErrorEnabled(true);
            loginPassParent.setError(getString(R.string.login_mail_error_2));
        } else {
            hideLoginButton(true); // ✅ Primero mostramos el progreso
            signInEmail();         // Luego iniciamos sesión
        }
    }

    private void signInEmail() {
        mAuth.signInWithEmailAndPassword(loginEmail.getText().toString(), loginPass.getText().toString())
                .addOnCompleteListener(this, task -> {
                    if (!task.isSuccessful() || task.getResult().getUser() == null) {
                        showErrorDialogMail();
                    } else if (!task.getResult().getUser().isEmailVerified()) {
                        showErrorEmailVerified(task.getResult().getUser());
                    } else {
                        FirebaseUser user = task.getResult().getUser();
                        checkUserDatabaseLogin(user);
                    }
                });
    }

    private void checkUserDatabaseLogin(FirebaseUser user) {
        SessionManager.userId = user.getUid();

        ActorService.getCurrentActorRef().get().addOnSuccessListener(snapshot -> {
            if (!snapshot.exists()) {
                // ✅ Crear solo si es la primera vez
                Actor actor = new Actor(
                        user.getUid(),
                        user.getDisplayName() != null ? user.getDisplayName() : "",
                        "", // apellido
                        user.getEmail(),
                        user.getPhoneNumber(),
                        "CONSUMER" // ❗ Valor por defecto SOLO si no existe aún
                );

                ActorService.createOrUpdateActor(actor)
                        .addOnSuccessListener(unused -> irAMainActivity());
            } else {
                // ✅ Ya existe: NO sobrescribimos, solo vamos al Main
                irAMainActivity();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Error al acceder al perfil", Toast.LENGTH_SHORT).show();
        });
    }

    private void irAMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void showGooglePlayServicesError() {
        Snackbar.make(loginButtonSignup, R.string.login_google_play_services_error, Snackbar.LENGTH_LONG)
                .setAction(R.string.login_download_gps, view -> {
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse(getString(R.string.gps_download_url))));
                    } catch (Exception ex) {
                        startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse(getString(R.string.market_download_url))));
                    }
                }).show();
    }

    private void showErrorDialogMail() {
        hideLoginButton(false);
        Snackbar.make(signinButtonMail, getString(R.string.login_mail_access_error), Snackbar.LENGTH_SHORT).show();
    }

    private void showErrorEmailVerified(FirebaseUser user) {
        hideLoginButton(false);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.login_verified_mail_error)
                .setPositiveButton(R.string.login_verified_mail_error_ok, (dialog, which) -> {
                    user.sendEmailVerification().addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            Snackbar.make(loginEmail, R.string.login_verified_mail_error_sent, Snackbar.LENGTH_SHORT).show();
                        } else {
                            Snackbar.make(loginEmail, R.string.login_verified_mail_error_no_sent, Snackbar.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton(R.string.login_verified_mail_error_cancel, null)
                .show();
    }

    private void hideLoginButton(boolean hide) {
        TransitionSet transitionSet = new TransitionSet();
        Transition layoutFade = new AutoTransition();
        layoutFade.setDuration(1000);
        transitionSet.addTransition(layoutFade);

        ViewGroup rootLayout = findViewById(R.id.login_main_layout);
        TransitionManager.beginDelayedTransition(rootLayout, transitionSet);

        progressBar.setVisibility(hide ? View.VISIBLE : View.GONE);
        signinButtonMail.setVisibility(hide ? View.GONE : View.VISIBLE);
        signinButtonGoogle.setVisibility(hide ? View.GONE : View.VISIBLE);
        loginButtonSignup.setVisibility(hide ? View.GONE : View.VISIBLE);
        loginEmailParent.setEnabled(!hide);
        loginPassParent.setEnabled(!hide);
    }
}