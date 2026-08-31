package evrp_0.algorithms.ensembles;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import evrp_0.algorithms.rules.Regla;
import evrp_0.domain.Instance;
import evrp_0.domain.Point;
import evrp_0.domain.Solution;
import evrp_0.util.AlgoUtil;

public class Ensemble extends Regla {

	public Regla[] rules;
	public Regla activeRule;

	// modeEnsembles=1
	@Override
	public Solution solve(Instance instance) {
		if (AlgoUtil.modeEnsembles == 1) {
			Solution best = null;
			Solution solution;
			double criteria = 0;
			for (Regla rule : rules) {
				if (rule != null) {
					activeRule = rule; // set the rule to calculate the solution
					solution = super.solve(instance);
					switch (AlgoUtil.criterio) {
					case 0:
						criteria = solution.getTotalVehicles();
						if (best == null || criteria < best.getTotalVehicles()) {
							best = solution;
						}
						break;
					case 1:
						criteria = solution.getTotalEnergy();
						if (best == null || criteria < best.getTotalEnergy()) {
							best = solution;
						}
						break;
					case 2:
						criteria = solution.getTotalTardiness();
						if (best == null || criteria < best.getTotalTardiness()) {
							best = solution;
						}
					default:
						break;
					}
				}
			}
			return best;
		} else {
			return super.solve(instance);
		}
	}

	@Override
	public Point getNext() {
		if (AlgoUtil.modeEnsembles == 0) { // with this mode ensembles uses a combination method
			if (AlgoUtil.combinationMethod == 0)
				return sum();
			else
				return vote();
		} else {
			activeRule.domain = domain;
			return activeRule.getNext(); // otherwise this uses a rule on it
		}
	}

	// modeEnsembles=0
	Point sum() {
		double mostPriorityCustomer = -Double.MAX_VALUE;
		double priority;
		double priorityRule;
		Point cityChoosed = null;
		for (Point customer : domain.noVisitedCustomers) {
			priority = 0;
			for (int rule = 0; rule < rules.length; rule++) {
				domain.selected = customer;
				rules[rule].domain = domain;
				priorityRule = rules[rule].computePriority();
				if (!Double.isFinite(priorityRule))
					priorityRule = 0;
				priority += priorityRule;
			}
			if (priority > mostPriorityCustomer) {
				mostPriorityCustomer = priority;
				cityChoosed = customer;
			}
		}
		return cityChoosed;
	}

	Point vote() {
		double[][] matrixVotes = new double[rules.length][domain.instance.customers.size()];
		double mostPriorityCustomer;
		double priorityRule;
		Point customerChoosed = null;
		// compute a mtrix with the priority of each unvisited city with each rule
		for (int rule = 0; rule < rules.length; rule++) {
			mostPriorityCustomer = -Double.MAX_VALUE;
			customerChoosed = null;
			for (Point customer : domain.noVisitedCustomers) {
				matrixVotes[rule][customer.idCustomer] = 0;
				domain.selected = customer;
				rules[rule].domain = domain;
				priorityRule = rules[rule].computePriority();
				if (!Double.isFinite(priorityRule))
					priorityRule = 0;
				if (priorityRule > mostPriorityCustomer) {
					mostPriorityCustomer = priorityRule;
					customerChoosed = customer;
				}
			}
			matrixVotes[rule][customerChoosed.idCustomer] = 1; // the most priority has 1, others have 0
		}
		// like sum combination vote
		mostPriorityCustomer = -Double.MAX_VALUE;
		double priority;
		for (Point customer : domain.noVisitedCustomers) {
			domain.selected = customer;
			priority = 0;
			for (int regla = 0; regla < rules.length; regla++)
				priority += matrixVotes[regla][customer.idCustomer];
			if (priority > mostPriorityCustomer) {
				mostPriorityCustomer = priority;
				customerChoosed = customer;
			}
		}
		return customerChoosed;
	}

	// rest of methods

