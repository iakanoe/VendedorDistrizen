package net.iakanoe.distrizen.vendedor;

class Linea {
	private String codigo;
	private String descripcion;

	Linea(String codigo, String descripcion){
		this.codigo = codigo;
		this.descripcion = descripcion;
	}

	String getDescripcion(){
		return descripcion;
	}

	String getCodigo(){
		return codigo;
	}
}
