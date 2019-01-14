package net.iakanoe.distrizen.vendedor;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LoginActivity extends AppCompatActivity{
	
	@Override protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		setupUI();
	}
	
	void setupUI(){
		findViewById(R.id.btnLogin).setOnClickListener(new View.OnClickListener(){
			@Override public void onClick(View v){
				tryLogin();
			}
		});
	}
	
	void tryLogin(){
		startActivity(new Intent(getApplicationContext(), MainActivity.class)); // PROVISORIO
		//Tratar de hacer login
	}
}
