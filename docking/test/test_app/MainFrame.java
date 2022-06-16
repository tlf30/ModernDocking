package test_app;/*
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

import docking.Docking;
import docking.DockingRegion;
import docking.RootDockingPanel;
import exception.FailOnThreadViolationRepaintManager;
import persist.RootDockState;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;

public class MainFrame extends JFrame {
	static RootDockState state;

	public MainFrame() {
		setTitle("Test Docking Framework");

		setSize(800, 600);

		JLabel test = new JLabel("Test");
		test.setOpaque(true);
		test.setBackground(Color.RED);
		test.setSize(100, 100);
		test.setLocation(100, 100);

//		getLayeredPane().add(test, 2);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		SimplePanel one = new SimplePanel("one", "one");
		SimplePanel two = new SimplePanel("two", "two");
		SimplePanel three = new SimplePanel("three", "three");
		SimplePanel four = new SimplePanel("four", "four");
		SimplePanel five = new SimplePanel("five", "five");
		SimplePanel six = new SimplePanel("six", "six");
		SimplePanel seven = new SimplePanel("seven", "seven");
		SimplePanel eight = new SimplePanel("eight", "eight");
		ToolPanel explorer = new ToolPanel("Explorer", "explorer", true);
		ToolPanel output = new ToolPanel("Output", "output", false);

		JToolBar toolBar = new JToolBar();
		JButton test1 = new JButton("Test1");
		test1.addActionListener(e -> Docking.undock(one));
		toolBar.add(test1);
		JButton test2 = new JButton("Test2");
		toolBar.add(test2);
		JButton test3 = new JButton("Test3");
		toolBar.add(test3);
		JButton save = new JButton("save");
		JButton restore = new JButton("restore");
		toolBar.add(save);
		toolBar.add(restore);


		save.addActionListener(e -> {
			state = Docking.getRootState(this);
		});
		restore.addActionListener(e -> Docking.restoreState(this, state));

		test2.addActionListener(e -> test.setVisible(false));
		test3.addActionListener(e -> test.setVisible(true));

		setLayout(new GridBagLayout());

		GridBagConstraints gbc = new GridBagConstraints();

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;

		add(toolBar, gbc);

		gbc.gridy++;
		gbc.weighty = 1.0;
		gbc.fill = GridBagConstraints.BOTH;

		RootDockingPanel dockingPanel = new RootDockingPanel();
		Docking.registerDockingPanel(dockingPanel, this);
		Random rand = new Random();
		dockingPanel.setBackground(new Color(rand.nextInt(255),rand.nextInt(255),rand.nextInt(255)));

		add(dockingPanel, gbc);

		gbc.gridy++;
		gbc.weighty = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.fill = GridBagConstraints.NONE;

		JTabbedPane tabs = new JTabbedPane();
		tabs.setTabPlacement(JTabbedPane.BOTTOM);
		tabs.add("Test", null);

		tabs.addChangeListener(e -> test.setVisible(!test.isVisible()));
		tabs.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				test.setVisible(!test.isVisible());
			}
		});
//		add(tabs, gbc);

		JToggleButton button = new JToggleButton("Test");
		button.addActionListener(e -> test.setVisible(button.isSelected()));

		Docking.setMainFrame(this);

		Docking.dock(one, this);
		Docking.dock(two, one, DockingRegion.SOUTH);
		Docking.dock(three, this, DockingRegion.WEST);
		Docking.dock(four, two, DockingRegion.CENTER);
		Docking.dock(output, this, DockingRegion.SOUTH);
		Docking.dock(explorer, this, DockingRegion.EAST);

		// save the default layout so that we have something to restore, do it later so that the splits setup properly
		SwingUtilities.invokeLater(save::doClick);
	}

	public static void main(String[] args) throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

		SwingUtilities.invokeLater(() -> {
			FailOnThreadViolationRepaintManager.install();
			FailOnThreadViolationRepaintManager.install();

			MainFrame mainFrame = new MainFrame();
			mainFrame.setVisible(true);
		});
	}
}