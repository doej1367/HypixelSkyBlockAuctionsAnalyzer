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
import javax.swing.JCheckBox;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import java.awt.Font;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

public class MainWindow extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField textField_API;
	private JCheckBox checkBox_CT;
	private JTextField textField_CT;

	private JCheckBox checkBox_SL;
	private JSpinner spinner_SL;

	private JCheckBox checkBox_HB;
	private JSpinner spinner_HB;

	private JTextArea consoleOut;
	private JScrollPane sp;
	private JCheckBox chckbxMatchCase;
	private JCheckBox chckbxMinutesSL;
	private JLabel lblFilterTotalTime;
	private JCheckBox checkBox_TT;
	private JSpinner spinner_TT;
	private JCheckBox checkBox_MinutesTT;
	private JButton btnFilterButton;
	private JProgressBar progressBar;
	private JRadioButton rdbtnHighestbidasc;

	public JRadioButton getRdbtnHighestbidasc() {
		return rdbtnHighestbidasc;
	}

	public JRadioButton getRdbtnSecondsleftdec() {
		return rdbtnSecondsleftdec;
	}

	private JRadioButton rdbtnSecondsleftdec;
	private JCheckBox chckbxKeepOldData;
	private JSpinner spinner_multireq;
	private JButton btn_multireq;
	private JButton btnSendRequest;

	public JProgressBar getProgressBar() {
		return progressBar;
	}

	public JCheckBox getChckbxMatchCase() {
		return chckbxMatchCase;
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
	public MainWindow(Main Main) {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 850, 580);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));

		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.SOUTH);
		panel.setLayout(new BorderLayout(0, 0));

		consoleOut = new JTextArea();
		consoleOut.setEditable(false);
		consoleOut.setFont(new Font("Consolas", Font.PLAIN, 13));
		consoleOut.setRows(15);

		sp = new JScrollPane(consoleOut);
		sp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		panel.add(sp);

		JPanel panel_1 = new JPanel();
		contentPane.add(panel_1, BorderLayout.CENTER);
		panel_1.setLayout(new MigLayout("fill", "[][][][][22.00][][30.00,grow][30.00,grow][][][][][][][][][][]",
				"[][][][][][][][][][][][][][][]"));

		JLabel lblNewLabel = new JLabel("API key:");
		panel_1.add(lblNewLabel, "cell 0 1 5 1,growx");

		textField_API = new JTextField(Main.getApi_key());
		panel_1.add(textField_API, "cell 6 1 6 1,growx");
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
					bw.write("" + textField_API.getText());
					bw.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}

			@Override
			public void keyPressed(KeyEvent e) {

			}
		});

		JLabel lblFilterContainingText = new JLabel("Filter Containing Text:");
		panel_1.add(lblFilterContainingText, "cell 0 3 5 1,growx");

		checkBox_CT = new JCheckBox("");
		checkBox_CT.setSelected(true);
		panel_1.add(checkBox_CT, "cell 5 3");

		textField_CT = new JTextField();
		textField_CT.setText("Super Comp");
		textField_CT.setColumns(10);
		panel_1.add(textField_CT, "cell 6 3 6 1,growx");

		chckbxMatchCase = new JCheckBox("Match Case");
		chckbxMatchCase.setToolTipText("");
		panel_1.add(chckbxMatchCase, "cell 12 3 5 1,growx");

		JLabel lblFilterSecondsLeft = new JLabel("Filter Seconds Left (less than):");
		panel_1.add(lblFilterSecondsLeft, "cell 0 4 5 1,growx");

		checkBox_SL = new JCheckBox("");
		checkBox_SL.setSelected(true);
		panel_1.add(checkBox_SL, "cell 5 4");

		spinner_SL = new JSpinner();
		spinner_SL.setModel(new SpinnerNumberModel(new Integer(10), new Integer(0), null, new Integer(1)));
		panel_1.add(spinner_SL, "cell 6 4 6 1,growx");

		chckbxMinutesSL = new JCheckBox("Minutes");
		chckbxMinutesSL.setToolTipText("");
		panel_1.add(chckbxMinutesSL, "cell 12 4 5 1");

		lblFilterTotalTime = new JLabel("Filter Total Time Available (more than):");
		panel_1.add(lblFilterTotalTime, "cell 0 5 5 1");

		checkBox_TT = new JCheckBox("");
		panel_1.add(checkBox_TT, "cell 5 5");

		spinner_TT = new JSpinner();
		spinner_TT.setModel(new SpinnerNumberModel(new Integer(5), new Integer(0), null, new Integer(1)));
		panel_1.add(spinner_TT, "cell 6 5 6 1,growx");

		checkBox_MinutesTT = new JCheckBox("Minutes");
		checkBox_MinutesTT.setSelected(true);
		checkBox_MinutesTT.setToolTipText("");
		panel_1.add(checkBox_MinutesTT, "cell 12 5 5 1");

		JLabel lblFilterHighestBidMinimum = new JLabel("Filter Highest Bid (more than):");
		panel_1.add(lblFilterHighestBidMinimum, "cell 0 6 5 1,growx");

		checkBox_HB = new JCheckBox("");
		checkBox_HB.setSelected(true);
		panel_1.add(checkBox_HB, "cell 5 6");

		spinner_HB = new JSpinner();
		spinner_HB.setModel(new SpinnerNumberModel(new Integer(1), new Integer(0), null, new Integer(1)));
		panel_1.add(spinner_HB, "cell 6 6 6 1,growx");

		btnFilterButton = new JButton("Filter Collected Data");
		btnFilterButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Main.filterData(checkBox_CT.isSelected(), textField_CT.getText(), chckbxMatchCase.isSelected(),
						checkBox_SL.isSelected(),
						(int) (spinner_SL.getValue()) * (chckbxMinutesSL.isSelected() ? 60 : 1),
						checkBox_TT.isSelected(),
						(int) (spinner_TT.getValue()) * (checkBox_MinutesTT.isSelected() ? 60 : 1),
						checkBox_HB.isSelected(), (int) (spinner_HB.getValue()));
			}
		});

		rdbtnHighestbidasc = new JRadioButton("Sort HighestBidAsc");
		rdbtnHighestbidasc.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				if (((JRadioButton) arg0.getSource()).isSelected())
					rdbtnSecondsleftdec.setSelected(false);
			}
		});
		panel_1.add(rdbtnHighestbidasc, "cell 0 8 5 1,growx");

		rdbtnSecondsleftdec = new JRadioButton("Sort SecondsLeftDec");
		rdbtnSecondsleftdec.setSelected(true);
		rdbtnSecondsleftdec.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (((JRadioButton) e.getSource()).isSelected())
					rdbtnHighestbidasc.setSelected(false);
			}
		});
		panel_1.add(rdbtnSecondsleftdec, "cell 0 9 5 1,growx");

		spinner_multireq = new JSpinner();
		spinner_multireq.setModel(new SpinnerNumberModel(new Integer(5), new Integer(1), null, new Integer(1)));
		spinner_multireq.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				btn_multireq.setText((int) (spinner_multireq.getValue()) + " Requests in a row");
			}
		});
		panel_1.add(spinner_multireq, "cell 12 9 5 1,growx");

		btn_multireq = new JButton((int) (spinner_multireq.getValue()) + " Requests in a row");
		btn_multireq.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				enableButtons(false);
				Thread t = new Thread() {
					@Override
					public void run() {
						Main.setApi_key(textField_API.getText());
						int tmp = (int) (spinner_multireq.getValue());
						for (int i = tmp; i > 0; i--) {
							Main.sendRequest(checkBox_CT.isSelected(), textField_CT.getText(),
									chckbxMatchCase.isSelected(), checkBox_SL.isSelected(),
									(int) (spinner_SL.getValue()) * (chckbxMinutesSL.isSelected() ? 60 : 1),
									checkBox_TT.isSelected(),
									(int) (spinner_TT.getValue()) * (checkBox_MinutesTT.isSelected() ? 60 : 1),
									checkBox_HB.isSelected(), (int) (spinner_HB.getValue()));
							Main.filterData(checkBox_CT.isSelected(), textField_CT.getText(),
									chckbxMatchCase.isSelected(), checkBox_SL.isSelected(),
									(int) (spinner_SL.getValue()) * (chckbxMinutesSL.isSelected() ? 60 : 1),
									checkBox_TT.isSelected(),
									(int) (spinner_TT.getValue()) * (checkBox_MinutesTT.isSelected() ? 60 : 1),
									checkBox_HB.isSelected(), (int) (spinner_HB.getValue()));
							btn_multireq.setText(i + " Requests in a row");
							btn_multireq.paint(btn_multireq.getGraphics());
							try {
								Thread.sleep(60 * 1000);
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
		panel_1.add(btn_multireq, "cell 17 9,growx");

		chckbxKeepOldData = new JCheckBox("Keep Old Data");
		chckbxKeepOldData.setSelected(true);
		panel_1.add(chckbxKeepOldData, "cell 17 13");
		panel_1.add(btnFilterButton, "cell 0 14 5 1,grow");

		progressBar = new JProgressBar();
		panel_1.add(progressBar, "cell 6 14 11 1,grow");

		btnSendRequest = new JButton("Send Request and Filter");
		panel_1.add(btnSendRequest, "cell 17 14,grow");
		btnSendRequest.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				enableButtons(false);
				Thread t = new Thread() {
					@Override
					public void run() {
						Main.setApi_key(textField_API.getText());
						Main.sendRequest(checkBox_CT.isSelected(), textField_CT.getText(), chckbxMatchCase.isSelected(),
								checkBox_SL.isSelected(),
								(int) (spinner_SL.getValue()) * (chckbxMinutesSL.isSelected() ? 60 : 1),
								checkBox_TT.isSelected(),
								(int) (spinner_TT.getValue()) * (checkBox_MinutesTT.isSelected() ? 60 : 1),
								checkBox_HB.isSelected(), (int) (spinner_HB.getValue()));
						Main.filterData(checkBox_CT.isSelected(), textField_CT.getText(), chckbxMatchCase.isSelected(),
								checkBox_SL.isSelected(),
								(int) (spinner_SL.getValue()) * (chckbxMinutesSL.isSelected() ? 60 : 1),
								checkBox_TT.isSelected(),
								(int) (spinner_TT.getValue()) * (checkBox_MinutesTT.isSelected() ? 60 : 1),
								checkBox_HB.isSelected(), (int) (spinner_HB.getValue()));
						super.run();
						enableButtons(true);
					}
				};
				t.start();
			}
		});
	}

	public JButton getBtnFilterButton() {
		return btnFilterButton;
	}

	public JCheckBox getChckbxKeepOldData() {
		return chckbxKeepOldData;
	}

	private void enableButtons(boolean lock) {
		textField_API.setEnabled(lock);
		spinner_multireq.setEnabled(lock);
		btn_multireq.setEnabled(lock);
		chckbxKeepOldData.setEnabled(lock);
		btnSendRequest.setEnabled(lock);
	}

}
