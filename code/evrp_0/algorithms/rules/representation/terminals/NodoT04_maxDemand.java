package evrp_0.algorithms.rules.representation.terminals;

import evrp_0.algorithms.rules.representation.Nodo;
import evrp_0.algorithms.rules.representation.NodoTerminal;
import evrp_0.domain.DomainInfo;

public class NodoT04_maxDemand extends NodoTerminal {

	private static final NodoT04_maxDemand INSTANCE = new NodoT04_maxDemand();

	public static NodoT04_maxDemand getInstance() { // implements singleton
		return INSTANCE;
	}

	@Override
	public Nodo clona() {
		return INSTANCE;
	}

	@Override
	public double value(DomainInfo domain) {
		return domain.minDemand.get(domain.minDemand.size() - 1); // it is not ok because it would return the maximum but could be already visited!
	}

	@Override
	public String simbolo() {
		return "Dmax";
	}

	@Override
	public double orden() {
		return 4;
	}
}
