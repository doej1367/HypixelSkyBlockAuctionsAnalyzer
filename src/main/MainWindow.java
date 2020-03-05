package main;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextArea;
import net.miginfocom.swing.MigLayout;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import java.awt.Font;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.JSplitPane;
import javax.swing.JComboBox;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

public class MainWindow extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JSplitPane contentPane;
	private JTextField textField_API;
	private JTextField textField_IGN;

	private JTextArea consoleOut;
	private JScrollPane sp;

	private JSpinner spinner_multireq;
	private JButton btn_multireq;
	private JButton btnSendRequest;
	private JSpinner spinner_multireq_wait;
	private JLabel lblTimes;
	private JLabel lblMinsBreak;
	private JComboBox<String> comboBox_profile;
	private JButton btnLoadProfiles;
	private JButton btnSetStart;
	private JLabel lblNewLabel_killCount;

	public JLabel getLblNewLabel_killCount() {
		return lblNewLabel_killCount;
	}

	private JLabel lblKills;
	private JSeparator separator;
	private JLabel lblUpdated;
	private JLabel lblLabel_lastUpdate;
	private JLabel lblSecsAgo;

	public JComboBox<String> getComboBox_profile() {
		return comboBox_profile;
	}

	public JScrollPane getSp() {
		return sp;
	}

	public JTextArea getConsoleOut() {
		return consoleOut;
	}

	/**
	 * Create the frame.
	 */
	public MainWindow() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 583, 308);
		contentPane = new JSplitPane();
		contentPane.setContinuousLayout(true);
		contentPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));

		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.CENTER);
		panel.setLayout(new BorderLayout(0, 0));

		consoleOut = new JTextArea();
		consoleOut.setEditable(false);
		consoleOut.setFont(new Font("Consolas", Font.PLAIN, 13));
		consoleOut.setRows(3);

		sp = new JScrollPane(consoleOut);
		sp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		panel.add(sp);

		JPanel panel_1 = new JPanel();
		contentPane.add(panel_1, BorderLayout.NORTH);
		panel_1.setLayout(new MigLayout("fill", "[][][22.00][53.00][64.00px:n][43.00][][40px:n][][][][grow]", "[][][][][][][][][][][][][][][][][][][][]"));

		JLabel lblNewLabel = new JLabel("API key:");
		panel_1.add(lblNewLabel, "cell 1 1 2 1,growx");

		textField_API = new JTextField(Main.api_key);
		panel_1.add(textField_API, "cell 3 1 9 1,growx");
		textField_API.setColumns(10);
		textField_API.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				try {
					File config = new File("config.txt");
					BufferedWriter bw = new BufferedWriter(new FileWriter(config));
					bw.write(textField_API.getText() + "\n" + textField_IGN.getText() + "\n");
					bw.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}

			@Override
			public void keyPressed(KeyEvent e) {

			}
		});

		JLabel lblFilterContainingText = new JLabel("In game name:");
		panel_1.add(lblFilterContainingText, "cell 1 3 2 1,growx");

		textField_IGN = new JTextField(Main.ign);
		textField_IGN.setColumns(10);
		panel_1.add(textField_IGN, "cell 3 3 3 1,growx");
		textField_IGN.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				try {
					File config = new File("config.txt");
					BufferedWriter bw = new BufferedWriter(new FileWriter(config));
					bw.write(textField_API.getText() + "\n" + textField_IGN.getText() + "\n");
					bw.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}

			@Override
			public void keyPressed(KeyEvent e) {

			}
		});

		comboBox_profile = new JComboBox<String>();
		comboBox_profile.setEnabled(false);
		comboBox_profile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Main.setCurrentProfile();
			}
		});

		panel_1.add(comboBox_profile, "cell 7 3 2 1,growx");

		btnLoadProfiles = new JButton("load profiles");
		btnLoadProfiles.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				enableButtons(false);
				Thread t = new Thread() {
					@Override
					public void run() {
						Main.api_key = textField_API.getText();
						Main.loadProfiles();
						super.run();
						enableButtons(true);
						btnSetStart.setEnabled(false);
						spinner_multireq.setEnabled(false);
						spinner_multireq_wait.setEnabled(false);
						btn_multireq.setEnabled(false);
					}
				};
				t.start();
			}
		});
		panel_1.add(btnLoadProfiles, "cell 11 3,grow");

		btnSendRequest = new JButton("send request");
		btnSendRequest.setEnabled(false);
		panel_1.add(btnSendRequest, "cell 11 5,grow");
		btnSendRequest.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				enableButtons(false);
				Thread t = new Thread() {
					@Override
					public void run() {
						Main.api_key = textField_API.getText();
						Main.sendRequest();
						super.run();
						enableButtons(true);
						spinner_multireq.setEnabled(false);
						spinner_multireq_wait.setEnabled(false);
						btn_multireq.setEnabled(false);
					}
				};
				t.start();
			}
		});

		spinner_multireq = new JSpinner();
		spinner_multireq.setEnabled(false);
		spinner_multireq.setModel(new SpinnerNumberModel(new Integer(30), new Integer(1), null, new Integer(1)));
		spinner_multireq.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				btn_multireq.setText((int) (spinner_multireq.getValue()) + " requests in a row");
			}
		});
		
		lblUpdated = new JLabel("last update");
		panel_1.add(lblUpdated, "cell 4 11");
		
		lblLabel_lastUpdate = new JLabel("0");
		panel_1.add(lblLabel_lastUpdate, "cell 5 11,alignx right");
		
		lblSecsAgo = new JLabel("secs ago");
		panel_1.add(lblSecsAgo, "cell 6 11 2 1");

		lblNewLabel_killCount = new JLabel("0");
		lblNewLabel_killCount.setHorizontalAlignment(SwingConstants.RIGHT);
		panel_1.add(lblNewLabel_killCount, "cell 8 11,grow");

		lblKills = new JLabel("Kills");
		panel_1.add(lblKills, "cell 9 11");

		btnSetStart = new JButton("set start");
		btnSetStart.setEnabled(false);
		btnSetStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				enableButtons(false);
				Main.setStart();
				lblNewLabel_killCount.setText("0");
				enableButtons(true);
			}
		});
		panel_1.add(btnSetStart, "cell 11 11,growx");

		separator = new JSeparator();
		panel_1.add(separator, "cell 4 12 8 1,growx");
		panel_1.add(spinner_multireq, "cell 4 18,growx");

		lblTimes = new JLabel("times with");
		panel_1.add(lblTimes, "cell 5 18");

		spinner_multireq_wait = new JSpinner();
		spinner_multireq_wait.setEnabled(false);
		spinner_multireq_wait.setModel(new SpinnerNumberModel(new Integer(1), new Integer(1), null, new Integer(1)));
		panel_1.add(spinner_multireq_wait, "cell 7 18,growx");

		lblMinsBreak = new JLabel("mins break");
		panel_1.add(lblMinsBreak, "cell 8 18");

		btn_multireq = new JButton("30 requests in a row");
		btn_multireq.setEnabled(false);
		panel_1.add(btn_multireq, "cell 11 18,growx");

		btn_multireq.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				enableButtons(false);
				Thread t = new Thread() {
					@Override
					public void run() {
						Main.api_key = textField_API.getText();
						int tmp = (int) (spinner_multireq.getValue());
						for (int i = tmp; i > 0; i--) {
							Main.sendRequest();
							btn_multireq.setText(i + " Requests in a row");
							btn_multireq.paint(btn_multireq.getGraphics());
							try {
								Thread.sleep((int) spinner_multireq_wait.getValue() * 60 * 1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						btn_multireq.setText(tmp + " Requests in a row");
						btn_multireq.paint(btn_multireq.getGraphics());
						enableButtons(true);
					}
				};
				t.start();
			}
		});
	}

	public JLabel getLblLabel_lastUpdate() {
		return lblLabel_lastUpdate;
	}

	public JTextField getTextField_IGN() {
		return textField_IGN;
	}

	private void enableButtons(boolean lock) {
		textField_API.setEnabled(lock);

		comboBox_profile.setEnabled(lock);
		btnLoadProfiles.setEnabled(lock);
		
		btnSendRequest.setEnabled(lock);
		btnSetStart.setEnabled(lock);

		spinner_multireq.setEnabled(lock);
		spinner_multireq_wait.setEnabled(lock);
		btn_multireq.setEnabled(lock);
	}

}
