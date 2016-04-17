package module6;
//Java utilities libraries
import java.util.List;
//Unfolding libraries
import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.marker.SimpleLinesMarker;
//Processing library
import processing.core.PConstants;
import processing.core.PGraphics;

/** 
 * A class to represent AirportMarkers on a world map.
 *   
 * @author Adam Setters and the UC San Diego Intermediate Software Development
 * MOOC team
 *
 */
public class AirportMarker extends CommonMarker {
	
	public static int ELL_SIZE = 5;  // The size of the ellipse marker
	public static List<SimpleLinesMarker> routes;
	
	public AirportMarker(Feature city) {
		super(((PointFeature)city).getLocation(), city.getProperties());
	
	}
	
	@Override
	public void drawMarker(PGraphics pg, float x, float y) {
		// offset -12 ~ +13
		float offset = getOffset();
		// altitude -1226 ~ +14472
		int altitude = getAltitude();
		if (altitude > 3000) {
			pg.fill(255,0,0);//red
		} else if (altitude > 2000 ) {
			pg.fill(255,128,0);//orange
		} else if (altitude > 1000) {
			pg.fill(255,255,51);//yellow
		} else if (altitude > 0) {
			pg.fill(128,255,0);//green
		} else {
			pg.fill(51,153,255);//blue
		}
		
		pg.ellipse(x, y, 10, 10);
		
		
	}

	@Override
	public void showTitle(PGraphics pg, float x, float y) {
		 // show rectangle with title
		String name = getCity() + " " + getCountry() + " ";
		String TZ = "Time Zone: " + getTimezone();
		
		pg.pushStyle();
		
		pg.fill(255, 255, 255);
		pg.textSize(12);
		pg.rectMode(PConstants.CORNER);
		pg.rect(x, y-ELL_SIZE-39, Math.max(pg.textWidth(name), pg.textWidth(TZ)) + 6, 39);
		pg.fill(0, 0, 0);
		pg.textAlign(PConstants.LEFT, PConstants.TOP);
		pg.text(name, x+3, y-ELL_SIZE-33);
		pg.text(TZ, x+3, y - ELL_SIZE -18);
		
		pg.popStyle();
		// show routes
		
		
	}
	private String getCity()
	{
		return getStringProperty("name");
	}
	
	private String getCountry()
	{
		return getStringProperty("country");
	}
	
	private String getTimezone()
	{
		return getStringProperty("timezone");
	}
	private float getOffset()
	{
		return Float.parseFloat(getStringProperty("offset"));
	}
	private int getAltitude()
	{
		return Integer.parseInt(getStringProperty("altitude"));
	}
	
	
}
