package evrp_0.algorithms.rules.representation.functions;

import evrp_0.algorithms.rules.representation.Nodo;
import evrp_0.algorithms.rules.representation.NodoOperacion;

public class NodoDivision extends NodoOperacion {

	@Override
	public Nodo clona() {
		return new NodoDivision();
	}

	@Override
	public double value(double izq, double der) {
		double d = izq / der;
		if (Double.isNaN(d) || Double.isInfinite(d)) { 
			d = 0;
		}
		return d;
	}

	@Override
	public boolean esUnaria() {
		return false;
	}

	@Override
	public String simbolo() {
		return "/";
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
		return 112;
	}
}
