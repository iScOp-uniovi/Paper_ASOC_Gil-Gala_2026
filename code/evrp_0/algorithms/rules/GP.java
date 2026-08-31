package evrp_0.algorithms.rules;

import java.util.List;

import evrp_0.util.AlgoUtil;
import evrp_0.util.Algoritmo;

public abstract class GP implements Algoritmo{

	// VARIABLES INTERNAS
	public Population pop;

	// PARAMETROS
	public int sizePop = 200;
	public int nCruces = 1; // si es 1 entonces no es SM-GP
	public double ratioCruce = 1.0;
	public double ratioMutacion = 0.02;

	// METRICAS
	public int generations;
	public int tiempoEjecucion;

	public void run() {
		generations++;
		evolucionar();
	}

	public void generarPoblacionInicial() {
		pop = new Population(sizePop);
		generaRandom();
		pop.getBest();
	}
	
	public void generaHeuristica(List<Regla> reglas) {
		pop = new Population(sizePop);
		for (int i = 0; i < sizePop && i < reglas.size(); i++) {
			pop.add(reglas.get(i));
		}
		while (pop.size() < sizePop) {
			pop.add(new Regla().random());
		}
	}

	public void generaRandom() {
		for (int i = 0; i < sizePop; i++) {
			pop.add(new Regla().random());
		}
	}
	
	public void evolucionar() { // always crossover 100%
		pop.shuffle();
		for (int i = 0; i < sizePop && !AlgoUtil.timeOut(); i = i + 2) {

			Regla padre1 = pop.get(i);
			Regla padre2 = pop.get(i + 1);

			// Primer hijo
			Regla hijo1 = cruzar(padre1, padre2);

			if (Math.random() <= ratioMutacion) // mutacion
				mutar(hijo1);

			// Segundo hijo
			Regla hijo2 = cruzar(padre2, padre1);

			if (Math.random() <= ratioMutacion) // mutacion
				mutar(hijo2);

			// torneo padres e hijos, pasa siempre un hijo
			Population tournament = new Population(3);
			tournament.add(padre1);
			tournament.add(padre2);

			// hijo1 mejor hijo2
			if (AlgoUtil.mejoraTraining(hijo1, hijo2)) {
				pop.set(i, hijo1); // pasa hijo1
				tournament.add(hijo2); // y el mejor entre hijo2,
				pop.set(i + 1, tournament.getBest());
			}
			// hijo2 mejor hijo1
			else {
				pop.set(i, hijo2); // pasa hijo2
				tournament.add(hijo1); // y el mejor entre hijo1, padre1 y padre2
				pop.set(i + 1, tournament.getBest());
			}

			// MORE PRESURE AS OPTIONAL EVOLUTIONARY SCHEME
//			Population tournament = new Population(4);
//			tournament.add(padre1);
//			tournament.add(padre2);
//			tournament.add(hijo1);
//			tournament.add(hijo2);
//			tournament.sort();
//			reglas.set(i, tournament.get(0));
//			reglas.set(i+1, tournament.get(1));	
		}
	}
	
	protected Regla cruzar(Regla padre1, Regla padre2) {
		Regla bestChild = cruzar1(padre1, padre2);
		Regla child;
		for (int i = 1; i < nCruces; i++) {
			child = cruzar1(padre1, padre2);
			if (AlgoUtil.mejoraSM(child, bestChild) && padre1.filter() != child.filter() && padre2.filter() != child.filter())
				bestChild = child;
		}
		return bestChild;
	}
	
	protected Regla cruzar1(Regla padre1, Regla padre2) {
		int i, j, k = 0, n = 1000;
		do {
			i = padre1.posAzar();
			j = padre2.posAzar(); // NO DIMENSIONALY AWARE RULES
			k++;
		} while (j == -1 && k < n);
		if (j == -1) {
			return new Regla().random();
		}
		Regla hijo1 = new Regla(i, j, padre1, padre2);
		Regla hijo2 = new Regla(j, i, padre2, padre1);

		if (!hijo1.esValido())
			hijo1 = null;

		if (!hijo2.esValido())
			hijo2 = null;

		if (hijo1 == null && hijo2 == null) {
			return new Regla().random();
		} else if (hijo1 != null && hijo2 == null)
			return hijo1;
		else if (hijo1 == null && hijo2 != null) {
			return hijo2;
		} else {
			return hijo1;
		}
	}

	protected Regla mutar(Regla cromosoma) {
		Regla mutacion = new Regla().random();
		int i, j, k = 0, n = 1000;
		do {
			i = cromosoma.posAzar();
			j = mutacion.posAzar();
			k++;
		} while (j != -1 && k < n);
		if (j == -1) {
			return mutacion;
		}
		Regla hijo = new Regla(i, j, cromosoma, mutacion);
		if (hijo.esValido())
			return hijo;
		else {
			return mutacion;
		}
	}

	public abstract String getLinea();

	public abstract void cabecera();

}