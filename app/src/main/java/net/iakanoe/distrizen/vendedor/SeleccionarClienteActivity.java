package net.iakanoe.distrizen.vendedor;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class SeleccionarClienteActivity extends AppCompatActivity {
	ClientesAdapter adapter;
	String user;

	@Override protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_seleccionar_cliente);
		user = getIntent().getStringExtra("user");
		getData();
	}

	void setupUI(){
		findViewById(R.id.seleccionarClienteProgressBar).setVisibility(View.GONE);
		setSupportActionBar((Toolbar) findViewById(R.id.seleccionarClienteToolbar));
		if(adapter.getItemCount() <= 0){
			findViewById(R.id.txtSeleccionarClienteEmpty).setVisibility(View.VISIBLE);
		}
		RecyclerView rv = findViewById(R.id.seleccionarClienteRecyclerView);
		rv.setLayoutManager(new LinearLayoutManager(rv.getContext(), LinearLayoutManager.VERTICAL, false));
		rv.addItemDecoration(new DividerItemDecoration(rv.getContext(), DividerItemDecoration.VERTICAL));
		rv.setAdapter(adapter);

	}

	@Override public void onBackPressed(){
		Intent i = new Intent(this, MainActivity.class);
		i.putExtras(getIntent());
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(i);
	}

	void clientSelected(Cliente c){
		Intent i = new Intent(this, SeleccionarArticuloActivityV2.class);
		i.putExtra("pedido", new Pedido(c));
		i.putExtras(getIntent());
		startActivity(i);
	}

	void getData(){
		ClientesTask clientesTask = new ClientesTask();
		clientesTask.setListener(new ClientesListener() {
			@Override void onGotClientes(List<Cliente> list){
				adapter = new ClientesAdapter(list);
				setupUI();
			}

			@Override public void onCommunicationFailed(){
				new AlertDialog.Builder(SeleccionarClienteActivity.this)
					.setMessage("Ocurrió un fallo en la comunicación con el servicio de clientes.")
					.setCancelable(true)
					.setNegativeButton("Volver", new DialogInterface.OnClickListener() {
						@Override public void onClick(DialogInterface dialog, int which){
							dialog.cancel();
						}
					})
					.setPositiveButton("Intentar de nuevo", new DialogInterface.OnClickListener() {
						@Override public void onClick(DialogInterface dialog, int which){
							getData();
						}
					})
					.create()
					.show();
			}
		});
		clientesTask.getClientes(user);
	}

	private class ClientesAdapter extends RecyclerView.Adapter<ClientesAdapter.ClienteHolder> {
		List<Cliente> list;

		ClientesAdapter(List<Cliente> list){
			this.list = list;
		}

		@NonNull @Override public ClienteHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i){
			return new ClienteHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_cliente, viewGroup, false));
		}

		@Override public void onBindViewHolder(@NonNull ClienteHolder clienteHolder, int i){
			clienteHolder.setClient(list.get(i));
		}

		@Override public int getItemCount(){
			return list.size();
		}

		Cliente getItemAt(int index){
			return list.get(index);
		}

		class ClienteHolder extends RecyclerView.ViewHolder {
			ClienteHolder(@NonNull View itemView){
				super(itemView);
			}

			void setClient(Cliente c){
				((TextView) itemView.findViewById(R.id.txtNombreCliente)).setText(c.getNombre());
				((TextView) itemView.findViewById(R.id.txtCodigoCliente)).setText(c.getCodigo());
				itemView.setOnClickListener(new View.OnClickListener() {
					@Override public void onClick(View v){
						clientSelected(getItemAt(getAdapterPosition()));
					}
				});
			}
		}
	}
}
