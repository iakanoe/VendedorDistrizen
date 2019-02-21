package net.iakanoe.distrizen.vendedor;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class LoginActivity extends AppCompatActivity {
	@Override protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setupUI();
	}

	void setupUI(){
		setContentView(R.layout.activity_login);
		findViewById(R.id.btnLogin).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v){
				tryLogin();
			}
		});
	}

	void tryLogin(){
		activationUI(false);
		String user = ((TextInputLayout) findViewById(R.id.tilUsuario)).getEditText().getText().toString();
		String pass = ((TextInputLayout) findViewById(R.id.tilContrasena)).getEditText().getText().toString();
		LoginTask loginTask = new LoginTask();
		loginTask.setListener(new LoginListener() {
			@Override void onLoggedIn(String name, String token){
				Intent i = new Intent(getApplicationContext(), MainActivity.class);
				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
				i.putExtra("login", true);
				i.putExtra("user", ((TextInputLayout) findViewById(R.id.tilUsuario)).getEditText().getText().toString());
				i.putExtra("pass", ((TextInputLayout) findViewById(R.id.tilContrasena)).getEditText().getText().toString());
				i.putExtra("token", token);
				i.putExtra("name", name);
				startActivity(i);
			}

			@Override void onLoginError(String error){
				new AlertDialog.Builder(getApplicationContext())
					.setMessage(error)
					.setCancelable(true)
					.setNegativeButton("Volver", new DialogInterface.OnClickListener() {
						@Override public void onClick(DialogInterface dialog, int which){
							dialog.cancel();
						}
					})
					.create()
					.show();
				activationUI(true);
			}

			@Override public void onCommunicationFailed(){
				new AlertDialog.Builder(LoginActivity.this)
					.setMessage("Ocurrió un fallo en la comunicación con el servicio de inicio de sesión.")
					.setCancelable(true)
					.setNegativeButton("Volver", new DialogInterface.OnClickListener() {
						@Override public void onClick(DialogInterface dialog, int which){
							dialog.cancel();
						}
					})
					.setPositiveButton("Intentar de nuevo", new DialogInterface.OnClickListener() {
						@Override public void onClick(DialogInterface dialog, int which){
							tryLogin();
						}
					})
					.create()
					.show();
				activationUI(true);
			}
		});
		loginTask.login(user, pass);
	}

	void activationUI(boolean b){
		findViewById(R.id.tilUsuario).setEnabled(b);
		findViewById(R.id.tilContrasena).setEnabled(b);
		findViewById(R.id.btnLogin).setEnabled(b);
		findViewById(R.id.progressBar).setVisibility(b ? View.GONE : View.VISIBLE);
	}
}
