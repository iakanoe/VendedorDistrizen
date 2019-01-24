package net.iakanoe.distrizen.vendedor;

import org.json.JSONException;
import org.json.JSONObject;

interface CommunicationListener {
	void onCommunicationFailed();

	void onCommunicationAchieved(String result);
}

abstract class LoginListener implements CommunicationListener {
	@Override public void onCommunicationAchieved(String result){
		try {
			JSONObject resultado = new JSONObject(result);
			if(resultado.getBoolean("result")){
				String name = resultado.getString("nombre");
				String token = resultado.getString("codigo");
				onLoggedIn(name, token);
			} else {
				onLoginError("El usuario o la contraseña son incorrectos.");
			}
		} catch(JSONException e) {
			e.printStackTrace();
			onLoginError("Hubo un error.");
		}
	}

	abstract void onLoggedIn(String name, String token);

	abstract void onLoginError(String error);
}
