package evrp_0.algorithms.rules.representation.terminals;

import evrp_0.algorithms.rules.Regla;
import evrp_0.algorithms.rules.representation.Nodo;
import evrp_0.algorithms.rules.representation.NodoTerminal;
import evrp_0.domain.DomainInfo;

public class NodoT11_energyToRP extends NodoTerminal {

	private static final NodoT11_energyToRP INSTANCE = new NodoT11_energyToRP();

	public static NodoT11_energyToRP getInstance() { // implements singleton
		return INSTANCE;
	}

	@Override
	public Nodo clona() {
		return INSTANCE;
	}

	@Override
	public double value(DomainInfo regla) {
		return regla.instance.computeEnergy(regla.selected, regla.selected.nearestRechargingPoint);
	}

	@Override
	public String simbolo() {
		return "ERP";
	}

	@Override
	public double orden() {
		return 11;
	}
}
