package evrp_0.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import evrp_0.util.AlgoUtil;

public class Solution {

	public Instance instance;
	SolvingMethod method;
	public List<Vehicle> vehicles;
	List<PointVisited> tour;
	int totalVehicles;
	public double totalEnergy;
	double totalTardiness;
	public double totalDistance;
	public boolean factible;

	public Solution(Instance instance, SolvingMethod method, DomainInfo domain) {
		this.instance = instance;
		this.method = method;
		this.vehicles = new ArrayList<Vehicle>(domain.vehicles);
		this.totalVehicles = vehicles.size();
		this.tour = new ArrayList<PointVisited>();
		for (PointVisited point : domain.tour)
			this.tour.add(point);
		this.totalEnergy = domain.totalComputedEnergy;
		this.totalTardiness = domain.totalTardiness;
		if (method.solucionCalculadaNoFactible)
			factible = false;
		else
			factible = true;
	}

	public Solution(Instance instance, SolvingMethod method) {
		this.instance = instance;
		this.method = method;
		this.vehicles = new ArrayList<Vehicle>();
		this.tour = new ArrayList<PointVisited>();
		this.totalVehicles = Integer.MAX_VALUE;
		this.totalEnergy = Double.MAX_VALUE;
		this.totalTardiness = Double.MAX_VALUE;
	}

	public Solution(Solution originalSolution) {
		this.instance = originalSolution.instance;
		this.method = originalSolution.method;
		this.vehicles = new ArrayList<>(originalSolution.vehicles.size());
		for (Vehicle vehicle : originalSolution.vehicles) {
			this.vehicles.add(new Vehicle(vehicle));
		}
		this.totalVehicles = originalSolution.totalVehicles;
		this.tour = new ArrayList<>(originalSolution.tour.size());
		for (PointVisited pointVisited : originalSolution.tour) {
			this.tour.add(new PointVisited(pointVisited));
		}
		this.totalEnergy = originalSolution.totalEnergy;
		this.totalTardiness = originalSolution.totalTardiness;
	}

	public double getTotalDistance() {
		totalDistance = 0;
		for (int i = 1; i < tour.size(); i++) {
			totalDistance += tour.get(i - 1).point.distance(tour.get(i).point);
		}
		return totalDistance;
	}

	public double getTotalVehicles() {
		return totalVehicles;
	}

	public double getTotalEnergy() {
		return totalEnergy;
	}

	public double getTotalTardiness() {
		return totalTardiness;
	}

	public void printSolution() {
		System.out.println(getPrintable());
	}

	public String getPrintable() {
		return instance.name + ";" + vehicles.size() + ";" + totalEnergy + ";" + totalTardiness + ";"
				+ getTourPerVehicle() + ";";
	}

	public String getTourPerVehicle() {
		String tour_s = "";
		for (Vehicle v : vehicles) {
			tour_s += "{";
			Point p;
			for (int i = 0; i < v.tour.size(); i++) {
				p = v.tour.get(i).point;
				tour_s += "(" + p.id + "," + p.isCustomer + ")";
			}
			tour_s += "}";
		}
		return tour_s;
	}
	
	
	/*
	 * 
	 * DESDE AQUI ES EXPERIMENTAL TODO
	 * 
	 */
	

	/**
	 * De momento solo para soft TW
	 */
	public boolean validSolution() {
		// Verificar cada vehículo en la solución
		for (Vehicle v : vehicles) {
			double currentEnergy = instance.Q; // Comienza con la capacidad completa de combustible
			double currentLoad = 0; // La carga empieza vacía
			double currentTime = 0; // Tiempo actual en el vehículo
			double totalTardiness = 0; // Total de tardanza (si ocurre)

			List<PointVisited> route = v.tour;

			// Recorre cada punto en la ruta del vehículo
			for (int i = 1; i < route.size(); i++) {
				PointVisited currentPointVisited = route.get(i);
				PointVisited prevPointVisited = route.get(i - 1);

				Point p = currentPointVisited.point;
				Point prev = prevPointVisited.point;

				// Calcular el tiempo necesario para viajar de un punto a otro
				double travelTime = instance.computeTime(prev, p, currentTime);
				currentTime += travelTime; // Aumentar el tiempo de llegada al siguiente punto

				// Verificar si el vehículo llega al cliente dentro de su ventana de tiempo
				if (p.isCustomer) {
					if (currentTime < p.readyTime) {
						currentTime = p.readyTime; // El vehículo debe esperar si llega antes del readyTime
					} else if (currentTime > p.dueDate) {
						// Si llega después del dueDate, generamos tardanza
						totalTardiness += currentTime - p.dueDate;
						if (AlgoUtil.mode == 1)
							return false; // Si se genera tardanza, la solución no es válida
					}
				}

				// Calcular la energía utilizada para llegar de un punto a otro
				double energyConsumed = instance.computeEnergy(prev, p);
				currentEnergy -= energyConsumed; // Reducir la energía del vehículo

				// Verificar si el vehículo tiene suficiente energía para continuar
				if (currentEnergy < 0) {
					return false; // Si no tiene suficiente energía, la solución no es válida
				}

				// Si el punto actual es un cliente, aumentar la carga del vehículo
				if (p.isCustomer) {
					currentLoad += p.demand;
					if (currentLoad > instance.C) {
						return false; // Si la carga supera la capacidad del vehículo, la solución no es válida
					}
				}
				// Si el punto actual es una estación de recarga, el vehículo recarga su energía
				if (p.isRecharging) {
					currentEnergy = instance.Q; // Recupera toda la energía en la estación de recarga
				}
			}
		}
		return true; // Si todas las rutas son válidas, la solución es válida
	}


