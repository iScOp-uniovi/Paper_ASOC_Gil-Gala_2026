package evrp_0.domain;

import java.util.ArrayList;
import java.util.List;

import evrp_0.util.AlgoUtil;

public abstract class Instance implements Comparable<Instance> {

	// The data set
	public String name;
	public List<Point> points;
	public List<Point> customers;
	public List<Point> rechargings;
	public double[][] distance;
	public double totalDistance;
	public List<Double> sortedDemand;
	public double weightSum;
	public double totalDD;
	public double totalRT;

	// centroid
	public double xSum;
	public double ySum;

	// New vehicles will be generated according these values
	public double Q; // fuel tank capacity
	public double C; // load capacity
	public double r; // fuel consumption rate
	public double g; // inverse refueling rate
	public double v; // average velocity
	public double initialTime = 0; // always 0.0
	public Point depot;

	public Instance(String fullName) {
		points = new ArrayList<Point>();
		customers = new ArrayList<Point>();
		rechargings = new ArrayList<Point>();
		sortedDemand = new ArrayList<Double>();
		name = fullName;
		load(fullName);
	}

	public abstract void load(String file);

	public abstract double computeTime(Point p1, Point p2, double actualTime);

	public abstract double computeEnergy(Point p1, Point p2);

	public abstract void computePoints();

	public double distance(Point p1, Point p2) {
		return distance[p1.id][p2.id];
	}

	@Override
	public boolean equals(Object obj) {
		return name.equals(((Instance) obj).name);
	}

	@Override
	public int compareTo(Instance o) {
		return new Integer(customers.size()).compareTo(o.customers.size());
	}

	public int lowerNumberOfVehicles() {
		return (int) Math.ceil(weightSum / C);
	}

	public double lowerCapacity() {
		return lowerNumberOfVehicles() * C;
	}

	List<List<Point>> routesRP;

	public void routesRP() { // if there are more than 2 level instances I will make this more elegant the code...
		if (routesRP == null) {
			routesRP = new ArrayList<List<Point>>();
			// LEVEL 1
			for (Point p1 : rechargings) {
				List<Point> state = new ArrayList<Point>();
				state.add(p1);
				routesRP.add(state);
			}
			// LEVEL 2
			for (Point p1 : rechargings) {
				for (Point p2 : rechargings) {
					if (!p1.equals(p2)) { 
						List<Point> state = new ArrayList<Point>();
						state.add(p1);
						state.add(p2);
						routesRP.add(state);
					}
				}
			}
			// LEVEL 3
//			for (Point p1 : rechargings) {
//					for (Point p2 : rechargings) 
//						for (Point p3 : rechargings) {
//							if (!p1.equals(p2) && !p1.equals(p3) && !p2.equals(p3)) { 
//								List<Point> state = new ArrayList<Point>();
//								state.add(p1);
//								state.add(p2);
//								state.add(p3);
//								routesRP.add(state);
//							}
//					}
//			}
		}
	}

	public String getName() {
		return name.replace(".txt", "").replace(AlgoUtil.directory_training_set,"").replace("SchneiderInstancesTest/","");
	}
}