	@Override
	public boolean esEnsemble() {
		return true;
	}

	public Ensemble(int cardinalidadEnsembles) {
		this.rules = new Regla[cardinalidadEnsembles];
	}

	public Ensemble() {
		this.rules = new Regla[AlgoUtil.sizeEnsembles];
	}

	public Ensemble(Ensemble ensemble) {
		this.rules = new Regla[ensemble.rules.length];
		for (int i = 0; i < ensemble.rules.length; i++)
			if (ensemble.rules[i] != null)
				rules[i] = ensemble.rules[i].getClon();
	}

	public Ensemble(Regla[] copia) {
		int sizeReal = 0;
		for (Regla regla : copia)
			if (regla == null)
				break;
			else
				sizeReal++;
		rules = new Regla[sizeReal];
		for (int i = 0; i < sizeReal; i++)
			if (copia[i] == null)
				break;
			else
				rules[i] = copia[i];
	}

	public int getSize() {
		int sizeReal = 0;
		for (int i = 0; i < rules.length; i++)
			if (rules[i] != null)
				sizeReal = sizeReal + rules[i].getSize();
		return sizeReal;
	}

	public int getDepth() {
		int sizeReal = 0;
		for (int i = 0; i < rules.length; i++)
			if (rules[i] != null)
				sizeReal = sizeReal + rules[i].getDepth();
		return sizeReal;
	}

	public int getNReglas() {
		int sizeReal = 0;
		for (int i = 0; i < rules.length; i++)
			if (rules[i] != null)
				sizeReal++;
		return sizeReal;
	}

	public void mezclar() {
		Random rnd = ThreadLocalRandom.current();
		for (int i = rules.length - 1; i > 0; i--) {
			int index = rnd.nextInt(i + 1);
			Regla a = rules[index];
			rules[index] = rules[i];
			rules[i] = a;
		}
	}

	public Regla get(int index) {
		return rules[index];
	}

	public void cambiaRegla(int i, Regla regla) {
		borrar(i);
		rules[i] = regla;
	}

	public void borrarAleatoriamente() {
		borrar((int) (rules.length * Math.random()));
	}

	public void set(int i, Regla hijo1) {
		if (i >= rules.length) {
			Regla[] copia = new Regla[rules.length + 1];
			for (int j = 0; j < copia.length - 1; j++)
				copia[j] = rules[j];
			rules = copia;
		}
		rules[i] = hijo1;
	}

	public void borrar(int i) {
		set(i, null);
	}

	public String representa() {
		String ensemble = "[";
		for (Regla regla : rules)
			if (regla != null)
				ensemble += "{" + regla.representa() + "}";
			else
				break;
		return ensemble + "]";
	}

	public String representaMonticulo() {
		String ensemble = "[";
		for (Regla regla : rules)
			if (regla != null)
				ensemble += "{" + regla.representaMonticulo() + "}";
			else
				break;
		return ensemble + "]";
	}

	public boolean isEnsemble() {
		return true;
	}

	public Regla getRandom() {
		return rules[(int) (rules.length * Math.random())];
	}

	public Ensemble random(List<Regla> seleccionables) {
		for (int i = 0; i < rules.length; i++)
			rules[i] = AlgoUtil.getReglaRandom();
		return this;
	}

	public Ensemble random() {
		return random(AlgoUtil.rulesForBuildingEnsembles);
	}

	public Ensemble getClon() {
		return new Ensemble(this);
	}

	public Ensemble loadEnsemble(String stringMonticulo) {
		stringMonticulo = stringMonticulo.replace("[", "").replace("]", " ");
		stringMonticulo = stringMonticulo.replace("{", "");
		String[] rulesString = stringMonticulo.split("}");
		rules = new Regla[rulesString.length - 1];
		for (int i = 0; i < rulesString.length - 1; i++) {
			rules[i] = new Regla().loadRule(rulesString[i]);
		}
		return this;
	}

}
