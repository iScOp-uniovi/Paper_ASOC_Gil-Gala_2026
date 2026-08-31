package evrp_0.algorithms.ensembles;

import evrp_0.algorithms.rules.GP;
import evrp_0.algorithms.rules.Population;
import evrp_0.util.AlgoUtil;

public abstract class GA extends GP {

	public GA() {
		pop = new Population(sizePop);
	}
	
	public GA(int sizePop) {
		this.sizePop = sizePop;
		pop = new Population(sizePop);
	}
	
	@Override
	public void generaRandom() {
		for (int i = 0; i < sizePop; i++) {
			pop.add(new Ensemble().random());
		}
	}
	
	@Override
	public void evolucionar() {
		pop.shuffle();
		for (int i = 0; i < sizePop && !AlgoUtil.timeOut(); i = i + 2) {
			Ensemble padre1 = (Ensemble) pop.get(i);
			Ensemble padre2 = (Ensemble) pop.get(i + 1);
			if (Math.random() <= ratioCruce) {

				// cruce
				Ensemble hijo1 = cruzar(padre1, padre2);
				Ensemble hijo2 = cruzar(padre2, padre1);

				// mutacion
				if (Math.random() <= ratioMutacion)
					mutar(hijo1);
				if (Math.random() <= ratioMutacion)
					mutar(hijo2);

				// selección
				if (AlgoUtil.mejoraTraining(hijo1, hijo2)) {
					Population torneo = new Population(3);
					torneo.add(padre1);
					torneo.add(padre2);
					torneo.add(hijo2);
					pop.set(i, hijo1);
					pop.set(i + 1, (Ensemble) torneo.getBest());
				} else {
					Population torneo = new Population(3);
					torneo.add(padre1);
					torneo.add(padre2);
					torneo.add(hijo1);
					pop.set(i, hijo2);
					pop.set(i + 1, (Ensemble) torneo.getBest());
				}
			} else {
				Ensemble hijo1 = padre1.getClon(), hijo2 = padre2.getClon();
				Ensemble mutacion1 = null, mutacion2 = null;
				// mutacion
				if (Math.random() <= ratioMutacion) {
					mutacion1 = padre1.getClon();
					mutar(mutacion1);
				}
				if (Math.random() <= ratioMutacion) {
					mutacion2 = padre2.getClon();
					mutar(mutacion2);
				}

				if (mutacion1 == null && mutacion2 == null) {
					pop.set(i, hijo1);
					pop.set(i + 1, hijo2);
				} else {
					Population torneo;
					if (mutacion1 != null && mutacion2 == null) {
						torneo = new Population(3);
						torneo.add(padre1);
						torneo.add(padre2);
						torneo.add(mutacion1);
					} else if (mutacion1 == null && mutacion2 != null) {
						torneo = new Population(3);
						torneo.add(padre1);
						torneo.add(padre2);
						torneo.add(mutacion2);
					} else {
						torneo = new Population(4);
						torneo.add(padre1);
						torneo.add(padre2);
						torneo.add(mutacion1);
						torneo.add(mutacion2);
					}
					Ensemble first = (Ensemble) torneo.getBest();
					pop.set(i, first);
					torneo.remove(first);
					Ensemble second = (Ensemble) torneo.getBest();
					pop.set(i + 1, second);
				}
			}
		}
	}

	public Ensemble getMejor() {
		return (Ensemble) pop.getBest();
	}

	protected void mutar(Ensemble ensemble) {
		ensemble.mezclar();
		int n = (int) (Math.random() * ensemble.rules.length / 2);
		for (int i = 0; i < n; i++)
			ensemble.set(i, AlgoUtil.getReglaRandom());
	}

	protected Ensemble cruzar(Ensemble padre1, Ensemble padre2) {
		padre1.mezclar(); 
		padre2.mezclar(); 
		int n = (int) (Math.random() * padre1.rules.length);
		Ensemble hijo = padre2.getClon();
		for (int i = 0; i < n; i++)
			hijo.set(i, padre1.rules[i]);
		return hijo;
	}
}