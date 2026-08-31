package evrp_0.algorithms.rules.representation.terminals;

import evrp_0.algorithms.rules.representation.Nodo;
import evrp_0.algorithms.rules.representation.NodoTerminal;
import evrp_0.domain.DomainInfo;

public class NodoT05_avgDemand extends NodoTerminal {

	private static final NodoT05_avgDemand INSTANCE = new NodoT05_avgDemand();

	public static NodoT05_avgDemand getInstance() { // implements singleton
		return INSTANCE;
	}

	@Override
	public Nodo clona() {
		return INSTANCE;
	}

	@Override
	public double value(DomainInfo domain) {
		return (domain.demandSum - domain.selected.demand) / (domain.noVisitedCustomers.size() - 1);
	}

	@Override
	public String simbolo() {
		return "Davg";
	}

	@Override
	public double orden() {
		return 5;
	}
}