	// aqui se pueden introducir las busquedas locales para trabajar directamente
	// con la solución.
	private void reverseSegment(List<PointVisited> route, int i, int j) {
		while (i < j) {
			Collections.swap(route, i, j);
			i++;
			j--;
		}
	}

	private double routeEnergy(List<PointVisited> route) {
		double total = 0.0;
		for (int i = 1; i < route.size(); i++) {
			total += instance.computeEnergy(route.get(i - 1).point, route.get(i).point);
		}
		return total;
	}

	/**
	 * Método de búsqueda local que aplica el algoritmo 2-opt a una única ruta de un
	 * vehículo. Consiste en invertir segmentos de la ruta para reducir la energía
	 * total del trayecto.
	 * 
	 * @return Solution con una nueva ruta optimizada si hubo mejora, de lo
	 *         contrario retorna la solución original.
	 */
	public Solution twoOptIntraRoute() {
	    Solution improved = new Solution(this);
	    boolean changed = false;
	    for (Vehicle v : improved.vehicles) {
	        List<PointVisited> route = v.tour;
	        boolean localImproved = true;
	        while (localImproved) {
	            localImproved = false;
	            for (int i = 1; i < route.size() - 2; i++) {
	                for (int j = i + 1; j < route.size() - 1; j++) {
	                    Point A = route.get(i - 1).point;
	                    Point B = route.get(i).point;
	                    Point C = route.get(j).point;
	                    Point D = route.get(j + 1).point;
	                    double currE = instance.computeEnergy(A, B) + instance.computeEnergy(C, D);
	                    double newE = instance.computeEnergy(A, C) + instance.computeEnergy(B, D);
	                    if (newE < currE) {
	                        reverseSegment(route, i, j);
	                        if (!improved.validSolution()) { // Verifica si la solución es válida
	                            reverseSegment(route, i, j); // Si no es válida, revierte la inversión
	                        } else {
	                            localImproved = true;
	                            changed = true;
	                        }
	                        break;
	                    }
	                }
	                if (localImproved)
	                    break;
	            }
	        }
	    }
	    return changed ? improved : this;
	}

	/**
	 * Método de búsqueda local que mueve un cliente de una ruta a otra y evalúa si
	 * se mejora la energía total. Este movimiento implica remover un cliente de una
	 * ruta y agregarlo en otra, evaluando si se obtiene una solución de menor
	 * energía.
	 * 
	 * @return Solution con la mejor ruta si se mejora la energía, de lo contrario
	 *         retorna la solución original.
	 */
	public Solution relocateCustomer() {
	    Solution improved = new Solution(this);

	    // Recorre todas las combinaciones de vehículos
	    for (int v1 = 0; v1 < improved.vehicles.size(); v1++) {
	        for (int v2 = 0; v2 < improved.vehicles.size(); v2++) {
	            if (v1 == v2)
	                continue;

	            List<PointVisited> r1 = improved.vehicles.get(v1).tour;
	            List<PointVisited> r2 = improved.vehicles.get(v2).tour;

	            // Recorre todos los clientes de la ruta 1 y prueba moverlos a la ruta 2
	            for (int i = 1; i < r1.size() - 1; i++) {
	                PointVisited client = r1.get(i);

	                for (int j = 1; j < r2.size(); j++) {
	                    double before = routeEnergy(r1) + routeEnergy(r2);

	                    // Mueve el cliente de la ruta 1 a la ruta 2
	                    r1.remove(i);
	                    r2.add(j, client);

	                    double after = routeEnergy(r1) + routeEnergy(r2);

	                    // Si la nueva energía es mejor y la solución es válida, acepta el cambio
	                    if (after < before && improved.validSolution()) {
	                        return improved;
	                    } else {
	                        // Si no mejora o la solución no es válida, vuelve a poner al cliente en su ruta original
	                        r2.remove(j);
	                        r1.add(i, client);
	                    }
	                }
	            }
	        }
	    }
	    return this;
	}

