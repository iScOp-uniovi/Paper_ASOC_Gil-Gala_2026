package evrp_0.domain;

import java.util.ArrayList;
import java.util.List;

public class Route {

	public List<PointVisited> route;
	boolean isValid;
	double energyUsed;
	public double tardinessProduced;

	public Route() {
		route = new ArrayList<PointVisited>();
		isValid = true;
	}

	public void addPoint(PointVisited p) {
		route.add(p);
	}

	public void processRoute(List<PointVisited> generalTour) {
		for (PointVisited point : route)
			generalTour.add(point);
	}

	public String getRoute() {
		String s = "{";
		for (PointVisited point : route)
			s += "[" + point.point.id + "]";
		return s + "}";

	}
	
	public boolean contains(Point point) {
		for (PointVisited p : route)
			if (p.point.equals(point))
				return true;
		return false;
	}

}
