package evrp_0.algorithms.rules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import evrp_0.algorithms.rules.representation.Nodo;
import evrp_0.algorithms.rules.representation.functions.NodoDivision;
import evrp_0.algorithms.rules.representation.functions.NodoExponencial;
import evrp_0.algorithms.rules.representation.functions.NodoLoge;
import evrp_0.algorithms.rules.representation.functions.NodoMax;
import evrp_0.algorithms.rules.representation.functions.NodoMax0;
import evrp_0.algorithms.rules.representation.functions.NodoMin;
import evrp_0.algorithms.rules.representation.functions.NodoMin0;
import evrp_0.algorithms.rules.representation.functions.NodoMultiplicacion;
import evrp_0.algorithms.rules.representation.functions.NodoNegacion;
import evrp_0.algorithms.rules.representation.functions.NodoPotencia;
import evrp_0.algorithms.rules.representation.functions.NodoRaiz;
import evrp_0.algorithms.rules.representation.functions.NodoResta;
import evrp_0.algorithms.rules.representation.functions.NodoSuma;
import evrp_0.algorithms.rules.representation.terminals.NodoT01_Energy;
import evrp_0.algorithms.rules.representation.terminals.NodoT02_Demand;
import evrp_0.algorithms.rules.representation.terminals.NodoT03_minDemand;
import evrp_0.algorithms.rules.representation.terminals.NodoT04_maxDemand;
import evrp_0.algorithms.rules.representation.terminals.NodoT05_avgDemand;
import evrp_0.algorithms.rules.representation.terminals.NodoT06_capacityVehicle;
import evrp_0.algorithms.rules.representation.terminals.NodoT07_energyToCentroid;
import evrp_0.algorithms.rules.representation.terminals.NodoT08_dueDate;
import evrp_0.algorithms.rules.representation.terminals.NodoT09_serviceTime;
import evrp_0.algorithms.rules.representation.terminals.NodoT10_readyTime;
import evrp_0.algorithms.rules.representation.terminals.NodoT11_energyToRP;
import evrp_0.algorithms.rules.representation.terminals.NodoT12_energyToDepot;
import evrp_0.algorithms.rules.representation.terminals.NodoT13_energyToRP_a;
import evrp_0.algorithms.rules.representation.terminals.NodoT14_energyToDepot_a;
import evrp_0.algorithms.rules.representation.terminals.NodoT15_timeVehicle;
import evrp_0.algorithms.rules.representation.terminals.NodoT16_energyVehicle;
import evrp_0.domain.Instance;
import evrp_0.domain.Point;
import evrp_0.domain.Solution;
import evrp_0.domain.SolvingMethod;
import evrp_0.util.AlgoUtil;

public class Regla extends SolvingMethod implements Comparable<Regla> {
	/*
	 * VARIABLES
	 */
	public Nodo[] nodos;
	public int size;
	protected String regla;
	protected boolean valido = false;

	@Override
	public Point getNext() {
		if (domain.noVisitedCustomers.size() == 0) // no points feasible to be visited
			return null;
		double morePriorited = -Double.MAX_VALUE;
		Point next = null;
		double priority;
		for (Point p : domain.noVisitedCustomers) { // customers
			domain.selected = p;
			priority = computePriority();
			// if priority is NaN, -inf or +inf maybe we could do something more... for
			// instance, use a classical rule to take the decision
			if (Double.isNaN(priority) || Double.isInfinite(priority))
				priority = 0; //
			if (priority > morePriorited) {
				next = p;
				morePriorited = priority;
			}
		}
		return next;
	}

	public double computePriority() {
		return computePriority(0);
	}

	double computePriority(int i) {
		if (nodos[i] != null) {
			if (nodos[i].esTerminal())
				return nodos[i].value(domain);
			else {
				double izq = 0, der = 0;
				if ((2 * i + 1) < nodos.length) {
					izq = computePriority((2 * i + 1));
					if ((2 * i + 2) < nodos.length && !nodos[i].esUnaria())
						der = computePriority((2 * i + 2));
				}
				return nodos[i].value(izq, der);
			}
		}
		return 0;
	}

