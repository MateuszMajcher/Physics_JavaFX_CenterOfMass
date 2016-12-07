package application;
	
import java.util.ArrayList;
import java.util.List;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Insets;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;


/**
 * klasa Main
 * @author Mateusz Majcher
 *
 */
public class Main extends Application {
	
	int width = 40;
	int height = 100;
	int px = 300;
	int py = 300;
	
	//param
	Polygon glass;
	double rho = 0.2;  //masa
	double g = 10.0;
	double cr = 0.4;
	double k = 10;
	double im =5000;
	double mass;
	double friction = 0.03; //tarcie
	double dt;
	double torque;
	double alp;
	Vector2D force, acc;
	//wypelnienie
	double fill = 1.0;
	double centerY;
	
	//statystyki
	private final FrameStats frameStats = new FrameStats();
	//Panel symulacji
	final static Pane panel = new Pane();
	//sciana 
	private Wall wall;
	//uderzenie
	Path path;
	
	//Aktualny srodek masy
	Vector2D centerMass;
	
	//kontrolki
	Label massLabel;
	Label centerMassLabel;
	
	@Override
	public void start(Stage primaryStage) {
		try {
			//glowny layout
			BorderPane root = new BorderPane();
			//Panel statystyk
			final Label stats = new Label();
			
			//ustawienie bindowania dla statystyk
			stats.textProperty().bind(frameStats.textProperty());
			
			//utworzenie sceny
			createScene();
			//ustawienie rozmiaru sceny
			panel.setPrefSize(600, 380);
			
			path = new Path();
			path.setStrokeWidth(1);
			path.setStroke(Color.BLACK);
			
			panel.setOnMousePressed(mouseEvent);
			panel.setOnMouseDragged(mouseEvent);
			panel.setOnMouseReleased(mouseEvent);
			
			
			
			
			//Panel sterowania
			root.setCenter(panel);
			root.setBottom(addHBox());
			root.setTop(stats);
			Scene scene = new Scene(root,600,400);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/*Events*/
	double x = 0, y = 0;
	EventHandler<MouseEvent> mouseEvent = new EventHandler<MouseEvent>() {
		
		@Override
		public void handle(MouseEvent e) {
			
			 if (e.getEventType() == MouseEvent.MOUSE_PRESSED) {
			       x = e.getX();
			       y = e.getY(); 
			      } else if (e.getEventType() == MouseEvent.MOUSE_DRAGGED) {
			    	  path.getElements().clear();
			    	 
			    	  path.getElements()
			            .add(new MoveTo(x, y));
			        path.getElements()
			            .add(new LineTo(e.getX(), e.getY()));
			      
			      } else if (e.getEventType() == MouseEvent.MOUSE_RELEASED) {
			    	  System.out.println("relased");
			    	  checkWallBounce();
			    	  
			    	  Vector2D v = new Vector2D(x, y);
			    	  Vector2D vec = v.para(1);
			    	  Vector2D rp1 = glass.getVert().get(1).rotate(glass.getRotation());
			    	  Vector2D vp1 = glass.velo2D().add(rp1.perp(-glass.getAngVelo() * rp1.length()));
			    	  
			    	  double invm1 = 1/glass.getMass();
			    	  double invI1 = 1/glass.getMb();
			    	  
			    	  double impulse = Math.abs(x-e.getX()*20);
			    	 
			    	  glass.setVelo2D(glass.velo2D().add(vec.multiply(impulse*invm1)));
	
			    	  double d = py - centerMass.getY(); //dol
			    	  System.out.println("Dol " + d);
			    	  double g = height - d;  //gora
			    	  System.out.println("Gora " + g);
			    	  double cY = py - centerY;
			    	  int t = (int) (centerMass.getY() - y);
			    	  System.out.println("Odleglosc od srodka masy: " + t);
			    	
			    	  double zx;
			    	  System.out.println(y + " "+centerMass.getY());
			    	  if (y <= centerMass.getY()) {
			    		  
			    		  zx = rp1.crossProduct(vec)*impulse*invI1 * getPercentage(t, (int)g);
			    		  System.out.println("gora " + getPercentage(t, (int)g));
			    	  } else {
			    		  zx = rp1.crossProduct(vec)*impulse*invI1 * getPercentage(t, (int)d);
			    		  System.out.println("dol " + getPercentage(t, (int)d));
			    	  }
			    	 
			    	
			    	  glass.setAngVelo(glass.getAngVelo() + zx);
			    	  
			    	  path.getElements().clear();
			      } 
		}
	};
	
	public static double getPercentage(int n, int total) {
		double proportion = ((double) n) / ((double) total);
	    return proportion;
	}
	
	public void createScene() {
		addShape(width, height,0, px, py);
		//dodanie sciany
		wall = new Wall(new Vector2D(0, 300), new Vector2D(600, 300));
		wall.draw(panel);
		startAnimation();
	}
	
	private void startAnimation() {
		final LongProperty lastUpdateTime = new SimpleLongProperty(0);
		final AnimationTimer timer = new AnimationTimer() {
			
			@Override
			public void handle(long now) {
				if (lastUpdateTime.get() > 0) {
					long elapsedTime = now - lastUpdateTime.get();
					move();
					dt = 0.000000001 * elapsedTime;
					frameStats.addFrame(elapsedTime);
				}
				lastUpdateTime.set(now);
			}
		};
		timer.start();
	}
	
	private void move() {
		moveObject();
		checkWallBounce();
		calcForce();
		updateAccel();
		updateVelo();
		checkWallBounce();
		wall.draw(panel);
		panel.getChildren().add(path);
		centerMass = getCenterOfMass();
		
		Circle circle = new Circle();
        circle.setCenterX(centerMass.getX());
        circle.setCenterY(centerMass.getY());
        circle.setRadius(2.0f);
        
        panel.getChildren().add(circle);
        centerMassLabel.setText("Srodek : " + centerMass);
        massLabel.setText("Masa: " + glass.getMass());
	}
	
	private void updateVelo() {
		if (glass.velo2D().getX() > friction) {
			glass.setVelo2D(glass.velo2D().subtract(new Vector2D(friction, 0)));
		} else {
			glass.setVelo2D(new Vector2D(0, glass.velo2D().getY()));
		}
		glass.setVelo2D(glass.velo2D().addScaled(acc, dt));
		
		glass.setAngVelo(glass.getAngVelo() + (alp * dt));
	}

	private void updateAccel() {
		acc = force.multiply(1/glass.getMass());
		alp = torque/im;
	}

	private void calcForce() {
		force = Forces.constantGravity(glass.getMass(), g);
		torque = 0;
		torque += -k*glass.getAngVelo();
	}

	private void checkWallBounce() {
		boolean col1 = false;
		boolean col2 = false;
		int j = 0, j2 = 0;
		
		for (int i = 0; i < glass.getVert().size(); i++) {
			if (glass.pos2D().add(glass.getVert().get(i).rotate(glass.getRotation())).getY() >= wall.getP1().getY()) {
				if (col1 == false) {
					col1 = true;
					j = i;
				} else {
					j2 = i;
					col2 = true;
				}
			}
		}
		
		// collision resolution
		if (col1 == true){
			glass.setPosY(
					glass.pos2D().getY() + ( 
							glass.pos2D()
							.add(glass.getVert()
									.get(j)
									.rotate(glass.getRotation())).getY()*(-1) + wall.getP1().getY()));
			
			if (col2 == true){ // reposition the other vertex too to prevent it from sinking
				glass.setPosY(
						glass.pos2D().getY() + ( 
								glass.pos2D()
								.add(glass.getVert()
										.get(j2)
										.rotate(glass.getRotation())).getY()*(-1) + wall.getP1().getY()));
				col2 = false;
			}		
			Vector2D normal = wall.normal();
			Vector2D rp1 = glass.getVert().get(j).rotate(glass.getRotation());
			Vector2D vp1 = glass.velo2D().add(rp1.perp(-glass.getAngVelo()*rp1.length()));
			double rp1Xnormal = rp1.crossProduct(normal);
			
			double impulse = -(1+cr)*vp1.dotProduct(normal)/(1/glass.getMass() + rp1Xnormal*rp1Xnormal/glass.getMb()); 
			
			glass.setVelo2D(glass.velo2D().add(normal.multiply(impulse/glass.getMass())));
			//System.out.println(glass.velo2D());
			glass.setAngVelo(glass.getAngVelo() + (rp1.crossProduct(normal)*impulse/glass.getMb()));
			col1 = false;
		}			
		
	}

	private void moveObject() {
		glass.setPos2D(glass.pos2D().addScaled(glass.velo2D(),  dt));
		glass.setRotation(glass.getAngVelo() * dt);
		glass.draw(panel);
	}

	public void addShape(int width, int height, double angle, int x , int y) {
		mass = rho * width * height;
		
		double mb = mass * (width * width + height * height)/12;
		centerY = height/2;
		glass = makeShape(width, height, Color.BLUE, mass, mb);
		glass.setRotation(angle * Math.PI/180);
		glass.setPos2D(new Vector2D(x, y));
		glass.draw(panel);
	}
	
	public Polygon makeShape(int width, int height , Color color, double mass, double mb) {
		List<Vector2D> vert = new ArrayList<Vector2D>();
		vert.add(new Vector2D(-width/2,-height/2));
		vert.add(new Vector2D(width/2,-height/2));
		vert.add(new Vector2D(width/2,height/2));
		vert.add(new Vector2D(-width/2,height/2));
		return new Polygon(vert, color, mass, mb);
	}
	
	/**
	 * Tworzy panel sterowania
	 * @return 
	 */
	public HBox addHBox() {
		HBox hbox = new HBox();
		Label text = new Label("Napelnienie");
		
		final Slider gSlider = new Slider(0.1, centerY*2, 1);
		gSlider.setShowTickMarks(true);
		gSlider.setShowTickLabels(true);
		gSlider.setMaxWidth(Double.MAX_VALUE);
		gSlider.setValue(0.1);
		
		
		gSlider.valueProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
				fill = arg2.doubleValue();
				glass.setMass(mass/(arg2.doubleValue()/10));
				System.out.println(glass.getMass());
			}
		});
		
