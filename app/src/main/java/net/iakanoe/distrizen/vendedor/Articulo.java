package net.iakanoe.distrizen.vendedor;

public class Articulo {
	private String codigo;
	private String descripcion;
	private String linea;
	private double precioV1;
	private double precioV2;
	private double precioV3;

	double getPrecio(int v){
		if(v == 1) return precioV1;
		if(v == 2) return precioV2;
		if(v == 3) return precioV3;
		return 0;
	}

	String getDescripcion(){
		return descripcion;
	}

	public String getLinea(){
		return linea;
	}
}