	/*
	 * BUILDERS
	 */
	public Regla() {
		this.nodos = new Nodo[(int) (Math.pow(2, AlgoUtil.profundidadMaximaGlobal) - 1)];
		size = 0;
	}

	/**
	 * Create a child
	 * 
	 * @param posicionPadre1
	 * @param posicionPadre2
	 * @param padre1
	 * @param padre2
	 */
	public Regla(int posicionPadre1, int posicionPadre2, Regla padre1, Regla padre2) {
		this(padre1);
		borrar(posicionPadre1);
		insertar(posicionPadre1, posicionPadre2, padre2);
	}

	public Regla getClon() {
		return new Regla(this);
	}

	public Regla(Regla monticulo) {
		this.nodos = new Nodo[monticulo.nodos.length];
		this.size = monticulo.size;
		for (int i = 0; i < nodos.length; i++)
			if (monticulo.nodos[i] != null) {
				nodos[i] = monticulo.nodos[i].clona();
			}
	}

	public Regla random() {
		if (AlgoUtil.metodoKoza)
			return metodoKoza();
		else
			return random_private();
	}

	public Regla random_private() {
		do {
			nodos = new Nodo[(int) (Math.pow(2, AlgoUtil.profundidadMaximaGlobal) - 1)];
			size = 0;
			generarDegenerado(nodos.length);
			if (esValido())
				valido = true;
		} while (!valido);
		return this;
	}

	public Regla metodoKoza() {
		boolean completo = java.util.concurrent.ThreadLocalRandom.current().nextBoolean();
		int profundidadMaxima;
		if (AlgoUtil.profundidadMaximaInicialReglas > 1)
			profundidadMaxima = (ThreadLocalRandom.current().nextInt(1, AlgoUtil.profundidadMaximaInicialReglas) + 1);
		else
			profundidadMaxima = AlgoUtil.profundidadMaximaInicialReglas;
		if (completo && profundidadMaxima > AlgoUtil.profundidadMaximaCompletasReglas)
			profundidadMaxima = AlgoUtil.profundidadMaximaCompletasReglas;

		int sizeMax = (int) (Math.pow(2, profundidadMaxima) - 1);

		// generamos la regla
		do {
			nodos = new Nodo[(int) (Math.pow(2, AlgoUtil.profundidadMaximaGlobal) - 1)];
			size = 0;
			if (completo) {
				if (AlgoUtil.onlyBinaryTerminals)
					generarCompletoOnlyBinaryTerminals(sizeMax);
				else
					generarCompleto(sizeMax);
			} else {
				if (AlgoUtil.onlyBinaryTerminals)
					generarDegeneradoOnlyBinaryTerminals(sizeMax);
				else
					generarDegenerado(sizeMax);
			}
			if (esValido())
				valido = true;
		} while (!valido);
		return this;
	}

	private void generarCompleto(int sizeMax) {
		for (int i = sizeMax - 1; i >= 0; i--) {
			if (i >= (int) sizeMax / 2) {
				generaTerminalConstante(i);
				size++;
			} else {
				if (nodos[2 * i + 1] != null && nodos[2 * i + 2] != null)
					generaBinaria(i);
				size++;
			}
		}
	}

	private void generarDegenerado(int sizeMax) {
		for (int i = sizeMax - 1; i >= 0; i--) {
			if (i >= (int) sizeMax / 2 || (nodos[2 * i + 1] == null)) { // terminal
				if (!hijoDerecho(i) && nodos[i + 1] != null) {
					if (!nodos[i + 1].esConstante())
						generaTerminalConstante(i);
					else
						generaTerminal(i);
				} else {
					generaHoja(i);
				}
			} else { // funcion
				if (nodos[2 * i + 2] != null) // binaria
					generaBinaria(i);
				else // unaria
					generaUnaria(i);
			}
			if (nodos[i] != null)
				size++;
		}
		if (nodos[0] == null)
			random_private();
	}

	private void generarCompletoOnlyBinaryTerminals(int sizeMax) {
		for (int i = 0; i < sizeMax; i++) {
			if (i == 0)
				generaBinaria(i);
			else if (!padreNull(i) && !padreTerminal(i)) {
				if (i >= (int) sizeMax / 2 || padreFuncion(i))
					generaTerminal(i);
				else
					generaRandomBinariaTerminal(i);
			}
			if (nodos[i] != null)
				size++;
		}
	}

