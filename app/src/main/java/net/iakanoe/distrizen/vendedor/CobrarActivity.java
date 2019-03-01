package net.iakanoe.distrizen.vendedor;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class CobrarActivity extends AppCompatActivity {

	@Override protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cobrar);
		setupUI();
	}

	void setupUI(){
		findViewById(R.id.btnCobrar).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v){
				cobrar();
			}
		});
		if(getIntent().getBooleanExtra("frommain", false)){
			findViewById(R.id.textView13).setVisibility(View.VISIBLE);
			findViewById(R.id.etxtCobrarFactura).setVisibility(View.VISIBLE);
			findViewById(R.id.textView17).setVisibility(View.GONE);
			findViewById(R.id.txtCobrarTotal).setVisibility(View.GONE);
			findViewById(R.id.textView8).setVisibility(View.GONE);
			findViewById(R.id.txtCobrarPedidoID).setVisibility(View.GONE);
			findViewById(R.id.txtCobrarCliente).setVisibility(View.GONE);
			findViewById(R.id.etxtCobrarCliente).setVisibility(View.VISIBLE);
		}
		((EditText) findViewById(R.id.etxtCobrarImporte)).setFilters(
			new InputFilter[]{new DigitsInputFilter(Integer.MAX_VALUE, 2, Double.POSITIVE_INFINITY)});
	}

	void cobrar(){
		int factura = Integer.parseInt(((TextView) findViewById(R.id.etxtCobrarFactura)).getText().toString());
		String cliente = ((TextView) findViewById(R.id.etxtCobrarCliente)).getText().toString();
		double importe = Double.parseDouble(((TextView) findViewById(R.id.etxtCobrarImporte)).getText().toString());
		CobrarTask cobrarTask = new CobrarTask();
		cobrarTask.setListener(new CobrarListener() {
			@Override void onCobrarSuccess(){
				new AlertDialog.Builder(CobrarActivity.this)
					.setMessage("Se subió el pago a la base de datos correctamente.")
					.setCancelable(false)
					.setPositiveButton("Volver", new DialogInterface.OnClickListener() {
						@Override public void onClick(DialogInterface dialog, int which){
							finish();
						}
					})
					.create()
					.show();
			}

			@Override public void onCommunicationFailed(){
				new AlertDialog.Builder(CobrarActivity.this)
					.setMessage("Ocurrió un fallo en la comunicación con el servicio de pagos.")
					.setCancelable(true)
					.setNegativeButton("Volver", new DialogInterface.OnClickListener() {
						@Override public void onClick(DialogInterface dialog, int which){
							dialog.cancel();
						}
					})
					.setPositiveButton("Intentar de nuevo", new DialogInterface.OnClickListener() {
						@Override public void onClick(DialogInterface dialog, int which){
							cobrar();
						}
					})
					.create()
					.show();
			}
		});
		cobrarTask.cobrar(cliente, importe, factura);
	}

	class DigitsInputFilter implements InputFilter {

		private final String DOT = ".";

		private int mMaxIntegerDigitsLength;
		private int mMaxDigitsAfterLength;
		private double mMax;


		DigitsInputFilter(int maxDigitsBeforeDot, int maxDigitsAfterDot, double maxValue){
			mMaxIntegerDigitsLength = maxDigitsBeforeDot;
			mMaxDigitsAfterLength = maxDigitsAfterDot;
			mMax = maxValue;
		}

		@Override
		public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend){
			String allText = getAllText(source, dest, dstart);
			String onlyDigitsText = getOnlyDigitsPart(allText);

			if(allText.isEmpty()){
				return null;
			} else {
				double enteredValue;
				try {
					enteredValue = Double.parseDouble(onlyDigitsText);
				} catch(NumberFormatException e) {
					return "";
				}
				return checkMaxValueRule(enteredValue, onlyDigitsText);
			}
		}


		private CharSequence checkMaxValueRule(double enteredValue, String onlyDigitsText){
			if(enteredValue > mMax){
				return "";
			} else {
				return handleInputRules(onlyDigitsText);
			}
		}

		private CharSequence handleInputRules(String onlyDigitsText){
			if(isDecimalDigit(onlyDigitsText)){
				return checkRuleForDecimalDigits(onlyDigitsText);
			} else {
				return checkRuleForIntegerDigits(onlyDigitsText.length());
			}
		}

		private boolean isDecimalDigit(String onlyDigitsText){
			return onlyDigitsText.contains(DOT);
		}

		private CharSequence checkRuleForDecimalDigits(String onlyDigitsPart){
			String afterDotPart = onlyDigitsPart.substring(onlyDigitsPart.indexOf(DOT), onlyDigitsPart.length() - 1);
			if(afterDotPart.length() > mMaxDigitsAfterLength){
				return "";
			}
			return null;
		}

		private CharSequence checkRuleForIntegerDigits(int allTextLength){
			if(allTextLength > mMaxIntegerDigitsLength){
				return "";
			}
			return null;
		}

		private String getOnlyDigitsPart(String text){
			return text.replaceAll("[^0-9?!\\.]", "");
		}

		private String getAllText(CharSequence source, Spanned dest, int dstart){
			String allText = "";
			if(!dest.toString().isEmpty()){
				if(source.toString().isEmpty()){
					allText = deleteCharAtIndex(dest, dstart);
				} else {
					allText = new StringBuilder(dest).insert(dstart, source).toString();
				}
			}
			return allText;
		}

		private String deleteCharAtIndex(Spanned dest, int dstart){
			StringBuilder builder = new StringBuilder(dest);
			builder.deleteCharAt(dstart);
			return builder.toString();
		}
	}
}