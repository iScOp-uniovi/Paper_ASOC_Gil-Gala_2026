package evrp_0.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

import evrp_0.algorithms.rules.Regla;
import evrp_0.algorithms.rules.representation.Nodo;
import evrp_0.algorithms.rules.representation.functions.NodoDivision;
import evrp_0.algorithms.rules.representation.functions.NodoMax;
import evrp_0.algorithms.rules.representation.functions.NodoMin;
import evrp_0.algorithms.rules.representation.functions.NodoMultiplicacion;
import evrp_0.algorithms.rules.representation.functions.NodoResta;
import evrp_0.algorithms.rules.representation.functions.NodoSuma;
import evrp_0.algorithms.rules.representation.terminals.NodoConstante;
import evrp_0.algorithms.rules.representation.terminals.NodoT01_Energy;
import evrp_0.algorithms.rules.representation.terminals.NodoT02_Demand;
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

public class AlgoUtil {

	// parametros
	// rules
//	public static String directory_training_set = "SchneiderInstances/"; // all the instances
	public static String directory_training_set = "SchneiderInstancesTraining/";
	public static int profundidadMaximaInicialReglas = 4;
	public static int profundidadMaximaGlobal = 8;
	public static int profundidadMaximaCompletasReglas = 5;
	public static boolean metodoKoza = true;
	public static boolean onlyBinaryTerminals = false;
	public static int indiceNullHoja = 95;

	// ensembles
	public static String set_rules = "";
	public static int modeEnsembles = 0; // 0=combinationMethod, 1=bestSolution, 2=bestSolutionInEachSteap
	public static int combinationMethod = 0; // 0:suma, 1:voteSimple
	public static int sizeEnsembles = 3;

	// domain parameters
	public static int mode = 0; // schedule builder
	public static int criterio = 0; // objetive to minimization: 0:vehicles, 1:energy, 2:tardines, 3:vechiles * 10^9 + energy
	public static int methodComputeRPTour = 1; // way to compute route recharging points: 0:nearestRP 1:minimumEnergyRP

	// surrogate evaluation ensembles
	public static boolean V2 = false;
	
	// mejora de las soluciones de las reglas con LSA
	public static boolean improveLSA = false;
	
	// interno
	public static Regla mejorGlobal;
	public static List<Instance> set_test;
	public static List<Instance> set_train;
	public static List<Instance> set_filter;
	public static boolean cache = true;
	public static Map<Regla, CacheRuleSolutions> soluciones = new HashMap<Regla, CacheRuleSolutions>();
	public static List<Regla> rulesForBuildingEnsembles = new ArrayList<Regla>();
	public static double coste;
	public static int size;
	public static int depth;
	public static int minutos;
	public static int intervalosMedicion;
	public static boolean monitoriza = true;

	protected static BufferedWriter bw;
	protected static long inicio;
	protected static List<Long> tiempos;
	protected static int indiceTiempos;
	public static Algoritmo algorithm;
	
	public static Map<String, Double> bestSolutions = new HashMap<String, Double>();

	public static void run(String msg) {
		loadBasicToRun();
		if (intervalosMedicion <= 0 || minutos <= 0) {
			monitoriza = false;
			intervalosMedicion = 1;
			minutos = 60 * 24; // el tiempo limite es un dia?
		}
		iniciaMedicionTiempo();
		iniciaLogger(msg);
	}

	public static void loadBasicToRun() {
		loadInstances();
//		soluciones = new HashMap<Rule, CacheRuleSolutions>();
		rulesForBuildingEnsembles = new ArrayList<Regla>();
		coste = Double.MAX_VALUE;
		size = Integer.MAX_VALUE;
		depth = Integer.MAX_VALUE;
	}

	public static void run(String msg, Algoritmo gp) {
		run(msg);
		algorithm = gp;
	}

	public static Nodo getConstanteRandom() {
		return getConstante(Math.random());
	}

	public static Nodo getConstante(double d) {
		return new NodoConstante(Math.round(d * 10.0) / 10.0);
	}

	// METODOS PARA EVALUAR REGLAS
	public static boolean mejoraTraining(Regla a, Regla b) {
		// revisamos si previamente han sido ya comparadas o evaluadas
		a = getFullEvaluate(a);
		b = getFullEvaluate(b);
		// comprobamos si a es mejor que b
		if (a.train() < b.train()) {
			return true;
		} else if (a.train() == b.train()) {
			if (a.getSize() < b.getSize()) {
				return true;
			} else if (a.getSize() == b.getSize() && a.getDepth() < b.getDepth()) {
				return true;
			}
		}
		return false;
	}

	public static boolean mejoraSM(Regla a, Regla b) {
		// comprobamos si a es mejor que b
		if (a.filter() < b.filter()) {
			return true;
		} else if (a.filter() == b.filter()) {
			if (a.getSize() < b.getSize()) {
				return true;
			} else if (a.getSize() == b.getSize() && a.getDepth() < b.getDepth()) {
				return true;
			}
		}
		return false;
	}

