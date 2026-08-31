package evrp_0.algorithms.rules;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import evrp_0.util.AlgoUtil;

public class Population {

	List<Regla> poblacion;
	int sizeMax;

	public Population(int size) {
		poblacion = new ArrayList<Regla>(size);
		sizeMax = size;
	}
	
	public Regla getBest() {
		Regla mejor = get(0);
		for (Regla cromosoma : poblacion) {
			if (AlgoUtil.timeOut()) break;
			if (AlgoUtil.mejoraTraining(cromosoma, mejor))
				mejor = cromosoma;
		}
		return mejor;
	}

	public double getAvgTrain() {
		double coste = 0;
		for (int i = 0; i < poblacion.size(); i++)
			coste += AlgoUtil.getFullEvaluate(poblacion.get(i)).train();
		return 1.0 * coste / poblacion.size();
	}

	public double getAvgVehicles() {
		double coste = 0;
		for (int i = 0; i < poblacion.size(); i++)
			coste += poblacion.get(i).vehicles;
		return 1.0 * coste / poblacion.size();
	}

	public double getAvgEnergy() {
		double coste = 0;
		for (int i = 0; i < poblacion.size(); i++)
			coste += poblacion.get(i).energy;
		return 1.0 * coste / poblacion.size();
	}
	
	public double getAvgTardiness() {
		double coste = 0;
		for (int i = 0; i < poblacion.size(); i++)
			coste += poblacion.get(i).tardiness;
		return 1.0 * coste / poblacion.size();
	}
	
	public double getAvgSize() {
		double coste = 0;
		for (int i = 0; i < poblacion.size(); i++)
			coste += poblacion.get(i).getSize();
		return 1.0 * coste / poblacion.size();
	}

	public double getAvgDepth() {
		double coste = 0;
		for (int i = 0; i < poblacion.size(); i++)
			coste += poblacion.get(i).getDepth();
		return 1.0 * coste / poblacion.size();
	}

	public Regla get(int index) {
		return poblacion.get(index);
	}

	public void add(Regla cromosoma) {
		poblacion.add(cromosoma);
	}

	public void set(int i, Regla cromosoma) {
		poblacion.set(i, cromosoma);
	}

	public int size() {
		return poblacion.size();
	}

	public void remove(Regla cromosoma) {
		poblacion.remove(cromosoma);
	}

	public void shuffle() {
		Collections.shuffle(poblacion);
	}
	
	public void sort() {
		Collections.sort(poblacion);
	}

	public Regla getRandom() {
		return poblacion.get((int) (poblacion.size() * Math.random()));
	}

	public void reiniciaEvaluacion() {
		for (Regla cromosoma : poblacion) {
			cromosoma.evalTrain = false;
			cromosoma.evalFilter = false;
		}
	}

	public void reiniciaEvaluacionFilter() {
		for (Regla cromosoma : poblacion) {
			cromosoma.evalFilter = false;
		}		
	}

	public void reiniciaEvaluacionTrain() {
		for (Regla cromosoma : poblacion) {
			cromosoma.evalTrain = false;
		}		
	}
	
	public List<Regla> getPop(){
		return poblacion;
	}
	
	public void mantenerMejoresReglas(Regla nuevaRegla) {
		poblacion.sort(Comparator.comparingDouble(Regla::train));
		if (poblacion.contains(nuevaRegla))
			return;
		Regla peorRegla = poblacion.get(poblacion.size() - 1);
		if (AlgoUtil.mejoraTraining(nuevaRegla, peorRegla)) {
			poblacion.set(poblacion.size() - 1, nuevaRegla);
			poblacion.sort(Comparator.comparingDouble(Regla::train));
		}
	}
	
}