	private void generarDegeneradoOnlyBinaryTerminals(int sizeMax) {
		for (int i = 0; i < sizeMax; i++) {
			if (i == 0)
				generaBinaria(i);
			else if (!padreNull(i) && !padreTerminal(i)) {
				if (i >= (int) sizeMax / 2 || padreFuncion(i))
					generaTerminal(i);
				else
					generaRandomBinariaTerminalNull(i);
			}
			if (nodos[i] != null)
				size++;
		}
	}

	public int iguales(int i, int j) {
		if (j >= nodos.length) {
			return 0;
		} else if (nodos[i] == null && nodos[j] == null) { // der == izq
			return 0;
		} else { // (nodos[i] != null && nodos[j] != null)
			if (nodos[i].orden() > nodos[j].orden()) // izq > der
				return 1;
			else if (nodos[i].orden() < nodos[j].orden()) // izq < der
				return -1;
			else { // izq == der
				if (nodos[i].esTerminal()) // esTerminal -> T o C.
					return 0;
				else {
					int value = iguales(2 * i + 1, 2 * j + 1);
					if (value == 0)
						return iguales(2 * i + 2, 2 * j + 2);
					return value;
				}
			}
		}
	}

	private void generaHoja(int i) {
		int random = (int) (Math.random() * (1 + AlgoUtil.getNterminales() + AlgoUtil.indiceNullHoja));
		if (random == 0)
			nodos[i] = AlgoUtil.getConstanteRandom();
		else if (random >= 1 && random <= AlgoUtil.getNterminales())
			nodos[i] = AlgoUtil.getTerminalRandom();
		else
			nodos[i] = null;
	}

	private void generaTerminalConstante(int i) {
		int random = (int) (Math.random() * (1 + AlgoUtil.getNterminales()));
		if (random == 0)
			nodos[i] = AlgoUtil.getConstanteRandom();
		else
			nodos[i] = AlgoUtil.getTerminalRandom();
	}

	private void generaTerminal(int i) {
		nodos[i] = AlgoUtil.getTerminalRandom();
	}

	private void generaBinaria(int i) {
		nodos[i] = AlgoUtil.getBinaryFunctionRandom();
	}

	private void generaRandomBinariaTerminal(int i) {
		nodos[i] = AlgoUtil.getTerminalBinaryFunctionRandom();
	}

	private void generaRandomBinariaTerminalNull(int i) {
		int random = (int) (Math.random() * (AlgoUtil.getNTerminalBinaryFunctionRandom() + AlgoUtil.indiceNullHoja));
		if (random < AlgoUtil.getNTerminalBinaryFunctionRandom())
			nodos[i] = AlgoUtil.getTerminalBinaryFunctionRandom();
		else
			nodos[i] = null;
	}

	private void generaUnaria(int i) {
		switch ((int) (Math.random() * 7)) {
		case 0:
			nodos[i] = new NodoRaiz();
			break;
		case 1:
			nodos[i] = new NodoPotencia();
			break;
		case 2:
			nodos[i] = new NodoMax0();
			break;
		case 3:
			nodos[i] = new NodoMin0();
			break;
		case 4:
			nodos[i] = new NodoNegacion();
			break;
		case 5:
			nodos[i] = new NodoLoge();
			break;
		default:
			nodos[i] = new NodoExponencial();
			break;
		}
	}

	/*
	 * RECORRIDOS DEL Ã�RBOL
	 */
	public String representa() {
		List<String> elementos = new ArrayList<String>();
		representa(0, elementos);
		return generaRegla(elementos);
	}

	String monticulo;

	public String representaMonticulo() {
		if (monticulo == null) {
			monticulo = "";
			for (int i = 0; i < nodos.length; i++)
				if (nodos[i] != null)
					monticulo = monticulo + nodos[i].simbolo().toString() + " ";
				else
					monticulo = monticulo + "null ";
		}
		return monticulo;
	}

