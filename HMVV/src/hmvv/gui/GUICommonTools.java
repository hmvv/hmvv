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
import java.text.SimpleDateFormat;

import javax.swing.JFrame;

public class GUICommonTools {
//	public static final Color ERROR_COLOR = new Color(255,51,51);
//	public static final Color COMPLETE_COLOR = new Color(102,255,102);
	public static final Color ERROR_COLOR = new Color(255,204,204);
	public static final Color COMPLETE_COLOR = new Color(204,255,204);
	public static final Color WHITE_COLOR = Color.WHITE;
	public static final Color RUNNING_COLOR = new Color(255,255,204);
	
	public static final Color PROGRESS_BACKGROUND_COLOR = Color.YELLOW;
	public static final Color PROGRESS_FOREGROUND_COLOR = Color.GREEN.darker();
	
	public static Font TAHOMA_PLAIN_13 = new Font("Tahoma", Font.BOLD, 13);
	
	public static Font TAHOMA_BOLD_11 = new Font("Tahoma", Font.BOLD, 11);
	public static Font TAHOMA_BOLD_12 = new Font("Tahoma", Font.BOLD, 12);
	public static Font TAHOMA_BOLD_13 = new Font("Tahoma", Font.BOLD, 13);
	public static Font TAHOMA_BOLD_14 = new Font("Tahoma", Font.BOLD, 14);
	
	//public static final SimpleDateFormat extendedDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static final SimpleDateFormat extendedDateFormat = new SimpleDateFormat("M/d/y HH:mm:ss");
	public static final SimpleDateFormat shortDateFormat = new SimpleDateFormat("yyyy-MM-dd");
	
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
	
	public static Rectangle getBounds(JFrame jframe){
	    Rectangle bounds = new Rectangle();
	    Point topLeftCorner = jframe.getLocationOnScreen();
	    bounds.x = topLeftCorner.x;
	    bounds.y = topLeftCorner.y;
	    bounds.width = jframe.getSize().width;
	    bounds.height = jframe.getSize().height;
	    return bounds;
	}	
	
	public static String abbreviationtoLetter(String mutation){
		return mutation
			.replaceAll("Ala", "A")
			.replaceAll("Cys", "C")
			.replaceAll("Glu", "E")
			.replaceAll("Phe", "F")
			.replaceAll("Gly", "G")
			.replaceAll("His", "H")
			.replaceAll("Ile", "I")
			.replaceAll("Lys", "K")
			.replaceAll("Leu", "L")
			.replaceAll("Met", "M")
			.replaceAll("Asn", "N")
			.replaceAll("Hyp", "O")
			.replaceAll("Pro", "P")
			.replaceAll("Gln", "Q")
			.replaceAll("Arg", "R")
			.replaceAll("Ser", "S")
			.replaceAll("Thr", "T")
			.replaceAll("Glp", "U")
			.replaceAll("Val", "V")
			.replaceAll("Trp", "W")
			.replaceAll("Ter", "X")
			.replaceAll("Tyr", "Y");
	}
}