	/**
	 * Método de búsqueda local que intercambia clientes entre dos rutas y evalúa si
	 * se mejora la energía total. Este movimiento consiste en seleccionar dos
	 * clientes de rutas diferentes e intercambiarlos entre ellas, evaluando si la
	 * energía total del sistema se reduce.
	 * 
	 * @return Solution con la mejor ruta si se mejora la energía, de lo contrario
	 *         retorna la solución original.
	 */
	public Solution swapCustomers() {
	    Solution improved = new Solution(this);

	    // Recorre todas las combinaciones de vehículos
	    for (int v1 = 0; v1 < improved.vehicles.size(); v1++) {
	        for (int v2 = v1 + 1; v2 < improved.vehicles.size(); v2++) {
	            List<PointVisited> r1 = improved.vehicles.get(v1).tour;
	            List<PointVisited> r2 = improved.vehicles.get(v2).tour;

	            // Recorre todos los clientes de ambas rutas e intenta intercambiarlos
	            for (int i = 1; i < r1.size() - 1; i++) {
	                for (int j = 1; j < r2.size() - 1; j++) {
	                    PointVisited c1 = r1.get(i);
	                    PointVisited c2 = r2.get(j);

	                    double before = routeEnergy(r1) + routeEnergy(r2);

	                    // Intercambia los clientes
	                    r1.set(i, c2);
	                    r2.set(j, c1);

	                    double after = routeEnergy(r1) + routeEnergy(r2);

	                    // Si la nueva energía es mejor y la solución es válida, acepta el intercambio
	                    if (after < before && improved.validSolution()) {
	                        return improved;
	                    } else {
	                        // Si no mejora o la solución no es válida, vuelve a intercambiar los clientes
	                        r1.set(i, c1);
	                        r2.set(j, c2);
	                    }
	                }
	            }
	        }
	    }

	    return this;
	}
	
	public Solution optimizeRechargeStationsByInsertion() {
	    Solution improved = new Solution(this);
	    boolean changed = false;

	    boolean localImproved = true;
	    while (localImproved) {
	        localImproved = false;

	        for (Vehicle v : improved.vehicles) {
	            List<PointVisited> route = v.tour;

	            for (int i = 1; i < route.size() - 1; i++) {
	                PointVisited current = route.get(i);
	                if (!current.point.isRecharging) continue;

	                for (Point newStation : instance.rechargings) {
	                    if (newStation.id == current.point.id) continue;

	                    // --------- REEMPLAZO EN LA MISMA POSICIÓN ---------
	                    Point A = route.get(i - 1).point;
	                    Point B = current.point;
	                    Point C = route.get(i + 1).point;

	                    double energyBefore = instance.computeEnergy(A, B) + instance.computeEnergy(B, C);

	                    PointVisited candidate = new PointVisited(newStation, 0);
	                    route.set(i, candidate);

	                    double energyAfter = instance.computeEnergy(A, newStation) + instance.computeEnergy(newStation, C);

	                    if (energyAfter < energyBefore && improved.validSolution()) {
	                        localImproved = true;
	                        changed = true;
	                        break;
	                    }

	                    // revertir
	                    route.set(i, current);

	                    // --------- INSERCIÓN EN OTRA POSICIÓN ---------
	                    for (int insertPos = 1; insertPos < route.size(); insertPos++) {
	                        if (insertPos == i) continue;

	                        A = route.get(insertPos - 1).point;
	                        C = route.get(insertPos).point;

	                        energyBefore = instance.computeEnergy(route.get(i - 1).point, B) + instance.computeEnergy(B, route.get(i + 1).point);
	                        energyAfter = instance.computeEnergy(A, newStation) + instance.computeEnergy(newStation, C);

	                        route.remove(i);
	                        route.add(insertPos, candidate);

	                        if (energyAfter < energyBefore && improved.validSolution()) {
	                            localImproved = true;
	                            changed = true;
	                            break;
	                        }

	                        // revertir
	                        route.remove(insertPos);
	                        route.add(i, current);

	                        if (localImproved) break;
	                    }

	                    if (localImproved) break;
	                }

	                if (localImproved) break;
	            }

	            if (localImproved) break;
	        }
	    }

	    return changed ? improved : this;
	}



}
