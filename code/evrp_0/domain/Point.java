package evrp_0.domain;

public class Point implements Comparable<Point> {

	public int id;
	public int idCustomer;
	public boolean isRecharging;
	public boolean isDepot;
	public boolean isCustomer;
	public double x, y;
	public double demand;
	public double readyTime;
	public double dueDate;
	public double serviceTime;
	public Point nearestRechargingPoint;
	
	public Point(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public Point(int id, String type, double x, double y, double demand, double readyTime, double dueDate, double serviceTime) {
		this.id = id;
		switch (type) {
		case "d":
			this.isRecharging = true;
			this.isDepot = true;
			break;
		case "f":
			this.isRecharging = true;
			break;
		case "c":
			this.isRecharging = false;
			this.isCustomer = true;
			break;
		default:
			break;
		}
		this.x = x;
		this.y = y;
		this.demand = demand;
		this.readyTime = readyTime;
		this.dueDate = dueDate;
		this.serviceTime = serviceTime;
	}

	public double distance(Point p2) {
		return Math.sqrt(Math.pow(this.x - p2.x, 2) + Math.pow(this.y - p2.y, 2));
	}

	@Override
	public boolean equals(Object obj) {
		return Double.compare(x, ((Point) obj).x) == 0 && Double.compare(y, ((Point) obj).y) == 0;
	}

	@Override
	public String toString() {
		return id + " " + x + " " + y + " " + demand + " " + isCustomer + " " + isRecharging + " " + isDepot;
	}

	@Override
	public int compareTo(Point o) {
		if (x > o.x)
			return 1;
		else if (x < o.x)
			return -1;
		else {
			if (y > o.y)
				return 1;
			else
				return 0;
		}
	}
}