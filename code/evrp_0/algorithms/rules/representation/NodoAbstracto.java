package evrp_0.algorithms.rules.representation;

public abstract class NodoAbstracto implements Nodo {

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		return orden() == ((Nodo) obj).orden();
	}

	@Override
	public String toString() {
		return simbolo();
	}
	
	@Override
	public String simboloPrintable() {
		return simbolo();
	}
	
	
}