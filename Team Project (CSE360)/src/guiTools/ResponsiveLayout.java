package guiTools;

import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Scale;

/*******
 * <p> Title: ResponsiveLayout Class. </p>
 * 
 * <p> Description: A small shared utility that makes a Foundations-style page -- one built as
 * a Pane with every widget's position and size hardcoded for one specific design size (usually
 * FoundationsMain.WINDOW_WIDTH x WINDOW_HEIGHT) -- resize proportionally when its Stage is
 * resized, instead of staying pinned at its original size while the Stage shows empty space
 * around it.
 * 
 * It does this without changing how any page lays out its own widgets.  It pins the page's
 * root Pane at its original design size (so Scene's normal root-stretching behavior cannot
 * change it) and then applies a single Scale transform to that Pane, keeping the transform's x
 * and y factors in sync with how much larger or smaller the Scene has become than the page's
 * original design size.  Every existing page's widget positions, fonts, and event handlers are
 * untouched; each page's View class only needs one extra call, right after its Scene is
 * created, to opt in. </p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Ojas Patil
 * 
 * @version 1.00		2026-10-22 Initial version: app-wide responsive resizing for TP2
 */
public class ResponsiveLayout {
	
	/**
	 * Default constructor is not used; every method here is static.
	 */
	private ResponsiveLayout() {
	}
	
	/**********
	 * <p> Method: makeResponsive(Pane rootPane, Scene scene, double baseWidth,
	 * 		double baseHeight) </p>
	 * 
	 * <p> Description: Call this once, right after a page's Scene is created (and after that
	 * page's root Pane has been assigned to it), passing the size the page's widgets were
	 * originally laid out for -- usually FoundationsMain.WINDOW_WIDTH and WINDOW_HEIGHT.  From
	 * then on, whenever the Stage is resized, the whole page scales up or down together so it
	 * fills the available space, rather than staying a fixed size with empty space around it or
	 * clipping. </p>
	 * 
	 * @param rootPane the Pane that holds every widget on the page
	 * 
	 * @param scene the Scene that rootPane has been set as the root of
	 * 
	 * @param baseWidth the width, in pixels, the page's widgets were laid out for
	 * 
	 * @param baseHeight the height, in pixels, the page's widgets were laid out for
	 */
	public static void makeResponsive(Pane rootPane, Scene scene, double baseWidth,
			double baseHeight) {
		// Pin the Pane at its original design size.  Without this, JavaFX automatically
		// stretches a Scene's root node to match the Scene's current size, which would fight
		// with the Scale transform below and distort the page instead of simply enlarging it.
		rootPane.setMinSize(baseWidth, baseHeight);
		rootPane.setMaxSize(baseWidth, baseHeight);
		
		// One Scale transform, anchored at the top-left corner (0, 0), grows or shrinks
		// everything inside the Pane together, in the same proportions the page was designed
		// with, so nothing inside the page needs its own position or size recalculated.
		Scale scale = new Scale(1, 1, 0, 0);
		rootPane.getTransforms().add(scale);
		
		scene.widthProperty().addListener((_, _, newWidth) ->
				scale.setX(Math.max(newWidth.doubleValue(), 1) / baseWidth));
		scene.heightProperty().addListener((_, _, newHeight) ->
				scale.setY(Math.max(newHeight.doubleValue(), 1) / baseHeight));
	}
}
