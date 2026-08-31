package evrp_0._experiments;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;

import evrp_0.algorithms.ensembles.Ensemble;
import evrp_0.algorithms.ensembles.GA;
import evrp_0.algorithms.rules.Population;
import evrp_0.algorithms.rules.Regla;
import evrp_0.domain.Instance;
import evrp_0.util.AlgoUtil;

/**
 * Memetic Genetic Algorithm (MGA) for evolving ensembles of priority rules.
 *
 * <p>
 * The algorithm extends the genetic algorithm implemented by {@link GA} and
 * augments it with a local-search procedure that mutates individual rules of an
 * ensemble. The local search can evaluate candidate mutations either on the
 * complete training set or on a randomly sampled subset of training instances.
 * </p>
 *
 * <p>
 * Two execution parameters are intentionally fixed in this implementation:
 * {@code AlgoUtil.mode = 0} and {@code AlgoUtil.criterio = 1}. They are not
 * exposed as command-line arguments.
 * </p>
 *
 * <p>
 * The ensemble combination method is configurable from the command line:
 * </p>
 * <ul>
 * <li>{@code 0}: sum</li>
 * <li>{@code 1}: vote</li>
 * </ul>
 *
 * @author Francisco Javier Gil-Gala
 */
public class MGA extends GA {

	/** Combination method based on the sum of the rule outputs. */
	private static final int COMBINATION_SUM = 0;

	/** Combination method based on voting among the rules. */
	private static final int COMBINATION_VOTE = 1;

	/**
	 * Number of command-line arguments expected when a custom configuration is
	 * supplied.
	 */
	private static final int NUMBER_OF_ARGUMENTS = 9;

	/** Maximum execution time, in minutes. */
	private static final int EXECUTION_MINUTES = 100;

	/** Number of measurement intervals used by the experimental framework. */
	private static final int MEASUREMENT_INTERVALS = 100;

	/** Base name of the CSV file containing the rules used to build ensembles. */
	private static final String RULES_FILE = "reglas_ensembles_lex1";

	/**
	 * Program entry point.
	 *
	 * <p>
	 * If no arguments are supplied, the default configuration is used. Otherwise,
	 * exactly nine arguments must be provided in the following order:
	 * </p>
	 *
	 * <ol>
	 * <li>ensemble size</li>
	 * <li>ensemble mode</li>
	 * <li>combination method: 0 = sum, 1 = vote</li>
	 * <li>population size</li>
	 * <li>crossover probability, as an integer percentage</li>
	 * <li>ensemble mutation probability, as an integer percentage</li>
	 * <li>rule-level local-search probability, as an integer percentage</li>
	 * <li>number of candidate rule mutations generated per local-search
	 * iteration</li>
	 * <li>number of training instances used by the filter; 0 disables
	 * filtering</li>
	 * </ol>
	 *
	 * <p>
	 * The execution mode and criterion are fixed to 0 and 1, respectively.
	 * </p>
	 *
	 * @param args command-line arguments described above
	 * @throws IllegalArgumentException if the number of arguments is invalid or an
	 *                                  unsupported combination method is requested
	 */
	public static void main(String[] args) {
		if (args.length == 0) {
			args = new String[] { "3", // sizeEnsembles
					"0", // modeEnsembles
					"0", // combinationMethod: 0 = sum, 1 = vote
					"100", // sizePop
					"80", // crossoverProbability
					"20", // mutationProbability
					"50", // mutationRulesProbability; 0 -> GA without rule-level local search
					"5", // nMutations
					"0" // nInstanceFilter; 0 -> no filtering
			};
		} else if (args.length != NUMBER_OF_ARGUMENTS) {
			throw new IllegalArgumentException("Expected " + NUMBER_OF_ARGUMENTS + " arguments, but received "
					+ args.length + ". " + "Usage: MGA <sizeEnsembles> <modeEnsembles> <combinationMethod> <popSize> "
					+ "<crossoverProbability> <mutationProbability> <mutationRulesProbability> "
					+ "<nMutations> <nInstanceFilter>");
		}

		// Ensemble configuration.
		AlgoUtil.sizeEnsembles = Integer.parseInt(args[0]);
		AlgoUtil.modeEnsembles = Integer.parseInt(args[1]);
		AlgoUtil.combinationMethod = Integer.parseInt(args[2]);

		if (AlgoUtil.combinationMethod != COMBINATION_SUM && AlgoUtil.combinationMethod != COMBINATION_VOTE) {
			throw new IllegalArgumentException("Invalid combinationMethod: " + AlgoUtil.combinationMethod
					+ ". Supported values are 0 (sum) and 1 (vote).");
		}

		AlgoUtil.minutos = EXECUTION_MINUTES;
		AlgoUtil.intervalosMedicion = MEASUREMENT_INTERVALS;

		int popSize = Integer.parseInt(args[3]);
		int crossoverProbability = Integer.parseInt(args[4]);
		int mutationProbability = Integer.parseInt(args[5]);
		int mutationRulesProbability = Integer.parseInt(args[6]);
		int nMutations = Integer.parseInt(args[7]);
		int nInstanceFilter = Integer.parseInt(args[8]);

		String msg2 = "GA_" + AlgoUtil.getInfoMode() + "_" + AlgoUtil.getCriterio() + "_" + AlgoUtil.sizeEnsembles + "_"
				+ AlgoUtil.modeEnsembles + "_" + AlgoUtil.combinationMethod + "_" + popSize + "_" + crossoverProbability
				+ "_" + mutationProbability + "_" + nMutations + "_" + mutationRulesProbability + "_" + nInstanceFilter
				+ "_" + RULES_FILE;

		MGA ga = new MGA();
		AlgoUtil.run(msg2, ga);

		loadRules(RULES_FILE, 0);
//		loadRules(RULES_FILE, 4);

		// Genetic and memetic algorithm parameters.
		ga.sizePop = popSize;
		ga.ratioCruce = crossoverProbability / 100.0;
		ga.ratioMutacion = mutationProbability / 100.0;
		ga.ratioMutacionReglas = mutationRulesProbability / 100.0;
		ga.nMutations = nMutations;
		ga.nInstanceFilter = nInstanceFilter;

		ga.cabecera();
		ga.run();

		AlgoUtil.end();
	}

