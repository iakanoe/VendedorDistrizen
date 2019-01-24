package net.iakanoe.distrizen.vendedor;

import android.net.Uri;
import android.os.AsyncTask;

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
	private static final String url = "TODO"; // TODO  http://url.com/android.php
	protected T listener;

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
		connection.setReadTimeout(10000);
		connection.setConnectTimeout(15000);
		connection.setRequestMethod("POST");
		connection.setDoInput(true);
		connection.setDoOutput(true);
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
			.build()
			.getEncodedQuery();

		execute(query);
	}
}
