package evrp_0.domain;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class SchneiderInstance extends Instance {

	public SchneiderInstance(String fullName) {
		super(fullName);
	}

	@Override
	public void load(String urlfile) {
		Scanner kbd;
		points = new ArrayList<Point>();
		Point point;
		try {
			kbd = new Scanner(new File(urlfile));
			kbd.nextLine(); // omit first line
			int indexLine = 0;
			String type = null;
			double x = 0, y = 0, demand = 0, readyTime = 0, dueDate = 0, serviceTime = 0;
			String s;
			int id = 0;
			while (kbd.hasNext()) {
				if (indexLine == 8) { // create the entity
					indexLine = 0;
					point = new Point(id, type, x, y, demand, readyTime, dueDate, serviceTime);
					if (!points.contains(point)) { // to avoid repeat in the same point two different objects
						points.add(point);
						id++;
					}
				}
				s = kbd.next();
				if (s.equals("Q")) // if omit fuel
					break;
				switch (indexLine) {
				case 1: // type
					type = s;
					break;
				case 2: // x
					x = Double.parseDouble(s);
					break;
				case 3: // y
					y = Double.parseDouble(s);
					break;
				case 4: // demand
					demand = Double.parseDouble(s);
					break;
				case 5: // readyTime
					readyTime = Double.parseDouble(s);
					break;
				case 6: // dueDate
					dueDate = Double.parseDouble(s);
					break;
				case 7: // serviceTime
					serviceTime = Double.parseDouble(s);
					break;
				}
				indexLine++;
			}
			indexLine = 0;
			while (kbd.hasNext()) {
				s = kbd.nextLine().split("/")[1];
				switch (indexLine) {
				case 0: // fuel tank capacity
					Q = Double.parseDouble(s);
					break;
				case 1: // load capacity
					C = Double.parseDouble(s);
					break;
				case 2: // fuel consumption rate
					r = Double.parseDouble(s);
					break;
				case 3: // inverse refueling rate
					g = Double.parseDouble(s);
					break;
				case 4: // average velocity
					v = Double.parseDouble(s);
					break;
				}
				indexLine++;
			}
			kbd.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		computePoints();
	}

	@Override
	public double computeTime(Point p1, Point p2, double actualTime) { // we don't consider time service
		double time = distance(p1, p2) / v;
		if ((time + actualTime) < p2.readyTime) 
			time =  p2.readyTime - actualTime; // the vehicle has to wait
		return time;
	}

	@Override
	public double computeEnergy(Point p1, Point p2) {
		return distance(p1, p2) * r;
	}

	@Override
	public void computePoints() {
		distance = new double[points.size()][points.size()];
		Point point;
		double d;
		List<Point> updated = new ArrayList<>();
		for (int i = 0; i < distance.length; i++) {
			point = points.get(i);
			xSum += point.x;
			ySum += point.y;
			for (int j = 0; j < distance.length; j++) {
				d = points.get(i).distance(points.get(j));
				distance[i][j] = d;
				totalDistance += d;
			}
			if (point.isCustomer) {
				customers.add(point);
				sortedDemand.add(point.demand);
				weightSum += point.demand;
			}
			if (point.isRecharging)
				rechargings.add(point);
			if (point.isDepot)
				depot = point;
			totalDD += point.dueDate;
			totalRT += point.readyTime;
			updated.add(point); //ojo con esto!
		}
		points = updated;
		// compute the nearest recharging point of each customer to prevent
		// that a vehicle go to a customer without enought energy to, at least,
		// go to nearest recharging point from the customer.
		for (Point customer : points) {
			customer.idCustomer = customer.id - rechargings.size();
			customer.nearestRechargingPoint = searchNearestRechargingPoint(customer);
		}
		Collections.sort(sortedDemand);
	}

	// Point is a customer
	// Also, it should be the position on the weight matrix
	protected Point searchNearestRechargingPoint(Point point) {
		Point nearest = null;
		Point aux;
		double sortPath = Double.MAX_VALUE;
		for (int i = 0; i < rechargings.size(); i++) {
			aux = rechargings.get(i);
			if (!point.equals(aux) && distance[point.id][i] < sortPath) {
				sortPath = distance[point.id][i];
				nearest = aux;
			}
		}
		return nearest;
	}
}
