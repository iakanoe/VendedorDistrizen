package net.iakanoe.distrizen.vendedor;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;

public class PedidosActivity extends AppCompatActivity {

	@Override protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);

	}

	void setupUI(){
		setContentView(R.layout.activity_pedidos);
		setSupportActionBar((Toolbar) findViewById(R.id.toolbarPedidos));
		((RecyclerView) findViewById(R.id.recyclerView)).setLayoutManager(
			new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
		new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.START | ItemTouchHelper.END) {
			@Override public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1){
				return false;
			}

			@Override public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction){
				//Borrar de la lista
				//Ofrecer deshacer el borrado
			}
		}).attachToRecyclerView((RecyclerView) findViewById(R.id.recyclerView));
	}
}
