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
import ModernDocking.Docking;
import ModernDocking.exception.DockableRegistrationFailureException;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DockingInternal {
	private static final Map<String, DockableWrapper> dockables = new HashMap<>();

	// register a dockable with the framework
	public static void registerDockable(Dockable dockable) {
		if (dockables.containsKey(dockable.getPersistentID())) {
			throw new DockableRegistrationFailureException("Registration for Dockable failed. Persistent ID " + dockable.getPersistentID() + " already exists.");
		}
		if (dockable.getTabText() == null) {
			throw new RuntimeException("Dockable '" + dockable.getPersistentID() + "' should not return 'null' for tabText()");
		}
		dockables.put(dockable.getPersistentID(), new DockableWrapper(dockable));
	}

	// Dockables must be deregistered so it can be properly disposed
	public static void deregisterDockable(Dockable dockable) {
		getWrapper(dockable).removedListeners();
		dockables.remove(dockable.getPersistentID());
	}

	// internal function to get the dockable wrapper
	public static DockableWrapper getWrapper(Dockable dockable) {
		if (dockables.containsKey(dockable.getPersistentID())) {
			return dockables.get(dockable.getPersistentID());
		}
		throw new DockableRegistrationFailureException("Dockable with Persistent ID " + dockable.getPersistentID() + " has not been registered.");
	}

	public static Dockable getDockable(String persistentID) {
		if (dockables.containsKey(persistentID)) {
			return dockables.get(persistentID).getDockable();
		}
		throw new DockableRegistrationFailureException("Dockable with Persistent ID " + persistentID + " has not been registered.");
	}

	public static void fireDockedEventForFrame(Window window) {
		// everything has been restored, go through the list of dockables and fire docked events for the ones that are docked
		List<DockableWrapper> wrappers = dockables.values().stream()
				.filter(wrapper -> wrapper.getWindow() == window)
				.collect(Collectors.toList());

		for (DockableWrapper wrapper : wrappers) {
			DockingListeners.fireDockedEvent(wrapper.getDockable());
		}
	}

	public static void fireDockedEventForAll() {
		// everything has been restored, go through the list of dockables and fire docked events for the ones that are docked
		for (DockableWrapper wrapper : dockables.values()) {
			if (Docking.isDocked(wrapper.getDockable())) {
				DockingListeners.fireDockedEvent(wrapper.getDockable());
			}
		}
	}
}
