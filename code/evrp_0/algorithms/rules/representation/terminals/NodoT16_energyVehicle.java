package evrp_0.algorithms.rules.representation.terminals;

import evrp_0.algorithms.rules.Regla;
import evrp_0.algorithms.rules.representation.Nodo;
import evrp_0.algorithms.rules.representation.NodoTerminal;
import evrp_0.domain.DomainInfo;

public class NodoT16_energyVehicle extends NodoTerminal {

	private static final NodoT16_energyVehicle INSTANCE = new NodoT16_energyVehicle();

	public static NodoT16_energyVehicle getInstance() { // implements singleton
		return INSTANCE;
	}

	@Override
	public Nodo clona() {
		return INSTANCE;
	}

	@Override
	public double value(DomainInfo regla) {
		return regla.active.remainningEnergy;
	}

	@Override
	public String simbolo() {
		return "VE";
	}

	@Override
	public double orden() {
		return 16;
	}
}
