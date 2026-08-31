package evrp_0.algorithms.rules.representation.terminals;

import evrp_0.algorithms.rules.Regla;
import evrp_0.algorithms.rules.representation.Nodo;
import evrp_0.algorithms.rules.representation.NodoTerminal;
import evrp_0.domain.DomainInfo;

public class NodoT15_timeVehicle extends NodoTerminal {

	private static final NodoT15_timeVehicle INSTANCE = new NodoT15_timeVehicle();

	public static NodoT15_timeVehicle getInstance() { // implements singleton
		return INSTANCE;
	}

	@Override
	public Nodo clona() {
		return INSTANCE;
	}

	@Override
	public double value(DomainInfo regla) {
		return regla.active.time;
	}

	@Override
	public String simbolo() {
		return "VT";
	}

	@Override
	public double orden() {
		return 15;
	}
}
