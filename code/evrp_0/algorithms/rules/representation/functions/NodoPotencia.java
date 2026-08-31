package evrp_0.algorithms.rules.representation.functions;

import evrp_0.algorithms.rules.representation.Nodo;
import evrp_0.algorithms.rules.representation.NodoOperacion;

public class NodoPotencia extends NodoOperacion {

	@Override
	public Nodo clona() {
		return new NodoPotencia();
	}

	@Override
	public double value(double izq, double der) {
		return Math.pow(izq, 2);
	}

	@Override
	public boolean esUnaria() {
		return true;
	}

	@Override
	public String simbolo() {
		return "Math.pow";
	}

	@Override
	public boolean esBinariaInversa() {
		return false;
	}

	@Override
	public boolean noAplicaRestriccionesDimension() {
		return true;
	}

	@Override
	public boolean simetrico() {
		return false;
	}

	@Override
	public double orden() {
		return 103;
	}
}
