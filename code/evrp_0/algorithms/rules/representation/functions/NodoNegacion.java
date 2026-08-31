package evrp_0.algorithms.rules.representation.functions;

import evrp_0.algorithms.rules.representation.Nodo;
import evrp_0.algorithms.rules.representation.NodoOperacion;

public class NodoNegacion extends NodoOperacion {

	@Override
	public Nodo clona() {
		return new NodoNegacion();
	}

	@Override
	public double value(double izq, double der) {
		return -(izq);
	}

	@Override
	public boolean esUnaria() {
		return true;
	}

	@Override
	public String simbolo() {
		return "-";
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
		return 104;
	}
}
