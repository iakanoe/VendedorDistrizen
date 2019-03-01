package net.iakanoe.distrizen.vendedor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

class Pedido implements Serializable {
	private List<Articulo> carrito = new ArrayList<>();
	private List<Integer> cantidades = new ArrayList<>();
	private Cliente cliente;

	Pedido(Cliente cliente){
		this.cliente = cliente;
	}

	Pedido(){}

	Articulo getItem(int i){
		return carrito.get(i);
	}

	int getCantidad(int i){
		return cantidades.get(i);
	}

	Cliente getCliente(){
		return cliente;
	}

	void addArticulo(Articulo a, int cantidad){
		carrito.add(a);
		cantidades.add(cantidad);
	}

	double getPrecioTotal(){
		double total = 0.0;
		for(Articulo a : carrito)
			total += a.getPrecio(cliente.getListaPrecios()) * cantidades.get(carrito.indexOf(a));
		return total;
	}

	void addArticulo(Articulo a, int cantidad, int posicion){
		carrito.add(posicion, a);
		cantidades.add(posicion, cantidad);
	}

	void removeArticulo(int position){
		carrito.remove(position);
		cantidades.remove(position);
	}

	int getListaPrecios(){
		return cliente.getListaPrecios();
	}

	int size(){
		return carrito.size();
	}

	List<Articulo> getItems(){
		return carrito;
	}

	void setCantidad(int a, int c){
		if(a < 0 | a > cantidades.size()) return;
		cantidades.set(a, c);
	}

	double getPrecioUnitario(int i){
		return carrito.get(i).getPrecio(cliente.getListaPrecios());
	}

	double getPrecio(int i){
		if(carrito.get(i) == null || cantidades.get(i) == null) return 0;
		return carrito.get(i).getPrecio(cliente.getListaPrecios()) * cantidades.get(i);
	}

	String getDescripcion(int i){
		return carrito.get(i).getDescripcion();
	}
}

class PedidoRaw extends Pedido {
	private int id;
	private String cliente;
	private double total;
	private int size;

	PedidoRaw(int id, String cliente, double total, int size){
		super();
		this.id = id;
		this.cliente = cliente;
		this.total = total;
		this.size = size;
	}

	@Override int size(){
		return size;
	}

	public int getId(){
		return id;
	}

	public String getClienteRaw(){
		return cliente;
	}

	public double getTotal(){
		return total;
	}
}
