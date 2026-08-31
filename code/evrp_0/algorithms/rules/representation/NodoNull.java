package evrp_0.algorithms.rules.representation;

import evrp_0.algorithms.rules.Regla;
import evrp_0.domain.DomainInfo;

public class NodoNull extends NodoAbstracto {

	private static final NodoNull INSTANCE = new NodoNull();

	public static NodoNull getInstance() {
		return INSTANCE;
	}

	@Override
	public boolean esTerminal() {
		return false;
	}

	@Override
	public boolean esConstante() {
		return false;
	}

	@Override
	public boolean noAplicaRestriccionesDimension() {
		return false;
	}

	@Override
	public double value() {
		return 0;
	}

	@Override
	public double value(DomainInfo regla) {
		return -1;
	}

	@Override
	public double value(double izq, double der) {
		return -1;
	}

	@Override
	public Nodo clona() {
		return INSTANCE;
	}

	@Override
	public boolean esOperacion() {
		return false;
	}

	@Override
	public boolean esUnaria() {
		return false;
	}

	@Override
	public boolean esBinariaInversa() {
		return false;
	}

	@Override
	public String simbolo() {
		return "NULL";
	}

	@Override
	public boolean esNull() {
		return true;
	}

	@Override
	public boolean simetrico() {
		return false;
	}

	@Override
	public double orden() {
		return -1.0;
	}
	
	@Override
	public double getDimension() {
		return 0;
	}

	@Override
	public void setDimension(double d) {
	}

}
