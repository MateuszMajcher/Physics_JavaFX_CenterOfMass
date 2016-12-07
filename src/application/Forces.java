package application;

public class Forces {
	
	public static Vector2D constantGravity(double mass, double g) {
		return new Vector2D(0, mass * g);
	}
}
