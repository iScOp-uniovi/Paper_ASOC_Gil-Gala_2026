package evrp_0.domain;

import java.util.ArrayList;
import java.util.List;

public class DomainInfo {
	
		
	// Info about the actual state of the domain
	public Instance instance;
	public List<Vehicle> vehicles = new ArrayList<Vehicle>();
	public List<Point> noVisitedCustomers;
	public List<PointVisited> tour = new ArrayList<PointVisited>();
	public Point selected;
	public Point previousVisited;
	public Vehicle active;
	public double totalComputedEnergy;
	public double totalTardiness;
	public List<Double> minDemand; 	// because active vehicle could be no avariable for any customer
	public double demandSum;
	public int activeId;  
	
	// centroid
	public Point centroid; // it is calculated without consider the candidate customer to visit
	public double xSum;
	public double ySum;
	
	public DomainInfo(Instance instance) {
		this.instance = instance;
		tour.clear();
		vehicles.clear();
		totalComputedEnergy = 0;
		totalTardiness = 0;
		xSum = instance.xSum;
		ySum = instance.ySum;
		noVisitedCustomers = new ArrayList<Point>(instance.customers);
		minDemand = new ArrayList<Double>(instance.sortedDemand);
		demandSum = instance.weightSum;
	}

}
