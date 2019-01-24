package net.iakanoe.distrizen.vendedor;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Objects;

public class SeleccionarArticuloActivity extends AppCompatActivity {

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

	boolean getData(){
		// Descargar lista de artículos y guardarla en variable "carrito"
		// Devolver verdadero si se pudo descargar algo (aunque sea vacio)
		// Devolver falso si no se pudo descargar porque no hubo internet o algo
		return false; // Provisional
	}

	void showData(){
		findViewById(R.id.seleccionarProgressBar).setVisibility(View.GONE);
		// Mostrar los datos o el txtSeleccionarEmpty
	}

	void setupUI(){
		setContentView(R.layout.activity_seleccionar_articulo);
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

	static class ArticulosAdapter extends RecyclerView.Adapter<ArticulosAdapter.ArticuloHolder> {
		private Pedido pedido;

		ArticulosAdapter(Pedido pedido){
			this.pedido = pedido;
		}

		void add(Articulo a, int cantidad){
			pedido.addArticulo(a, cantidad);
			notifyItemInserted(pedido.getItems().indexOf(a));
		}

		@NonNull @Override public ArticuloHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i){
			return new ArticuloHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_articulo, viewGroup, false));
		}

		@Override public void onBindViewHolder(@NonNull ArticuloHolder articuloHolder, int i){
			articuloHolder.setArticulo(i);
		}

		@Override public int getItemCount(){
			return pedido.size();
		}

		class ArticuloHolder extends RecyclerView.ViewHolder {
			ArticuloHolder(@NonNull View itemView){
				super(itemView);
			}

			void setArticulo(int a){
				((TextView) itemView.findViewById(R.id.txtDescripcion)).setText(pedido.getDescripcion(a));
				((TextView) itemView.findViewById(R.id.txtPrecio)).setText(String.valueOf(pedido.getPrecio(a)));
			}
		}
	}
}