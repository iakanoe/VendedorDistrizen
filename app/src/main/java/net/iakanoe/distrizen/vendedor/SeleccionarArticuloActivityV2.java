package net.iakanoe.distrizen.vendedor;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SeleccionarArticuloActivityV2 extends AppCompatActivity {
	ArticulosAdapter articulosAdapter;
	LineasAdapter lineasAdapter;
	SharedPreferences preferences;
	Pedido pedidoTotal;
	List<Articulo> listacompleta;
	boolean prepared = false;
	boolean shrunk = true;
	Linea filtroLinea = null;
	String filtroBusca = "";

	@Override protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_seleccionar_articulo_v2);
		preferences = getPreferences(MODE_PRIVATE);
		pedidoTotal = (Pedido) getIntent().getSerializableExtra("pedido");
		if(getIntent().getBooleanExtra("fromver", false)) getDataFromPref();
		else getDataFromDB();
	}

	@Override public void onBackPressed(){
		if(shrunk) super.onBackPressed();
		else shrinkLineas();
	}

	@Override public boolean onCreateOptionsMenu(Menu menu){
		getMenuInflater().inflate(R.menu.menu_toolbar_seleccionar_articulo, menu);
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		SearchView searchView = (SearchView) menu.findItem(R.id.seleccionarArticuloToolbar_action_search).getActionView();
		searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
		searchView.setIconifiedByDefault(true);
		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			@Override public boolean onQueryTextSubmit(String query){
				buscarArticulo(query);
				return true;
			}

			@Override public boolean onQueryTextChange(String newText){
				buscarArticulo(newText);
				return true;
			}
		});
		searchView.setOnCloseListener(new SearchView.OnCloseListener() {
			@Override public boolean onClose(){
				buscarArticulo("");
				return true;
			}
		});

		return super.onCreateOptionsMenu(menu);
	}

	void setupUI(){
		listacompleta = articulosAdapter.getItems();
		findViewById(R.id.seleccionarArticuloProgressBar).setVisibility(View.GONE);
		setSupportActionBar((Toolbar) findViewById(R.id.seleccionarArticuloToolbar));
		((RecyclerView) findViewById(R.id.seleccionarArticuloRecyclerView)).setLayoutManager(
			new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
		((RecyclerView) findViewById(R.id.lineasRecyclerView)).setLayoutManager(
			new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
		((RecyclerView) findViewById(R.id.seleccionarArticuloRecyclerView)).setAdapter(articulosAdapter);
		((RecyclerView) findViewById(R.id.seleccionarArticuloRecyclerView))
			.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
		((RecyclerView) findViewById(R.id.lineasRecyclerView)).setAdapter(lineasAdapter);
		if(articulosAdapter.getItemCount() <= 0)
			findViewById(R.id.txtSeleccionarArticuloEmpty).setVisibility(View.VISIBLE);
		if(lineasAdapter.getItemCount() <= 0)
			findViewById(R.id.btnLineaLayout).setVisibility(View.GONE);
		findViewById(R.id.btnLinea).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v){
				expandLineas();
			}
		});
		findViewById(R.id.lineasRecyclerView).setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override public void onFocusChange(View v, boolean hasFocus){
				if(!hasFocus) shrinkLineas();
			}
		});
	}

	void expandLineas(){
		findViewById(R.id.btnLineaLayout).setVisibility(View.GONE);
		findViewById(R.id.lineasRecyclerView).setVisibility(View.VISIBLE);
		findViewById(R.id.seleccionarArticuloRecyclerView).setVisibility(View.GONE);
		shrunk = false;
	}

	void buscarArticulo(String query){
		filtroBusca = query;
		filtrar();
	}

	void filtrar(){
		if(filtroBusca.equals("") && filtroLinea == null){
			articulosAdapter.setItems(listacompleta);
			return;
		}
		List<Articulo> listafiltrada = new ArrayList<>();
		if(filtroLinea == null){
			for(Articulo a : listacompleta)
				if(a.getDescripcion().toLowerCase().contains(filtroBusca.toLowerCase()))
					listafiltrada.add(a);
		} else {
			for(Articulo a : listacompleta)
				if(a.getDescripcion().toLowerCase().contains(filtroBusca.toLowerCase())
					&& a.getLinea().equals(filtroLinea.getCodigo()))
					listafiltrada.add(a);
		}
		articulosAdapter.setItems(listafiltrada);
	}

	void deselectLinea(){
		((TextView) findViewById(R.id.txtLineaIndicador)).setText(R.string.linea);
		findViewById(R.id.signalDropDown).setVisibility(View.VISIBLE);
		findViewById(R.id.signalEraseFilter).setVisibility(View.GONE);
		findViewById(R.id.btnLinea).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v){
				expandLineas();
			}
		});
		filtroLinea = null;
		filtrar();
	}

	void lineaSelected(Linea linea){
		((TextView) findViewById(R.id.txtLineaIndicador)).setText(linea.getDescripcion());
		findViewById(R.id.signalDropDown).setVisibility(View.GONE);
		findViewById(R.id.signalEraseFilter).setVisibility(View.VISIBLE);
		findViewById(R.id.btnLinea).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v){
				deselectLinea();
			}
		});
		filtroLinea = linea;
		filtrar();
		shrinkLineas();
	}

	void shrinkLineas(){
		findViewById(R.id.btnLineaLayout).setVisibility(View.VISIBLE);
		findViewById(R.id.lineasRecyclerView).setVisibility(View.GONE);
		findViewById(R.id.seleccionarArticuloRecyclerView).setVisibility(View.VISIBLE);
		shrunk = true;
	}

	void articuloSelected(Articulo a){
		pedidoTotal.addArticulo(a, 1);
		Intent i = new Intent(getApplicationContext(), VerPedidoActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		i.putExtras(getIntent());
		i.putExtra("pedido", pedidoTotal);
		startActivity(i);
	}

	void getDataFromDB(){
		ArticulosTask articulosTask = new ArticulosTask();
		LineasTask lineasTask = new LineasTask();
		articulosTask.setListener(new ArticulosListener() {
			@Override void onGotArticulos(List<Articulo> list){
				articulosAdapter = new ArticulosAdapter(list, pedidoTotal.getListaPrecios());
				if(prepared) setupUI();
				prepared = true;
			}

			@Override void onGotPlainTextArticulos(String json){
				preferences.edit().putString("articulos", json).apply();
			}

			@Override public void onCommunicationFailed(){
				new AlertDialog.Builder(SeleccionarArticuloActivityV2.this)
					.setMessage(R.string.articulos_message)
					.setCancelable(true)
					.setNegativeButton("Volver", new DialogInterface.OnClickListener() {
						@Override public void onClick(DialogInterface dialog, int which){
							dialog.cancel();
						}
					})
					.setPositiveButton("Intentar de nuevo", new DialogInterface.OnClickListener() {
						@Override public void onClick(DialogInterface dialog, int which){
							getDataFromDB();
						}
					})
					.create()
					.show();
			}
		});
		lineasTask.setListener(new LineasListener() {
			@Override void onGotLineas(List<Linea> list){
				lineasAdapter = new LineasAdapter(list);
				if(prepared) setupUI();
				prepared = true;
			}

			@Override void onGotPlainTextLineas(String json){
				preferences.edit().putString("lineas", json).apply();
			}

			@Override public void onCommunicationFailed(){
				new AlertDialog.Builder(SeleccionarArticuloActivityV2.this)
					.setMessage(R.string.lineas_message)
					.setCancelable(true)
					.setNegativeButton("Volver", new DialogInterface.OnClickListener() {
						@Override public void onClick(DialogInterface dialog, int which){
							dialog.cancel();
						}
					})
					.setPositiveButton("Intentar de nuevo", new DialogInterface.OnClickListener() {
						@Override public void onClick(DialogInterface dialog, int which){
							getDataFromDB();
						}
					})
					.create()
					.show();
			}
		});

		articulosTask.getArticulos();
		lineasTask.getLineas();
	}

	void getDataFromPref(){
		if(!preferences.contains("articulos") || !preferences.contains("lineas")){
			getDataFromDB();
			return;
		}

		try {
			JSONArray artjsonarray = new JSONArray(preferences.getString("articulos", "[]"));
			JSONArray linjsonarray = new JSONArray(preferences.getString("lineas", "[]"));
			List<Articulo> articuloslist = new ArrayList<>();
			List<Linea> lineaslist = new ArrayList<>();

			for(int i = 0; i < artjsonarray.length(); i++){
				JSONObject linea = artjsonarray.getJSONObject(i);
				String cod = linea.getString("codiarti");
				String des = linea.getString("descrip");
				String rub = linea.getString("codirubr");
				double pr1 = linea.getDouble("venta");
				double pr2 = linea.getDouble("venta2");
				double pr3 = linea.getDouble("venta3");
				Articulo a = new Articulo(cod, des, rub, pr1, pr2, pr3);
				articuloslist.add(a);
			}

			for(int i = 0; i < linjsonarray.length(); i++){
				JSONObject linea = linjsonarray.getJSONObject(i);
				String cod = linea.getString("codigo");
				String des = linea.getString("descrip");
				Linea line = new Linea(cod, des);
				lineaslist.add(line);
			}

			articulosAdapter = new ArticulosAdapter(articuloslist, pedidoTotal.getListaPrecios());
			lineasAdapter = new LineasAdapter(lineaslist);

			setupUI();
		} catch(JSONException e) {
			e.printStackTrace();
		}
	}

	private class ArticulosAdapter extends RecyclerView.Adapter<ArticulosAdapter.ArticuloHolder> {
		List<Articulo> list;
		int mode;

		ArticulosAdapter(List<Articulo> list, int mode){
			this.list = list;
			this.mode = mode;
		}

		@NonNull @Override public ArticuloHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i){
			return new ArticuloHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_articulo, viewGroup, false));
		}

		@Override public void onBindViewHolder(@NonNull ArticuloHolder articuloHolder, int i){
			articuloHolder.setArticulo(list.get(i));
		}

		@Override public int getItemCount(){
			return list.size();
		}

		Articulo getItemAt(int index){
			return list.get(index);
		}

		List<Articulo> getItems(){
			return list;
		}

		void setItems(List<Articulo> list){
			this.list = list;
			notifyDataSetChanged();
		}

		class ArticuloHolder extends RecyclerView.ViewHolder {
			ArticuloHolder(@NonNull View itemView){
				super(itemView);
			}

			void setArticulo(Articulo a){
				((TextView) itemView.findViewById(R.id.txtDescripcion)).setText(a.getDescripcion());
				((TextView) itemView.findViewById(R.id.txtPrecio)).setText(
					String.format(getResources().getString(R.string.dinero_format), a.getPrecio(mode)));
				itemView.setOnClickListener(new View.OnClickListener() {
					@Override public void onClick(View v){
						articuloSelected(getItemAt(getAdapterPosition()));
					}
				});
			}
		}
	}

	private class LineasAdapter extends RecyclerView.Adapter<LineasAdapter.LineaHolder> {
		List<Linea> list;

		LineasAdapter(List<Linea> list){
			this.list = list;
		}

		@NonNull @Override public LineaHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i){
			return new LineaHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_linea, viewGroup, false));
		}

		@Override public void onBindViewHolder(@NonNull LineaHolder lineaHolder, int i){
			lineaHolder.setLinea(list.get(i));
		}

		@Override public int getItemCount(){
			return list.size();
		}

		Linea getItemAt(int index){
			return list.get(index);
		}

		class LineaHolder extends RecyclerView.ViewHolder {
			LineaHolder(@NonNull View itemView){
				super(itemView);
			}

			void setLinea(Linea linea){
				((TextView) itemView.findViewById(R.id.txtLinea)).setText(linea.getDescripcion());
				itemView.setOnClickListener(new View.OnClickListener() {
					@Override public void onClick(View v){
						lineaSelected(getItemAt(getAdapterPosition()));
					}
				});
			}
		}
	}
}