	public String representaMonticuloPorNivel() {
		String text = "";
		int nivel = 0;
		for (int i = 0; i < nodos.length; i++) {
			if ((((int) (Math.log(i + 1) / Math.log(2))) + 1) > nivel) {
				nivel++;
				text += "\n";
			}
			if (nodos[i] != null)
				text += "{" + nodos[i].simbolo() + "}";
			else
				text += "{<NULL>}";
		}
		return text;
	}

	protected String generaRegla(List<String> elementos) {
		regla = "";
		for (String s : elementos)
			regla = regla + s;
		return regla;
	}

	protected void representa(int i, List<String> formula) {
		if (nodos[i] != null) {
			if (!nodos[i].esTerminal())
				formula.add("(");
			else
				formula.add(nodos[i].simbolo());

			if (nodos[i].simbolo().equals("Math.max") || nodos[i].simbolo().equals("Math.min") || nodos[i].esUnaria())
				formula.add(nodos[i].simbolo() + "(");

			if ((2 * i + 1) < nodos.length)
				representa(2 * i + 1, formula);

			if (nodos[i].simbolo().equals("Math.pow") || nodos[i].simbolo().equals("Math.max")
					|| nodos[i].simbolo().equals("Math.min"))
				formula.add(",");

			if (nodos[i].simbolo().equals("+") || (nodos[i].simbolo().equals("-") && !nodos[i].esUnaria())
					|| nodos[i].simbolo().equals("/") || nodos[i].simbolo().equals("*"))
				formula.add(nodos[i].simbolo());

			if (nodos[i].simbolo().equals("Math.pow"))
				formula.add("2");

			if ((nodos[i].simbolo().equals("Math.max") && nodos[i].esUnaria())
					|| (nodos[i].simbolo().equals("Math.min") && nodos[i].esUnaria()))
				formula.add("0");

			if ((2 * i + 2) < nodos.length)
				representa(2 * i + 2, formula);

			if (nodos[i].simbolo().equals("Math.max") || nodos[i].simbolo().equals("Math.min") || nodos[i].esUnaria())
				formula.add(")");

			if (!nodos[i].esTerminal())
				formula.add(")");
		}
	}

	public int posAzar() {
		List<Integer> pos = new ArrayList<Integer>();
		for (int i = 0; i < nodos.length; i++)
			if (nodos[i] != null)
				pos.add(i);
		if (!pos.isEmpty())
			return pos.get((int) (Math.random() * pos.size()));
		return -1;
	}

	public void resetEvaluation() {
		evalFilter = false;
		evalTrain = false;
		evalTest = false;
		solutions.clear();
	}
	
	public Map<Instance, Solution> solutions = new HashMap<Instance, Solution>();
	
	@Override
	public Solution solve(Instance instance) {
		if (solutions.containsKey(instance))
			return solutions.get(instance);
		else {
			Solution solution = super.solve(instance);
			solutions.put(instance, solution);
			return solution;
		}
	}

	double train;
	boolean evalTrain;
	
	public double train() {
		if (!evalTrain) {
			double fitness = 0.0;
			tardiness = 0.0;
			vehicles = 0.0;
			energy = 0.0;

//			CacheRuleSolutions solutions = null;
//			if (AlgoUtil.cache) {
//				solutions = AlgoUtil.soluciones.get(this);
//				if (solutions == null) {
//					AlgoUtil.soluciones.put(this, new CacheRuleSolutions());
//					solutions = AlgoUtil.soluciones.get(this);
//				}
//			}
			Solution solution = null;
			//fitness = AlgoUtil.set_train.size();
			for (Instance instance : AlgoUtil.set_train) {
//				if (AlgoUtil.cache)
//					solution = solutions.getSolucion(instance);
//				if (solution == null) {
					solution = solve(instance);
//					if (AlgoUtil.cache)
//						solutions.setSolucion(instance, solution);
//				}
				fitness += calculaFitness(solution);
				//double value = calculaFitness(solution);
				//if (!AlgoUtil.bestSolutions.containsKey(instance.name) || value < AlgoUtil.bestSolutions.get(instance.name)) 
				//	AlgoUtil.bestSolutions.put(instance.name, value);
				//if (value == AlgoUtil.bestSolutions.get(instance.name))
				//	fitness--;
				
				if (Double.isInfinite(fitness))
					break;
			}
//			int n = 0;
//			if (AlgoUtil.historial && solutions != null) {
//				Set<Entry<Instance, Solution>> previosEvaluated = solutions.getSolutions();
//				for (Entry<Instance, Solution> e : previosEvaluated) {
//					if (!AlgoUtil.set_train.contains(e.getKey())) {
//						fitness += calculaFitness(e.getValue());
//						n++;
//					}
//				}
//			}
//			fitness /= (1.0 * AlgoUtil.set_train.size() + n);
			train = fitness;
			evalTrain = true;
		}
		return train;
	}

