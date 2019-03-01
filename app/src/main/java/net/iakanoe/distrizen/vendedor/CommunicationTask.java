package net.iakanoe.distrizen.vendedor;

import android.net.Uri;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

class CommunicationTask<T extends CommunicationListener> extends AsyncTask<String, Void, String> {
	private static final String url = "http://softway.com.ar/distrizen/android.php";
	T listener;
	boolean json = false;

	void setListener(T listener){
		this.listener = listener;
	}

	@Override protected String doInBackground(String... query){
		if(query.length == 0) return null;
		try {
			return executeQuery(query[0]);
		} catch(IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override protected void onPostExecute(String result){
		super.onPostExecute(result);
		if(result == null) listener.onCommunicationFailed();
		else listener.onCommunicationAchieved(result);
	}

	private String executeQuery(String query) throws IOException{
		HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
		connection.setReadTimeout(15000);
		connection.setConnectTimeout(15000);
		connection.setRequestMethod("POST");
		connection.setDoInput(true);
		connection.setDoOutput(true);
		if(json){
			connection.setRequestProperty("Content-Type", "application/json");
		}
		connection.connect();

		OutputStream outputStream = connection.getOutputStream();
		BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
		bufferedWriter.write(query);
		bufferedWriter.flush();
		bufferedWriter.close();
		outputStream.close();

		InputStream inputStream = connection.getInputStream();
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
		StringBuilder stringBuilder = new StringBuilder();
		String line;
		while((line = bufferedReader.readLine()) != null)
			stringBuilder.append(line).append('\n');
		bufferedReader.close();
		inputStream.close();
		connection.disconnect();
		return stringBuilder.toString();
	}
}

class LoginTask extends CommunicationTask<LoginListener> {
	@Override void setListener(LoginListener listener){
		this.listener = listener;
	}

	void login(String user, String pass){
		String query = new Uri.Builder()
			.appendQueryParameter("action", "login")
			.appendQueryParameter("user", user)
			.appendQueryParameter("pass", pass)
			.appendQueryParameter("auth", "00distrizen00")
			.build()
			.getEncodedQuery();

		execute(query);
	}
}

class ClientesTask extends CommunicationTask<ClientesListener> {
	@Override void setListener(ClientesListener listener){
		this.listener = listener;
	}

	void getClientes(String user){
		String query = new Uri.Builder()
			.appendQueryParameter("action", "clientes")
			.appendQueryParameter("user", user)
			.appendQueryParameter("auth", "00distrizen00")
			.build()
			.getEncodedQuery();

		execute(query);
	}
}

class ArticulosTask extends CommunicationTask<ArticulosListener> {
	@Override void setListener(ArticulosListener listener){
		this.listener = listener;
	}

	void getArticulos(){
		String query = new Uri.Builder()
			.appendQueryParameter("action", "articulos")
			.appendQueryParameter("auth", "00distrizen00")
			.build()
			.getEncodedQuery();
		execute(query);
	}
}

class LineasTask extends CommunicationTask<LineasListener> {
	@Override void setListener(LineasListener listener){
		this.listener = listener;
	}

	void getLineas(){
		String query = new Uri.Builder()
			.appendQueryParameter("action", "lineas")
			.appendQueryParameter("auth", "00distrizen00")
			.build()
			.getEncodedQuery();
		execute(query);
	}
}

class CrearPedidoTask extends CommunicationTask<CrearPedidoListener> {
	@Override void setListener(CrearPedidoListener listener){
		this.listener = listener;
	}

	void subirPedido(Pedido pedido){
		if(pedido == null) return;
		try {
			json = true;
			JSONArray array = new JSONArray();
			for(int i = 0; i < pedido.size(); i++){
				JSONObject object = new JSONObject();
				object.put("articulo", pedido.getItem(i).getCodigo());
				object.put("cantidad", pedido.getCantidad(i));
				object.put("importe", pedido.getPrecio(i));
				array.put(object);
			}
			JSONObject query = new JSONObject();
			query.put("action", "crearpedido");
			query.put("auth", "00distrizen00");
			query.put("pedidoarray", array);
			query.put("pedidoclient", pedido.getCliente().getCodigo());
			execute(query.toString());
		} catch(JSONException e) {
			e.printStackTrace();
			listener.onCommunicationFailed();
		}
	}
}

class GetPedidosTask extends CommunicationTask<GetPedidosListener> {
	@Override void setListener(GetPedidosListener listener){
		this.listener = listener;
	}

	void getPedidos(String user){
		String query = new Uri.Builder()
			.appendQueryParameter("action", "getpedidos")
			.appendQueryParameter("auth", "00distrizen00")
			.appendQueryParameter("user", user)
			.build()
			.getEncodedQuery();
		execute(query);
	}
}

class PedidoInfoTask extends CommunicationTask<PedidoInfoListener> {
	@Override void setListener(PedidoInfoListener listener){
		this.listener = listener;
	}

	void getPedidoInfo(int pedido){
		String query = new Uri.Builder()
			.appendQueryParameter("action", "pedidoinfo")
			.appendQueryParameter("auth", "00distrizen00")
			.appendQueryParameter("id", String.valueOf(pedido))
			.build()
			.getEncodedQuery();
		execute(query);
	}
}

class CobrarTask extends CommunicationTask<CobrarListener> {
	@Override void setListener(CobrarListener listener){
		this.listener = listener;
	}

	void cobrar(String cliente, double importe, int factura){
		String query = new Uri.Builder()
			.appendQueryParameter("action", "cobrar")
			.appendQueryParameter("auth", "00distrizen00")
			.appendQueryParameter("cliente", cliente)
			.appendQueryParameter("importe", String.valueOf(importe))
			.appendQueryParameter("factura", String.valueOf(factura))
			.build()
			.getEncodedQuery();
		execute(query);
	}
}