	/** Probability of applying rule-level local search to an offspring. */
	double ratioMutacionReglas;

	/**
	 * Number of training instances used by the local-search filter; 0 disables the
	 * filter.
	 */
	int nInstanceFilter;

	/**
	 * Number of candidate rule mutations evaluated in each local-search iteration.
	 */
	int nMutations;

	/**
	 * Executes the evolutionary search until the global time limit is reached.
	 *
	 * <p>
	 * The initial population is generated using the implementation inherited from
	 * {@link GA}. Afterwards, generations are repeatedly evolved until
	 * {@link AlgoUtil#timeOut()} reports that the execution time has expired.
	 * </p>
	 */
	public void run() {
		generarPoblacionInicial();
		while (!AlgoUtil.timeOut()) {
			generations++;
			evolucionar();
		}
	}

	/**
	 * Evolves one generation of the population.
	 *
	 * <p>
	 * The population is shuffled and processed in pairs. Depending on the crossover
	 * probability, offspring are produced either by crossover or by cloning.
	 * Ensemble-level mutation and rule-level local search are then applied
	 * probabilistically. Finally, parents and offspring compete for the two
	 * population positions associated with each pair.
	 * </p>
	 */
	@Override
	public void evolucionar() {
		pop.shuffle();
		for (int i = 0; i < sizePop && !AlgoUtil.timeOut(); i = i + 2) {
			Ensemble padre1 = (Ensemble) pop.get(i);
			Ensemble padre2 = (Ensemble) pop.get(i + 1);
			if (Math.random() <= ratioCruce) {
				// Crossover.
				Ensemble hijo1 = cruzar(padre1, padre2);
				Ensemble hijo2 = cruzar(padre2, padre1);

				// Ensemble mutation.
				if (Math.random() <= ratioMutacion)
					mutar(hijo1);
				if (Math.random() <= ratioMutacion)
					mutar(hijo2);

				// Rule-level local search (LSA).
				if (Math.random() <= ratioMutacionReglas) {
					hijo1 = mutarReglas(hijo1);
				}
				if (Math.random() <= ratioMutacionReglas) {
					hijo2 = mutarReglas(hijo2);
				}

				// Selection.
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

				// Ensemble mutation.
				if (Math.random() <= ratioMutacion) {
					mutacion1 = padre1.getClon();
					mutar(mutacion1);
				}
				if (Math.random() <= ratioMutacion) {
					mutacion2 = padre2.getClon();
					mutar(mutacion2);
				}

				// Rule-level local search (LSA).
				if (Math.random() <= ratioMutacionReglas) {
					hijo1 = mutarReglas(hijo1);
				}
				if (Math.random() <= ratioMutacionReglas) {
					hijo2 = mutarReglas(hijo2);
				}

				// Selection.
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

	/**
	 * Applies iterative rule-level local search to an ensemble.
	 *
	 * <p>
	 * At each iteration, {@link #nMutations} neighboring ensembles are generated by
	 * replacing one rule. The best candidate is retained when it improves the
	 * current solution on the training set. If {@link #nInstanceFilter} is greater
	 * than zero, candidates are first compared using a random subset of training
	 * instances before the accepted candidate is checked on the full training set.
	 * </p>
	 *
	 * @param ensemble ensemble from which local search starts
	 * @return the best ensemble found by the local search
	 */
	Ensemble mutarReglas(Ensemble ensemble) {
		vecesAplicaLSA++;
		Ensemble best = ensemble;
		Ensemble bestLocal;
		Ensemble mutacion;
		boolean mejora;
		if (nInstanceFilter > 0) { // Filtered/surrogate evaluation.
			AlgoUtil.set_filter = getInstancias(nInstanceFilter);
			ensemble.evalFilter = false;
			do {
				iteracionesLSA++;
				mejora = false;
				bestLocal = best;
				for (int i = 0; i < nMutations; i++) {
					mutacion = mutar3(best);
					if (mutacion.filter() < bestLocal.filter()) {
						bestLocal = mutacion;
					}
				}
				if (bestLocal.train() < best.train()) {
					mejora = true;
					best = bestLocal;
				}
			} while (mejora);
		} else { // Full training-set evaluation.
			do {
				iteracionesLSA++;
				mejora = false;
				bestLocal = best;
				for (int i = 0; i < nMutations; i++) {
					mutacion = mutar3(best);
					if (mutacion.train() < bestLocal.train()) {
						bestLocal = mutacion;
					}
				}
				if (bestLocal.train() < best.train()) {
					mejora = true;
					best = bestLocal;
				}
			} while (mejora);
		}
		if (best.train() < ensemble.train()) {
			vecesMejoraLSA++;
			porcentajeMejoraFitness += (Math.abs(best.train() - ensemble.train()) / ensemble.train());
			int dif = 0;
			for (int i = 0; i < AlgoUtil.sizeEnsembles; i++) {
				if (!best.get(i).equals(ensemble.get(i)))
					dif++;
			}
			porcentajeCambio += (1.0 * dif / AlgoUtil.sizeEnsembles);
		}
		return best;
	}

	/**
	 * Creates one neighboring ensemble by cloning the input ensemble and replacing
	 * one randomly selected rule with a mutated rule.
	 *
	 * @param ensemble ensemble to mutate
	 * @return a mutated clone of the input ensemble
	 */
	protected Ensemble mutar3(Ensemble ensemble) {
		ensemble = ensemble.getClon();
		int n = (int) (Math.random() * ensemble.rules.length);
		ensemble.set(n, mutar2(ensemble.get(n)));
		return ensemble;
	}

	/**
	 * Mutates a rule by combining it with a randomly generated rule whenever a
	 * valid combination can be produced. If that is not possible, the random rule
	 * itself is returned.
	 *
	 * @param cromosoma rule to mutate
	 * @return mutated rule
	 */
	private Regla mutar2(Regla cromosoma) {
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

	/**
	 * Randomly samples distinct instances from the training set.
	 *
	 * @param maxInstances number of distinct instances to sample
	 * @return sampled training instances
	 */
	public static List<Instance> getInstancias(int maxInstances) {
		Set<Instance> instances = new HashSet<Instance>();
		int instance;
		while (instances.size() < maxInstances) {
			instance = new Random().nextInt(AlgoUtil.set_train.size());
			instances.add(AlgoUtil.set_train.get(instance));
		}
		return new ArrayList<Instance>(instances);
	}

	/**
	 * Writes the header used by the experimental log.
	 */
	public void cabecera() {
//		AlgoUtil.vln("Generations;BestTrain;BestTest;Heap");
		AlgoUtil.vln("Generations;VecesLSA;VecesMejoraLSA;IteracionesLSA;%MejoraFitness;%Cambio;%Mejora;N. Ite.;Heap");
	}

	/** Number of times rule-level local search has been invoked. */
	int vecesAplicaLSA = 0;

	/** Number of local-search invocations that improved the starting ensemble. */
	int vecesMejoraLSA = 0;

	/** Total number of local-search iterations performed. */
	int iteracionesLSA = 0;

	/**
	 * Accumulated relative fitness improvement over successful local-search calls.
	 */
	double porcentajeMejoraFitness = 0;

	/**
	 * Accumulated fraction of ensemble rules changed by successful local-search
	 * calls.
	 */
	double porcentajeCambio = 0;

	/**
	 * Builds one semicolon-separated monitoring line with local-search statistics
	 * and the representation of the globally best solution.
	 *
	 * @return monitoring line, or an empty string if no global best solution exists
	 */
	public String getLinea() {
		if (AlgoUtil.mejorGlobal == null)
			return "";
		try {
//			return AlgoUtil.mejorGlobal.train() + ";" + pop.getAvgTrain() + ";" + AlgoUtil.mejorGlobal.vehicles + ";" + pop.getAvgVehicles() + ";"
//					+ AlgoUtil.mejorGlobal.energy + ";" + pop.getAvgEnergy() + ";" + AlgoUtil.mejorGlobal.tardiness + ";" + pop.getAvgTardiness() + ";"
//					+ AlgoUtil.mejorGlobal.getSize() + ";" + pop.getAvgSize() + ";" + AlgoUtil.mejorGlobal.getDepth() + ";" + pop.getAvgDepth() + ";"
//					+ AlgoUtil.tiempoEjecucion() + ";" + generations + ";" + AlgoUtil.mejorGlobal.representa() + ";"
//					+ AlgoUtil.mejorGlobal.representaMonticulo() + ";";
//			return generations + ";" + AlgoUtil.mejorGlobal.train() + ";" + AlgoUtil.mejorGlobal.test() + ";" + AlgoUtil.mejorGlobal.representaMonticulo() + ";";
			if (vecesMejoraLSA > 0)
				return generations + ";" + vecesAplicaLSA + ";" + vecesMejoraLSA + ";" + iteracionesLSA + ";"
						+ porcentajeMejoraFitness / vecesMejoraLSA + ";" + porcentajeCambio / vecesMejoraLSA + ";"
						+ 1.0 * vecesMejoraLSA / vecesAplicaLSA + ";" + 1.0 * iteracionesLSA / vecesAplicaLSA + ";"
						+ AlgoUtil.mejorGlobal.representaMonticulo();
			else
				return generations + ";" + vecesAplicaLSA + ";" + vecesMejoraLSA + ";" + iteracionesLSA + ";" + 0 + ";"
						+ 0 + ";" + 0 + ";" + 1.0 * iteracionesLSA / vecesAplicaLSA + ";"
						+ AlgoUtil.mejorGlobal.representaMonticulo();
//			return "" + AlgoUtil.mejorGlobal.train() + " " + AlgoUtil.mejorGlobal.test() + " " + generations;
//			AlgoUtil.mejorGlobal.test();
//			return "" + AlgoUtil.mejorGlobal.vehicles + " " + AlgoUtil.mejorGlobal.energy + " " + generations;
		} catch (Exception e) {
			e.printStackTrace();
			return e.getMessage();
		}
	}

	/**
	 * Loads rules from a semicolon-separated CSV file.
	 *
	 * <p>
	 * The file name is built as {@code set_rules + ".csv"}. The rule stored in
	 * column {@code pos} of every row is parsed and added to
	 * {@link AlgoUtil#rulesForBuildingEnsembles}. The returned map groups
	 * equivalent textual rule representations.
	 * </p>
	 *
	 * @param set_rules base name of the CSV file, without extension
	 * @param pos       zero-based column containing the rule representation
	 * @return map from textual rule representation to all parsed rules with that
	 *         representation
	 */
	public static Map<String, List<Regla>> loadRules(String set_rules, int pos) {
		Scanner kbd;
		AlgoUtil.rulesForBuildingEnsembles = new ArrayList<Regla>();
		Map<String, List<Regla>> map = new HashMap<String, List<Regla>>();
		try {
			kbd = new Scanner(new File(set_rules + ".csv"));
			String s;
			String n;
			String[] line;
			Regla rule;
			while (kbd.hasNext()) {
				s = kbd.nextLine();
				line = s.split(";");
//				n = line[0];
//				rule = new Rule().loadRule(line[0]);
				n = line[pos];
				rule = new Regla().loadRule(line[pos]);

				AlgoUtil.rulesForBuildingEnsembles.add(rule);
				if (map.containsKey(n)) {
					map.get(n).add(rule);
				} else {
					List<Regla> rules = new ArrayList<Regla>();
					rules.add(rule);
					map.put(n, rules);
				}
			}
			kbd.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * Evaluates groups of rules on the training and test sets and prints aggregate
	 * information for each group.
	 *
	 * @param map groups of rules indexed by their textual representation
	 */
	protected static void evaluaReglas(Map<String, List<Regla>> map) {
		for (Entry<String, List<Regla>> entry : map.entrySet()) {
			System.out.println(entry.getKey() + " " + entry.getValue().size());
			List<Regla> reglas = entry.getValue();

			double energyMin = Double.MAX_VALUE;
			double avg = 0.0;
			Regla bestTrain = null;
			for (Regla r : reglas) {
				double t = r.train();
				t = r.energy;
				avg += t;
				if (t < energyMin) {
					energyMin = t;
					bestTrain = r;
				}
				r.evalTest = false;
			}
			System.out.println("Train:" + energyMin + ";" + avg / reglas.size());

			energyMin = Double.MAX_VALUE;
			avg = 0.0;
			for (Regla r : reglas) {
				double t = r.test();
				avg += t;
				if (t < energyMin) {
					energyMin = t;
				}
				r.evalTest = false;
			}
			System.out.println("Test:" + energyMin + ";" + avg / reglas.size());
			System.out.println("BestRuleTrain:" + bestTrain.train() + ";" + bestTrain.test());
		}
	}

	/**
	 * Evaluates a list of rules on the training and test sets and prints the best,
	 * mean, and standard deviation of the number of vehicles.
	 *
	 * @param reglas rules to evaluate
	 */
	protected static void evaluaReglas(List<Regla> reglas) {
		double energyMin = Double.MAX_VALUE;
		Regla bestTrain = null;
		double[] values = new double[reglas.size()];
		int i = 0;
		for (Regla r : reglas) {
			r.train();
			double t = r.vehicles;
			values[i] = t;
			i++;
			if (t < energyMin) {
				energyMin = t;
				bestTrain = r;
			}
			r.evalTest = false;
		}
		System.out.println("Train:" + energyMin + ";" + promedio(values) + ";" + desviacion(values));

		energyMin = Double.MAX_VALUE;
		values = new double[reglas.size()];
		i = 0;
		for (Regla r : reglas) {
			r.test();
			double t = r.vehicles;
			values[i] = t;
			i++;
			if (t < energyMin) {
				energyMin = t;
			}
			r.evalTest = false;
		}
		System.out.println("Test:" + energyMin + ";" + promedio(values) + ";" + desviacion(values));
		System.out.println("BestRuleTrain:" + bestTrain.train() + ";" + bestTrain.test());
	}

	/**
	 * Computes the population standard deviation of an array of values.
	 *
	 * @param v values to process
	 * @return population standard deviation
	 */
	static double desviacion(double[] v) {
		double prom, sum = 0;
		int i, n = v.length;
		prom = promedio(v);
		for (i = 0; i < n; i++)
			sum += Math.pow(v[i] - prom, 2);
		return Math.sqrt(sum / (double) n);
	}

	/**
	 * Computes the arithmetic mean of an array of values.
	 *
	 * @param v values to process
	 * @return arithmetic mean
	 */
	static double promedio(double[] v) {
		double prom = 0.0;
		for (int i = 0; i < v.length; i++)
			prom += v[i];
		return prom / (double) v.length;
	}

	/** Names of experiment files discovered by {@link #cargarFicheros(String)}. */
	protected static List<String> nombres = new ArrayList<String>();

	/**
	 * Contents of experiment files discovered by {@link #cargarFicheros(String)}.
	 */
	protected static List<String> ficheros = new ArrayList<String>();

	/**
	 * Builds a CSV file containing one rule extracted from each experiment file in
	 * the supplied directory.
	 *
	 * <p>
	 * The method reads the experiment outputs, groups them by experiment, extracts
	 * the rule stored in the configured measurement line, and writes the resulting
	 * rule representations to {@code reglas_ensembles_lex1.csv}.
	 * </p>
	 *
	 * @param directorio directory containing the experiment files
	 */
	public static void generaFicheroReglas(String directorio) {
		AlgoUtil.profundidadMaximaGlobal = 8;
		int numeroTomaTiempo = 100;
		int posRule = 1;
		cargarFicheros(directorio);
		Map<String, List<String[]>> datos = extraerFicheros(nombres, ficheros, directorio);
		StringBuilder sb = new StringBuilder();
		Map<String, List<Regla>> reglas = new HashMap<String, List<Regla>>();
		for (Entry<String, List<String[]>> entry : datos.entrySet()) {
			List<String[]> ficheros = entry.getValue();
			List<Regla> r = new ArrayList<Regla>();
			for (int i = 0; i < ficheros.size(); i++) {
				Regla rule = new Regla().loadRule(ficheros.get(i)[numeroTomaTiempo - 1].split(";")[posRule]);
				r.add(rule);
//				sb.append(entry.getKey().replace("_", ";") + ";" + rule.representaMonticulo() + ";\n");
				sb.append(rule.representaMonticulo() + ";\n");
			}
			reglas.put(entry.getKey(), r);
		}
		guardarFichero(sb.toString(), RULES_FILE);
	}

	/**
	 * Groups raw experiment files according to fields encoded in their file names.
	 *
	 * @param nombres    file names
	 * @param datos      contents corresponding to {@code nombres}
	 * @param directorio directory prefix to remove before parsing each file name
	 * @return grouped file contents indexed by experiment identifier
	 */
	protected static Map<String, List<String[]>> extraerFicheros(List<String> nombres, List<String> datos,
			String directorio) {
		Map<String, List<String[]>> datos_agrupados = new HashMap<String, List<String[]>>();
		String nombre;
		String[] pars;
		for (int i = 0; i < nombres.size(); i++) {
			pars = nombres.get(i).replace(directorio, "").split("_");
			nombre = pars[0] + "_" + pars[2] + "_" + pars[3] + "_" + pars[4];
			if (!datos_agrupados.containsKey(nombre))
				datos_agrupados.put(nombre, new ArrayList<String[]>());
			datos_agrupados.get(nombre).add(datos.get(i).split("\n"));
		}
		return datos_agrupados;
	}

	/**
	 * Loads the names and contents of all files directly contained in a directory.
	 *
	 * @param directorio directory to scan
	 */
	public static void cargarFicheros(String directorio) {
		File file = new File(directorio);
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			if (files != null) {
				for (File fichero : files)
					nombres.add(directorio + fichero.getName());
			}
		}
		for (String fichero : nombres)
			ficheros.add(cargaFichero(fichero));
	}

	/**
	 * Reads the complete textual contents of a file.
	 *
	 * @param fichero path of the file to read
	 * @return file contents, with a newline appended after each input line
	 */
	static String cargaFichero(String fichero) {
		Scanner kbd;
		String text = "";
		try {
			kbd = new Scanner(new File(fichero));
			while (kbd.hasNext()) {
				text += kbd.nextLine() + "\n";
			}
			kbd.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return text;
	}

	/**
	 * Writes text to a CSV file named {@code nombre + ".csv"}.
	 *
	 * @param fichero text to write
	 * @param nombre  base output file name, without the {@code .csv} extension
	 */
	protected static void guardarFichero(String fichero, String nombre) {
		BufferedWriter bw;
		try {
			bw = new BufferedWriter(new FileWriter(new File(nombre + ".csv")));
			bw.write(fichero);
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Fichero " + nombre + " generado!");
	}
}