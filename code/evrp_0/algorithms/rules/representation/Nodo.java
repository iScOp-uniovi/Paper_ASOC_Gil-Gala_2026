package evrp_0.algorithms.rules.representation;

import evrp_0.domain.DomainInfo;

public interface Nodo {

	boolean esTerminal();

	boolean esConstante();

	boolean noAplicaRestriccionesDimension();

	double value();
	
	double value(DomainInfo domain);

	double value(double izq, double der);

	Nodo clona();

	boolean esOperacion();

	boolean esUnaria();

	boolean esBinariaInversa();

	String simbolo();

	boolean esNull();

	boolean simetrico();

	double orden();

	String simboloPrintable();

	double getDimension();

	void setDimension(double dimension);


}
