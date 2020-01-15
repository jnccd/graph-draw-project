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
import java.nio.file.Path;

import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JSlider;
import javax.swing.BoxLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.Box;
import java.awt.SystemColor;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import java.awt.Color;
import java.awt.BorderLayout;
import javax.swing.border.MatteBorder;
import javax.swing.border.SoftBevelBorder;

import mdlaf.MaterialLookAndFeel;

import javax.swing.JTextArea;
import javax.swing.JTabbedPane;
import javax.swing.JEditorPane;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JScrollPane;
import javax.swing.JScrollBar;
import javax.swing.ScrollPaneConstants;

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
	private JPanel drawPanel = new JPanel() {
		private static final long serialVersionUID = 1L;

		@Override
		public void paint(Graphics g) {
			g.setColor(drawPanel.getBackground());
			g.fillRect(0, 0, drawPanel.getWidth(), drawPanel.getHeight());
			if (states.size() != 0)
				states.getCurrentState().draw(g, drawPanel, frame);
		}
	};
	
	private long counter = 0;
	private boolean playing = false;
	
	private String currentFilePath;
	private String lastPathPath = ".//lastPath.txt";
	private String tmpPath = ".//tmp.graph";
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel (new MaterialLookAndFeel ());
		}
		catch (UnsupportedLookAndFeelException e) {
			
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

	void refresh() {
		drawPanel.repaint();
		if (states.getCurrentState() != null) {
			stateLabel.setText(states.getCurrentState().getTitle());
			
			slider.setMaximum(states.size() - 1);
			slider.setValue(states.getCurrentStateIndex());
		}
	}

	/**
	 * Create the frame.
	 */
	public MainFrame() {
		try {
			fc.setSelectedFile(new File(GraphLoader.readTextfile(lastPathPath)));
		} catch (Exception e) {
			
		}
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				new Thread(() -> {
					while (true) {
						counter++;
						try {
							Thread.sleep(16);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
						
						if (playing && counter % 60 == 0)
						{
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
		setMinimumSize(new Dimension(700, 500));
		contentPane = new JPanel();
		contentPane.setBackground(SystemColor.controlHighlight);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		SpringLayout sl_contentPane = new SpringLayout();
		
				sl_contentPane.putConstraint(SpringLayout.SOUTH, drawPanel, -30, SpringLayout.SOUTH, contentPane);
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
		
		JPanel optionsPanel = new JPanel();
		sl_contentPane.putConstraint(SpringLayout.NORTH, drawPanel, 40, SpringLayout.NORTH, optionsPanel);
		optionsPanel.setBackground(SystemColor.controlHighlight);
		sl_contentPane.putConstraint(SpringLayout.NORTH, optionsPanel, 0, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, optionsPanel, -250, SpringLayout.EAST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, stateLabel, 0, SpringLayout.WEST, optionsPanel);
		sl_contentPane.putConstraint(SpringLayout.EAST, drawPanel, -5, SpringLayout.WEST, optionsPanel);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, optionsPanel, 0, SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, optionsPanel, 0, SpringLayout.EAST, contentPane);
		contentPane.add(optionsPanel);

		JLayeredPane layeredPane = new JLayeredPane();
		layeredPane.setBackground(SystemColor.scrollbar);
		sl_contentPane.putConstraint(SpringLayout.NORTH, layeredPane, 5, SpringLayout.SOUTH, drawPanel);
		sl_contentPane.putConstraint(SpringLayout.WEST, layeredPane, 0, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, layeredPane, 0, SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, layeredPane, -5, SpringLayout.WEST, optionsPanel);
		SpringLayout sl_optionsPanel = new SpringLayout();
		optionsPanel.setLayout(sl_optionsPanel);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		sl_optionsPanel.putConstraint(SpringLayout.NORTH, tabbedPane, -23, SpringLayout.NORTH, optionsPanel);
		sl_optionsPanel.putConstraint(SpringLayout.WEST, tabbedPane, 0, SpringLayout.WEST, optionsPanel);
		sl_optionsPanel.putConstraint(SpringLayout.SOUTH, tabbedPane, 0, SpringLayout.SOUTH, optionsPanel);
		sl_optionsPanel.putConstraint(SpringLayout.EAST, tabbedPane, 0, SpringLayout.EAST, optionsPanel);
		optionsPanel.add(tabbedPane);
		
		JPanel editorTab = new JPanel();
		tabbedPane.addTab("Editor", null, editorTab, null);
		editorTab.setLayout(new BoxLayout(editorTab, BoxLayout.X_AXIS));
		
		editorPane = new JEditorPane();
		editorPane.setBackground(SystemColor.text);
		editorPane.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				
			}
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == 83 && e.isControlDown()) {
					String path = "";
					try {
						GraphLoader.saveTextfile(currentFilePath, editorPane.getText());
						path = currentFilePath;
					} catch (IOException e1) {
						try {
							new File(tmpPath).createNewFile();
							GraphLoader.saveTextfile(tmpPath, editorPane.getText());
							path = tmpPath;
						} catch (IOException e2) {
							
						}
					}
					
					GraphLoader.load(path, frame, drawPanel);
					refresh();
				}
			}
		});
		JScrollPane scrollPane = new JScrollPane(editorPane);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		editorTab.add(scrollPane);
		
		JPanel optionsTab = new JPanel();
		tabbedPane.addTab("Options", null, optionsTab, null);
		optionsTab.setLayout(new BoxLayout(optionsTab, BoxLayout.Y_AXIS));
		
		JLabel label = new JLabel("Node Size");
		label.setVerticalAlignment(SwingConstants.TOP);
		label.setHorizontalAlignment(SwingConstants.LEFT);
		label.setAlignmentX(0.5f);
		optionsTab.add(label);
		
		JSlider sizeSlider = new JSlider();
		sizeSlider.setBorder(null);
		sizeSlider.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				Options.NODE_SIZE = sizeSlider.getValue();
				GraphLoader.load(currentFilePath, frame, drawPanel);
				refresh();
			}
		});
		sizeSlider.setValue(40);
		sizeSlider.setMinimum(30);
		sizeSlider.setMaximum(125);
		sizeSlider.setAlignmentY(0.0f);
		optionsTab.add(sizeSlider);
		
		Component verticalStrut = Box.createVerticalStrut(20);
		optionsTab.add(verticalStrut);
		
		contentPane.add(layeredPane);
		layeredPane.setLayout(new BoxLayout(layeredPane, BoxLayout.X_AXIS));

		JButton btnRight = new JButton(">");
		btnRight.setBackground(SystemColor.controlLtHighlight);
		btnRight.setFont(new Font("Noto Sans", Font.PLAIN, 25));
		btnRight.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				states.forwardStep();
				refresh();
			}
		});

		JButton btnLeft = new JButton("<");
		btnLeft.setBackground(SystemColor.controlLtHighlight);
		btnLeft.setFont(new Font("Noto Sans", Font.PLAIN, 25));
		btnLeft.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				states.backwardStep();
				refresh();
			}
		});
		btnLeft.setHorizontalAlignment(SwingConstants.LEFT);
		layeredPane.add(btnLeft);
		btnRight.setHorizontalAlignment(SwingConstants.RIGHT);
		layeredPane.add(btnRight);
		
		Component sliderPadding1 = Box.createHorizontalStrut(20);
		sliderPadding1.setBackground(SystemColor.controlHighlight);
		layeredPane.add(sliderPadding1);
		
				btnPlay = new JButton("►");
				btnPlay.setBackground(SystemColor.controlLtHighlight);
				btnPlay.setFont(new Font("SansSerif", Font.BOLD, 18));
				btnPlay.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						counter = 0;
						playing = !playing;
						
						if (playing)
							btnPlay.setText("❚❚");
						else
							btnPlay.setText("►");
					}
				});
				layeredPane.add(btnPlay);

		slider = new JSlider();
		slider.setBackground(SystemColor.controlLtHighlight);
		slider.setValue(0);
		slider.setEnabled(false);
		layeredPane.add(slider);

		JButton btnLoadFile = new JButton("Load File");
		btnLoadFile.setBackground(SystemColor.controlLtHighlight);
		btnLoadFile.setFont(new Font("Noto Sans", Font.BOLD, 18));
		btnLoadFile.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int returnVal = fc.showOpenDialog(frame);

				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					if (file.canRead()) {
						currentFilePath = file.getAbsolutePath();
						GraphLoader.load(currentFilePath, frame, drawPanel);
						
						File lastPath = new File(lastPathPath);
						try {
							if (lastPath.createNewFile())
								GraphLoader.saveTextfile(lastPath.getAbsolutePath(), currentFilePath);
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
					else
						JOptionPane.showMessageDialog(frame, "I can't read that file :/");
					refresh();
				}
			}
		});
		
		Component sliderPadding2 = Box.createHorizontalStrut(20);
		layeredPane.add(sliderPadding2);
		btnLoadFile.setHorizontalAlignment(SwingConstants.RIGHT);
		layeredPane.add(btnLoadFile);
	}
	public JEditorPane getEditorPane() {
		return editorPane;
	}
}
