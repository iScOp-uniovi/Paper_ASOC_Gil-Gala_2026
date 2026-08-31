package evrp_0.domain;

import java.util.ArrayList;
import java.util.List;

import evrp_0.algorithms.rules.Regla;
import evrp_0.util.AlgoUtil;

public abstract class SolvingMethod {

	public DomainInfo domain;
	protected List<Point> clientesVisitados;
	public boolean solucionCalculadaNoFactible;

	public Solution solve(Instance instance) { // it is the schedule builder. The used rule only picks the next customer
												// to visit
		domain = new DomainInfo(instance);
		if (AlgoUtil.methodComputeRPTour != 0 && AlgoUtil.mode % 2 == 0) {
//			System.out.println("AlgoUtil.methodComputeRPTour = 0 because only this mode is supported by soft-TW");
			AlgoUtil.methodComputeRPTour = 0;
		} else if (AlgoUtil.methodComputeRPTour != 1 && AlgoUtil.mode % 2 != 0) {
//			System.out.println("AlgoUtil.methodComputeRPTour = 1 because only this mode is supported by hard-TW");
			AlgoUtil.methodComputeRPTour = 1;
		}
		return serial_hard_time_windows(instance);
	}

	public Solution serial_hard_time_windows(Instance instance) {
		// initial data
		clientesVisitados = new ArrayList<>(); // por la LSA

		Route routeToNextCustomer;
		Route routeToDepot;
		Vehicle activeBeforeVisitSelected;
		Vehicle activeAfterVisitSelected;
		// generate initial active vehicle
		setNewVehicleActive();
		while (!domain.noVisitedCustomers.isEmpty()) {
			domain.selected = getNext();
			if (domain.selected != null && domain.active.capacityFor(domain.selected)) { // has enough capacity to serve
																							// the selected customer
				// if the time-windows are hard contraints, then it needs to save the state of
				// the active vehicle
				activeBeforeVisitSelected = domain.active.getClon();
				routeToNextCustomer = domain.active.goTo(domain.previousVisited, domain.selected);
				if (routeToNextCustomer == null || !AlgoUtil.equalZero(routeToNextCustomer.tardinessProduced)) {
					domain.active = activeBeforeVisitSelected.getClon();
					discardVehicleSerial(instance);
				} else {
					// also, we need to validate if the vehicle could go to the depot from the
					// selected to the depot without produce tardiness
					activeAfterVisitSelected = domain.active.getClon();
					routeToDepot = activeAfterVisitSelected.goToDepot(); // we simulate go to the depot
					if (routeToDepot == null || !AlgoUtil.equalZero(routeToDepot.tardinessProduced)) {
						domain.active = activeBeforeVisitSelected.getClon();
						tryToComputeAfeasibleRouteSerial(instance, activeBeforeVisitSelected);
					} else {
						clientesVisitados.add(domain.selected);
						setVisitedCustomer(routeToNextCustomer);
					}
				}
			} else { // if it doesn't have enough capacity for the selected customer, then we discard
						// this vehicle a used a new one
				discardVehicleSerial(instance);
			}
		}
		goToDepot();
		return new Solution(instance, this, domain);
	}

	protected void tryToComputeAfeasibleRouteSerial(Instance instance, Vehicle activeBeforeVisitSelected) {
		if (!computeFeasibleRoute()) { // if this line is commented then if it doesnt use the last strategy
			domain.active = activeBeforeVisitSelected.getClon();
			discardVehicleSerial(instance);
		}
	}

	protected void discardVehicleSerial(Instance instance) {
		domain.vehicles.set(domain.activeId, domain.active); // update in the list the object!
		setNewVehicleActive();
	}

	protected void setNewVehicleActive() {
		if (domain.active != null) { // the previous active vehicle should go to the deposit
			goToDepot();
		}
		domain.active = new Vehicle(domain.instance, domain.tour);
		domain.vehicles.add(domain.active);
		domain.previousVisited = domain.active.position();
		domain.activeId = domain.vehicles.size() - 1;
	}

	protected void setVisitedCustomer(Route routeToNextCustomer) {
		domain.vehicles.set(domain.activeId, domain.active); // update in the list the object!
		domain.noVisitedCustomers.remove(domain.selected);
		updateTerminalsInformation(); // update terminals information
		domain.previousVisited = domain.selected;
		compute(routeToNextCustomer);
	}

	private boolean computeFeasibleRoute() {
		int aux = AlgoUtil.methodComputeRPTour;
		AlgoUtil.methodComputeRPTour = 3;
		Route routeToNextCustomer = domain.active.getComputeRPTour(domain.previousVisited, domain.selected);
		AlgoUtil.methodComputeRPTour = aux;
		if (routeToNextCustomer != null && AlgoUtil.equalZero(routeToNextCustomer.tardinessProduced)) {
			Vehicle activeAfterVisitSelected = domain.active.getClon();
			Route routeToDepot = activeAfterVisitSelected.goToDepot(); // we simulate go to the depot
			if (routeToDepot != null && AlgoUtil.equalZero(routeToDepot.tardinessProduced)) {
				setVisitedCustomer(routeToNextCustomer);
				return true;
			}
			return false;
		} else {
			return false;
		}
	}

	void updateTerminalsInformation() {
		if (domain.selected.isCustomer && Double.compare(domain.selected.demand, domain.minDemand.get(0)) == 0)
			domain.minDemand.remove(0); // update min weigh
		domain.demandSum -= domain.selected.demand;
		// for centroid
		domain.xSum -= domain.selected.x;
		domain.ySum -= domain.selected.y;
	}

	protected void goToDepot() {
		if (domain.active.position().isDepot) { // if it is already in the depot it is discarted
			domain.active.done = true;
		} else {
			compute(domain.active.goToDepot());
		}
	}

	void compute(Route route) {
		domain.totalComputedEnergy += route.energyUsed;
		domain.totalTardiness += route.tardinessProduced;
		route.processRoute(domain.tour);
		if (!route.isValid)
			solucionCalculadaNoFactible = true;
	}

	public abstract Point getNext();
}
