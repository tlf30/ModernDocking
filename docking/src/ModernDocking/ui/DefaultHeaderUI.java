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
package ModernDocking.ui;

import ModernDocking.internal.DockingProperties;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

// this can be replaced by the user or with the docking-ui FlatLaf header UI
public class DefaultHeaderUI extends JPanel implements DockingHeaderUI, AncestorListener {
	private final HeaderController headerController;
	private final HeaderModel headerModel;

	protected final JLabel titleLabel = new JLabel();
	protected final JButton settings = new JButton();
	protected final JButton close = new JButton();
	protected final JLabel maximizedIndicator = new JLabel("Maximized");

	private final JPopupMenu settingsMenu = new JPopupMenu();

	private final JMenuItem pinned = new JMenuItem("Pinned");
	private final JMenuItem unpinned = new JMenuItem("Unpinned");
	private final JMenuItem window = new JMenuItem("Window");
	private final JCheckBoxMenuItem maximizeOption = new JCheckBoxMenuItem("Maximize");

	private boolean initialized = false;

	public DefaultHeaderUI(HeaderController headerController, HeaderModel headerModel) {
		this.headerController = headerController;
		this.headerModel = headerModel;

		setOpaque(true);

		// delay the actual init of the UI in case the dockable object is partially constructed
		JComponent component = (JComponent) headerModel.dockable;
		component.addAncestorListener(this);
	}

	private void init() {
		if (initialized) {
			return;
		}
		initialized = true;

		try {
			settings.setIcon(new ImageIcon(getClass().getResource("/icons/settings.png")));
			close.setIcon(new ImageIcon(getClass().getResource("/icons/close.png")));
		}
		catch (Exception ignored) {
		}

		settings.addActionListener(e -> this.settingsMenu.show(settings, settings.getWidth(), settings.getHeight()));
		close.addActionListener(e -> headerController.close());

		setupButton(settings);
		setupButton(close);

		Color color = DockingProperties.getTitlebarBackgroundColor();
		setBackground(color);

		if (DockingProperties.isTitlebarBorderEnabled()) {
			Border border = BorderFactory.createLineBorder(DockingProperties.getTitlebarBorderColor(), DockingProperties.getTitlebarBorderSize());

			Border bo = BorderFactory.createMatteBorder(0, 0, DockingProperties.getTitlebarBorderSize(), 0, DockingProperties.getTitlebarBorderColor());

			setBorder(bo);
		}

		UIManager.addPropertyChangeListener(e -> {
			if ("lookAndFeel".equals(e.getPropertyName())) {
				Color bg = DockingProperties.getTitlebarBackgroundColor();
				SwingUtilities.invokeLater(() -> {
					setBackground(bg);

					if (DockingProperties.isTitlebarBorderEnabled()) {
						Border border = BorderFactory.createLineBorder(DockingProperties.getTitlebarBorderColor(), DockingProperties.getTitlebarBorderSize());
						setBorder(border);
					}
				});

			}
			else if (e.getPropertyName().equals("ModernDocking.titlebar.background")) {
				Color bg = DockingProperties.getTitlebarBackgroundColor();
				SwingUtilities.invokeLater(() -> setBackground(bg));
			}
		});

		setLayout(new GridBagLayout());

		GridBagConstraints gbc = new GridBagConstraints();

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(1, 6, 1, 2);

		JLabel iconLabel = new JLabel(headerModel.icon());
		add(iconLabel, gbc);
		gbc.gridx++;

		gbc.insets = new Insets(0, 0, 0, 0);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;

		titleLabel.setText(headerModel.titleText());
		titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 0));
		titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD));
		titleLabel.setMinimumSize(new Dimension(0, 28));
		titleLabel.setPreferredSize(new Dimension(0, 28));

		add(titleLabel, gbc);

		gbc.gridx++;
		gbc.weightx = 0.2;

		maximizedIndicator.setVisible(false);
		maximizedIndicator.setFont(maximizedIndicator.getFont().deriveFont(Font.BOLD));

		add(maximizedIndicator, gbc);
		gbc.gridx++;
		gbc.fill = GridBagConstraints.NONE;
		gbc.weightx = 0;

		if (headerModel.hasMoreOptions() || headerModel.isMaximizeAllowed() || headerModel.isPinnedAllowed() || headerModel.isFloatingAllowed()) {
			addOptions();

			add(settings, gbc);
			gbc.gridx++;
		}
		if (headerModel.isCloseAllowed()) {
			add(close, gbc);
			gbc.gridx++;
		}
	}

	private void addOptions() {
		headerModel.addMoreOptions(settingsMenu);

		if (settingsMenu.getComponentCount() > 0) {
			settingsMenu.addSeparator();
		}

		window.setEnabled(headerModel.isFloatingAllowed());

		pinned.addActionListener(e -> headerController.pinDockable());
		unpinned.addActionListener(e -> headerController.unpinDockable());
		window.addActionListener(e -> headerController.newWindow());

		JMenu viewMode = new JMenu("View Mode");
		viewMode.add(pinned);
		viewMode.add(unpinned);
		viewMode.add(window);

		settingsMenu.add(viewMode);
		settingsMenu.addSeparator();

		settingsMenu.add(maximizeOption);

		maximizeOption.addActionListener(e -> {
			boolean maxed = headerModel.isMaximized();

			maximizeOption.setSelected(!maxed);
			maximizedIndicator.setVisible(!maxed);

			if (maxed) {
				headerController.minimize();
			}
			else {
				headerController.maximize();
			}
		});
	}

	private void setupButton(JButton button) {
		Color color = DockingProperties.getTitlebarBackgroundColor();
		button.setBackground(color);
		button.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		button.setFocusable(false);
		button.setOpaque(false);
		button.setContentAreaFilled(false);

		button.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				button.setContentAreaFilled(true);
				button.setOpaque(true);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				button.setContentAreaFilled(false);
				button.setOpaque(false);
			}
		});
	}

	@Override
	public void setBackground(Color bg) {
		super.setBackground(bg);

		if (close != null) close.setBackground(bg);
		if (settings != null) settings.setBackground(bg);
	}

	@Override
	public void update() {
		maximizedIndicator.setVisible(headerModel.isMaximized());
		maximizeOption.setSelected(headerModel.isMaximized());
		maximizeOption.setEnabled(headerModel.isMaximizeAllowed());

		pinned.setEnabled(headerModel.isPinnedAllowed() && headerModel.isUnpinned());
		unpinned.setEnabled(headerModel.isPinnedAllowed() && !headerModel.isUnpinned());
	}

	@Override
	public void ancestorAdded(AncestorEvent event) {
		init();
	}

	@Override
	public void ancestorRemoved(AncestorEvent event) {

	}

	@Override
	public void ancestorMoved(AncestorEvent event) {

	}
}
