package net.iakanoe.distrizen.vendedor;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
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

public class ResumenPedidoActivity extends AppCompatActivity {
	String user;
	int idpedido;
	ArticulosAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_resumen_pedido);
		user = getIntent().getStringExtra("user");
		idpedido = getIntent().getIntExtra("idpedido", -1);
		if(idpedido == -1) finish();
		setSupportActionBar((Toolbar) findViewById(R.id.resumenToolbar));
		getData();
	}

	void getData(){
		PedidoInfoTask pedidoInfoTask = new PedidoInfoTask();
		pedidoInfoTask.setListener(new PedidoInfoListener() {
			@Override void onGotInfo(Pedido pedido){
				adapter = new ArticulosAdapter(pedido);
				setupUI();
			}

			@Override public void onCommunicationFailed(){
				new AlertDialog.Builder(ResumenPedidoActivity.this)
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
		pedidoInfoTask.getPedidoInfo(idpedido);
	}

	void setupUI(){
		findViewById(R.id.resumenProgressBar).setVisibility(View.GONE);
		if(adapter.getItemCount() <= 0)
			findViewById(R.id.txtResumenEmpty).setVisibility(View.VISIBLE);
		RecyclerView rv = findViewById(R.id.resumenRecyclerView);
		rv.setLayoutManager(new LinearLayoutManager(rv.getContext(), LinearLayoutManager.VERTICAL, false));
		rv.addItemDecoration(new DividerItemDecoration(rv.getContext(), DividerItemDecoration.VERTICAL));
		rv.setAdapter(adapter);
		findViewById(R.id.btnResumenCobrar).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v){
				cobrar();
			}
		});

		((TextView) findViewById(R.id.txtResumenPedidoID)).setText(String.valueOf(idpedido));
		((TextView) findViewById(R.id.txtResumenCodicli)).setText(adapter.pedido.getCliente().getCodigo());
		((TextView) findViewById(R.id.txtResumenNombreCli)).setText(adapter.pedido.getCliente().getNombre());
		((TextView) findViewById(R.id.txtResumenTotal)).setText(
			String.format(getResources().getString(R.string.dinero_format), adapter.pedido.getPrecioTotal()));
	}

	void cobrar(){
		Intent i = new Intent(this, CobrarActivity.class);
		i.putExtras(getIntent());
		startActivity(i);
	}

	private class ArticulosAdapter extends RecyclerView.Adapter<ArticulosAdapter.ArticuloHolder> {
		Pedido pedido;

		ArticulosAdapter(Pedido p){
			this.pedido = p;
		}

		@NonNull @Override public ArticuloHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i){
			return new ArticuloHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_articulo_ver, viewGroup, false));
		}

		@Override public void onBindViewHolder(@NonNull ArticuloHolder viewHolder, int i){
			viewHolder.setArticulo(i);
		}

		@Override public int getItemCount(){
			return pedido.size();
		}

		class ArticuloHolder extends RecyclerView.ViewHolder {
			ArticuloHolder(@NonNull View itemView){
				super(itemView);
			}

			void setArticulo(int a){
				((TextInputEditText) itemView.findViewById(R.id.etxtCantidad)).setText(String.valueOf(pedido.getCantidad(a)));
				((TextView) itemView.findViewById(R.id.txtDescripcion)).setText(pedido.getDescripcion(a));
				((TextView) itemView.findViewById(R.id.txtPrecio)).setText(
					String.format(getResources().getString(R.string.dinero_format), pedido.getPrecio(a)));
				((TextView) itemView.findViewById(R.id.txtUnidades)).setText(
					String.format(getResources().getString(R.string.unidades_format), pedido.getCantidad(a)));
			}
		}
	}
}
