package evrp_0.algorithms.rules.representation.terminals;

import evrp_0.algorithms.rules.Regla;
import evrp_0.algorithms.rules.representation.Nodo;
import evrp_0.algorithms.rules.representation.NodoTerminal;
import evrp_0.domain.DomainInfo;

public class NodoT10_readyTime extends NodoTerminal {

	private static final NodoT10_readyTime INSTANCE = new NodoT10_readyTime();

	public static NodoT10_readyTime getInstance() { // implements singleton
		return INSTANCE;
	}

	@Override
	public Nodo clona() {
		return INSTANCE;
	}

	@Override
	public double value(DomainInfo regla) {
		return regla.selected.readyTime;
	}

	@Override
	public String simbolo() {
		return "RT";
	}

	@Override
	public double orden() {
		return 10;
	}
}
