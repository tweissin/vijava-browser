package com.pitchcat.vijavabrowser;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import com.vmware.vim25.mo.ServiceInstance;

public class LoginDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;
	private JLabel jLabelServer = null;
	private JTextField jTextFieldServer = null;
	private JLabel jLabelPort = null;
	private JTextField jTextFieldPort = null;
	private JLabel jLabelUser = null;
	private JTextField jTextFieldUser = null;
	private JLabel jLabelPassword = null;
	private JPasswordField jPasswordField = null;
	private JButton jButtonOk = null;
	private JButton jButtonCancel = null;

	/**
	 * @param owner
	 */
	public LoginDialog(Frame owner) {
		super(owner);
		initialize();
		getRootPane().setDefaultButton(getJButtonOk());
		setLocationRelativeTo(owner);
	}

	private void initialize() {
		this.setSize(300, 200);
		this.setResizable(false);
		this.setContentPane(getJContentPane());
		this.setTitle("Login to VIServer");
		this.setModal(true);
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.setVisible(false);
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jLabelPassword = new JLabel();
			jLabelPassword.setText("Password:");
			jLabelPassword.setLocation(new Point(10, 104));
			jLabelPassword.setSize(new Dimension(80, 13));
			jLabelUser = new JLabel();
			jLabelUser.setText("User:");
			jLabelUser.setLocation(new Point(10, 74));
			jLabelUser.setSize(new Dimension(80, 13));
			jLabelPort = new JLabel();
			jLabelPort.setText("Port:");
			jLabelPort.setLocation(new Point(10, 44));
			jLabelPort.setSize(new Dimension(80, 13));
			jLabelServer = new JLabel();
			jLabelServer.setText("Server:");
			jLabelServer.setLocation(new Point(10, 14));
			jLabelServer.setSize(new Dimension(80, 13));
			jContentPane = new JPanel();
			jContentPane.setLayout(null);
			jContentPane.add(jLabelServer, null);
			jContentPane.add(getJTextFieldServer(), null);
			jContentPane.add(jLabelPort, null);
			jContentPane.add(getJTextFieldPort(), null);
			jContentPane.add(jLabelUser, null);
			jContentPane.add(getJTextFieldUser(), null);
			jContentPane.add(jLabelPassword, null);
			jContentPane.add(getJPasswordField(), null);
			jContentPane.add(getJButtonOk(), null);
			jContentPane.add(getJButtonCancel(), null);
		}
		return jContentPane;
	}

	/**
	 * This method initializes jTextFieldServer
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTextFieldServer() {
		if (jTextFieldServer == null) {
			jTextFieldServer = new JTextField();
			jTextFieldServer.setSize(new Dimension(160, 19));
			jTextFieldServer.setLocation(new Point(100, 10));
		}
		return jTextFieldServer;
	}

	/**
	 * This method initializes jTextFieldPort
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTextFieldPort() {
		if (jTextFieldPort == null) {
			jTextFieldPort = new JTextField();
			jTextFieldPort.setPreferredSize(new Dimension(50, 19));
			jTextFieldPort.setLocation(new Point(100, 40));
			jTextFieldPort.setHorizontalAlignment(JTextField.RIGHT);
			jTextFieldPort.setText("");
			jTextFieldPort.setSize(new Dimension(50, 19));
		}
		return jTextFieldPort;
	}

	/**
	 * This method initializes jTextFieldUser
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTextFieldUser() {
		if (jTextFieldUser == null) {
			jTextFieldUser = new JTextField();
			jTextFieldUser.setSize(new Dimension(160, 19));
			jTextFieldUser.setLocation(new Point(100, 70));
		}
		return jTextFieldUser;
	}

	/**
	 * This method initializes jPasswordField
	 * 
	 * @return javax.swing.JPasswordField
	 */
	private JPasswordField getJPasswordField() {
		if (jPasswordField == null) {
			jPasswordField = new JPasswordField();
			jPasswordField.setSize(new Dimension(160, 19));
			jPasswordField.setLocation(new Point(100, 100));
		}
		return jPasswordField;
	}

	/**
	 * This method initializes jButtonOk
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJButtonOk() {
		if (jButtonOk == null) {
			jButtonOk = new JButton();
			jButtonOk.setText("OK");
			jButtonOk.setSize(new Dimension(80, 24));
			jButtonOk.setLocation(new Point(10, 134));
			jButtonOk.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					onOk();
				}
			});
		}
		return jButtonOk;
	}

	/**
	 * This method initializes jButtonCancel
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJButtonCancel() {
		if (jButtonCancel == null) {
			jButtonCancel = new JButton();
			jButtonCancel.setText("Cancel");
			jButtonCancel.setSize(new Dimension(80, 24));
			jButtonCancel.setLocation(new Point(100, 134));
			jButtonCancel
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							onCancel();
						}
					});
		}
		return jButtonCancel;
	}

	void onCancel() {
		dispose();
	}

	private ServiceInstance serviceInstance = null;

	public ServiceInstance getServiceInstance() {
		return serviceInstance;
	}

	void onOk() {
		serviceInstance = null;
		String server = jTextFieldServer.getText().trim();
		if (server.length() == 0) {
			JOptionPane.showMessageDialog(this, "Missing Server", "Error",
					JOptionPane.ERROR_MESSAGE);
			jTextFieldServer.requestFocusInWindow();
			return;
		}

		String port = jTextFieldPort.getText().trim();
		if (port.length() != 0) {
			try {
				int nPort = Integer.parseInt(port);
				if ((nPort < 0) || (65535 < nPort)) {
					JOptionPane.showMessageDialog(this, "Invalid Port",
							"Error", JOptionPane.ERROR_MESSAGE);
					jTextFieldPort.requestFocusInWindow();
					return;
				}
			} catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(this, "Invalid Port", "Error",
						JOptionPane.ERROR_MESSAGE);
				jTextFieldPort.requestFocusInWindow();
				return;
			}
		}

		String user = jTextFieldUser.getText().trim();
		if (user.length() == 0) {
			JOptionPane.showMessageDialog(this, "Missing User", "Error",
					JOptionPane.ERROR_MESSAGE);
			jTextFieldUser.requestFocusInWindow();
			return;
		}

		String password = (new String(jPasswordField.getPassword()));

		String url = "https://" + server;
		if (port.length() > 0) {
			url += ":" + port;
		}
		url += "/sdk";
		Cursor orgCursor = getCursor();
		try {
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			serviceInstance = new ServiceInstance(new URL(url), user, password,
					true);
			dispose();
		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(this, ex, "Error",
					JOptionPane.ERROR_MESSAGE);
		} finally {
			setCursor(orgCursor);
		}
	}
}
