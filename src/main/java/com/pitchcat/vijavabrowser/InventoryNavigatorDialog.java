package com.pitchcat.vijavabrowser;

import javax.swing.JPanel;
import java.awt.Frame;
import javax.swing.JDialog;

import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.ManagedEntity;
import java.awt.Dimension;
import javax.swing.JLabel;
import java.awt.Rectangle;
import javax.swing.JTextField;
import java.awt.Point;
import java.awt.event.KeyEvent;
import javax.swing.JList;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

public class InventoryNavigatorDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	private ManagedEntity[] searchResult = null;
	private String searchMethod = null;

	private ManagedEntity searchBase = null;
	private JPanel jContentPane = null;
	private JLabel baseEntiryLabel = null;
	private JTextField baseEntityText = null;
	private JLabel searchTypeLabel = null;
	private JList searchTypeList = null;
	private JLabel searchNameLabel = null;
	private JTextField searchNameText = null;
	private JButton searchButton = null;
	private JButton cancelButton = null;
	private JScrollPane searchTypeScrollPane = null;

	public ManagedEntity[] getSearchResult() {
		return searchResult;
	}

	public String getSearchMethod() {
		return searchMethod;
	}

	/**
	 * @param owner
	 */
	public InventoryNavigatorDialog(Frame owner, ManagedEntity searchBase) {
		super(owner);
		this.searchBase = searchBase;
		initialize();
		baseEntityText.setText(VITreeNode.getValueText(searchBase));
		getRootPane().setDefaultButton(searchButton);
		setLocationRelativeTo(owner);
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setBounds(new Rectangle(0, 0, 500, 300));
		this.setTitle("Inventory Navigator");
		this.setContentPane(getJContentPane());
		this.setModal(true);
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			searchNameLabel = new JLabel();
			searchNameLabel.setBounds(new Rectangle(15, 180, 136, 20));
			searchNameLabel.setText("Search Name:");
			searchNameLabel.setDisplayedMnemonic(KeyEvent.VK_N);
			searchTypeLabel = new JLabel();
			searchTypeLabel.setText("Search Type:");
			searchTypeLabel.setSize(new Dimension(136, 20));
			searchTypeLabel.setDisplayedMnemonic(KeyEvent.VK_T);
			searchTypeLabel.setLocation(new Point(15, 45));
			baseEntiryLabel = new JLabel();
			baseEntiryLabel.setBounds(new Rectangle(15, 15, 136, 20));
			baseEntiryLabel.setText("Search Base Entity:");
			jContentPane = new JPanel();
			jContentPane.setLayout(null);
			jContentPane.add(baseEntiryLabel, null);
			jContentPane.add(getBaseEntityText(), null);
			jContentPane.add(searchTypeLabel, null);
			jContentPane.add(getSearchTypeScrollPane(), null);
			jContentPane.add(searchNameLabel, null);
			jContentPane.add(getSearchNameText(), null);
			jContentPane.add(getSearchButton(), null);
			jContentPane.add(getCancelButton(), null);
		}
		return jContentPane;
	}

	/**
	 * This method initializes baseEntityText
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getBaseEntityText() {
		if (baseEntityText == null) {
			baseEntityText = new JTextField();
			baseEntityText.setBounds(new Rectangle(165, 15, 301, 20));
			baseEntityText.setEnabled(false);
		}
		return baseEntityText;
	}

	/**
	 * This method initializes searchTypeList
	 * 
	 * @return javax.swing.JList
	 */
	private JList getSearchTypeList() {
		if (searchTypeList == null) {
			searchTypeList = new JList(Application.getVimManagedEntityClasses()
					.toArray());
			searchTypeList
					.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		}
		return searchTypeList;
	}

	/**
	 * This method initializes searchNameText
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getSearchNameText() {
		if (searchNameText == null) {
			searchNameText = new JTextField();
			searchNameText.setBounds(new Rectangle(165, 180, 301, 20));
		}
		return searchNameText;
	}

	/**
	 * This method initializes searchButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getSearchButton() {
		if (searchButton == null) {
			searchButton = new JButton();
			searchButton.setBounds(new Rectangle(15, 225, 136, 28));
			searchButton.setMnemonic(KeyEvent.VK_ACCEPT);
			searchButton.setText("Search");
			searchButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					doSearch();
				}
			});
		}
		return searchButton;
	}

	void doSearch() {
		try {
			InventoryNavigator nav = new InventoryNavigator(searchBase);
			searchResult = null;

			String searchName = searchNameText.getText().trim();
			searchName = (searchName.length() > 0) ? searchName : null;
			Object selectedTypeValue = searchTypeList.getSelectedValue();
			String searchType = (selectedTypeValue == null) ? null
					: selectedTypeValue.toString();

			if (searchType != null) {
				if (searchName != null) {
					searchResult = new ManagedEntity[1];
					searchResult[0] = nav.searchManagedEntity(searchType,
							searchName);
					if (searchResult[0] == null) {
						searchResult = null;
						JOptionPane.showMessageDialog(this, "Not found",
								"Error", JOptionPane.ERROR_MESSAGE);
						return;
					}
					searchMethod = "searchManagedEntity(\"" + searchType
							+ "\",\"" + searchName + "\")";
				} else {
					searchResult = nav.searchManagedEntities(searchType);
					searchMethod = "searchManagedEntity(\"" + searchType
							+ "\")";
				}
			} else {
				if (searchName != null) {
					JOptionPane.showMessageDialog(this, "Please specify type",
							"Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				searchResult = nav.searchManagedEntities(true);
				searchMethod = "searchManagedEntity(true)";
			}

			if ((searchResult == null) || (searchResult.length == 0)) {
				searchResult = null;
				JOptionPane.showMessageDialog(this, "Not found", "Error",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			dispose();
		} catch (Throwable th) {
			th.printStackTrace();
			JOptionPane.showMessageDialog(this, th, "Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * This method initializes cancelButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getCancelButton() {
		if (cancelButton == null) {
			cancelButton = new JButton();
			cancelButton.setBounds(new Rectangle(165, 225, 136, 28));
			cancelButton.setText("Cancel");
			cancelButton.setMnemonic(KeyEvent.VK_ESCAPE);
			cancelButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					dispose();
				}
			});
		}
		return cancelButton;
	}

	/**
	 * This method initializes searchTypeScrollPane
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getSearchTypeScrollPane() {
		if (searchTypeScrollPane == null) {
			searchTypeScrollPane = new JScrollPane();
			searchTypeScrollPane.setBounds(new Rectangle(165, 45, 301, 121));
			searchTypeScrollPane.setViewportView(getSearchTypeList());
		}
		return searchTypeScrollPane;
	}

} // @jve:decl-index=0:visual-constraint="10,10"