	double test;
	public boolean evalTest;

	public double test() {
		if (!evalTest) {
			double fitness = 0.0;
			evalTest = true;
			tardiness = 0.0;
			vehicles = 0.0;
			energy = 0.0;
			Solution solution;
			for (Instance instance : AlgoUtil.set_test) {
				solution = solve(instance);
				fitness += calculaFitness(solution);
				if (Double.isInfinite(fitness))
					break;
//				System.out.println(instance.name + "  "+solution.getTotalVehicles());
			}
//			System.out.println(vehicles);
			test = fitness;
		}
		return test;
	}

	public double filter;
	public boolean evalFilter;

	public double filter() {
		if (!evalFilter) {
			double fitness = 0.0;
			evalFilter = true;		
			tardiness = 0.0;
			vehicles = 0.0;
			energy = 0.0;
			Solution solution;
			for (Instance instance : AlgoUtil.set_filter) {
				solution = solve(instance);
				fitness += calculaFitness(solution);
				if (Double.isInfinite(fitness))
					break;
			}
			filter = fitness;
		}
		return filter;
	}
	

	
	// here is where the fitness function is implemented.
	public double tardiness;
	public double vehicles;
	public double energy;

//	protected double fitness(List<Instance> set) {
//		double fitness = 0.0;
//		tardiness = 0.0;
//		vehicles = 0.0;
//		energy = 0.0;
//
//		CacheRuleSolutions solutions = null;
//		if (AlgoUtil.cache) {
//			solutions = AlgoUtil.soluciones.get(this);
//			if (solutions == null) {
//				AlgoUtil.soluciones.put(this, new CacheRuleSolutions());
//				solutions = AlgoUtil.soluciones.get(this);
//			}
//		}
//		Solution solution = null;
//		for (Instance instance : set) {
//			if (AlgoUtil.cache)
//				solution = solutions.getSolucion(instance);
//			if (solution == null) {
//				solution = solve(instance);
//				solutions.setSolucion(instance, solution);
//			}
//			fitness += calculaFitness(solution);
//			if (Double.isInfinite(fitness))
//				break;
//		}
//
//		int n = 0;
//		if (AlgoUtil.historical && solutions != null) {
//			Set<Entry<Instance, Solution>> previosEvaluated = solutions.getSolutions();
//			for (Entry<Instance, Solution> e : previosEvaluated) {
//				if (!set.contains(e.getKey())) {
//					fitness += calculaFitness(e.getValue());
//					n++;
//				}
//			}
//		}
//
//		fitness /= (1.0 * set.size() + n);
//		return fitness;
//	}
	
	public double calculaFitness(Solution solution) {
		double value = 0;
		switch (AlgoUtil.criterio) {
		case 0:
			value = solution.getTotalVehicles();
			break;
		case 1:
			value = solution.getTotalEnergy();
			break;
		case 2:
			value = solution.getTotalTardiness();
			break;
		case 3:
			value = solution.getTotalVehicles()*1000000000 + solution.getTotalEnergy(); // ojo!
			break;
		default:
			break;
		}
		vehicles += solution.getTotalVehicles();
		energy += solution.getTotalEnergy();
		tardiness += solution.getTotalTardiness();
		return value;
	}