		centerMassLabel = new Label("0");
		massLabel = new Label("0");
		hbox.getChildren().add(text);
		hbox.getChildren().add(gSlider);
		hbox.getChildren().add(centerMassLabel);
		hbox.getChildren().add(massLabel);
		hbox.setPadding(new Insets(15, 12, 15, 12));
		hbox.setSpacing(10);
		hbox.setStyle("-fx-background-color: #336699");
		return hbox;
	}
	/**
	 * Computes the area of any two-dimensional polygon.
	 * 
	 * @param polygon
	 *            The polygon to compute the area of input as an array of points
	 * @param N
	 *            The number of points the polygon has, first and last point
	 *            inclusive.
	 * 
	 * @return The area of the polygon.
	 */
	public static double PolygonArea(Vector2D[] polygon, int N) {

		int i, j;
		double area = 0;

		for (i = 0; i < N; i++) {
			j = (i + 1) % N;
			area += polygon[i].getX() * polygon[j].getY();
			area -= polygon[i].getY() * polygon[j].getX();
		}

		area /= 2.0;
		return (Math.abs(area));
	}

	/**
	 * Obliczenie srodka ciezkosci.
	 * @param 
	 * @return srodek ciezkosci.
	 */
	public static Vector2D polygonCenterOfMass(List<Vector2D> pg) {

		if (pg == null)
			return null;

		int N = pg.size();
		Vector2D[] polygon = new Vector2D[N];

		for (int q = 0; q < N; q++){
			polygon[q] = new Vector2D(pg.get(q).getX(), pg.get(q).getY());
		
		}
		double cx = 0, cy = 0;
		double A = PolygonArea(polygon, N);
		//System.out.println("A: " + A);
		int i, j;

		double factor = 0;
		for (i = 0; i < N; i++) {
			j = (i + 1) % N;
			factor = (polygon[i].getX() * polygon[j].getY() - polygon[j].getX() * polygon[i].getY());
			cx += (polygon[i].getX() + polygon[j].getX()) * factor;
			cy += (polygon[i].getY() + polygon[j].getY()) * factor;
			//System.out.println(cy);
		}
		factor = 1.0 / (6.0 * A);
		
		cx *= factor;
		cy *= factor;

		return new Vector2D( Math.abs(Math.round(cx)),  Math.abs(Math
				.round(cy)));
	}
	
	public Vector2D getCenterOfMass() {
		List<Vector2D> v = new ArrayList<Vector2D>();
		 int size = glass.getVert().size();
		 for (int i = 0; i < size; i++) {
			 v.add(glass.getVert().get(i).add(glass.pos2D()));
		 }
		 v.get(2).setY(v.get(2).getY() + fill);
		 v.get(3).setY(v.get(3).getY() + fill);
		return polygonCenterOfMass(v);
	}
	
	private static class FrameStats {
		private long frameCount;
		private double meanFrameInterval;
		private final ReadOnlyStringWrapper text = new ReadOnlyStringWrapper(this, "text", "Liczba klatek na sekunde: 0 czas: 0");
		
		public long getFrameCount() {
			return frameCount;
		}

		public double getMeanFrameInterval() {
			return meanFrameInterval;
		}

		public String getText() {
			return text.get();
		}
		
		public ReadOnlyStringProperty textProperty() {
			return text.getReadOnlyProperty();
		}
		
		public void addFrame(long frameDuration) {
			meanFrameInterval = (meanFrameInterval * frameCount + frameDuration / 1_000_000.0) / (frameCount + 1) ;
			frameCount++;
			text.set(toString());
		}
		
		public String toString() {
			return String.format("Liczba klatek na sekunde: %,d czas: %.3f milis", getFrameCount(), getMeanFrameInterval());
		}
		
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
