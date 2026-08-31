package evrp_0.domain;

public class PointVisited {

	public Point point;
	public double time;

	public PointVisited(Point point, double time) {
		this.point = point;
		this.time = time;
	}

	public PointVisited(PointVisited pointVisited) {
		this.point = pointVisited.point;
		this.time = pointVisited.time;
	}

}
