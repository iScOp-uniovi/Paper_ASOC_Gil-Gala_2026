package evrp_0.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import evrp_0.domain.Instance;
import evrp_0.domain.Solution;

public class CacheRuleSolutions {
	
	public Map<Instance, Solution> mapa = new HashMap<Instance, Solution>();
	
	public void setSolucion(Instance instance, Solution solution) {
		mapa.put(instance, solution);
	}
	
	public Solution getSolucion(Instance instance) {
		return mapa.get(instance);
	}	
	
	public Set<Entry<Instance, Solution>> getSolutions() {
		return mapa.entrySet();
	}
	
}