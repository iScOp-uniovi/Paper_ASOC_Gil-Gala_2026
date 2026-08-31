package evrp_0.domain;

import java.util.ArrayList;
import java.util.List;

import evrp_0.util.AlgoUtil;

public class Vehicle {

	public int id;
	public Instance instance;
	public Point initialPoint; // for the place to start
	public double remainningEnergy; // fuel tank capacity
	public double remainingCapacity; // load capacity
	public List<PointVisited> tour; // the visited points from this vehicle
	public double totalEnergyUsed;
	public double totalTardiness;
	public double time;
	boolean done;
	public double minEnergyThreshold;
	public double load;
	
	// static id
	private static int idC = 0;
	
	public Vehicle(Vehicle vehicle) {
		setClon(vehicle);
	}
	
	public Vehicle(Instance instance, List<PointVisited> generalTour) {
		this.remainingCapacity = instance.C;
		this.remainningEnergy = instance.Q;
		this.time = instance.initialTime;
		this.instance = instance;
		this.initialPoint = instance.depot;
		tour = new ArrayList<PointVisited>();
		tour.add(new PointVisited(initialPoint, time));
		generalTour.add(new PointVisited(initialPoint, time)); 
		idC++;
		this.id = idC;
		this.done = false;
	}

	public Vehicle() {
		// TODO Auto-generated constructor stub
	}

	public double computeTime(Point p1, Point p2) {
		return instance.computeTime(p1, p2, time);
	}

	public double computeEnergy(Point p1, Point p2) {
		return instance.computeEnergy(p1, p2);
	}

	public boolean isAvailable(double minDemandNeed) {
		return remainingCapacity >= minDemandNeed;
	}
	
	public boolean energyFor(Point target) {
		return energyNumber(position(), target) <= remainningEnergy;
	}
	
	public boolean energyFor(Point current, Point target) {
		return energyNumber(current, target) <= remainningEnergy;
	}

	public double energyNumber(Point current, Point target) {
		double energy = computeEnergy(current, target);
		if (target.isCustomer) 
			energy += computeEnergy(target, target.nearestRechargingPoint); // nearest recharging point
		return energy;
	}

	public boolean capacityFor(Point target) {
		return target.demand <= remainingCapacity;
	}
	
	public Route goToDepot() {
		return goToDepot(position());
	}

	public Route goToDepot(Point previousVisited) {
		done = true;
		if (AlgoUtil.unfesiable) {
			Route route = goToDirectly(previousVisited, initialPoint);
			if (computeEnergy(previousVisited, initialPoint) > remainningEnergy) {
				route.isValid = false;
			}
			return route;
		}
		if (energyFor(previousVisited, initialPoint)) { // it can go directly (considering that it also has, at least, enough energy to go to nearest recharging point
//			System.out.println("direclty");
			return goToDirectly(previousVisited, initialPoint);
		} else { // it needs to use some recharging points to go to deposit
//			System.out.println("compute route using RPs");
			return getComputeRPTour(previousVisited, initialPoint);
		}
	}
	
	public Route goTo(Point previousVisited, Point target) {
		if (AlgoUtil.unfesiable) {
			Route route = goToDirectly(previousVisited, target);
			if (computeEnergy(previousVisited, target) > remainningEnergy) {
				route.isValid = false;
			}
			return route;
		}
		if (energyFor(previousVisited, target)) { // it can go directly (considering that it also has, at least, enough energy to go to nearest recharging point
			return goToDirectly(previousVisited, target);
		} else { // it needs to use some recharging points to go to the target
			return getComputeRPTour(previousVisited, target);
		}
	}
	
	private Route goToDirectly(Point target) {
		return goToDirectly(tour.get(tour.size()-1).point, target);
	}

	private Route goToDirectly(Point current, Point target) {
		Route route = new Route();
		remainingCapacity -= target.demand;
		time += computeTime(current, target);
		route.energyUsed = computeEnergy(current, target);
//		if (!target.isRecharging) // if recharging points don't produce tardiness
//		if (!target.isDepot) // if depots don't produce tardiness
		route.tardinessProduced = Math.max(0.0, time - target.dueDate);
		// update other things
		totalEnergyUsed += route.energyUsed;
		totalTardiness += route.tardinessProduced;
		remainningEnergy -= route.energyUsed;
		// now is included the time to service the customer, not before to compute the tardiness...
		time += target.serviceTime; 
		// also is here when refueling
		if (target.isRecharging)
			refueling();
		tour.add(new PointVisited(target, time));
		route.addPoint(new PointVisited(target, time));
		return route;
	}

	public Route getComputeRPTour(Point current, Point target) {
		switch (AlgoUtil.methodComputeRPTour) {
		case 0:
//			System.out.println("case 0");
			return getComputeRPTour_nearestRPtoTargetSoftTW(current, target);
		case 1:
//			System.out.println("case 1");
			return getComputeRPTour_minEnergyHardTW(current, target);
		case 2:
//			System.out.println("case 2");
			return getComputeRPTour_firstHardTW(current, target);
		case 3:
//			System.out.println("case 3");
			return getComputeRPTour_nearestRPtoTargetAtLeastOneRPHardTW(current, target);
		case 4:
//			System.out.println("case 1");
			return getComputeRPTour_minEnergySoftTW(current, target);	
		default:
			return null;
		} 
	}

