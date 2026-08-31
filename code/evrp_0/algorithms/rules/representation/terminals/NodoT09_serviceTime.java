package evrp_0.algorithms.rules.representation.terminals;

import evrp_0.algorithms.rules.Regla;
import evrp_0.algorithms.rules.representation.Nodo;
import evrp_0.algorithms.rules.representation.NodoTerminal;
import evrp_0.domain.DomainInfo;

public class NodoT09_serviceTime extends NodoTerminal {

	private static final NodoT09_serviceTime INSTANCE = new NodoT09_serviceTime();

	public static NodoT09_serviceTime getInstance() { // implements singleton
		return INSTANCE;
	}

	@Override
	public Nodo clona() {
		return INSTANCE;
	}

	@Override
	public double value(DomainInfo regla) {
		return regla.selected.serviceTime;
	}

	@Override
	public String simbolo() {
		return "ST";
	}

	@Override
	public double orden() {
		return 9;
	}
}
