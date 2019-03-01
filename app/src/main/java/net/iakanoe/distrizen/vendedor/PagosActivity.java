package net.iakanoe.distrizen.vendedor;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class PagosActivity extends AppCompatActivity {
	PedidosAdapter adapter;
	String user;
	
	@Override protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pagos);
		user = getIntent().getStringExtra("user");
		setSupportActionBar((Toolbar) findViewById(R.id.pagosToolbar));
		getData();
	}

	void setupUI(){
		findViewById(R.id.pagosProgressBar).setVisibility(View.GONE);
		if(adapter.getItemCount() <= 0)
			findViewById(R.id.txtPagosEmpty).setVisibility(View.VISIBLE);
		RecyclerView rv = findViewById(R.id.pagosRecyclerView);
		rv.setLayoutManager(new LinearLayoutManager(rv.getContext(), LinearLayoutManager.VERTICAL, false));
		rv.addItemDecoration(new DividerItemDecoration(rv.getContext(), DividerItemDecoration.VERTICAL));
		rv.setAdapter(adapter);
	}

	void getData(){
		GetPedidosTask getPedidosTask = new GetPedidosTask();
		getPedidosTask.setListener(new GetPedidosListener() {
			@Override void onGotPedidos(List<PedidoRaw> pedidos){
				adapter = new PedidosAdapter(pedidos);
				setupUI();
			}

			@Override public void onCommunicationFailed(){
				new AlertDialog.Builder(PagosActivity.this)
					.setMessage("Ocurrió un fallo en la comunicación con el servicio de pedidos.")
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
		getPedidosTask.getPedidos(user);
	}

	void pedidoSelected(PedidoRaw pedido){
		Intent i = new Intent(this, ResumenPedidoActivity.class);
		i.putExtras(getIntent());
		i.putExtra("idpedido", pedido.getId());
		startActivity(i);
	}

	private class PedidosAdapter extends RecyclerView.Adapter<PedidosAdapter.PedidoHolder> {
		List<PedidoRaw> lista;

		PedidosAdapter(List<PedidoRaw> lista){ this.lista = lista; }

		@NonNull @Override public PedidoHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i){
			return new PedidoHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_pedido, viewGroup, false));
		}

		@Override public void onBindViewHolder(@NonNull PedidoHolder pedidoHolder, int i){
			pedidoHolder.setPedido(lista.get(i));
		}

		@Override public int getItemCount(){
			return lista.size();
		}

		class PedidoHolder extends RecyclerView.ViewHolder {
			PedidoRaw pedido;

			PedidoHolder(@NonNull View itemView){ super(itemView); }

			void setPedido(PedidoRaw p){
				pedido = p;
				((TextView) itemView.findViewById(R.id.cardPedidoTxtID)).setText(String.valueOf(pedido.getId()));
				((TextView) itemView.findViewById(R.id.cardPedidoTxtTotal)).setText(
					String.format(getResources().getString(R.string.dinero_format), pedido.getTotal()));
				((TextView) itemView.findViewById(R.id.cardPedidoTxtCliente)).setText(pedido.getClienteRaw());
				((TextView) itemView.findViewById(R.id.cardPedidoTxtArticulos)).setText(String.valueOf(pedido.size()));
				itemView.setOnClickListener(new View.OnClickListener() {
					@Override public void onClick(View v){
						pedidoSelected(pedido);
					}
				});
			}
		}
	}
}
