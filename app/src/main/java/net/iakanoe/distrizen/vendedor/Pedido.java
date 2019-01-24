package net.iakanoe.distrizen.vendedor;

import android.util.SparseIntArray;

import java.util.List;

class Pedido {
	private List<Articulo> carrito;
	private Cliente cliente;
	private SparseIntArray cantidades = new SparseIntArray();

	Pedido(Cliente cliente){
		this.cliente = cliente;
	}

	void addArticulo(Articulo a, int cantidad){
		carrito.add(a);
		cantidades.append(carrito.indexOf(a), cantidad);
	}

	double getPrecioTotal(){
		double total = 0.0;
		for(Articulo a : carrito)
			total += (a.getPrecio(cliente.getListaPrecios()) * cantidades.get(carrito.indexOf(a)));
		return total;
	}

	int size(){
		return carrito.size();
	}

	List<Articulo> getItems(){
		return carrito;
	}

	double getPrecio(int i){
		return carrito.get(i).getPrecio(cliente.getListaPrecios());
	}

	String getDescripcion(int i){
		return carrito.get(i).getDescripcion();
	}
}
