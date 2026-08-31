package evrp_0.algorithms.rules.representation.terminals;

import evrp_0.algorithms.rules.representation.Nodo;
import evrp_0.algorithms.rules.representation.NodoConstanteAbstracto;
import evrp_0.domain.DomainInfo;

public class NodoConstante extends NodoConstanteAbstracto {

	private double value;

	public NodoConstante(double value) {
		this.value = value;
	}

	@Override
	public Nodo clona() {
		return new NodoConstante(value);
	}

	@Override
	public double value(DomainInfo domain) {
		return value;
	}

	@Override
	public double value(double izq, double der) {
		return value;
	}

	@Override
	public double value() {
		return value;
	}

	@Override
	public String simbolo() {
		return String.valueOf(value);
	}

}
