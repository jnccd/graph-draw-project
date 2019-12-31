package graph.drawing.RTProject;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.tools.DiagnosticListener;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;

import java.awt.GridLayout;
import java.awt.List;

import javax.swing.SwingConstants;
import javax.swing.BoxLayout;
import javax.swing.SpringLayout;
import java.awt.Color;
import java.awt.Component;

import javax.lang.model.SourceVersion;
import javax.swing.Box;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Set;

public class MainFrame extends JFrame {

	private JPanel contentPane;
	private final MainFrame frame = this;
	private JFileChooser fc = new JFileChooser();

	private int statesIndex = 0;
	private ArrayList<GraphState> states = new ArrayList<GraphState>();
	private JPanel panel = new JPanel() {
		private static final long serialVersionUID = 1L;

		@Override
		public void paint(Graphics g) {
			if (states.size() != 0)
				states.get(statesIndex).draw(g, panel, frame);
		}
	};

	public void clearStates() {
		statesIndex = 0;
		states.clear();
	}

	public void addState(GraphState gs) {
		states.add(gs);
	}

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

	/**
	 * Create the frame.
	 */
	public MainFrame() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		SpringLayout sl_contentPane = new SpringLayout();
		contentPane.setLayout(sl_contentPane);

		sl_contentPane.putConstraint(SpringLayout.SOUTH, panel, -30, SpringLayout.SOUTH, contentPane);
		panel.setBackground(Color.LIGHT_GRAY);
		sl_contentPane.putConstraint(SpringLayout.NORTH, panel, 0, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, panel, 0, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, panel, 0, SpringLayout.EAST, contentPane);
		contentPane.add(panel);

		JLayeredPane layeredPane = new JLayeredPane();
		sl_contentPane.putConstraint(SpringLayout.NORTH, layeredPane, 5, SpringLayout.SOUTH, panel);
		sl_contentPane.putConstraint(SpringLayout.WEST, layeredPane, 0, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, layeredPane, 0, SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, layeredPane, 0, SpringLayout.EAST, contentPane);
		contentPane.add(layeredPane);

		JButton btnLeft = new JButton("<");
		btnLeft.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {

			}
		});
		SpringLayout sl_layeredPane = new SpringLayout();
		sl_layeredPane.putConstraint(SpringLayout.NORTH, btnLeft, 0, SpringLayout.NORTH, layeredPane);
		sl_layeredPane.putConstraint(SpringLayout.WEST, btnLeft, 0, SpringLayout.WEST, layeredPane);
		sl_layeredPane.putConstraint(SpringLayout.SOUTH, btnLeft, 0, SpringLayout.SOUTH, layeredPane);
		layeredPane.setLayout(sl_layeredPane);
		btnLeft.setHorizontalAlignment(SwingConstants.LEFT);
		layeredPane.add(btnLeft);

		JButton btnRight = new JButton(">");
		sl_layeredPane.putConstraint(SpringLayout.NORTH, btnRight, 0, SpringLayout.NORTH, layeredPane);
		sl_layeredPane.putConstraint(SpringLayout.WEST, btnRight, 44, SpringLayout.WEST, layeredPane);
		sl_layeredPane.putConstraint(SpringLayout.SOUTH, btnRight, 0, SpringLayout.SOUTH, layeredPane);
		btnRight.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {

			}
		});
		btnRight.setHorizontalAlignment(SwingConstants.RIGHT);
		layeredPane.add(btnRight);

		JButton btnLoadFile = new JButton("Load File");
		sl_layeredPane.putConstraint(SpringLayout.NORTH, btnLoadFile, 0, SpringLayout.NORTH, layeredPane);
		sl_layeredPane.putConstraint(SpringLayout.SOUTH, btnLoadFile, 0, SpringLayout.SOUTH, layeredPane);
		sl_layeredPane.putConstraint(SpringLayout.EAST, btnLoadFile, 0, SpringLayout.EAST, layeredPane);
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
				}
			}
		});
		btnLoadFile.setHorizontalAlignment(SwingConstants.RIGHT);
		layeredPane.add(btnLoadFile);
	}
}
