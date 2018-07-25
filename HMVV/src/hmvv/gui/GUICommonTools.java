package hmvv.gui;

import java.awt.Component;
import java.awt.Font;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;

import javax.swing.JFrame;

public class GUICommonTools {

	public static Font TAHOMA_PLAIN_13 = new Font("Tahoma", Font.BOLD, 13);
	
	public static Font TAHOMA_BOLD_11 = new Font("Tahoma", Font.BOLD, 11);
	public static Font TAHOMA_BOLD_12 = new Font("Tahoma", Font.BOLD, 12);
	public static Font TAHOMA_BOLD_13 = new Font("Tahoma", Font.BOLD, 13);
	public static Font TAHOMA_BOLD_14 = new Font("Tahoma", Font.BOLD, 14);
	
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
