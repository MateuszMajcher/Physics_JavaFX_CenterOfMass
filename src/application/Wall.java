package application;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

public class Wall {
	
	private Vector2D p1;
	private Vector2D p2;
	int side = 1;
	
	
	Wall(Vector2D p1, Vector2D p2) {
		this.p1 = p1;
		this.p2 = p2;
		this.side = 1;
	}
	
	public Vector2D dir() {
		return this.p2.subtract(this.p1);
	}
	
	public Vector2D normal() {
		return this.dir().perp(1);
	}
	
	public void draw(Pane pane) {
		Line line = new Line(); 
		line.setFill(Color.BLACK);
		line.setStrokeWidth(2.0);
		line.setStartX(p1.getX()); 
		line.setStartY(p1.getY()); 
		line.setEndX(p2.getX()); 
		line.setEndY(p2.getY());
		pane.getChildren().add(line);
	}

	public Vector2D getP1() {
		return p1;
	}

	public Vector2D getP2() {
		return p2;
	}
	
	

}
