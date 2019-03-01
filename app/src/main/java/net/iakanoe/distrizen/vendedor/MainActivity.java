package net.iakanoe.distrizen.vendedor;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
	String nombre;
	String user;
	String token;
	String pass;

	@Override protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		checkLoggedIn();
	}

	void checkLoggedIn(){
		if(getIntent().hasExtra("login") && getIntent().getBooleanExtra("login", false)){
			nombre = getIntent().getStringExtra("name");
			user = getIntent().getStringExtra("user");
			token = getIntent().getStringExtra("token");
			pass = getIntent().getStringExtra("pass");
			saveCredentials();
			setupUI();
			return;
		}

		if(getPreferences(MODE_PRIVATE).contains("user") && getPreferences(MODE_PRIVATE).contains("pass")){
			user = getPreferences(MODE_PRIVATE).getString("user", "");
			pass = getPreferences(MODE_PRIVATE).getString("pass", "");
			LoginTask loginTask = new LoginTask();
			loginTask.setListener(new LoginListener() {
				@Override void onLoggedIn(String n, String t){
					token = t;
					nombre = n;
					saveCredentials();
					setupUI();
				}

				@Override void onLoginError(String error){
					logOut();
				}

				@Override public void onCommunicationFailed(){
					logOut();
				}
			});
			loginTask.login(user, pass);
			return;
		}

		logOut();
	}

	void logOut(){
		getPreferences(MODE_PRIVATE).edit().clear().apply();
		startActivity(new Intent(getApplicationContext(), LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
	}

	void saveCredentials(){
		getPreferences(MODE_PRIVATE)
			.edit()
			.putString("user", user)
			.putString("pass", pass)
			.putString("name", nombre)
			.apply();
	}

	void setupUI(){
		((TextView) findViewById(R.id.txtNombre)).setText(nombre);
		findViewById(R.id.btnPedidos).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v){
				nuevoPedido();
			}
		});
		findViewById(R.id.btnPagos).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v){
				startActivity(new Intent(getApplicationContext(), PagosActivity.class).putExtra("user", user));
			}
		});
		findViewById(R.id.btnCobrar).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v){
				startActivity(new Intent(getApplicationContext(), CobrarActivity.class).putExtra("frommain", true));
			}
		});
		findViewById(R.id.btnLogout).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v){
				logOut();
			}
		});
		findViewById(R.id.loadingBar).setVisibility(View.GONE);
		findViewById(R.id.mainLayout).setVisibility(View.VISIBLE);
	}

	void nuevoPedido(){
		Intent i = new Intent(this, SeleccionarClienteActivity.class);
		i.putExtra("user", user);
		i.putExtra("pass", pass);
		i.putExtra("token", token);
		i.putExtra("name", nombre);
		startActivity(i);
	}
}
