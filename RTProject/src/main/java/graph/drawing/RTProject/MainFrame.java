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
import javax.swing.SpringLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JSlider;
import javax.swing.BoxLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.Component;
import javax.swing.Box;
import java.awt.SystemColor;
import javax.swing.border.BevelBorder;

public class MainFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	private final MainFrame frame = this;
	private JLabel stateLabel;
	private JPanel contentPane;
	private JFileChooser fc = new JFileChooser();

	public GraphStatesManager states = new GraphStatesManager();
	private JPanel panel = new JPanel() {
		private static final long serialVersionUID = 1L;

		@Override
		public void paint(Graphics g) {
			g.clearRect(0, 0, panel.getWidth(), panel.getHeight());
			if (states.size() != 0)
				states.getCurrentState().draw(g, panel, frame);
		}
	};
	private JSlider slider;
	
	private long counter = 0;
	private boolean playing = false;
	private JButton btnPlay;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
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
		panel.repaint();
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
		setBounds(100, 100, 623, 456);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		SpringLayout sl_contentPane = new SpringLayout();
		
				sl_contentPane.putConstraint(SpringLayout.SOUTH, panel, -30, SpringLayout.SOUTH, contentPane);
		contentPane.setLayout(sl_contentPane);

		stateLabel = new JLabel("No Graph Loaded");
		sl_contentPane.putConstraint(SpringLayout.NORTH, panel, 27, SpringLayout.NORTH, stateLabel);
		stateLabel.setFont(new Font("Open Sans", Font.BOLD, 20));
		stateLabel.setHorizontalAlignment(SwingConstants.CENTER);
		sl_contentPane.putConstraint(SpringLayout.NORTH, stateLabel, 0, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, stateLabel, 0, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, stateLabel, 0, SpringLayout.EAST, contentPane);
		contentPane.add(stateLabel);
		sl_contentPane.putConstraint(SpringLayout.WEST, panel, 0, SpringLayout.WEST, contentPane);
		contentPane.add(panel);
		
		JPanel optionsPanel = new JPanel();
		optionsPanel.setBackground(SystemColor.controlHighlight);
		sl_contentPane.putConstraint(SpringLayout.EAST, panel, -5, SpringLayout.WEST, optionsPanel);
		sl_contentPane.putConstraint(SpringLayout.WEST, optionsPanel, -150, SpringLayout.EAST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.NORTH, optionsPanel, 0, SpringLayout.NORTH, panel);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, optionsPanel, 0, SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, optionsPanel, 0, SpringLayout.EAST, contentPane);
		contentPane.add(optionsPanel);

		JLayeredPane layeredPane = new JLayeredPane();
		layeredPane.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		layeredPane.setBackground(SystemColor.scrollbar);
		sl_contentPane.putConstraint(SpringLayout.NORTH, layeredPane, 5, SpringLayout.SOUTH, panel);
		sl_contentPane.putConstraint(SpringLayout.WEST, layeredPane, 0, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, layeredPane, 0, SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, layeredPane, -5, SpringLayout.WEST, optionsPanel);
		optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.X_AXIS));
		
		JSlider slider_1 = new JSlider();
		optionsPanel.add(slider_1);
		contentPane.add(layeredPane);
		layeredPane.setLayout(new BoxLayout(layeredPane, BoxLayout.X_AXIS));

		JButton btnRight = new JButton(">");
		btnRight.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				states.forwardStep();
				refresh();
			}
		});

		JButton btnLeft = new JButton("<");
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

		btnPlay = new JButton("►");
		btnPlay.setFont(new Font("SansSerif", Font.BOLD, 12));
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
		
		Component sliderPadding1 = Box.createHorizontalStrut(20);
		sliderPadding1.setBackground(SystemColor.controlHighlight);
		layeredPane.add(sliderPadding1);

		slider = new JSlider();
		slider.setBackground(SystemColor.controlHighlight);
		slider.setValue(0);
		slider.setEnabled(false);
		layeredPane.add(slider);

		JButton btnLoadFile = new JButton("Load File");
		btnLoadFile.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int returnVal = fc.showOpenDialog(frame);

				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					if (file.canRead())
						GraphLoader.load(file.getAbsolutePath(), frame, panel);
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
}