	public static Regla getFullEvaluate(Regla regla) {
		regla.train();
//		seleccionables.add(regla); // aqui es donde se podria añadir un criba a las reglas que compondrán los ensembles
		compruebaSiMejora(regla);
		return regla;
	}

	public static Regla getReglaRandom() {
		return rulesForBuildingEnsembles.get(new Random().nextInt(rulesForBuildingEnsembles.size()));
	}

	private static void compruebaSiMejora(Regla regla) {
		if (regla.train() < coste)
			actualizaMejor(regla);
		else if (regla.train() == coste) {
			if (regla.getSize() < size)
				actualizaMejor(regla);
			else if (regla.getSize() == size && regla.getDepth() < depth)
				actualizaMejor(regla);
		}
	}

	public static void actualizaMejor(Regla regla) {
		coste = regla.train();
		size = regla.getSize();
		depth = regla.getDepth();
		mejorGlobal = regla;
	}
	
	public static void reiniciaMejor() {
		coste = Double.MAX_VALUE;
		size = Integer.MAX_VALUE;
		depth = Integer.MAX_VALUE;
		mejorGlobal = null;
	}

	// METODOS PARA GUARDAR DATOS
	public static void iniciaLogger(String mensaje) {
		try {
			bw = new BufferedWriter(new FileWriter(
					new File(mensaje + "_" + calculaHoraFecha() + "_" + new Random().nextInt() + ".csv")));
			ln(mensaje);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void vln(String linea) {
		ln(linea);
		nvln(linea);
	}

	public static void nvln(String linea) {
		if (bw != null)
			try {
				bw.write(linea + "\n");
			} catch (IOException e) {
				e.printStackTrace();
			}
	}

	public static void ln(String linea) {
		linea = "[" + tiempoEjecucion() + "]" + linea;
		System.out.println(linea);
	}

	protected static void close() {
		try {
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// METODOS PARA MEDIR EL TIEMPO
	/**
	 * Este metodo se llama de forma recurrente para comprobar si se cumple el
	 * timeOut. Además, si toca, imprime una linea con información de la ejecucion.
	 * 
	 * @return
	 */
	public static boolean timeOut() {
		long value = System.currentTimeMillis() - inicio;
		if (monitoriza && indiceTiempos < tiempos.size() && value > tiempos.get(indiceTiempos)) {
			AlgoUtil.vln(algorithm.getLinea());
			indiceTiempos++;
		}
		return value > getTiempoLimite();
	}

	public static void iniciaMedicionTiempo() {
		inicio = System.currentTimeMillis();
		estableceIntervalosMedicion(intervalosMedicion);
	}

	public static int tiempoEjecucion() {
		return (int) ((System.currentTimeMillis() - inicio) / 1000);
	}

	private static void estableceIntervalosMedicion(int n) {
		tiempos = new ArrayList<Long>();
		indiceTiempos = 0;
		long paso = (getTiempoLimite() / n);
		for (int i = 0; i < n; i++)
			tiempos.add(paso * (i + 1));
	}

	public static int getTiempoLimite() {
		return 1000 * 60 * minutos;
	}

	private static String calculaHoraFecha() {
		Date date = new Date();
		DateFormat hourdateFormat = new SimpleDateFormat("HH.mm.ss_dd.MM.yyyy");
		return hourdateFormat.format(date);
	}

	// RULES FOR ENSEMBLES
	public static void loadRules() {
		Scanner kbd;
		rulesForBuildingEnsembles = new ArrayList<Regla>();
		try {
			kbd = new Scanner(new File(set_rules + ".csv"));
			String s;
			Regla rule;
			while (kbd.hasNext()) {
				s = kbd.nextLine();
				rule = new Regla().loadRule(s);
				rulesForBuildingEnsembles.add(rule);
			}
			kbd.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// CONJUNTO DE ENTRENAMIENTO Y TEST
	public static void loadInstances() {
		set_test = InstancesManager.loadSchneiderInstances("SchneiderInstancesTest/");
		set_train = InstancesManager.loadSchneiderInstances(directory_training_set);
	}

	public static void setFilterSet(int n) {
		// preliminary
		setFilterSetFirstNfromTraining(n);
	}

	static void setFilterSetFirstNfromTraining(int n) {
		set_filter = new ArrayList<Instance>();
		for (int i = 0; i < n; i++)
			set_filter.add(set_train.get(i));
//		for (int i = 0; i < n; i++)
//			set_train.remove(i);
	}

	// LISTAR LOS SIMBOLOS DISPONIBLES
	public static Nodo getTerminalBinaryFunctionRandom() {
		int N = (int) (Math.random() * (getNTerminalBinaryFunctionRandom()));
		if (N < getBinaryFunction().size())
			return getBinaryFunctionRandom();
		else
			return getTerminalRandom();
	}

	public static int getNTerminalBinaryFunctionRandom() {
		return getTerminales().size() + getBinaryFunction().size();
	}

	public static Nodo getTerminalRandom() {
		int random = (int) (Math.random() * (getTerminales().size()));
		return identificadorTerminal(getTerminales().get(random).toString());
	}

	public static Nodo getBinaryFunctionRandom() {
		int random = (int) (Math.random() * (getBinaryFunction().size()));
		return identificadorBinaryFunction(getBinaryFunction().get(random).toString());
	}

	static List<String> terminales;

	public static int getNterminales() {
		return getTerminales().size();
	}

	static List<String> getTerminales() {
		if (terminales == null) {
			terminales = new ArrayList<String>();
			// capacity / demand
			terminales.add("D");
			// TW
			terminales.add("DD");
			terminales.add("RT");
			terminales.add("ST");
			// energy
			terminales.add("E");
			terminales.add("Ec");
			terminales.add("ERP");
			terminales.add("EDep");
			terminales.add("ERP_a");
			terminales.add("EDep_a");
			// vehicle
			terminales.add("VC");
			terminales.add("VE");
			terminales.add("VT");
		}
		return terminales;
	}

	static List<String> binaryFunctions;

	private static List<String> getBinaryFunction() {
		if (binaryFunctions == null) {
			binaryFunctions = new ArrayList<String>();
			binaryFunctions.add("+");
			binaryFunctions.add("-");
			binaryFunctions.add("*");
			binaryFunctions.add("/");
			binaryFunctions.add("Math.min");
			binaryFunctions.add("Math.max");
		}
		return binaryFunctions;
	}

	static Nodo identificadorTerminal(String terminal) {
		Nodo nodo = null;
		switch (terminal) {
		case "E":
			nodo = NodoT01_Energy.getInstance();
			break;
		case "D":
			nodo = NodoT02_Demand.getInstance();
			break;
		case "VC":
			nodo = NodoT06_capacityVehicle.getInstance();
			break;
		case "Ec":
			nodo = NodoT07_energyToCentroid.getInstance();
			break;
		case "DD":
			nodo = NodoT08_dueDate.getInstance();
			break;
		case "ST":
			nodo = NodoT09_serviceTime.getInstance();
			break;
		case "RT":
			nodo = NodoT10_readyTime.getInstance();
			break;
		case "ERP":
			nodo = NodoT11_energyToRP.getInstance();
			break;
		case "EDep":
			nodo = NodoT12_energyToDepot.getInstance();
			break;
		case "ERP_a":
			nodo = NodoT13_energyToRP_a.getInstance();
			break;
		case "EDep_a":
			nodo = NodoT14_energyToDepot_a.getInstance();
			break;
		case "VT":
			nodo = NodoT15_timeVehicle.getInstance();
			break;
		case "VE":
			nodo = NodoT16_energyVehicle.getInstance();
			break;
		default:
			break;
		}
		return nodo;
	}

	static Nodo identificadorBinaryFunction(String binaryFunction) {
		Nodo nodo = null;
		switch (binaryFunction) {
		case "+":
			nodo = new NodoSuma();
			break;
		case "-":
			nodo = new NodoResta();
			break;
		case "Math.max":
			nodo = new NodoMax();
			break;
		case "Math.min":
			nodo = new NodoMin();
			break;
		case "/":
			nodo = new NodoDivision();
			break;
		case "*":
			nodo = new NodoMultiplicacion();
			break;
		default:
			break;
		}
		return nodo;
	}

	public static void end() {
		close();
	}

	public static String getInfoMode() {
		switch (mode) {
		case 0:
			return "serial_softTW";
		case 1:
			return "serial_hardTW";
		case 2:
			return "semiparallel_softTW";
		case 3:
			return "semiparallel_hardTW";
		case 4:
			return "parallel_softTW";
		case 5:
			return "parallel_hardTW";
		}
		return "not valid mode";
	}

	public static int getMode(String mode) {
		switch (mode) {
		case "serial_softTW":
			return 0;
		case "serial_hardTW":
			return 1;
		case "semiparallel_softTW":
			return 2;
		case "semiparallel_hardTW":
			return 3;
		case "parallel_softTW":
			return 4;
		case "parallel_hardTW":
			return 5;
		}
		return -1;
	}

	public static String getCriterio() {
		switch (criterio) {
		case 0:
			return "vehicles";
		case 1:
			return "energy";
		case 2:
			return "tardiness";
		case 3:
			return "lext1";
		}
		return "not valid criterio";
	}

	public static int getCriterio(String criterio) {
		switch (criterio) {
		case "vehicles":
			return 0;
		case "energy":
			return 1;
		case "tardiness":
			return 2;
		case "lext1":
			return 3;
		}
		return -1;
	}
	
	// threshold 
	static double epsilon = 0.000001d;
	public static boolean historial= true;
	public static boolean unfesiable = false;

	public static boolean equal(double d1, double d2) {
		return Math.abs(d1 - d2) < epsilon;
	}
	
	public static boolean equalZero(double d1) {
		return equal(d1, 0.0);
	}
}