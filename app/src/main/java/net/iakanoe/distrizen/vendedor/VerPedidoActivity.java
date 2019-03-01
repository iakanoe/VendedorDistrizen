package net.iakanoe.distrizen.vendedor;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class VerPedidoActivity extends AppCompatActivity {
	Pedido pedido;
	ArticulosAdapter articulosAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		if(!getIntent().hasExtra("pedido"))
			startActivity(new Intent(this, SeleccionarClienteActivity.class).putExtras(getIntent()));
		else {
			pedido = (Pedido) getIntent().getSerializableExtra("pedido");
			setupUI();
		}
	}

	@Override protected void onNewIntent(Intent intent){
		super.onNewIntent(intent);
		if(!intent.hasExtra("pedido"))
			startActivity(new Intent(this, SeleccionarClienteActivity.class).putExtras(intent));
		else {
			pedido = (Pedido) intent.getSerializableExtra("pedido");
			setupUI();
		}
	}

	@Override public boolean onCreateOptionsMenu(Menu menu){
		getMenuInflater().inflate(R.menu.menu_toolbar_ver_pedido, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override public boolean onOptionsItemSelected(MenuItem item){
		switch(item.getItemId()){
			case R.id.verPedidoToolbar_action_add:
				nuevoArticulo();
				break;

			default:
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override public void onBackPressed(){
		new AlertDialog.Builder(this)
			.setMessage("¿Desea descartar el pedido?")
			.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
				@Override public void onClick(DialogInterface dialog, int which){
					dialog.dismiss();
				}
			})
			.setPositiveButton("Descartar", new DialogInterface.OnClickListener() {
				@Override public void onClick(DialogInterface dialog, int which){
					returnToMain();
				}
			})
			.setCancelable(true)
			.create()
			.show();
	}

	void returnToMain(){
		Intent i = new Intent(getApplicationContext(), MainActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		i.putExtras(getIntent());
		i.removeExtra("pedido");
		startActivity(i);
	}

	void nuevoArticulo(){
		startActivity(new Intent(this, SeleccionarArticuloActivity.class)
			.putExtra("pedido", articulosAdapter.getPedido())
			.putExtra("fromver", true));
	}

	void updateData(){
		double precioTotal = articulosAdapter.getPedido().getPrecioTotal();
		int cantidadTotal = 0;
		for(int i = 0; i < articulosAdapter.getItemCount(); i++)
			cantidadTotal += articulosAdapter.getPedido().getCantidad(i);

		((TextView) findViewById(R.id.txtCantidadTotal)).setText(String.valueOf(cantidadTotal));
		((TextView) findViewById(R.id.txtPrecioTotal)).setText(
			String.format(getResources().getString(R.string.dinero_format), precioTotal));

		findViewById(R.id.txtVerPedidoEmpty).setVisibility(cantidadTotal <= 0 ? View.VISIBLE : View.GONE);
		findViewById(R.id.btnConfirmarPedido).setEnabled(cantidadTotal > 0);
	}

	void setupUI(){
		setContentView(R.layout.activity_ver_pedido);
		articulosAdapter = new ArticulosAdapter(pedido);
		updateData();
		articulosAdapter.setOnItemsChangedListener(new OnItemsChangedListener() {
			@Override public void onItemsChanged(){
				updateData();
			}
		});
		setSupportActionBar((Toolbar) findViewById(R.id.verPedidoToolbar));
		new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.START | ItemTouchHelper.END) {
			@Override public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1){
				return false;
			}

			@Override public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i){
				articulosAdapter.removeItem(viewHolder.getAdapterPosition());
				Snackbar.make(findViewById(R.id.verPedidoCoordinator), "Artículo eliminado.", Snackbar.LENGTH_SHORT)
					.setAction("DESHACER", new View.OnClickListener() {
						@Override public void onClick(View v){
							articulosAdapter.restoreLastRemovedItem();
						}
					}).show();
			}
		}).attachToRecyclerView((RecyclerView) findViewById(R.id.verPedidoRecyclerView));
		((RecyclerView) findViewById(R.id.verPedidoRecyclerView)).setAdapter(articulosAdapter);
		((RecyclerView) findViewById(R.id.verPedidoRecyclerView)).setLayoutManager(
			new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
		if(articulosAdapter.getItemCount() <= 0){
			findViewById(R.id.txtVerPedidoEmpty).setVisibility(View.VISIBLE);
		}
		findViewById(R.id.btnConfirmarPedido).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v){
				new AlertDialog.Builder(v.getContext())
					.setMessage("¿Está seguro de querer confirmar el pedido?")
					.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
						@Override public void onClick(DialogInterface dialog, int which){
							confirmarPedido();
						}
					})
					.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
						@Override public void onClick(DialogInterface dialog, int which){
							dialog.dismiss();
						}
					})
					.setCancelable(true)
					.create()
					.show();
			}
		});
	}

	void confirmarPedido(){
		Pedido pedido = articulosAdapter.getPedido();
		final AlertDialog alertDialog = new AlertDialog.Builder(this)
			//.setView(getLayoutInflater().inflate(R.layout.dialog_loading, null))
			.setView(View.inflate(this, R.layout.dialog_loading, null))
			.setCancelable(false)
			.create();
		alertDialog.show();

		CrearPedidoTask crearPedidoTask = new CrearPedidoTask();
		crearPedidoTask.setListener(new CrearPedidoListener() {
			@Override public void onCommunicationFailed(){
				new AlertDialog.Builder(VerPedidoActivity.this)
					.setMessage(R.string.pedido_message)
					.setCancelable(true)
					.setNegativeButton("Volver", new DialogInterface.OnClickListener() {
						@Override public void onClick(DialogInterface dialog, int which){
							alertDialog.dismiss();
							dialog.cancel();
						}
					})
					.setPositiveButton("Intentar de nuevo", new DialogInterface.OnClickListener() {
						@Override public void onClick(DialogInterface dialog, int which){
							alertDialog.dismiss();
							confirmarPedido();
						}
					})
					.create()
					.show();
			}

			@Override void onCrearPedidoSuccess(){
				alertDialog.dismiss();
				returnToMain();
			}
		});
		crearPedidoTask.subirPedido(pedido);
	}

	interface OnItemsChangedListener {
		void onItemsChanged();
	}

	private class ArticulosAdapter extends RecyclerView.Adapter<ArticulosAdapter.ArticuloHolder> {
		private Pedido pedido;
		private int mExpandedPosition = -1;
		private int previousExpandedPosition = -1;
		private Articulo lastRemovedArticulo = null;
		private int lastRemovedCantidad = -1;
		private int lastRemovedIndex = -1;
		private OnItemsChangedListener onItemsChangedListener;

		ArticulosAdapter(Pedido pedido){
			this.pedido = pedido;
		}

		@NonNull @Override public ArticulosAdapter.ArticuloHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i){
			return new ArticulosAdapter.ArticuloHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_articulo_ver, viewGroup, false));
		}

		@Override public void onBindViewHolder(@NonNull final ArticulosAdapter.ArticuloHolder holder, int position){
			holder.setArticulo(position);
			holder.bindExpansion();
		}

		void removeItem(int position){
			lastRemovedIndex = position;
			lastRemovedArticulo = pedido.getItem(position);
			lastRemovedCantidad = pedido.getCantidad(position);
			pedido.removeArticulo(position);
			notifyItemRemoved(position);
			if(onItemsChangedListener != null) onItemsChangedListener.onItemsChanged();
		}

		Pedido getPedido(){
			return pedido;
		}

		void restoreLastRemovedItem(){
			if(lastRemovedArticulo == null) return;
			pedido.addArticulo(lastRemovedArticulo, lastRemovedCantidad, lastRemovedIndex);
			notifyItemInserted(lastRemovedIndex);
			if(onItemsChangedListener != null) onItemsChangedListener.onItemsChanged();
		}

		void addItem(Articulo a, int cantidad, int position){
			pedido.addArticulo(a, cantidad, position);
			notifyItemInserted(position);
			if(onItemsChangedListener != null) onItemsChangedListener.onItemsChanged();
		}

		@Override public int getItemCount(){
			return pedido.size();
		}

		Articulo getItemAt(int i){
			return pedido.getItem(i);
		}

		void setOnItemsChangedListener(OnItemsChangedListener onItemsChangedListener){
			this.onItemsChangedListener = onItemsChangedListener;
		}

		class ArticuloHolder extends RecyclerView.ViewHolder {
			boolean aaa = false;

			ArticuloHolder(@NonNull final View itemView){
				super(itemView);

				((TextInputEditText) itemView.findViewById(R.id.etxtCantidad)).addTextChangedListener(new TextWatcher() {
					@Override public void beforeTextChanged(CharSequence s, int start, int count, int after){
					}

					@Override public void onTextChanged(CharSequence s, int start, int before, int count){
					}

					@Override public void afterTextChanged(Editable s){
						if(aaa){
							aaa = false;
							return;
						}
						if(s.toString().equals("")) return;

						if(s.toString().equals("0")){
							new AlertDialog.Builder(itemView.getContext())
								.setMessage("Para borrar el artículo, debe deslizarlo hacia el costado.")
								.setCancelable(true)
								.create()
								.show();
							return;
						}

						pedido.setCantidad(getAdapterPosition(), Integer.parseInt(s.toString()));
						if(onItemsChangedListener != null) onItemsChangedListener.onItemsChanged();
						((TextView) itemView.findViewById(R.id.txtPrecio)).setText(
							String.format(getResources().getString(R.string.dinero_format), pedido.getPrecio(getAdapterPosition())));
						((TextView) itemView.findViewById(R.id.txtUnidades)).setText(
							String.format(getResources().getString(R.string.unidades_format), pedido.getCantidad(getAdapterPosition())));
					}
				});
			}

			void setArticulo(final int a){
				aaa = true;
				((TextInputEditText) itemView.findViewById(R.id.etxtCantidad)).setText(String.valueOf(pedido.getCantidad(a)));
				((TextView) itemView.findViewById(R.id.txtDescripcion)).setText(pedido.getDescripcion(a));
				((TextView) itemView.findViewById(R.id.txtPrecio)).setText(
					String.format(getResources().getString(R.string.dinero_format), pedido.getPrecio(a)));
				((TextView) itemView.findViewById(R.id.txtPrecioUnitario)).setText(
					String.format(getResources().getString(R.string.dinero_format), pedido.getPrecioUnitario(a)));
				((TextView) itemView.findViewById(R.id.txtUnidades)).setText(
					String.format(getResources().getString(R.string.unidades_format), pedido.getCantidad(a)));
			}

			void bindExpansion(){
				itemView.findViewById(R.id.cantidadLayout).setVisibility(getAdapterPosition() == mExpandedPosition ? View.VISIBLE : View.GONE);
				itemView.setActivated(getAdapterPosition() == mExpandedPosition);
				itemView.findViewById(R.id.txtUnidades).setVisibility(getAdapterPosition() == mExpandedPosition ? View.GONE : View.VISIBLE);
				ConstraintSet cset = new ConstraintSet();
				cset.clone((ConstraintLayout) itemView.findViewById(R.id.dataLayout));
				if(getAdapterPosition() == mExpandedPosition){
					cset.connect(R.id.txtPrecio, ConstraintSet.BOTTOM, R.id.dataLayout, ConstraintSet.BOTTOM);
				} else {
					cset.connect(R.id.txtPrecio, ConstraintSet.BOTTOM, R.id.txtUnidades, ConstraintSet.TOP);
				}
				cset.applyTo((ConstraintLayout) itemView.findViewById(R.id.dataLayout));

				if(getAdapterPosition() == mExpandedPosition)
					previousExpandedPosition = getAdapterPosition();

				itemView.setOnClickListener(new View.OnClickListener() {
					@Override public void onClick(View v){
						mExpandedPosition = (getAdapterPosition() == mExpandedPosition ? -1 : getAdapterPosition());
						notifyItemChanged(previousExpandedPosition);
						notifyItemChanged(getAdapterPosition());
					}
				});
			}
		}
	}
}
