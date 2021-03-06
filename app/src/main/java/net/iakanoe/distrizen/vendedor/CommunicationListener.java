package net.iakanoe.distrizen.vendedor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

interface CommunicationListener {
	void onCommunicationFailed();

	void onCommunicationAchieved(String result);
}

abstract class LoginListener implements CommunicationListener {
	@Override public void onCommunicationAchieved(String result){
		try {
			JSONObject resultado = new JSONObject(result);
			if(resultado.getInt("result") == 1){
				String name = resultado.getString("nombre");
				String token = resultado.getString("codigo");
				onLoggedIn(name, token);
			} else {
				onLoginError("El usuario o la contraseña son incorrectos.");
			}
		} catch(JSONException e) {
			e.printStackTrace();
			onCommunicationFailed();
		}
	}

	abstract void onLoggedIn(String name, String token);

	abstract void onLoginError(String error);
}

abstract class ClientesListener implements CommunicationListener {
	@Override public void onCommunicationAchieved(String result){
		try {
			JSONArray resultado = new JSONArray(result);
			List<Cliente> list = new ArrayList<>();
			for(int i = 0; i < resultado.length(); i++){
				JSONObject linea = resultado.getJSONObject(i);
				String cod = linea.getString("codigo");
				String nom = linea.getString("nombre");
				int listap = linea.getInt("listap");
				int civacl = linea.getInt("civa");
				Cliente c = new Cliente(cod, nom, listap, civacl);
				list.add(c);
			}
			onGotClientes(list);
		} catch(JSONException e) {
			e.printStackTrace();
			onCommunicationFailed();
		}
	}

	abstract void onGotClientes(List<Cliente> list);
}

abstract class ArticulosListener implements CommunicationListener {
	@Override public void onCommunicationAchieved(String result){
		onGotPlainTextArticulos(result);
		try {
			JSONArray resultado = new JSONArray(result);
			List<Articulo> list = new ArrayList<>();
			for(int i = 0; i < resultado.length(); i++){
				JSONObject linea = resultado.getJSONObject(i);
				String cod = linea.getString("codiarti");
				String des = linea.getString("descrip");
				String rub = linea.getString("codirubr");
				double pr1 = linea.getDouble("venta");
				double pr2 = linea.getDouble("venta2");
				double pr3 = linea.getDouble("venta3");
				Articulo a = new Articulo(cod, des, rub, pr1, pr2, pr3);
				list.add(a);
			}
			onGotArticulos(list);
		} catch(JSONException e) {
			e.printStackTrace();
			onCommunicationFailed();
		}
	}

	abstract void onGotArticulos(List<Articulo> list);

	abstract void onGotPlainTextArticulos(String json);
}

abstract class LineasListener implements CommunicationListener {
	@Override public void onCommunicationAchieved(String result){
		onGotPlainTextLineas(result);
		try {
			JSONArray resultado = new JSONArray(result);
			List<Linea> list = new ArrayList<>();
			for(int i = 0; i < resultado.length(); i++){
				JSONObject linea = resultado.getJSONObject(i);
				String cod = linea.getString("codigo");
				String des = linea.getString("descrip");
				Linea line = new Linea(cod, des);
				list.add(line);
			}
			onGotLineas(list);
		} catch(JSONException e) {
			e.printStackTrace();
			onCommunicationFailed();
		}
	}

	abstract void onGotLineas(List<Linea> list);

	abstract void onGotPlainTextLineas(String json);
}

abstract class CrearPedidoListener implements CommunicationListener {
	@Override public void onCommunicationAchieved(String result){
		if(!result.contains("success")){
			onCommunicationFailed();
			return;
		}
		onCrearPedidoSuccess();
	}

	abstract void onCrearPedidoSuccess();
}

abstract class GetPedidosListener implements CommunicationListener {
	@Override public void onCommunicationAchieved(String result){
		if(!result.contains("[")){
			onCommunicationFailed();
			return;
		}
		try {
			JSONArray resultado = new JSONArray(result);
			List<PedidoRaw> list = new ArrayList<>();
			for(int i = 0; i < resultado.length(); i++){
				JSONObject linea = resultado.getJSONObject(i);
				int pedido = linea.getInt("pedido");
				String cliente = linea.getString("cliente");
				double total = linea.getDouble("total");
				int size = linea.getInt("size");
				list.add(new PedidoRaw(pedido, cliente, total, size));
			}
			onGotPedidos(list);
		} catch(JSONException e) {
			e.printStackTrace();
			onCommunicationFailed();
		}
	}

	abstract void onGotPedidos(List<PedidoRaw> pedidos);
}

abstract class PedidoInfoListener implements CommunicationListener {
	@Override public void onCommunicationAchieved(String result){
		if(!result.contains("[")){
			onCommunicationFailed();
			return;
		}
		try {
			JSONObject resultado = new JSONObject(result);
			JSONObject clientejson = resultado.getJSONObject("cliente");
			String codigocli = clientejson.getString("codigo");
			String nombrecli = clientejson.getString("nombre");
			int listapcli = clientejson.getInt("listap");
			int civacli = clientejson.getInt("civa");
			Cliente cliente = new Cliente(codigocli, nombrecli, listapcli, civacli);
			JSONArray itemsjson = resultado.getJSONArray("items");
			Pedido pedido = new Pedido(cliente);
			for(int i = 0; i < itemsjson.length(); i++){
				JSONObject item = itemsjson.getJSONObject(i);
				double precio = item.getDouble("precio");
				int cantidad = item.getInt("cantidad");
				double preciounitario = precio / cantidad;
				String codigo = item.getString("codigo");
				String descrip = item.getString("descrip");
				pedido.addArticulo(new Articulo(codigo, descrip, preciounitario), cantidad);
			}
			onGotInfo(pedido);
		} catch(JSONException e) {
			e.printStackTrace();
			onCommunicationFailed();
		}
	}

	abstract void onGotInfo(Pedido pedido);
}

abstract class CobrarListener implements CommunicationListener {
	@Override public void onCommunicationAchieved(String result){
		if(result.contains("success")){
			onCobrarSuccess();
			return;
		}
		onCommunicationFailed();
	}

	abstract void onCobrarSuccess();
}