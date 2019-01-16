package net.iakanoe.distrizen.vendedor;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
	String nombre = null;

	@Override protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		if(!loggedIn()) logOut();
		setupUI();
	}

	boolean loggedIn(){
		/* TODO:
		 * Chequear si existe el usuario en las preferences, sino devolver falso
		 * Chequear si existe la contraseña en las preferences, sino devolver falso
		 * Probar login con el servicio PHP
		 * PHP no devuelve nada, devolver falso
		 * PHP devuelve boolean y string:
		 *     Poner string en el campo "nombre"
		 *     Devolver boolean
		 * (Futuro 1) PHP devuelve true/false, las consultas a la DB se deben hacer con el usuario y contraseña
		 * (Futuro 2) PHP devuelve codigo de usuario, las consultas a la DB se deben hacer con el codigo y contraseña
		 * (Futuro 3) PHP devuelve access token, se usa en conjunto con las consultas para verificar
		 * (Futuro 4) PHP devuelve access token, se usa para encriptar las consultas
		 */
		return false;
	}

	void logOut(){
		// TODO: Borrar contraseña de las preferences
		startActivity(new Intent(getApplicationContext(), LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
	}

	void setupUI(){
		setContentView(R.layout.activity_main);
		if(nombre != null)
			((TextView) findViewById(R.id.txtNombre)).setText(nombre);
		findViewById(R.id.btnPedidos).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v){
				startActivity(new Intent(getApplicationContext(), PedidosActivity.class));
			}
		});
		findViewById(R.id.btnPagos).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v){
				startActivity(new Intent(getApplicationContext(), PagosActivity.class));
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
}
