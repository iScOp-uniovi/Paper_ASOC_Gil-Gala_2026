package evrp_0.algorithms.rules.representation.terminals;

import evrp_0.algorithms.rules.representation.Nodo;
import evrp_0.algorithms.rules.representation.NodoTerminal;
import evrp_0.domain.DomainInfo;

public class NodoT03_minDemand extends NodoTerminal {

	private static final NodoT03_minDemand INSTANCE = new NodoT03_minDemand();

	public static NodoT03_minDemand getInstance() { // implements singleton 
		return INSTANCE;
	}

	@Override
	public Nodo clona() {
		return INSTANCE;
	}

	@Override
	public double value(DomainInfo domain) {
		return domain.minDemand.get(0); // it is not ok
	}

	@Override
	public String simbolo() {
		return "Dmin";
	}

	@Override
	public double orden() {
		return 3;
	}
}
