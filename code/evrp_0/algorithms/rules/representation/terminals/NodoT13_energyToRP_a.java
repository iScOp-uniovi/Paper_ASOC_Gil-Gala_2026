package evrp_0.algorithms.rules.representation.terminals;

import evrp_0.algorithms.rules.representation.Nodo;
import evrp_0.algorithms.rules.representation.NodoTerminal;
import evrp_0.domain.DomainInfo;

public class NodoT13_energyToRP_a extends NodoTerminal {

	private static final NodoT13_energyToRP_a INSTANCE = new NodoT13_energyToRP_a();

	public static NodoT13_energyToRP_a getInstance() { // implements singleton
		return INSTANCE;
	}

	@Override
	public Nodo clona() {
		return INSTANCE;
	}

	@Override
	public double value(DomainInfo regla) {
		if (regla.previousVisited.isDepot)	return 0.0;
		return regla.instance.computeEnergy(regla.previousVisited, regla.previousVisited.nearestRechargingPoint);
	}

	@Override
	public String simbolo() {
		return "ERP_a";
	}

	@Override
	public double orden() {
		return 13;
	}
}
