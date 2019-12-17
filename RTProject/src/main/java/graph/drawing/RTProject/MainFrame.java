package graph.drawing.RTProject;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;

import java.awt.GridLayout;
import javax.swing.SwingConstants;
import javax.swing.BoxLayout;
import javax.swing.SpringLayout;
import java.awt.Color;
import java.awt.Component;
import javax.swing.Box;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

public class MainFrame extends JFrame {

	private JPanel contentPane;
	private final JFrame frame = this;
	private JFileChooser fc = new JFileChooser();

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
		
		JPanel panel = new JPanel();
		sl_contentPane.putConstraint(SpringLayout.SOUTH, panel, -30, SpringLayout.SOUTH, contentPane);
		panel.setBackground(Color.MAGENTA);
		sl_contentPane.putConstraint(SpringLayout.NORTH, panel, 0, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, panel, 0, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, panel, 0, SpringLayout.EAST, contentPane);
		contentPane.add(panel);
		
		JLayeredPane layeredPane = new JLayeredPane();
		sl_contentPane.putConstraint(SpringLayout.WEST, layeredPane, -440, SpringLayout.EAST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, layeredPane, 0, SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, layeredPane, 0, SpringLayout.EAST, contentPane);
		contentPane.add(layeredPane);
		layeredPane.setLayout(new BoxLayout(layeredPane, BoxLayout.X_AXIS));
		
		JButton btnLeft = new JButton("<");
		btnLeft.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				
			}
		});
		btnLeft.setHorizontalAlignment(SwingConstants.LEFT);
		layeredPane.add(btnLeft);
		
		JButton btnRight = new JButton(">");
		btnRight.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				
			}
		});
		btnRight.setHorizontalAlignment(SwingConstants.RIGHT);
		layeredPane.add(btnRight);
		
		Component horizontalGlue = Box.createHorizontalGlue();
		layeredPane.add(horizontalGlue);
		
		JButton btnLoadFile = new JButton("Load File");
		btnLoadFile.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int returnVal = fc.showOpenDialog(frame);

		        if (returnVal == JFileChooser.APPROVE_OPTION) {
		            File file = fc.getSelectedFile();
		            if (file.canRead())
		            	GraphLoader.load(file.getAbsolutePath(), frame, contentPane.getComponent(0));
		            else
		            	JOptionPane.showMessageDialog(frame,
		    				    "I can't read that file :/");
		        }
			}
		});
		btnLoadFile.setHorizontalAlignment(SwingConstants.RIGHT);
		layeredPane.add(btnLoadFile);
	}
}
