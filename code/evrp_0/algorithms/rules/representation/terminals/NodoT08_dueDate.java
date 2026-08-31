package evrp_0.algorithms.rules.representation.terminals;

import evrp_0.algorithms.rules.representation.Nodo;
import evrp_0.algorithms.rules.representation.NodoTerminal;
import evrp_0.domain.DomainInfo;

public class NodoT08_dueDate extends NodoTerminal {

	private static final NodoT08_dueDate INSTANCE = new NodoT08_dueDate();

	public static NodoT08_dueDate getInstance() { // implements singleton
		return INSTANCE;
	}

	@Override
	public Nodo clona() {
		return INSTANCE;
	}

	@Override
	public double value(DomainInfo domain) {
		return domain.selected.dueDate;
	}

	@Override
	public String simbolo() {
		return "DD";
	}

	@Override
	public double orden() {
		return 8;
	}
}
