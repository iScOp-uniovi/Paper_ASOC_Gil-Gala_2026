package evrp_0.algorithms.rules.representation.terminals;

import evrp_0.algorithms.rules.Regla;
import evrp_0.algorithms.rules.representation.Nodo;
import evrp_0.algorithms.rules.representation.NodoTerminal;
import evrp_0.domain.DomainInfo;

public class NodoT12_energyToDepot extends NodoTerminal {

	private static final NodoT12_energyToDepot INSTANCE = new NodoT12_energyToDepot();

	public static NodoT12_energyToDepot getInstance() { // implements singleton
		return INSTANCE;
	}

	@Override
	public Nodo clona() {
		return INSTANCE;
	}

	@Override
	public double value(DomainInfo regla) {
		return regla.instance.computeEnergy(regla.selected, regla.active.initialPoint); 
	}

	@Override
	public String simbolo() {
		return "EDep";
	}

	@Override
	public double orden() {
		return 12;
	}
}
