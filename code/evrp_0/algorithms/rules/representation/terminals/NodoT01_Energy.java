package evrp_0.algorithms.rules.representation.terminals;

import evrp_0.algorithms.rules.representation.Nodo;
import evrp_0.algorithms.rules.representation.NodoTerminal;
import evrp_0.domain.DomainInfo;

public class NodoT01_Energy extends NodoTerminal {

	private static final NodoT01_Energy INSTANCE = new NodoT01_Energy();

	public static NodoT01_Energy getInstance() { // implements singleton 
		return INSTANCE;
	}

	@Override
	public Nodo clona() {
		return INSTANCE;
	}

	@Override
	public double value(DomainInfo domain) {
		return domain.active.computeEnergy(domain.previousVisited, domain.selected);
	}

	@Override
	public String simbolo() {
		return "E";
	}

	@Override
	public double orden() {
		return 1;
	}
}
