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
package ModernDocking.layouts;

import ModernDocking.DockingRegion;

import javax.swing.*;

// Utility to help create layouts without directly applying them to the actual app
public class DockingLayoutBuilder {
	private DockingLayoutRootNode rootNode = new DockingLayoutRootNode();

	public DockingLayoutBuilder(String firstID) {
		rootNode.dock(firstID, DockingRegion.CENTER, 0.0);
	}

	public DockingLayoutBuilder dock(String sourceID, String targetID) {
		return dock(sourceID, targetID, DockingRegion.CENTER);
	}

	public DockingLayoutBuilder dock(String sourceID, String targetID, DockingRegion region) {
		return dock(sourceID, targetID, region, 0.5);
	}

	public DockingLayoutBuilder dock(String sourceID, String targetID, DockingRegion region, double dividerProportion) {
		DockingLayoutNode node = findNode(targetID);

		if (exists(sourceID)) {
			throw new RuntimeException("Dockable already in layout: " + sourceID);
		}
		node.dock(sourceID, region, dividerProportion);

		return this;
	}

	public DockingLayoutBuilder dockToRoot(String persistentID, DockingRegion region) {
		return dockToRoot(persistentID, region, 0.25);
	}

	public DockingLayoutBuilder dockToRoot(String persistentID, DockingRegion region, double dividerProportion) {
		if (exists(persistentID)) {
			throw new RuntimeException("Dockable already in layout: " + persistentID);
		}

		int orientation = region == DockingRegion.EAST || region == DockingRegion.WEST ? JSplitPane.HORIZONTAL_SPLIT : JSplitPane.VERTICAL_SPLIT;

//		rootNode = new DockingSplitPanelNode(new DockingSimplePanelNode(persistentID), rootNode, orientation, dividerProportion);
		rootNode.dock(persistentID, region, dividerProportion);
		return this;
	}

	public WindowLayout build() {
		return new WindowLayout(rootNode.getNode());
	}

	public DockingLayoutNode findNode(String persistentID) {
		DockingLayoutNode node = rootNode.findNode(persistentID);

		if (node == null) {
			throw new RuntimeException("No node for dockable ID found: " + persistentID);
		}
		return node;
	}

	private boolean exists(String persistentID) {
		DockingLayoutNode node = rootNode.findNode(persistentID);

		return node != null;
	}
}
