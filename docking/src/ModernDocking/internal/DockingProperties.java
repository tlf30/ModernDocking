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

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class DockingProperties {
	private static final String handlesBackgroundColorKey = "Docking.handles.background";
	private static final String handlesBackgroundBorderColorKey = "Docking.handles.background.border";
	private static final String handlesOutlineColorKey = "Docking.handles.outline";
	private static final String handlesFillColorKey = "Docking.handles.fill";
	private static final String overlayBackgroundColorKey = "Docking.overlay.color";
	private static final String overlayBorderColorKey = "Docking.overlay.border.color";
	private static final String overlayAlphaKey = "Docking.overlay.alpha";

	private static final String titlebarBackgroundEnabledKey = "Docking.titlebar.border.enabled";
	private static final String titlebarBackgroundColorKey = "Docking.titlebar.background.color";
	private static final String titlebarBorderColorKey = "Docking.titlebar.border.color";
	private static final String titlebarBorderSizeKey = "Docking.titlebar.border.size";

	private final Map<String, Color> lightColors = Map.ofEntries(
			Map.entry(handlesBackgroundColorKey, new Color(0xf2f2f2)),
			Map.entry(handlesBackgroundBorderColorKey, new Color(0xC2C2C2)),
			Map.entry(handlesOutlineColorKey, new Color(0x000000)),
			Map.entry(handlesFillColorKey, new Color(0x4E9DE7)),
			Map.entry(overlayBackgroundColorKey, new Color(0x0063d4)),
			Map.entry(overlayBorderColorKey, new Color(0xC2C2C2)),
			Map.entry(titlebarBackgroundColorKey, new Color(0xffffff)),
			Map.entry(titlebarBorderColorKey, new Color(0xC2C2C2))
	);
	private final Map<String, Color> darkColors = Map.ofEntries(
			Map.entry(handlesBackgroundColorKey, new Color(0x3C3F41)),
			Map.entry(handlesBackgroundBorderColorKey, new Color(0x616365)),
			Map.entry(handlesOutlineColorKey, new Color(0x8C8C8C)),
			Map.entry(handlesFillColorKey, new Color(0x557394)),
			Map.entry(overlayBackgroundColorKey, new Color(0x42c0ff)),
			Map.entry(overlayBorderColorKey, new Color(0x111111)),
			Map.entry(titlebarBackgroundColorKey, new Color(0x46494b)),
			Map.entry(titlebarBorderColorKey, new Color(0x666666))
	);

	private final boolean defaultTitlebarBorderEnabled = true;
	private final int defaultTitlebarBorderSize = 1;

	private final int lightOverlayAlpha = 75;
	private final int darkOverlayAlpha = 85;

	private final Map<String, Color> currentColors = new HashMap<>(lightColors);
	private int overlayAlpha = lightOverlayAlpha;

	private boolean titlebarBorderEnabled = true;
	private int titlebarBorderSize = 1;

	private static final DockingProperties properties = new DockingProperties();

	private DockingProperties() {
		updateProperties();

		UIManager.addPropertyChangeListener(e -> {
			if (e.getPropertyName().equals("lookAndFeel")) {
				updateProperties();
			}
		});
	}

	private void updateProperties() {
		boolean isDarkTheme = UIManager.getLookAndFeel().getName().toLowerCase().contains("dark");

		for (String key : currentColors.keySet()) {
			Color color = UIManager.getColor(key);

			if (color != null) {
				currentColors.put(key, color);
			}
			else {
				if (isDarkTheme) {
					currentColors.put(key, darkColors.get(key));
				}
				else {
					currentColors.put(key, lightColors.get(key));
				}
			}
		}

		if (UIManager.get(overlayAlphaKey) != null) {
			overlayAlpha = UIManager.getInt(overlayAlphaKey);
		}
		else {
			if (isDarkTheme) {
				overlayAlpha = darkOverlayAlpha;
			}
			else {
				overlayAlpha = lightOverlayAlpha;
			}
		}

		if (UIManager.get(titlebarBackgroundEnabledKey) != null) {
			titlebarBorderEnabled = UIManager.getBoolean(titlebarBackgroundEnabledKey);
		}
		else {
			titlebarBorderEnabled = defaultTitlebarBorderEnabled;
		}

		if (UIManager.get(titlebarBorderSizeKey) != null) {
			titlebarBorderSize = UIManager.getInt(titlebarBorderSize);
		}
		else {
			titlebarBorderSize = defaultTitlebarBorderSize;
		}
	}

	public static Color getHandlesBackground() {
		return properties.currentColors.get(handlesBackgroundColorKey);
	}

	public static Color getHandlesBackgroundBorder() {
		return properties.currentColors.get(handlesBackgroundBorderColorKey);
	}

	public static Color getHandlesOutline() {
		return properties.currentColors.get(handlesOutlineColorKey);
	}

	public static Color getHandlesFill() {
		return properties.currentColors.get(handlesFillColorKey);
	}

	public static Color getDockingOverlay() {
		Color color = properties.currentColors.get(overlayBackgroundColorKey);
		return new Color(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, properties.overlayAlpha / 255.0f);
	}

	public static Color getDockingOverlayBorder() {
		Color color = properties.currentColors.get(overlayBorderColorKey);
		return new Color(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, properties.overlayAlpha / 255.0f);
	}

	public static boolean isTitlebarBorderEnabled() {
		return properties.titlebarBorderEnabled;
	}

	public static Color getTitlebarBackgroundColor() {
		return properties.currentColors.get(titlebarBackgroundColorKey);
	}

	public static int getTitlebarBorderSize() {
		return properties.titlebarBorderSize;
	}

	public static Color getTitlebarBorderColor() {
		return properties.currentColors.get(titlebarBorderColorKey);
	}
}