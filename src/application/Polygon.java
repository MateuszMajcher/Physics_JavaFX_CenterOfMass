package application;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;

public class Polygon {
	
	private List<Vector2D> vert;
	private Color color;
	private double mass;
	private double mb;
	private double x;
	private double y;
	private double vx;
	private double vy;
	private double angVelo;
	private double theta;
	
	public Polygon(List<Vector2D> vert, Color color, double mass, double mb) {
		this.vert = vert;
		this.color = color;
		this.mass = mass;
		this.mb = mb;
		this.x = 0;
		this.y = 0;
		this.vx = 0;
		this.vy = 0;	
		this.angVelo = 0;	
		this.theta = 0;
	}
	
	
	
	public List<Vector2D> getVert() {
		return vert;
	}

	public Vector2D pos2D() {
		return new Vector2D(this.x, this.y);
	}
	
	public void setPos2D(Vector2D pos) {
		this.x = pos.getX();
		this.y = pos.getY();
	}
	
	public void setPosX(double x) {
		this.x = x;
	}
	
	public void setPosY(double y) {
		this.y = y;
	}
	
	public Vector2D velo2D() {
		return new Vector2D(this.vx, this.vy);
	}
	
	public void setVelo2D(Vector2D velo) {
		this.vx = velo.getX();
		this.vy = velo.getY();
	}
	
	public double getRotation() {
		return this.theta;
	}
	
	public void setRotation(double angle) {
	
		this.theta = angle;
		for (int i = 0; i < vert.size(); i++) {
			Vector2D v = this.vert.get(i).rotate(angle);
			this.vert.get(i).setX(v.getX());
			this.vert.get(i).setY(v.getY());
		}
	}
	
	public double getAngVelo() {
		return this.angVelo;
	}
	
	public void setAngVelo(double angVelo) {
		this.angVelo = angVelo;
	}
	

	public double getMass() {
		return mass;
	}
	
	public void setMass(double mass) {
		this.mass = mass;
	}
	
	public double getMb() {
		return mb;
	}

	public void draw(Pane pane) {
		 pane.getChildren().clear();
		 List<Vector2D> v = new ArrayList<Vector2D>();
		 Path path = new Path();
		 
		
		 int size = vert.size();
		 for (int i = 0; i < size; i++) {
			 v.add(vert.get(i).add(this.pos2D()));
		 }
		
		 if (size > 0) {
			 Vector2D t = v.get(0);
			 path.getElements().add(new MoveTo(t.getX(), t.getY()));
		 }
		 for (int i = 1; i < size; i++) {
			 Vector2D t = v.get(i);
			 path.getElements().add(new LineTo(t.getX(), t.getY()));
		 }
		 path.setFill(color);
	     pane.getChildren().add(path);
	}
	
	
}
