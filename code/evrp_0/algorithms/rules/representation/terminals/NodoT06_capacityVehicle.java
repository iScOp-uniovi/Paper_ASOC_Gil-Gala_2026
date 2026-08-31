package evrp_0.algorithms.rules.representation.terminals;

import evrp_0.algorithms.rules.representation.Nodo;
import evrp_0.algorithms.rules.representation.NodoTerminal;
import evrp_0.domain.DomainInfo;

public class NodoT06_capacityVehicle extends NodoTerminal {

	private static final NodoT06_capacityVehicle INSTANCE = new NodoT06_capacityVehicle();

	public static NodoT06_capacityVehicle getInstance() { // implements singleton
		return INSTANCE;
	}

	@Override
	public Nodo clona() {
		return INSTANCE;
	}

	@Override
	public double value(DomainInfo domain) {
		return domain.active.remainingCapacity;
	}

	@Override
	public String simbolo() {
		return "VC";
	}

	@Override
	public double orden() {
		return 6;
	}
}
