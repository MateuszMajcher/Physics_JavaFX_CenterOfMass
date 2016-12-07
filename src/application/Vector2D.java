package application;

public class Vector2D {
	private double x;
	private double y;
	
	public Vector2D(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}
	
	public double lengthSquared() {
		return this.x*this.x + this.y*this.y;
	}
	
	/** 
	 * Zwraca dlugosc wektora
	 * @return dlugosc wektora
	 */
	public double length() {
		return Math.sqrt(this.lengthSquared());
	}
	
	public Vector2D subtract(Vector2D vec) {
		return new Vector2D(this.x - vec.x,this.y - vec.y);
	}
	
	public Vector2D add(Vector2D vec) {
		return new Vector2D(this.x + vec.x, this.y + vec.y);
	}
	
	public Vector2D addScaled(Vector2D vec, double s) {
		return new Vector2D(this.x + (s*vec.getX()), this.y + (s*vec.getY()));
	}
	
	public void scaleBy(double k) {
		this.x *= k;
		this.y *= k;
	}
	
	public Vector2D unit() {
		double length = this.length();
		if (length > 0) {
			return new Vector2D(this.x/length, this.y/length);
		} else {
			return new Vector2D(0.0,0.0);
		}
	}
	
	public Vector2D para(double a){
		double length = this.length();
		Vector2D vec = new Vector2D(this.x, this.y);
		
		vec.scaleBy(a/length);
		
		return vec;
	}
	
	public Vector2D multiply(double k) {
		return new Vector2D(k*this.getX(), k*this.getY());
	}
	
	public Vector2D rotate(double angle) {
		return new Vector2D(this.x * Math.cos(angle) - this.y * Math.sin(angle),
				this.x * Math.sin(angle) + this.y * Math.cos(angle));
	}
	
	public Vector2D perp(double u) {
		double length = this.length();
		Vector2D vec = new Vector2D(this.y, -this.x);
		if (length > 0) {
			vec.scaleBy(u/length);
		} else {
			vec = new Vector2D(0, 0);
		}
		return vec;
	}
	
	public double dotProduct(Vector2D vec) {
		return this.x*vec.getX() + this.y*vec.getY();
	}
	
	public double crossProduct(Vector2D vec) {
		return this.x*vec.getY() - this.y*vec.getX();
	}
	@Override
	public String toString() {
		return "Vector2D [x=" + x + ", y=" + y + "]";
	}
}