	// ALWAYS EXECUTED WITH SOFT TW
	private Route getComputeRPTour_nearestRPtoTargetSoftTW(Point current, Point target) {
		Route route = new Route();
		Route partialRoute;
		Point p = current;
		double energyToTarget, minEnergy, energyFromCurrent; // aux
		Point p1; // aux
		while (!energyFor(p, target)) {
			// search the nearest recharging point to the target point
			minEnergy = Double.MAX_VALUE;
			p1 = null;
			for (Point p2 : instance.rechargings) {
				energyToTarget = energyNumber(p2, target);
				energyFromCurrent = energyNumber(p, p2);
				if (energyFromCurrent <= remainningEnergy && energyToTarget < minEnergy) { // it is not valid for hard-TW
					p1 = p2;
					minEnergy = energyToTarget;
				}
			}
			partialRoute = goToDirectly(p, p1);
			route.energyUsed += partialRoute.energyUsed;
			route.tardinessProduced += partialRoute.tardinessProduced;
			route.addPoint(partialRoute.route.get(0));
			p = p1;
		}
		partialRoute = goToDirectly(p, target);
		route.energyUsed += partialRoute.energyUsed;
		route.tardinessProduced += partialRoute.tardinessProduced;
		route.addPoint(partialRoute.route.get(0));
		return route;
	}
	
	public Route getComputeRPTour_minEnergySoftTW(Point current, Point target) {
		if (instance.routesRP == null) { 
			instance.routesRP();
		}			
		Vehicle copy = null;
		Route route = null;
		Vehicle vBest = null;
		Route rBest = null;
		double min = Double.MAX_VALUE;	
		boolean valid = true;
		for (List<Point> tour : instance.routesRP) { // iterates over the routes ---> how they should be ordered????
			copy = getClon();
			route = new Route();
			Route partialRoute;
			valid = true;
			if (!tour.get(0).equals(current) && !tour.get(0).equals(target)) { // to avoid repeat with the depot 
				for (Point p : tour) {
					if (!copy.energyFor(copy.position(), p)) {
						valid = false;
						break; // comment to validate all posible combination are generated
					} 
					partialRoute = copy.goToDirectly(p);
					route.energyUsed += partialRoute.energyUsed;
					route.addPoint(partialRoute.route.get(0));
				}	
				if (valid) {
					if (!copy.position().equals(target)) {
						if (copy.energyFor(copy.position(), target)) { // comment to validate all posible combination are generated
							partialRoute = copy.goToDirectly(target);
							route.energyUsed += partialRoute.energyUsed;
							route.addPoint(partialRoute.route.get(0));
							if (route.energyUsed < min) { // the min energy
								min = route.energyUsed;
								vBest = copy;
								rBest = route;
							}
						}
					}
				} 
			}
		}
		if (vBest == null) return null;
		setClon(vBest);
		return rBest;
	}
	
	// ALWAYS USED WITH HARD TIME
	private Route getComputeRPTour_minEnergyHardTW(Point current, Point target) {
		if (instance.routesRP == null) { 
			instance.routesRP();
		}			
		Vehicle copy = null;
		Route route = null;
		Vehicle vBest = null;
		Route rBest = null;
		double min = Double.MAX_VALUE;	
		boolean valid = true;
		for (List<Point> tour : instance.routesRP) { // iterates over the routes ---> how they should be ordered????
			copy = getClon();
			route = new Route();
			Route partialRoute;
			valid = true;
			if (!tour.get(0).equals(current) && !tour.get(0).equals(target)) { // to avoid repeat with the depot 
				for (Point p : tour) {
					if (!copy.energyFor(copy.position(), p) || !AlgoUtil.equalZero(route.tardinessProduced)) {
						valid = false;
						break; // comment to validate all posible combination are generated
					} 
					partialRoute = copy.goToDirectly(p);
					route.energyUsed += partialRoute.energyUsed;
					route.tardinessProduced += partialRoute.tardinessProduced;
					route.addPoint(partialRoute.route.get(0));
				}	
				if (valid) {
					if (!copy.position().equals(target)) {
						if (copy.energyFor(copy.position(), target)) { // comment to validate all posible combination are generated
							partialRoute = copy.goToDirectly(target);
							route.energyUsed += partialRoute.energyUsed;
							route.tardinessProduced += partialRoute.tardinessProduced; 
							route.addPoint(partialRoute.route.get(0));
							if (route.energyUsed < min && AlgoUtil.equalZero(route.tardinessProduced)) { // the min energy
								min = route.energyUsed;
								vBest = copy;
								rBest = route;
							}
						}
						// to validate all posible combination are generated
//						System.out.println("1 "+valid+" "+route.getRoute()+" "+route.tardinessProduced);
					}
				} 
//				else {
////					// to validate all posible combination are generated
//					if (!copy.position().equals(target) && !copy.position().equals(current)) {
//						partialRoute = copy.goToDirectly(target);
//						route.energyUsed += partialRoute.energyUsed;
//						route.tardinessProduced += partialRoute.tardinessProduced; 
//						route.addPoint(partialRoute.route.get(0));
//						System.out.println("2 "+valid+" "+route.getRoute()+" "+route.tardinessProduced);
//					}
//				}
			}
		}
		if (vBest == null) return null;
		setClon(vBest);
		return rBest;
	}
	
