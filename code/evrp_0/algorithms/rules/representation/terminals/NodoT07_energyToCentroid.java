package evrp_0.algorithms.rules.representation.terminals;

import evrp_0.algorithms.rules.representation.Nodo;
import evrp_0.algorithms.rules.representation.NodoTerminal;
import evrp_0.domain.DomainInfo;
import evrp_0.domain.Point;

public class NodoT07_energyToCentroid extends NodoTerminal {

	private static final NodoT07_energyToCentroid INSTANCE = new NodoT07_energyToCentroid();

	public static NodoT07_energyToCentroid getInstance() { // implements singleton
		return INSTANCE;
	}

	@Override
	public Nodo clona() {
		return INSTANCE;
	}

	@Override
	public double value(DomainInfo domain) {
		Point centroid = new Point(domain.xSum - domain.selected.x, domain.ySum - domain.selected.y);
		return domain.instance.computeEnergy(domain.selected, centroid); // now it is equal to distance because the instances
	}

	@Override
	public String simbolo() {
		return "Ec";
	}

	@Override
	public double orden() {
		return 7;
	}
}
