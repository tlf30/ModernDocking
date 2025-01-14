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
package ModernDocking.internal;

import ModernDocking.Dockable;
import ModernDocking.RootDockingPanel;
import ModernDocking.util.SlideBorder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

public class DockedUnpinnedPanel extends JPanel implements ComponentListener, MouseMotionListener {
	private final RootDockingPanel root;
	private final DockableToolbar toolbar;

	private boolean configured = false;

	public DockedUnpinnedPanel(Dockable dockable, RootDockingPanel root, DockableToolbar toolbar) {
		this.root = root;
		this.toolbar = toolbar;

		root.addComponentListener(this);
		addComponentListener(this);

		setLayout(new GridBagLayout());

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;


		DockedSimplePanel panel = new DockedSimplePanel(DockingInternal.getWrapper(dockable));
		SlideBorder slideBorder = new SlideBorder(toolbar.getDockedLocation());

		if (toolbar.getDockedLocation() == DockableToolbar.Location.SOUTH) {
			gbc.weightx = 1.0;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			add(slideBorder, gbc);
			gbc.weightx = 1.0;
			gbc.weighty = 1.0;
			gbc.fill = GridBagConstraints.BOTH;
			gbc.gridy++;
			add(panel, gbc);
		}
		else if (toolbar.getDockedLocation() == DockableToolbar.Location.EAST) {
			gbc.weighty = 1.0;
			gbc.fill = GridBagConstraints.VERTICAL;
			add(slideBorder, gbc);
			gbc.weightx = 1.0;
			gbc.weighty = 1.0;
			gbc.fill = GridBagConstraints.BOTH;
			gbc.gridx++;
			add(panel, gbc);
		}
		else {
			gbc.weightx = 1.0;
			gbc.weighty = 1.0;
			gbc.fill = GridBagConstraints.BOTH;
			add(panel, gbc);
			gbc.weightx = 0.0;
			gbc.weighty = 1.0;
			gbc.fill = GridBagConstraints.VERTICAL;
			gbc.gridx++;
			add(slideBorder, gbc);
		}

		slideBorder.addMouseMotionListener(this);
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);

		setLocationAndSize(0);

		if (!configured) {
			configured = true;
		}
	}

	private void setLocationAndSize(int widthDifference) {
		Point toolbarLocation = toolbar.getLocation();
		SwingUtilities.convertPointToScreen(toolbarLocation, toolbar.getParent());

		Dimension toolbarSize = toolbar.getSize();

		// this panel will be in a layered pane without a layout manager
		// we must configure the size and position ourselves
		if (toolbar.isVertical()) {
			int width = (int) (root.getWidth() / 4.0);
			int height = toolbarSize.height;

			if (configured) {
				width = getWidth() + widthDifference;
			}

			Point location = new Point(toolbarLocation.x + toolbarSize.width, toolbarLocation.y);
			Dimension size = new Dimension(width, height);

			if (toolbar.getDockedLocation() == DockableToolbar.Location.EAST) {
				location.x = toolbarLocation.x - width;
			}

			SwingUtilities.convertPointFromScreen(location, getParent());

			setLocation(location);
			setSize(size);
		}
		else {
			int width = toolbarSize.width;
			int height = (int) (root.getHeight() / 4.0);

			if (configured) {
				height = getHeight() + widthDifference;
			}

			Point location = new Point(toolbarLocation.x, toolbarLocation.y - height);
			Dimension size = new Dimension(width, height);

			SwingUtilities.convertPointFromScreen(location, getParent());

			setLocation(location);
			setSize(size);
		}

		revalidate();
		repaint();
	}

	@Override
	public void componentResized(ComponentEvent e) {
		// component has resized, update the location and size of the unpinned panel
		if (e.getComponent() == root) {
			setLocationAndSize(0);
		}
	}

	@Override
	public void componentMoved(ComponentEvent e) {
	}

	@Override
	public void componentShown(ComponentEvent e) {

	}

	@Override
	public void componentHidden(ComponentEvent e) {
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// dragging the divider, update the size and location of the unpinned panel
		if (toolbar.getDockedLocation() == DockableToolbar.Location.SOUTH) {
			setLocationAndSize(-e.getY());
		}
		else if (toolbar.getDockedLocation() == DockableToolbar.Location.WEST) {
			setLocationAndSize(e.getX());
		}
		else {
			setLocationAndSize(-e.getX());
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
	}
}