	// NO USED. I THINK IT DOES NOT WORK I HAVE TO VERIFY!
	private Route getComputeRPTour_firstHardTW(Point current, Point target) {
		if (instance.routesRP == null) { 
			instance.routesRP();
		}			
		Vehicle copy = null;
		Route route = null;
		boolean valid = true;
		for (List<Point> tour : instance.routesRP) { // iterates over the routes ---> how they should be ordered????
			copy = getClon();
			route = new Route();
			Route partialRoute;
			valid = true;
			if (!tour.get(0).equals(current) && !tour.get(0).equals(target)) { // to avoid repeat with the depot 
				for (Point p : tour) {
					if (!copy.energyFor(copy.position(), p) || !AlgoUtil.equalZero(route.tardinessProduced)) {
						valid = false;
						break; // comment to validate all posible combination are generated
					} 
					partialRoute = copy.goToDirectly(p);
					route.energyUsed += partialRoute.energyUsed;
					route.tardinessProduced += partialRoute.tardinessProduced;
					route.addPoint(partialRoute.route.get(0));
				}	
				if (valid) {
					if (!copy.position().equals(target)) {
						if (copy.energyFor(copy.position(), target)) { 
							partialRoute = copy.goToDirectly(target);
							route.energyUsed += partialRoute.energyUsed;
							route.tardinessProduced += partialRoute.tardinessProduced; 
							route.addPoint(partialRoute.route.get(0));
							if (AlgoUtil.equalZero(route.tardinessProduced)) { // the first
								setClon(copy);
								return route;
							}
						}
					}
				} 
			}
		}
		return null;
	}
	
	// IT IS USED TO FORCE USE AT LEAST A CHARGING STATION 
	private Route getComputeRPTour_nearestRPtoTargetAtLeastOneRPHardTW(Point current, Point target) {
		Route route = new Route();
		Route partialRoute;
		double energyToTarget, minEnergy, energyFromCurrent; // aux
		Point p1; // aux
		Vehicle copy;
		// search the nearest recharging point to the target point
		minEnergy = Double.MAX_VALUE;
		p1 = null;
		for (Point p2 : instance.rechargings) {
			copy = getClon();
			partialRoute = copy.goToDirectly(p2);
			energyToTarget = energyNumber(p2, target);
			energyFromCurrent = energyNumber(current, p2);
			if (energyFromCurrent <= remainningEnergy 
					&& energyToTarget < minEnergy 
					&& AlgoUtil.equalZero(partialRoute.tardinessProduced)) { // hard TW
				p1 = p2;
				minEnergy = energyToTarget;
			}
		}
		if (p1 == null)
			return null;
		partialRoute = goToDirectly(current, p1);
		route.energyUsed += partialRoute.energyUsed;
		route.tardinessProduced += partialRoute.tardinessProduced;
		route.addPoint(partialRoute.route.get(0));
		partialRoute = goToDirectly(p1, target);
		route.energyUsed += partialRoute.energyUsed;
		route.tardinessProduced += partialRoute.tardinessProduced;
		route.addPoint(partialRoute.route.get(0));
		return route;
	}

	private void refueling() {
		double energyToFull = instance.Q - remainningEnergy;
		remainningEnergy = instance.Q; // energy is recharging
		time += energyToFull * instance.g; // time wasted for recharging energy
	}
	
	/**
	 * Return the last visited point (the customer)
	 */
	public Point position() {
		return tour.get(tour.size() - 1).point;
	}
	
	@Override
	public boolean equals(Object obj) {
		Vehicle copy = (Vehicle) obj;
		return copy.id == id;
	}

	public Vehicle getClon() {
		return new Vehicle().setClon(this);
	}
	
	private Vehicle setClon(Vehicle clon) {
		this.done = clon.done;
		this.remainningEnergy = clon.remainningEnergy;
		this.remainingCapacity = clon.remainingCapacity;
		this.time = clon.time;
		this.initialPoint = clon.initialPoint;
		this.instance = clon.instance;
		this.tour = new ArrayList<PointVisited>();
		for (PointVisited point : clon.tour) // they can be the same object, it is not necessary to clon
			this.tour.add(point);
		this.totalEnergyUsed = clon.totalEnergyUsed;
		this.totalTardiness = clon.totalTardiness;
		this.id = clon.id;
		return this;
	}

	
	// nuevo
	public double getInitialEnergy() {
		return instance.Q;
	}	
}