	// implementation with cache
//	private double fitness(List<Instance> set) {
//		double fitness = 0.0;
//		double value;
//		CacheRuleSolutions soluciones = AlgoUtil.soluciones.get(this);
//		if (soluciones == null) {
//			soluciones = new CacheRuleSolutions();
//			AlgoUtil.soluciones.put(this, soluciones);
//		}
//		Solution solution;
//		for (Instance instance : set) {
//			if (soluciones.mapa.containsKey(instance))	{
//				solution = soluciones.getSolucion(instance);
//			} else {
//				solution = solve(instance);
//				soluciones.setSolucion(instance, solution);
//			}
////			value = solution.getTotalVehicles(); // vehicles
////			value = solution.getTotalEnergy(); // energy
//			value = solution.getTotalTardiness(); // tardiness
//			// put here compute fitness value in another way
//			fitness += value; 
//		}
//		fitness /= set.size();
//		return fitness;
//	}

	/*
	 * OPERACIONES Y COMPROBACIONES DEBIDAS A LA IMPLEMENTACIÃ“N CON MONTICULOS
	 * BINARIOS
	 * 
	 * Inserta el subarbol j del monticulo en la posicion i de este monticulo
	 * 
	 */
	public void insertar(int i, int j, Regla monticulo) {
		if (monticulo.nodos[j] != null) {
			nodos[i] = monticulo.nodos[j].clona();
			size++;
			if ((2 * i + 1) < nodos.length && (2 * j + 1) < nodos.length)
				insertar(2 * i + 1, 2 * j + 1, monticulo);
			if ((2 * i + 2) < nodos.length && (2 * j + 2) < nodos.length)
				insertar(2 * i + 2, 2 * j + 2, monticulo);
		}
	}

	public void borrar(int i) {
		if (nodos[i] != null) {
			size--;
			nodos[i] = null;
			if ((2 * i + 1) < nodos.length)
				borrar(2 * i + 1);
			if ((2 * i + 2) < nodos.length)
				borrar(2 * i + 2);
		}
	}

	protected List<String> subarbol(int index) {
		List<String> subarbol = new ArrayList<String>();
		for (int i = index; i < nodos.length; i++)
			if (nodos[i] != null)
				subarbol.add(nodos[i].simbolo());
		return subarbol;
	}

	public boolean hijoDerecho(int i) {
		return i % 2 == 0 && i != 0;
	}

	protected boolean padreNull(int i) {
		return nodos[(i - 1) / 2] == null;
	}

	protected boolean padreFuncionBinaria(int i) {
		if (!padreNull(i))
			return nodos[(i - 1) / 2].esOperacion() && !nodos[(i - 1) / 2].esUnaria();
		return false;
	}

	protected boolean padreFuncionUnaria(int i) {
		if (!padreNull(i))
			return nodos[(i - 1) / 2].esOperacion() && nodos[(i - 1) / 2].esUnaria();
		return false;
	}

	protected boolean padreFuncion(int i) {
		if (!padreNull(i))
			return nodos[(i - 1) / 2].esOperacion();
		return false;
	}

	protected boolean padreTerminal(int i) {
		if (!padreNull(i))
			return !nodos[(i - 1) / 2].esOperacion();
		return false;
	}

	protected boolean hoja(int i) {
		return i >= (int) nodos.length / 2;
	}

	/*
	 * COMPARACIONES ENTRE REGLAS (MONTICULOS)
	 */
	@Override
	public boolean equals(Object obj) {
//		return Arrays.equals(nodos, ((Monticulo) obj).nodos);
		return representaMonticulo().equals(((Regla) obj).representaMonticulo());
//		return representa().equals(((Regla) obj).representa());
	}

	@Override
	public int hashCode() {
		return representaMonticulo().hashCode();
	}

	/*
	 * CARACTERISTICAS DE LA REGLA
	 */
	public int getSize() {
		return size;
	}

	public int getDepth() {
		int ultimo = 0;
		for (int i = 0; i < nodos.length; i++)
			if (nodos[i] != null)
				ultimo = i;
		return (int) (Math.log(ultimo + 1) / Math.log(2)) + 1;
	}

	public boolean esCompleta() {
		return size == (int) (Math.pow(2, getDepth()) - 1);
	}

