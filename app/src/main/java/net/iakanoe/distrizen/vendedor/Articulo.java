package net.iakanoe.distrizen.vendedor;

import java.io.Serializable;

class Articulo implements Serializable {
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
		return -1;
	}


	Articulo(String codigo, String descripcion, String linea, double precioV1, double precioV2, double precioV3){
		this.codigo = codigo;
		this.descripcion = descripcion;
		this.linea = linea;
		this.precioV1 = precioV1;
		this.precioV2 = precioV2;
		this.precioV3 = precioV3;
	}

	Articulo(String codigo, String descripcion, double precio){
		this.codigo = codigo;
		this.descripcion = descripcion;
		this.linea = null;
		this.precioV1 = precio;
		this.precioV2 = precio;
		this.precioV3 = precio;
	}

	String getDescripcion(){
		return descripcion;
	}

	String getLinea(){
		return linea;
	}

	String getCodigo(){
		return codigo;
	}
}