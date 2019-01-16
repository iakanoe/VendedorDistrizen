package net.iakanoe.distrizen.vendedor;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class LoginActivity extends AppCompatActivity {

	@Override protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setupUI();
	}

	void setupUI(){
		setContentView(R.layout.activity_login);

		findViewById(R.id.btnLogin).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v){
				tryLogin();
			}
		});
	}

	void tryLogin(){
		startActivity(new Intent(getApplicationContext(), MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)); // PROVISORIO
		//Tratar de hacer login
	}
}