	public boolean esValido() {
		if (nodos[0] == null)
			return false;
		for (int i = 0; i < nodos.length; i++) {
			if (nodos[i] != null) {
				if (i > 0) {
					if (nodos[(i - 1) / 2] == null) // padreNull
//						System.out.println("padreNull");
						return false;
					else if (nodos[(i - 1) / 2].esTerminal()) // padre terminal
//						System.out.println("padre terminal");
						return false;
				}
				if (nodos[i].esOperacion() && hoja(i)) // operacion y hoja
//					System.out.println("operacion y hoja");
					return false;
				if (!hoja(i)) {
					if (nodos[i].esTerminal() && (nodos[2 * i + 1] != null || nodos[2 * i + 2] != null))
						// es terminal y tiene hijos
//						System.out.println("es terminal y tiene hijos");
						return false;
					if (nodos[i].esOperacion() // es funcion con mal hijos
							&& (nodos[2 * i + 1] == null || (nodos[2 * i + 2] == null && !nodos[i].esUnaria())))
//						System.out.println("funcion con mal hijos");
						return false;
				}
			}
		}
		return true;
	}

	// simplifica
	public void simplifica() {
		for (int i = 0; i < nodos.length / 2; i++)
			if (nodos[i] != null && nodos[i].esBinariaInversa() && iguales(2 * i + 1, 2 * i + 2) <= 0) {
				intercambiaSubarbol(2 * i + 1, 2 * i + 2);
				i = 0;
			}
	}

	/*
	 * Metodo para reducir/simplificar las reglas
	 */
	void intercambiaSubarbol(int i, int j) {
		Regla copia = new Regla(this);
		copia.borrar(i);
		copia.insertar(i, j, this);
		copia.borrar(j);
		copia.insertar(j, i, this);
		nodos = copia.nodos;
	}

	public int getNsimbolos() {
		Set<String> simbolos = new HashSet<String>();
		for (int i = 0; i < nodos.length; i++)
			if (nodos[i] != null)
				simbolos.add(nodos[i].simbolo());
		return simbolos.size();
	}

	@Override
	public int compareTo(Regla o) { // REVISE
		int val = Double.compare(train(), o.train());
		if (val == 0)
			return Double.compare(getSize(), o.getSize());
		return val;
	}

	/*
	 * CARGAR REGLAS YA DEFINIDAS
	 */
	public Regla loadRule(String regla) {
//		System.out.println("ESTA SIN DEFINIR");
		int sizeMax = (int) (Math.pow(2, AlgoUtil.profundidadMaximaGlobal) - 1);
		nodos = new Nodo[sizeMax];
		String[] valores = regla.split(" ");
		for (int i = valores.length - 1; i >= 0; i--)
			if (!valores[i].equals("null")) {
				size++;
				switch (valores[i]) {
				case "/":
					nodos[i] = new NodoDivision();
					break;
				case "Math.exp":
					nodos[i] = new NodoExponencial();
					break;
				case "Math.log":
					nodos[i] = new NodoLoge();
					break;
				case "Math.max":
					if (nodos[2 * i + 2] != null && !nodos[2 * i + 2].simbolo().equals("0.0")
							&& !nodos[2 * i + 2].simbolo().equals("0"))
						nodos[i] = new NodoMax();
					else {
						nodos[i] = new NodoMax0();
						nodos[2 * i + 2] = null;
					}
					break;
				case "Math.min":
					if (nodos[2 * i + 2] != null && !nodos[2 * i + 2].simbolo().equals("0.0")
							&& !nodos[2 * i + 2].simbolo().equals("0"))
						nodos[i] = new NodoMin();
					else {
						nodos[i] = new NodoMin0();
						nodos[2 * i + 2] = null;
					}
					break;
				case "*":
					nodos[i] = new NodoMultiplicacion();
					break;
				case "-":
					if (nodos[2 * i + 2] != null)
						nodos[i] = new NodoResta();
					else
						nodos[i] = new NodoNegacion();
					break;
				case "Math.pow":
					nodos[i] = new NodoPotencia();
					break;
				case "Math.sqrt":
					nodos[i] = new NodoRaiz();
					break;
				case "+":
					nodos[i] = new NodoSuma();
					break;
				// Terminales
				case "E":
					nodos[i] = NodoT01_Energy.getInstance();
					break;
				case "D":
					nodos[i] = NodoT02_Demand.getInstance();
					break;
				case "Dmin":
					nodos[i] = NodoT03_minDemand.getInstance();
					break;
				case "Dmax":
					nodos[i] = NodoT04_maxDemand.getInstance();
					break;
				case "Davg":
					nodos[i] = NodoT05_avgDemand.getInstance();
					break;
				case "VC":
					nodos[i] = NodoT06_capacityVehicle.getInstance();
					break;
				case "Ec":
					nodos[i] = NodoT07_energyToCentroid.getInstance();
					break;
				case "DD":
					nodos[i] = NodoT08_dueDate.getInstance();
					break;
				case "ST":
					nodos[i] = NodoT09_serviceTime.getInstance();
					break;
				case "RT":
					nodos[i] = NodoT10_readyTime.getInstance();
					break;
				case "ERP":
					nodos[i] = NodoT11_energyToRP.getInstance();
					break;
				case "EDep":
					nodos[i] = NodoT12_energyToDepot.getInstance();
					break;
				case "ERP_a":
					nodos[i] = NodoT13_energyToRP_a.getInstance();
					break;
				case "EDep_a":
					nodos[i] = NodoT14_energyToDepot_a.getInstance();
					break;
				case "VT":
					nodos[i] = NodoT15_timeVehicle.getInstance();
					break;
				case "VE":
					nodos[i] = NodoT16_energyVehicle.getInstance();
					break;
				default:
					nodos[i] = AlgoUtil.getConstante(Double.parseDouble(valores[i]));
					break;
				}
			}
		return this;
	}

