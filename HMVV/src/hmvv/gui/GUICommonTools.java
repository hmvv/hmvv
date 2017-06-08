package hmvv.gui;

import java.awt.Component;
import java.awt.Font;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;

public class GUICommonTools {

	public static Font TAHOMA_PLAIN_13 = new Font("Tahoma", Font.BOLD, 13);
	
	public static Font TAHOMA_BOLD_11 = new Font("Tahoma", Font.BOLD, 11);
	public static Font TAHOMA_BOLD_12 = new Font("Tahoma", Font.BOLD, 12);
	public static Font TAHOMA_BOLD_13 = new Font("Tahoma", Font.BOLD, 13);
	public static Font TAHOMA_BOLD_14 = new Font("Tahoma", Font.BOLD, 14);
	
	public static Rectangle getBounds(Component componentOrNull){
		Insets insets;
	    Rectangle bounds;
	    if (componentOrNull == null) {
	        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
	        insets = Toolkit.getDefaultToolkit().getScreenInsets(ge.getDefaultScreenDevice().getDefaultConfiguration());
	        bounds = ge.getDefaultScreenDevice().getDefaultConfiguration().getBounds();
	    } else {
	        GraphicsConfiguration gc = componentOrNull.getGraphicsConfiguration();
	        insets = componentOrNull.getToolkit().getScreenInsets(gc);
	        bounds = gc.getBounds();
	    }
	    bounds.x += insets.left;
	    bounds.y += insets.top;
	    bounds.width -= (insets.left + insets.right);
	    bounds.height -= (insets.top + insets.bottom);
	    return bounds;
	}
}
