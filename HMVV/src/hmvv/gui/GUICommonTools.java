package hmvv.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.text.SimpleDateFormat;
import javax.swing.JTextField;

public class GUICommonTools {

	public static final Color ERROR_COLOR = new Color(255,204,204);
	public static final Color COMPLETE_COLOR = new Color(204,255,204);
	public static final Color WHITE_COLOR = Color.WHITE;
	public static final Color LIGHT_GRAY = new Color(240,240,240);
	public static final Color RUNNING_COLOR = new Color(255,255,204);
	public static final Color BLACK_COLOR = new Color(0,0,0);
	public static final Color WARNING_COLOR = new Color(255,200,0);
	
	public static final Color PROGRESS_BACKGROUND_COLOR = Color.YELLOW;
	public static final Color PROGRESS_FOREGROUND_COLOR = Color.GREEN.darker();
	
	public static Font TAHOMA_PLAIN_10 = new Font("Tahoma", Font.PLAIN, 10);
	public static Font TAHOMA_PLAIN_13 = new Font("Tahoma", Font.PLAIN, 13);
	public static Font TAHOMA_BOLD_11 = new Font("Tahoma", Font.BOLD, 11);
	public static Font TAHOMA_BOLD_12 = new Font("Tahoma", Font.BOLD, 12);
	public static Font TAHOMA_BOLD_13 = new Font("Tahoma", Font.BOLD, 13);
	public static Font TAHOMA_BOLD_14 = new Font("Tahoma", Font.BOLD, 14);
	public static Font TAHOMA_BOLD_17 = new Font("Tahoma", Font.BOLD, 17);
	public static Font TAHOMA_BOLD_20 = new Font("Tahoma", Font.BOLD, 20);
	
	public static final SimpleDateFormat extendedDateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static final SimpleDateFormat extendedDateFormat2 = new SimpleDateFormat("M/d/y HH:mm:ss");
	public static final SimpleDateFormat shortDateFormat = new SimpleDateFormat("yyyy-MM-dd");

	public static final String PIPELINE_INCOMPLETE_STATUS="Running";
	
	public static Rectangle getScreenBounds(Component parent){
	    GraphicsConfiguration gc = parent.getGraphicsConfiguration();
	    Insets insets = parent.getToolkit().getScreenInsets(gc);
	    Rectangle bounds = gc.getBounds();
	    return computeBounds(bounds, insets);
	}
	
	public static Rectangle getScreenBounds(){
	    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
	    Insets insets = Toolkit.getDefaultToolkit().getScreenInsets(ge.getDefaultScreenDevice().getDefaultConfiguration());
	    Rectangle bounds = ge.getDefaultScreenDevice().getDefaultConfiguration().getBounds();
	    return computeBounds(bounds, insets);
	}
	
	private static Rectangle computeBounds(Rectangle bounds, Insets insets) {
		bounds.x += insets.left;
	    bounds.y += insets.top;
	    bounds.width -= (insets.left + insets.right);
	    bounds.height -= (insets.top + insets.bottom);
	    return bounds;
	}
	
	public static Rectangle getBounds(Window window){
	    Rectangle bounds = new Rectangle();
	    Point topLeftCorner = window.getLocationOnScreen();
	    bounds.x = topLeftCorner.x;
	    bounds.y = topLeftCorner.y;
	    bounds.width = window.getSize().width;
	    bounds.height = window.getSize().height;
	    return bounds;
	}	

	public static int parseIntegerFromTextField(JTextField field, Integer defaultInt){
		String value = field.getText();
		Integer valueInt = null;
		if(value.equals("")){
			valueInt = defaultInt;
		}else{
			try{
				valueInt = Integer.parseInt(value);
			}catch(Exception e){
				return defaultInt;
			}
		}
		return valueInt;
	}


	public static double parseDoubleFromTextField(JTextField field, double defaultdouble){
		String value = field.getText();
		double valuedouble = 0.0;
		if(value.equals("")){
			valuedouble = defaultdouble;
		}else{
			try{
				valuedouble = Double.parseDouble(value);
			}catch(Exception e){
				return defaultdouble;
			}
		}
		return valuedouble;
	}
}
