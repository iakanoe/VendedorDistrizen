package net.iakanoe.distrizen.vendedor;

import java.io.Serializable;

class Cliente implements Serializable {
	private String codigo;
	private String nombre;
	private int listap;
	private int civa;

	Cliente(String codigo, String nombre, int listap, int civa){
		this.codigo = codigo;
		this.nombre = nombre;
		this.listap = listap;
		this.civa = civa;
	}

	int getListaPrecios(){
		return listap;
	}

	String getCodigo(){
		return codigo;
	}

	String getNombre(){
		return nombre;
	}

	int getCiva(){
		return civa;
	}
}
