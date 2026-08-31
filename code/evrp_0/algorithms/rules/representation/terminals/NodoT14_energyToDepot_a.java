package evrp_0.algorithms.rules.representation.terminals;

import evrp_0.algorithms.rules.Regla;
import evrp_0.algorithms.rules.representation.Nodo;
import evrp_0.algorithms.rules.representation.NodoTerminal;
import evrp_0.domain.DomainInfo;

public class NodoT14_energyToDepot_a extends NodoTerminal {

	private static final NodoT14_energyToDepot_a INSTANCE = new NodoT14_energyToDepot_a();

	public static NodoT14_energyToDepot_a getInstance() { // implements singleton
		return INSTANCE;
	}

	@Override
	public Nodo clona() {
		return INSTANCE;
	}

	@Override
	public double value(DomainInfo regla) {
		if (regla.previousVisited.isDepot)	return 0.0;
		return regla.instance.computeEnergy(regla.previousVisited, regla.active.initialPoint); 
	}

	@Override
	public String simbolo() {
		return "EDep_a";
	}

	@Override
	public double orden() {
		return 14;
	}
}