	public Map<String, Integer> getNumberOfEachSymbol() {
		Map<String, Integer> symbols = new HashMap<String, Integer>();
		for (Nodo nodo : nodos) {
			if (nodo != null) {
				if (!symbols.containsKey(nodo.simboloPrintable())) {
					symbols.put(nodo.simboloPrintable(), 1);
				} else {
					symbols.put(nodo.simboloPrintable(), symbols.get(nodo.simboloPrintable()) + 1);
				}
			}
		}
		return symbols;
	}

	public boolean esEnsemble() {
		return false;
	}

	@Override
	public String toString() {
		return representa();
	}

	public Regla generarNN() {
		nodos[0] = new NodoDivision();
		nodos[1] = AlgoUtil.getConstante(1.0);
		nodos[2] = NodoT01_Energy.getInstance();
		size = 3;
		asignaDimension();
		return this;
	}
	
	public void asignaDimension() {
		for (int i = nodos.length - 1; i >= 0; i--) {
			if (nodos[i] != null)
				dimensionSimboloEn(i);
		}
	}

	public void dimensionSimboloEn(int i) {
		try {
			if (nodos[i].esOperacion()) {
				switch (nodos[i].simbolo()) {
				case "/":
					nodos[i].setDimension(nodos[2 * i + 1].getDimension() - nodos[2 * i + 2].getDimension());
					break;
				case "*":
					nodos[i].setDimension(nodos[2 * i + 1].getDimension() + nodos[2 * i + 2].getDimension());
					break;
				case "Math.pow":
					nodos[i].setDimension(nodos[2 * i + 1].getDimension() * 2.0);
					break;
				case "Math.sqrt":
					nodos[i].setDimension(nodos[2 * i + 1].getDimension() / 2.0);
					break;
				case "Math.exp":
					nodos[i].setDimension(0);
					break;
				case "Math.log":
					nodos[i].setDimension(0);
					break;
				default:
					nodos[i].setDimension(nodos[2 * i + 1].getDimension());
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("FALLO AL ASIGNAR DIMENSION");
		}
	}

	public int posAzarMismaDim(Regla padre, int index) {
		List<Integer> pos = new ArrayList<Integer>();
		for (int i = 0; i < nodos.length; i++)
			if (nodos[i] != null)
				if (Double.compare(nodos[i].getDimension(), padre.nodos[index].getDimension()) == 0) 
					pos.add(i);
		if (!pos.isEmpty())
			return pos.get((int) (Math.random() * pos.size()));
		return -1;
	}
	
}