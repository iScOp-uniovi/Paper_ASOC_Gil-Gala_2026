package evrp_0.algorithms.rules.representation;

import evrp_0.algorithms.rules.Regla;
import evrp_0.domain.DomainInfo;

public abstract class NodoConstanteAbstracto extends NodoAbstracto {

	@Override
	public boolean esTerminal() {
		return true;
	}

	@Override
	public boolean esConstante() {
		return true;
	}

	@Override
	public boolean noAplicaRestriccionesDimension() {
		return false;
	}

	@Override
	public double value() {
		return -1;
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
	public boolean esNull() {
		return false;
	}

	@Override
	public boolean simetrico() {
		return false;
	}

	@Override
	public double orden() {
		return value();
	}
	
	@Override
	public double getDimension() {
		return 0;
	}

	@Override
	public void setDimension(double d) {
	}
}
