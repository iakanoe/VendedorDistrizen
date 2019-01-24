package net.iakanoe.distrizen.vendedor;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.Objects;

public class PedidosActivity extends AppCompatActivity {

	@Override protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setupUI();

		while(!getData()){
			// Mostrar popup que diga que no hay internet o un error o algo (Reintentar // Cerrar)
			// Cerrar app si se toca el boton de cerrar
			continue; // Probar de nuevo si se toca el de reintentar (seguir con el while)
		}
		showData();
	}

	void showData(){
		findViewById(R.id.pedidosProgressBar).setVisibility(View.GONE);
		// Mostrar los datos o el txtPedidosEmpty
	}

	boolean getData(){
		// Descargar lista de pedidos (pendientes?) y guardarla en variable
		// Devolver verdadero si se pudo descargar algo (aunque sea vacio)
		// Devolver falso si no se pudo descargar porque no hubo internet o algo
		return false; // Provisional
	}

	void setupUI(){
		setContentView(R.layout.activity_pedidos);
		setSupportActionBar((Toolbar) findViewById(R.id.pedidosToolbar));
		Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
		((RecyclerView) findViewById(R.id.pedidosRecyclerView)).setLayoutManager(
			new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
		new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.START | ItemTouchHelper.END) {
			@Override public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1){
				return false;
			}

			@Override public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction){
				//Borrar de la lista
				//Ofrecer deshacer el borrado
			}
		}).attachToRecyclerView((RecyclerView) findViewById(R.id.pedidosRecyclerView));
	}

	@Override public boolean onCreateOptionsMenu(Menu menu){
		getMenuInflater().inflate(R.menu.toolbar_pedidos_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override public boolean onOptionsItemSelected(MenuItem item){
		if(item.getItemId() != R.id.pedidosToolbar_action_add)
			return super.onOptionsItemSelected(item);
		startActivity(new Intent(getApplicationContext(), SeleccionarArticuloActivity.class));
		return super.onOptionsItemSelected(item);
	}
}
