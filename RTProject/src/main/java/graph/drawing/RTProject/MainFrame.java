package graph.drawing.RTProject;

import java.awt.EventQueue;
import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;

import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.SpringLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JSlider;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.Box;
import java.awt.SystemColor;
import java.awt.Color;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import helper.Node;
import mdlaf.MaterialLookAndFeel;

import javax.swing.JTabbedPane;
import javax.swing.JEditorPane;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import java.awt.event.MouseMotionAdapter;
import javax.swing.JCheckBox;
import javax.swing.JSeparator;

/**
 * This class contains the definitions for the GUI. The constructor was
 * generated using WindowBuilder
 * 
 * @author dobiko
 */
public class MainFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	private final MainFrame frame = this;
	private JLabel stateLabel;
	private JPanel contentPane;
	private JSlider slider;
	private JButton btnPlay;
	private JEditorPane editorPane;
	private JFileChooser fc = new JFileChooser();

	public GraphStatesManager states = new GraphStatesManager();

	private long counter = 0;
	private boolean playing = false;

	private String currentFilePath;
	private String lastPathPath = ".//lastPath.txt";

	/**
	 * This is the main entrypoint of the application.
	 * 
	 * @param args Program arguments
	 */
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(new MaterialLookAndFeel());
		} catch (UnsupportedLookAndFeelException e) {
			System.out.println("My fancy design! Noooooo :c");
		}

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainFrame frame = new MainFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public JEditorPane getEditorPane() {
		return editorPane;
	}

	public JLabel getStateLabel() {
		return stateLabel;
	}

	void enableControlElements() {
		btnLeft.setEnabled(true);
		btnRight.setEnabled(true);
		btnPlay.setEnabled(true);
		slider.setEnabled(true);

		slider.setBackground(SystemColor.controlHighlight);
	}

	void saveEditor() {
		// Save the changes to the text file iff the graph can be successfully loaded
		if (GraphLoader.load(editorPane.getText(), states)) {
			GraphLoader.saveTextfile(currentFilePath, editorPane.getText());
			enableControlElements();
		}
		refresh();
	}

	/**
	 * Redraw and recalculate crucial GUI elements
	 */
	void refresh() {
		drawPanel.repaint();
		legendPanel.repaint();
		if (states.getCurrentState() != null) {
			stateLabel.setText(states.getCurrentState().getTitle());

			slider.setMaximum(states.size() - 1);
			slider.setValue(states.getCurrentStateIndex());

			highlightEditorText();
		}
	}

	private JPanel drawPanel = new JPanel() {
		private static final long serialVersionUID = 1L;

		@Override
		/**
		 * Draw the current state to the drawPanel
		 */
		public void paint(Graphics g) {
			g.setColor(drawPanel.getBackground());
			g.fillRect(0, 0, drawPanel.getWidth(), drawPanel.getHeight());
			if (states.size() != 0)
				states.getCurrentState().draw(g, drawPanel, frame.getStateLabel().getFont().getName());
		}
	};

	private JPanel legendPanel = new JPanel() {
		private static final long serialVersionUID = 1L;

		@Override
		/**
		 * Draw the legendPanel
		 */
		public void paint(Graphics g) {
			g.setColor(drawPanel.getBackground());
			g.fillRect(0, 0, drawPanel.getWidth(), drawPanel.getHeight());

			g.setColor(Color.BLACK);
			g.setFont(new Font(frame.getStateLabel().getFont().getName(), Font.PLAIN, (int) (20)));

			int curY = 0;
			Node normal = new Node(10, curY + 10, 40, 40, "Example", "0", new ArrayList<helper.Edge>(),
					new ArrayList<helper.Edge>(), null);
			curY += 5 + 50;
			Node marked = new Node(10, curY + 10, 40, 40, "Example", "0", new ArrayList<helper.Edge>(),
					new ArrayList<helper.Edge>(), null);
			curY += 5 + 50;
			Node contour = new Node(10, curY + 10, 40, 40, "Example", "0", new ArrayList<helper.Edge>(),
					new ArrayList<helper.Edge>(), null);
			curY += 5 + 50;

			GraphState.drawNode(g, getStateLabel().getFont().getName(), normal, (int) normal.x, (int) normal.y, false,
					false);
			GraphState.drawNode(g, getStateLabel().getFont().getName(), marked, (int) marked.x, (int) marked.y, false,
					true);
			GraphState.drawNode(g, getStateLabel().getFont().getName(), contour, (int) contour.x, (int) contour.y, true,
					false);

			g.setColor(Color.BLACK);
			GraphState.drawLine(g, Color.BLACK, 3, 10, curY + 15, 50, curY + 15);
			if (!Options.hideThreads)
				GraphState.drawDashedLine(g, 10, curY + 3 + 15 + 15, 50, curY + 3 + 15 + 15);

			g.setColor(Color.BLACK);
			g.setFont(new Font(frame.getStateLabel().getFont().getName(), Font.PLAIN, (int) (12)));
			g.drawString("Normal Graph Node", (int) normal.x + (int) normal.w + 10,
					(int) normal.y + (int) normal.h / 2 + g.getFontMetrics().getAscent() / 2);
			g.drawString("Marked Graph Node", (int) marked.x + (int) marked.w + 10,
					(int) marked.y + (int) marked.h / 2 + g.getFontMetrics().getAscent() / 2);
			g.drawString("Contour Graph Node", (int) contour.x + (int) contour.w + 10,
					(int) contour.y + (int) contour.h / 2 + g.getFontMetrics().getAscent() / 2);

			g.drawString("Edge", 60, curY + 16 + g.getFontMetrics().getAscent() / 2);
			g.drawString("Reingold Tilford Thread", 60, curY + 3 + 15 + 16 + g.getFontMetrics().getAscent() / 2);
		}
	};
	private JButton btnLeft;
	private JButton btnRight;
	private JLabel lblPlayAnimationFrame;

	/**
	 * Do the editor text highlighting
	 */
	void highlightEditorText() {
		// remove previous highlight
		Highlighter hilite = editorPane.getHighlighter();
		Highlighter.Highlight[] hilites = hilite.getHighlights();
		for (int i = 0; i < hilites.length; i++)
			hilite.removeHighlight(hilites[i]);

		String markedNode = states.getCurrentState().getMarkedNodeName();
		List<String> contourNodes = states.getCurrentState().getContourNodeNames();

		// Find node occurrences and mark them
		String[] split = editorPane.getText().split("( )|(\n)");
		for (int i = 0; i < split.length; i++) {
			if (split[i].contentEquals(markedNode)) {
				int index = 0;
				for (int j = 0; j < i; j++)
					index += split[j].length() + 1;

				DefaultHighlighter.DefaultHighlightPainter highlightPainter = new DefaultHighlighter.DefaultHighlightPainter(
						Color.ORANGE);
				try {
					editorPane.getHighlighter().addHighlight(index, index + markedNode.length(), highlightPainter);
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
			}

			for (String s : contourNodes) {
				if (split[i].contentEquals(s)) {
					int index = 0;
					for (int j = 0; j < i; j++)
						index += split[j].length() + 1;

					DefaultHighlighter.DefaultHighlightPainter highlightPainter = new DefaultHighlighter.DefaultHighlightPainter(
							Color.LIGHT_GRAY);
					try {
						editorPane.getHighlighter().addHighlight(index, index + s.length(), highlightPainter);
					} catch (BadLocationException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	/**
	 * This constructor was mostly generated using WindowBuilder, the only
	 * exceptions are the events
	 */
	public MainFrame() {
		// Setup file chooser
		String path = GraphLoader.readTextfile(lastPathPath);
		if (!path.contentEquals(""))
			fc.setSelectedFile(new File(path));
		else
			fc.setCurrentDirectory(new File("."));
		fc.setDialogTitle("Choose a valid file to load");

		// Set the icon / Swing doesn't seem to like .ico files for some reason
		setIconImage(new ImageIcon(".\\icon.png").getImage());

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				new Thread(() -> {
					// Clock thread
					while (true) {
						counter++;
						try {
							Thread.sleep(16);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}

						if (playing && counter % Options.animationFrameInterval == 0) {
							states.forwardStep();
							refresh();

							if (states.isLastState()) {
								playing = false;
								if (playing)
									btnPlay.setText("❚❚");
								else
									btnPlay.setText("►");
							}
						}
					}
				}).start();
			}
		});
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 725, 512);
		setMinimumSize(new Dimension(700, 555));
		contentPane = new JPanel();
		contentPane.setBackground(SystemColor.controlHighlight);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		SpringLayout sl_contentPane = new SpringLayout();

		sl_contentPane.putConstraint(SpringLayout.SOUTH, drawPanel, -40, SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.NORTH, legendPanel, -215, SpringLayout.SOUTH, contentPane);
		contentPane.setLayout(sl_contentPane);

		stateLabel = new JLabel("No Graph Loaded");
		sl_contentPane.putConstraint(SpringLayout.NORTH, stateLabel, 0, SpringLayout.NORTH, contentPane);
		stateLabel.setBackground(SystemColor.controlHighlight);
		sl_contentPane.putConstraint(SpringLayout.WEST, stateLabel, 0, SpringLayout.WEST, contentPane);
		stateLabel.setFont(new Font("Open Sans", Font.BOLD, 24));
		stateLabel.setHorizontalAlignment(SwingConstants.CENTER);
		contentPane.add(stateLabel);
		sl_contentPane.putConstraint(SpringLayout.WEST, drawPanel, 0, SpringLayout.WEST, contentPane);
		drawPanel.setBackground(SystemColor.controlLtHighlight);
		contentPane.add(drawPanel);

		JPanel sidePanel = new JPanel();
		sl_contentPane.putConstraint(SpringLayout.NORTH, drawPanel, 40, SpringLayout.NORTH, sidePanel);
		sidePanel.setBackground(SystemColor.controlHighlight);
		sl_contentPane.putConstraint(SpringLayout.NORTH, sidePanel, 0, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, sidePanel, -250, SpringLayout.EAST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, stateLabel, 0, SpringLayout.WEST, sidePanel);
		sl_contentPane.putConstraint(SpringLayout.EAST, drawPanel, -5, SpringLayout.WEST, sidePanel);
		sl_contentPane.putConstraint(SpringLayout.EAST, sidePanel, 0, SpringLayout.EAST, contentPane);
		contentPane.add(sidePanel);

		JLayeredPane botPanel = new JLayeredPane();
		sl_contentPane.putConstraint(SpringLayout.NORTH, botPanel, 10, SpringLayout.SOUTH, drawPanel);
		sl_contentPane.putConstraint(SpringLayout.WEST, botPanel, 5, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, botPanel, -5, SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, botPanel, -10, SpringLayout.WEST, sidePanel);
		botPanel.setForeground(Color.WHITE);
		botPanel.setBackground(Color.WHITE);
		SpringLayout sl_sidePanel = new SpringLayout();
		sidePanel.setLayout(sl_sidePanel);

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		sl_sidePanel.putConstraint(SpringLayout.NORTH, tabbedPane, -23, SpringLayout.NORTH, sidePanel);
		sl_sidePanel.putConstraint(SpringLayout.WEST, tabbedPane, 0, SpringLayout.WEST, sidePanel);
		sl_sidePanel.putConstraint(SpringLayout.SOUTH, tabbedPane, 0, SpringLayout.SOUTH, sidePanel);
		sl_sidePanel.putConstraint(SpringLayout.EAST, tabbedPane, 0, SpringLayout.EAST, sidePanel);
		sidePanel.add(tabbedPane);

		JPanel editorTab = new JPanel();
		tabbedPane.addTab("Editor", null, editorTab, null);

		editorPane = new JEditorPane();
		editorPane.setBackground(SystemColor.text);
		editorPane.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == 83 && e.isControlDown()) {
					saveEditor();
				}
			}
		});
		SpringLayout sl_editorTab = new SpringLayout();
		editorTab.setLayout(sl_editorTab);
		JScrollPane editorScrollPane = new JScrollPane(editorPane);
		sl_editorTab.putConstraint(SpringLayout.NORTH, editorScrollPane, 5, SpringLayout.NORTH, editorTab);
		sl_editorTab.putConstraint(SpringLayout.WEST, editorScrollPane, 5, SpringLayout.WEST, editorTab);
		sl_editorTab.putConstraint(SpringLayout.EAST, editorScrollPane, -5, SpringLayout.EAST, editorTab);
		editorScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		editorTab.add(editorScrollPane);

		JButton btnSave = new JButton("Save");
		sl_editorTab.putConstraint(SpringLayout.NORTH, btnSave, -35, SpringLayout.SOUTH, editorTab);
		sl_editorTab.putConstraint(SpringLayout.WEST, btnSave, 5, SpringLayout.WEST, editorTab);
		sl_editorTab.putConstraint(SpringLayout.SOUTH, btnSave, -5, SpringLayout.SOUTH, editorTab);
		sl_editorTab.putConstraint(SpringLayout.EAST, btnSave, 75, SpringLayout.WEST, editorTab);
		btnSave.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				saveEditor();
			}
		});
		sl_editorTab.putConstraint(SpringLayout.SOUTH, editorScrollPane, 0, SpringLayout.NORTH, btnSave);
		editorTab.add(btnSave);

		JButton btnSaveNew = new JButton("Save as new File");
		btnSaveNew.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (GraphLoader.load(editorPane.getText(), states)) {
					enableControlElements();
					String fileName = JOptionPane.showInputDialog(frame, "How should the new file be named?");

					JFileChooser folderChooser = new JFileChooser();
					folderChooser.setCurrentDirectory(new java.io.File("."));
					folderChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
					folderChooser.setAcceptAllFileFilterUsed(false);
					if (folderChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
						String newFilePath = folderChooser.getSelectedFile().getAbsolutePath() + File.separatorChar + fileName;
						File newFile = new File(newFilePath);
						try {
							newFile.createNewFile();
						} catch (IOException e1) {
							JOptionPane.showMessageDialog(null, e1.getMessage());
							return;
						}
						GraphLoader.saveTextfile(newFile.getAbsolutePath(), editorPane.getText());
						refresh();
					}
				}
			}
		});
		sl_editorTab.putConstraint(SpringLayout.NORTH, btnSaveNew, 0, SpringLayout.NORTH, btnSave);
		sl_editorTab.putConstraint(SpringLayout.WEST, btnSaveNew, 0, SpringLayout.EAST, btnSave);
		sl_editorTab.putConstraint(SpringLayout.SOUTH, btnSaveNew, 0, SpringLayout.SOUTH, btnSave);
		sl_editorTab.putConstraint(SpringLayout.EAST, btnSaveNew, -5, SpringLayout.EAST, editorTab);
		editorTab.add(btnSaveNew);

		JPanel optionsTab = new JPanel();
		tabbedPane.addTab("Options", null, optionsTab, null);

		JPanel optionsPanel = new JPanel();
		optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));

		JScrollPane optionsScrollPane = new JScrollPane(optionsPanel);
		optionsScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		SpringLayout sl_optionsTab = new SpringLayout();
		sl_optionsTab.putConstraint(SpringLayout.NORTH, optionsScrollPane, 5, SpringLayout.NORTH, optionsTab);
		sl_optionsTab.putConstraint(SpringLayout.WEST, optionsScrollPane, 5, SpringLayout.WEST, optionsTab);
		sl_optionsTab.putConstraint(SpringLayout.SOUTH, optionsScrollPane, -5, SpringLayout.SOUTH, optionsTab);
		sl_optionsTab.putConstraint(SpringLayout.EAST, optionsScrollPane, -5, SpringLayout.EAST, optionsTab);
		optionsTab.setLayout(sl_optionsTab);

		JCheckBox chckbxHideContourStates = new JCheckBox("Show Contour States");
		chckbxHideContourStates.setAlignmentX(Component.CENTER_ALIGNMENT);
		chckbxHideContourStates.setSelected(true);
		chckbxHideContourStates.setHorizontalAlignment(SwingConstants.LEFT);
		optionsPanel.add(chckbxHideContourStates);

		JCheckBox chckbxHideContourDifferenceStates = new JCheckBox("Show Contour Differences");
		chckbxHideContourDifferenceStates.setAlignmentX(Component.CENTER_ALIGNMENT);
		chckbxHideContourDifferenceStates.setSelected(true);
		chckbxHideContourStates.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Options.hideContourStates = !chckbxHideContourStates.isSelected();

				chckbxHideContourDifferenceStates.setEnabled(chckbxHideContourStates.isSelected());

				GraphLoader.load(editorPane.getText(), states);
				refresh();
			}
		});
		chckbxHideContourDifferenceStates.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Options.hideContourDifferenceStates = !chckbxHideContourDifferenceStates.isSelected();
				GraphLoader.load(editorPane.getText(), states);
				refresh();
			}
		});
		chckbxHideContourDifferenceStates.setHorizontalAlignment(SwingConstants.LEFT);
		optionsPanel.add(chckbxHideContourDifferenceStates);

		JCheckBox chckbxHideNodeOffsetValues = new JCheckBox("Show Node Offset Values");
		chckbxHideNodeOffsetValues.setAlignmentX(Component.CENTER_ALIGNMENT);
		chckbxHideNodeOffsetValues.setSelected(true);
		chckbxHideNodeOffsetValues.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Options.hideNodeOffsetValues = !chckbxHideNodeOffsetValues.isSelected();
				refresh();
			}
		});

		JSeparator separator = new JSeparator();
		optionsPanel.add(separator);
		optionsPanel.add(chckbxHideNodeOffsetValues);

		JCheckBox chckbxHideThreads = new JCheckBox("Show Threads");
		chckbxHideThreads.setAlignmentX(Component.CENTER_ALIGNMENT);
		chckbxHideThreads.setSelected(true);
		chckbxHideThreads.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Options.hideThreads = !chckbxHideThreads.isSelected();
				refresh();
			}
		});

		JCheckBox chckbxChckbxshowoffsetvaluesonly = new JCheckBox("Show Offset Values Only");
		chckbxChckbxshowoffsetvaluesonly.setAlignmentX(Component.CENTER_ALIGNMENT);
		chckbxChckbxshowoffsetvaluesonly.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				Options.showOffsetOnly = chckbxChckbxshowoffsetvaluesonly.isSelected();

				chckbxHideNodeOffsetValues.setEnabled(!Options.showOffsetOnly);

				refresh();
			}
		});
		optionsPanel.add(chckbxChckbxshowoffsetvaluesonly);
		optionsPanel.add(chckbxHideThreads);

		JSeparator separator_1 = new JSeparator();
		optionsPanel.add(separator_1);

		lblPlayAnimationFrame = new JLabel("Play Animation Interval: " + (Options.animationFrameInterval * 16) + "ms");
		lblPlayAnimationFrame.setAlignmentX(Component.CENTER_ALIGNMENT);
		optionsPanel.add(lblPlayAnimationFrame);

		JSlider animationIntervalSlider = new JSlider();
		animationIntervalSlider.setBorder(null);
		optionsPanel.add(animationIntervalSlider);
		animationIntervalSlider.setPreferredSize(new Dimension(50, 50));
		animationIntervalSlider.setMaximum(180);
		animationIntervalSlider.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				Options.animationFrameInterval = animationIntervalSlider.getValue();
				lblPlayAnimationFrame
						.setText("Play Animation Interval: " + (Options.animationFrameInterval * 16) + "ms");
			}
		});
		animationIntervalSlider.setValue(60);
		animationIntervalSlider.setMinimum(5);
		optionsTab.add(optionsScrollPane);

		sl_contentPane.putConstraint(SpringLayout.SOUTH, sidePanel, -2, SpringLayout.NORTH, legendPanel);
		sl_contentPane.putConstraint(SpringLayout.WEST, legendPanel, 2, SpringLayout.WEST, sidePanel);
		sl_contentPane.putConstraint(SpringLayout.EAST, legendPanel, -2, SpringLayout.EAST, sidePanel);
		sl_sidePanel.putConstraint(SpringLayout.NORTH, legendPanel, 0, SpringLayout.NORTH, sidePanel);
		sl_sidePanel.putConstraint(SpringLayout.WEST, legendPanel, 0, SpringLayout.WEST, sidePanel);
		sl_sidePanel.putConstraint(SpringLayout.EAST, legendPanel, 0, SpringLayout.EAST, sidePanel);
		contentPane.add(legendPanel);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, legendPanel, 0, SpringLayout.SOUTH, contentPane);

		btnRight = new JButton(">");
		btnRight.setEnabled(false);
		btnRight.setFont(new Font("Noto Sans", Font.PLAIN, 25));
		btnRight.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				states.forwardStep();
				refresh();
			}
		});

		btnLeft = new JButton("<");
		btnLeft.setEnabled(false);
		btnLeft.setFont(new Font("Noto Sans", Font.PLAIN, 25));
		btnLeft.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				states.backwardStep();
				refresh();
			}
		});
		btnLeft.setHorizontalAlignment(SwingConstants.LEFT);
		botPanel.add(btnLeft);
		btnRight.setHorizontalAlignment(SwingConstants.RIGHT);
		botPanel.add(btnRight);

		Component sliderPadding1 = Box.createHorizontalStrut(20);
		botPanel.add(sliderPadding1);

		btnPlay = new JButton("►");
		btnPlay.setEnabled(false);
		btnPlay.setFont(new Font("SansSerif", Font.BOLD, 18));
		btnPlay.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (btnPlay.isEnabled()) {
					counter = 0;
					playing = !playing;

					if (playing)
						btnPlay.setText("❚❚");
					else
						btnPlay.setText("►");
				}
			}
		});
		botPanel.add(btnPlay);

		slider = new JSlider();
		slider.setEnabled(false);
		slider.setBackground(new Color(200, 200, 200));
		slider.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				states.setCurrentStateIndex(slider.getValue());
				refresh();
			}
		});
		slider.setValue(0);
		slider.setMinimum(0);
		botPanel.add(slider);

		JButton btnLoadFile = new JButton("Load File");
		btnLoadFile.setFont(new Font("Noto Sans", Font.BOLD, 18));
		btnLoadFile.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int returnVal = fc.showOpenDialog(frame);

				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					if (file.canRead()) {
						currentFilePath = file.getAbsolutePath();
						GraphLoader.loadFile(currentFilePath, frame);

						enableControlElements();

						File lastPath = new File(lastPathPath);
						try {
							lastPath.createNewFile();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
						GraphLoader.saveTextfile(lastPath.getAbsolutePath(), currentFilePath);
					} else
						JOptionPane.showMessageDialog(frame, "I can't read that file :/");
					refresh();
				}
			}
		});

		Component sliderPadding2 = Box.createHorizontalStrut(20);
		sliderPadding2.setBackground(Color.WHITE);
		botPanel.add(sliderPadding2);
		btnLoadFile.setHorizontalAlignment(SwingConstants.RIGHT);
		botPanel.add(btnLoadFile);

		contentPane.add(botPanel);
		botPanel.setLayout(new BoxLayout(botPanel, BoxLayout.X_AXIS));

		JPanel backPanel = new JPanel();
		sl_contentPane.putConstraint(SpringLayout.NORTH, backPanel, 5, SpringLayout.SOUTH, drawPanel);
		sl_contentPane.putConstraint(SpringLayout.WEST, backPanel, 0, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, backPanel, 0, SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, backPanel, -5, SpringLayout.WEST, sidePanel);
		contentPane.add(backPanel);
	}
}
