package evrp_0.algorithms.rules.representation.functions;

import evrp_0.algorithms.rules.representation.Nodo;
import evrp_0.algorithms.rules.representation.NodoOperacion;

public class NodoMax0 extends NodoOperacion {

	@Override
	public Nodo clona() {
		return new NodoMax0();
	}

	@Override
	public double value(double izq, double der) {
		return Math.max(izq, 0);
	}

	@Override
	public boolean esUnaria() {
		return true;
	}

	@Override
	public String simbolo() {
		return "Math.max";
	}

	@Override
	public String simboloPrintable() {
		return "Math.max0";
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
		return 108;
	}
}
