package evrp_0.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import evrp_0.domain.Instance;
import evrp_0.domain.SchneiderInstance;
import evrp_0.domain.Solution;

public class InstancesManager {
	
	
	public static List<Instance> loadSchneiderInstances(String path) {
		return loadSchneiderInstances(loadNamesFrom(path), path);
	}
		
	private static List<Instance> loadSchneiderInstances(List<String> names, String path) {
		List<Instance> set = new ArrayList<Instance>();
		for (String name : names) {
			set.add(new SchneiderInstance(path + name));
		}
		return set;
	}

	// load names in a directory
	private static List<String> loadNamesFrom(String path) {
//		System.out.println("-----");
		List<String> names = new ArrayList<String>();
		File file = new File(path);
		if (file.isDirectory()) {
			File[] ficheros = file.listFiles();
			for (File fichero : ficheros) {
				names.add(fichero.getName());
//				System.out.println(fichero.getName());
			}
		}
		return names;
	}
	
	// save solutions
	public static void save(List<Solution> solutions, String fileName) {
		String file ="Instance;NºVehicles;Energy;Tardiness;Route;\n";
		for (Solution solution : solutions)
			file += solution.getPrintable();
		BufferedWriter bw;
		try {
			bw = new BufferedWriter(new FileWriter(new File(fileName + ".csv")));	
			bw.write(file);
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
