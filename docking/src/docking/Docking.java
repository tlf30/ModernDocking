/*
Copyright (c) 2022 Andrew Auclair

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */
package docking;

import exception.DockableRegistrationFailureException;
import floating.FloatListener;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

// TODO we need to check if the window loses focus and kill the floating dialog. other wise strange things happen. -- Think I've gotten pretty close on this, requires more testing

// TODO persistence (saving and loading)

// TODO perspectives/views/layouts, probably calling them "layouts"

// TODO programmatic layout. we can dock/undock pretty well from a user perspective. now we need that ability from the programming side. -- done for root, need to allow the app to get a panel and call its dock/undock functions directly

// TODO allow the app to set the divider resize weight somehow
// TODO remember what the divider position is. For example, docking 2 panels in a split, moving the split and then docking a new panel with one of them. this resets the split instead of keeping the position

// TODO make empty root panel look better and add a "RC" Root Center docking handle in case the root is empty

// Main class for the docking framework
// register and dock/undock dockables here
public class Docking {
	public static Dimension frameBorderSize = new Dimension(0, 0);

	private static final Map<String, DockableWrapper> dockables = new HashMap<>();

	private static final Map<JFrame, RootDockingPanel> rootPanels = new HashMap<>();

	private static JFrame frameToDispose = null;

	private static JFrame mainFrame = null;

	public static void setMainFrame(JFrame frame) {
		mainFrame = frame;
	}

	public static void registerDockable(Dockable dockable) {
		if (dockables.containsKey(dockable.persistentID())) {
			throw new DockableRegistrationFailureException("Registration for Dockable failed. Persistent ID " + dockable.persistentID() + " already exists.");
		}
		dockables.put(dockable.persistentID(), new DockableWrapper(dockable));
	}

	// Dockables must be deregistered so it can be properly disposed
	public static void deregisterDockable(Dockable dockable) {
		dockables.remove(dockable.persistentID());
	}

	// package private registration function for DockingPanel
	public static void registerDockingPanel(RootDockingPanel panel, JFrame parent) {
		if (frameBorderSize.height == 0) {
			SwingUtilities.invokeLater(() -> {
				Dimension size = parent.getSize();
				Dimension contentsSize = parent.getContentPane().getSize();
				Insets insets = parent.getContentPane().getInsets();

				frameBorderSize = new Dimension(size.width - contentsSize.width - insets.left, size.height - contentsSize.height - insets.top);

				System.out.println("size: " + size + "\ncontents size: " + contentsSize + "\ninsets: " + insets + "\nframe border size: " + frameBorderSize);
			});
		}

		if (rootPanels.containsKey(parent)) {
			throw new DockableRegistrationFailureException("RootDockingPanel already registered for frame: " + parent);
		}
		rootPanels.put(parent, panel);
		FloatListener.registerDockingFrame(parent, panel);
	}

	// TODO add a possible listener for this. I'd like a way to listen for panels being auto undocked and being able to redock them somewhere else depending on what they are
	static void deregisterDockingPanel(JFrame parent) {
		if (rootPanels.containsKey(parent)) {
			RootDockingPanel root = rootPanels.get(parent);

			undockComponents(root);
		}

		rootPanels.remove(parent);
	}

	private static void undockComponents(Container container) {
		for (Component component : container.getComponents()) {
			if (component instanceof Container) {
				undockComponents((Container) component);
			}
			else if (component instanceof Dockable) {
				undock((Dockable) component);
			}
		}
	}
	public static JFrame findRootAtScreenPos(Point screenPos) {
		for (JFrame frame : rootPanels.keySet()) {
			Rectangle bounds = new Rectangle(frame.getX(), frame.getY(), frame.getWidth(), frame.getHeight());

			if (bounds.contains(screenPos) && frame.isVisible()) {
				return frame;
			}
		}
		return null;
	}

	public static JFrame findFrameForDockable(Dockable dockable) {
		Container parent = ((Component) dockable).getParent();

		while (parent != null) {
			if (parent instanceof JFrame) {
				if (rootPanels.containsKey(parent)) {
					return (JFrame) parent;
				}
			}
			parent = parent.getParent();
		}
		return null;
	}

	public static RootDockingPanel rootForFrame(JFrame frame) {
		if (rootPanels.containsKey(frame)) {
			return rootPanels.get(frame);
		}
		return null;
	}

	public static Dockable findDockableAtScreenPos(Point screenPos) {
		JFrame frame = findRootAtScreenPos(screenPos);

		// no frame found at the location, return null
		if (frame == null) {
			return null;
		}

		Point framePoint = new Point(screenPos);
		SwingUtilities.convertPointFromScreen(framePoint, frame);

		Component component = SwingUtilities.getDeepestComponentAt(frame, framePoint.x, framePoint.y);

		// no component found at the position, return null
		if (component == null) {
			return null;
		}

		while (!(component instanceof Dockable) && component.getParent() != null) {
			component = component.getParent();
		}

		// didn't find a Dockable, return null
		if (!(component instanceof Dockable)) {
			return null;
		}
		return (Dockable) component;
	}

	public static DockingPanel findDockingPanelAtScreenPos(Point screenPos) {
		JFrame frame = findRootAtScreenPos(screenPos);

		// no frame found at the location, return null
		if (frame == null) {
			return null;
		}

		Point framePoint = new Point(screenPos);
		SwingUtilities.convertPointFromScreen(framePoint, frame);

		Component component = SwingUtilities.getDeepestComponentAt(frame, framePoint.x, framePoint.y);

		// no component found at the position, return null
		if (component == null) {
			return null;
		}

		while (!(component instanceof DockingPanel) && component.getParent() != null) {
			component = component.getParent();
		}

		// didn't find a Dockable, return null
		if (!(component instanceof DockingPanel)) {
			return null;
		}
		return (DockingPanel) component;
	}

	// TODO allow setting the split weight somehow
	public static void dock(Dockable dockable, JFrame frame) {
		dock(dockable, frame, DockingRegion.CENTER);
	}

	public static void dock(Dockable dockable, JFrame frame, DockingRegion region) {
		if (frameToDispose != null) {
			frameToDispose.dispose();
			frameToDispose = null;
		}

		RootDockingPanel root = rootPanels.get(frame);

		if (root == null) {
			throw new DockableRegistrationFailureException("Frame does not have a RootDockingPanel: " + frame);
		}

		root.dock(dockable, region);
	}

	public static void dock(Dockable source, Dockable target, DockingRegion region) {
		DockableWrapper wrapper = Docking.getWrapper(target);
		wrapper.getParent().dock(source, region);
	}

	public static void undock(Dockable dockable) {
		DockableWrapper wrapper = getWrapper(dockable);
		wrapper.getParent().undock(dockable);
	}

	public static boolean canDisposeFrame(JFrame frame) {
		return frame != mainFrame;
	}

	public static Dockable getDockable(String persistentID) {
		if (dockables.containsKey(persistentID)) {
			return dockables.get(persistentID).getDockable();
		}
		return null;
	}

	// internal function to get the dockable wrapper
	static DockableWrapper getWrapper(Dockable dockable) {
		if (dockables.containsKey(dockable.persistentID())) {
			return dockables.get(dockable.persistentID());
		}
		throw new DockableRegistrationFailureException("Dockable with Persistent ID " + dockable.persistentID() + " has not been registered.");
	}
}
