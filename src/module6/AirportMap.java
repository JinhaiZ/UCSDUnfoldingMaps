package module6;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.data.ShapeFeature;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.marker.SimpleLinesMarker;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import de.fhpotsdam.unfolding.providers.AbstractMapProvider;
import de.fhpotsdam.unfolding.providers.Google;
import de.fhpotsdam.unfolding.providers.Microsoft;
import de.fhpotsdam.unfolding.utils.MapUtils;
import de.fhpotsdam.unfolding.geo.Location;
import parsing.ParseFeed;
import processing.core.PApplet;

/** An applet that shows airports (and routes)
 * on a world map.  
 * @author Adam Setters and the UC San Diego Intermediate Software Development
 * MOOC team
 *
 */
public class AirportMap extends PApplet {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// The map
	UnfoldingMap map;
	// Setup providers
	AbstractMapProvider provider1;
	AbstractMapProvider provider2;
	// Setup data
	private List<Marker> airportMarkers;
	List<Marker> routeList;
	// Setup buttons
	//int rectX, rectY;      // Position of square button
	//int rectSize = 20;     // Diameter of rect
	//int circleSize = 93;   // Diameter of circle
	int rectColor, baseColor;
	int rectHighlight;
	AbstractMapProvider currentProvider;
	boolean button1Over = false;
	boolean button2Over = false;
	// Hover method
	private CommonMarker lastSelected;
	private CommonMarker lastClicked;
	// Clock
	//int cx, cy;
	float secondsRadius;
	float minutesRadius;
	float hoursRadius;
	float clockDiameter;
	
	
	public void setup() {
		// setting up PAppler
		size(1000,700, OPENGL);
		provider1 = new Google.GoogleMapProvider();
	    provider2 = new Microsoft.HybridProvider();
		
		// setting up map and default events
		map = new UnfoldingMap(this, 200, 50, 800, 600, provider1);
		MapUtils.createDefaultEventDispatcher(this, map);
		
		// get features from airport data
		List<PointFeature> features = ParseFeed.parseAirports(this, "airports.dat");
		
		// list for markers, hashmap for quicker access when matching with routes
		airportMarkers = new ArrayList<Marker>();
		HashMap<Integer, Location> airports = new HashMap<Integer, Location>();
		
		// create markers from features
		for(PointFeature feature : features) {
			AirportMarker m = new AirportMarker(feature);
	
			//m.setRadius(15);
			//int color = color(0,0,255);
			//m.setColor(color(0,0,255));
			//System.out.println(m.getProperties());
			airportMarkers.add(m);
			
			// put airport in hashmap with OpenFlights unique id for key
			airports.put(Integer.parseInt(feature.getId()), feature.getLocation());
		
		}
		
		
		// parse route data
		List<ShapeFeature> routes = ParseFeed.parseRoutes(this, "routes.dat");
		routeList = new ArrayList<Marker>();
		for(ShapeFeature route : routes) {
			
			// get source and destination airportIds
			int source = Integer.parseInt((String)route.getProperty("source"));
			int dest = Integer.parseInt((String)route.getProperty("destination"));
			
			// get locations for airports on route
			if(airports.containsKey(source) && airports.containsKey(dest)) {
				route.addLocation(airports.get(source));
				route.addLocation(airports.get(dest));
			}
			
			SimpleLinesMarker sl = new SimpleLinesMarker(route.getLocations(), route.getProperties());
		
			//System.out.println(sl.getProperties());
			
			//UNCOMMENT IF YOU WANT TO SEE ALL ROUTES
			//routeList.add(sl);
		}
		//UNCOMMENT IF YOU WANT TO SEE ALL ROUTES
		//map.addMarkers(routeList);
		
		map.addMarkers(airportMarkers);
		
		// Setup button
		rectColor = color(0);
		rectHighlight = color(204);
		baseColor = color(102);
		currentProvider = provider1;
		
		// Setup clock
		//stroke(255);
		int radius = min(width, height) / 15;
		secondsRadius = (float) (radius * 0.72);
		minutesRadius = (float) (radius * 0.60);
		hoursRadius = (float) (radius * 0.50);
		clockDiameter = (float) (radius * 1.8);		  

	}
	
	public void draw() {
		background(0);
		map.draw();
		addKey();
		addButton(150,550,20);
		// clock 1
		addClock(110,320,0,"Current Time");
		addClock(110,450,5,"Local Time");
		//map.getZoomLevel();
	}
	// keyboard
	public void keyPressed() {
	    if (key == '1') {
	        map.mapDisplay.setProvider(provider1);
	    } else if (key == '2') {
	        map.mapDisplay.setProvider(provider2);
	    }
	}
	// Button 
	private void addButton(int rectX, int rectY, int rectSize) {
		// text
		fill(0);
		textAlign(LEFT, CENTER);
		textSize(16);
		text("Map Provider", rectX-90, rectY-30);
		text("Selection", rectX-70, rectY-12); 
		// button
		update(mouseX, mouseY, rectX, rectY, rectSize);
		if (button1Over) {
		  fill(rectHighlight);
		} else {
		  fill(rectColor);
		}
		// text
		textSize(12);
		text("Google Terran", rectX-100, rectY+6);
		
		// button
		stroke(255);
		rect(rectX, rectY, rectSize, rectSize);
		  
		if (button2Over) {
		  fill(rectHighlight);
		} else {
		  fill(rectColor);
		}
		//text
		textSize(12);
		text("Microsoft Hybrid", rectX-100, rectY+36);
		stroke(255);
		rect(rectX, rectY+30, rectSize, rectSize);
	}
	
