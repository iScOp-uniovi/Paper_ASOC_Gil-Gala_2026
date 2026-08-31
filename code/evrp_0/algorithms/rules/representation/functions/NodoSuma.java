package evrp_0.algorithms.rules.representation.functions;

import evrp_0.algorithms.rules.representation.Nodo;
import evrp_0.algorithms.rules.representation.NodoOperacion;

public class NodoSuma extends NodoOperacion {

	@Override
	public Nodo clona() {
		return new NodoSuma();
	}

	@Override
	public double value(double izq, double der) {
		return izq + der;
	}

	@Override
	public boolean esUnaria() {
		return false;
	}

	@Override
	public String simbolo() {
		return "+";
	}

	@Override
	public boolean esBinariaInversa() {
		return false;
	}

	@Override
	public boolean simetrico() {
		return true;
	}

	@Override
	public boolean noAplicaRestriccionesDimension() {
		return false;
	}

	@Override
	public double orden() {
		return 100;
	}
}
