package evrp_0.algorithms.rules.representation.terminals;

import evrp_0.algorithms.rules.representation.Nodo;
import evrp_0.algorithms.rules.representation.NodoTerminal;
import evrp_0.domain.DomainInfo;

public class NodoT02_Demand extends NodoTerminal {

	private static final NodoT02_Demand INSTANCE = new NodoT02_Demand();

	public static NodoT02_Demand getInstance() { // implements singleton 
		return INSTANCE;
	}

	@Override
	public Nodo clona() {
		return INSTANCE;
	}

	@Override
	public double value(DomainInfo domain) {
		return domain.selected.demand;
	}

	@Override
	public String simbolo() {
		return "D";
	}

	@Override
	public double orden() {
		return 2;
	}
}