	void update(int x, int y, int rectX, int rectY, int rectSize) {
		if ( overRect(rectX, rectY+30, rectSize, rectSize)  ) {
			button2Over = true;
			button1Over = false;
		    } else if ( overRect(rectX, rectY, rectSize, rectSize) ) {
		    	button1Over = true;
		    	button2Over = false;
		    } else {
		    	button2Over = button1Over = false;
		}
	}
	
	public void mousePressed() {
		if (button2Over) {
			map.mapDisplay.setProvider(provider2);
		}
		if (button1Over) {
			map.mapDisplay.setProvider(provider1);
		}
	}
	
	boolean overRect(int x, int y, int width, int height)  {
		if (mouseX >= x && mouseX <= x+width && 
				mouseY >= y && mouseY <= y+height) {
			return true;
		} else {
			return false;
		}
	}
	
	// Clock
	public void addClock(int cx, int cy, float offset, String name) {		  
		// Draw the clock background		
		fill(0);
		textAlign(LEFT, CENTER);
		textSize(16);
		text(name, cx/2, cy-70);
		fill(80);
		noStroke();
		ellipse(cx, cy, clockDiameter, clockDiameter);
		  
		// Angles for sin() and cos() start at 3 o'clock;
		// subtract HALF_PI to make them start at the top
		float s = map(second(), 0, 60, 0, TWO_PI) - HALF_PI;
		float m = map(minute() + norm(second(), 0, 60), 0, 60, 0, TWO_PI) - HALF_PI; 
		float h = map(hour()+ offset + norm(minute(), 0, 60), 0, 24, 0, TWO_PI * 2) - HALF_PI;
		  
		// Draw the hands of the clock
		stroke(255);
		strokeWeight(1);
		line(cx, cy, cx + cos(s) * secondsRadius, cy + sin(s) * secondsRadius);
		strokeWeight(2);
		line(cx, cy, cx + cos(m) * minutesRadius, cy + sin(m) * minutesRadius);
		strokeWeight(4);
		line(cx, cy, cx + cos(h) * hoursRadius, cy + sin(h) * hoursRadius);
		  
		// Draw the minute ticks
		strokeWeight(2);
		beginShape(POINTS);
		for (int a = 0; a < 360; a+=6) {
		  float angle = radians(a);
		  float x = cx + cos(angle) * secondsRadius;
		  float y = cy + sin(angle) * secondsRadius;
		  vertex(x, y);
		}
		endShape();
	}
	
	// Hover Method
	@Override
	public void mouseMoved()
	{
		// clear the last selection
		if (lastSelected != null) {
			lastSelected.setSelected(false);
			lastSelected = null;
		
		}
		selectMarkerIfHover(airportMarkers);
		//loop();
	}
	// If there is a marker selected 
		private void selectMarkerIfHover(List<Marker> markers)
		{
			// Abort if there's already a marker selected
			if (lastSelected != null) {
				return;
			}
			
			for (Marker m : markers) 
			{
				CommonMarker marker = (CommonMarker)m;
				if (marker.isInside(map,  mouseX, mouseY)) {
					lastSelected = marker;
					marker.setSelected(true);
					return;
				}
			}
		}
	
	private void addKey() {	
		// Remember you can use Processing's graphics methods here
		fill(255, 250, 240);
		
		int xbase = 25;
		int ybase = 50;
		
		rect(xbase, ybase, 170, 600);
		
		fill(0);
		textAlign(LEFT, CENTER);
		textSize(20);
		text("Airport Map", xbase+25, ybase+25);

		textSize(16);
		text("Airport Altitude", xbase+25, ybase+50);
		stroke(0);
		fill(color(255, 0, 0));
		ellipse(xbase+35, ybase+80, 12, 12);
		fill(color(255, 128, 0));
		ellipse(xbase+35, ybase+100, 12, 12);
		fill(color(255, 255, 51));
		ellipse(xbase+35, ybase+120, 12, 12);
		fill(color(128, 255, 0));
		ellipse(xbase+35, ybase+140, 12, 12);
		fill(color(51, 153, 255));
		ellipse(xbase+35, ybase+160, 12, 12);
		textSize(12);
		textAlign(LEFT, CENTER);
		fill(0, 0, 0);
		text("> 3000m", xbase+50, ybase+80);
		text("3000m ~ 2000m", xbase+50, ybase+100);
		text("2000m ~ 1000m", xbase+50, ybase+120);
		text("1000m ~ 0m", xbase+50, ybase+140);
		text("< 0 m", xbase+50, ybase+160);
	}
	

}
