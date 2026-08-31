package evrp_0.algorithms.rules.representation.functions;

import evrp_0.algorithms.rules.representation.Nodo;
import evrp_0.algorithms.rules.representation.NodoOperacion;

public class NodoMin0 extends NodoOperacion {

	@Override
	public Nodo clona() {
		return new NodoMin0();
	}

	@Override
	public double value(double izq, double der) {
		return Math.min(izq, 0);
	}

	@Override
	public boolean esUnaria() {
		return true;
	}

	@Override
	public String simbolo() {
		return "Math.min";
	}
	
	@Override
	public String simboloPrintable() {
		return "Math.min0";
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
		return 106;
	}	
}
