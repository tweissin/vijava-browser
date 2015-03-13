package com.pitchcat.vijavabrowser;

import com.vmware.vim25.mo.ManagedEntity;
import com.vmware.vim25.mo.ServerConnection;
import com.vmware.vim25.mo.ServiceInstance;

import javax.swing.*;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreePath;
import javax.tools.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.HashSet;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Application implements TreeWillExpandListener {

	private ViJavaBrowserTransferHandler transferHandler = new ViJavaBrowserTransferHandler(); // @jve:decl-index=0:
	private JComponent lastActiveObject = null;

	private JFrame jFrame = null;
	private JPanel jContentPane = null;
	private JMenuBar jJMenuBar = null;
	private JMenu fileMenu = null;
	private JMenuItem exitMenuItem = null;
	private JScrollPane jTreeScrollPane = null;
	private JTree jTree = null;
	private JMenuItem connectMenuItem = null;
	private JSplitPane jSplitPane = null;
	private JScrollPane jTableScrollPane = null;
	private JTable jTable = null;
	private JMenuItem searchMenu = null;

	private static TreeSet<String> vimManagedEntityClasses = new TreeSet<String>(); // @jve:decl-index=0:

	private JMenu editMenu = null;

	private JMenuItem copyMenuItem = null;

	public static TreeSet<String> getVimManagedEntityClasses() {
		return vimManagedEntityClasses;
	}

	/**
	 * This method initializes jFrame
	 * 
	 * @return javax.swing.JFrame
	 */
	private JFrame getJFrame() {
		if (jFrame == null) {
			jFrame = new JFrame();
			jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			jFrame.setBounds(new Rectangle(0, 0, 600, 400));
			jFrame.setJMenuBar(getJJMenuBar());
			jFrame.setContentPane(getJContentPane());
			jFrame.setTitle("vijava browser");
			jFrame.addWindowListener(new java.awt.event.WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					onWindowClosing();
				}
			});
			jFrame.setLocationRelativeTo(null);
		}
		return jFrame;
	}

	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(getJSplitPane(), BorderLayout.CENTER);
		}
		return jContentPane;
	}

	/**
	 * This method initializes jJMenuBar
	 * 
	 * @return javax.swing.JMenuBar
	 */
	private JMenuBar getJJMenuBar() {
		if (jJMenuBar == null) {
			jJMenuBar = new JMenuBar();
			jJMenuBar.add(getFileMenu());
			jJMenuBar.add(getEditMenu());
		}
		return jJMenuBar;
	}

	/**
	 * This method initializes jMenu
	 * 
	 * @return javax.swing.JMenu
	 */
	private JMenu getFileMenu() {
		if (fileMenu == null) {
			fileMenu = new JMenu();
			fileMenu.setText("File");
			fileMenu.setMnemonic(KeyEvent.VK_F);
			fileMenu.add(getConnectMenuItem());
			fileMenu.add(getExitMenuItem());
		}
		return fileMenu;
	}

	/**
	 * This method initializes jMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getExitMenuItem() {
		if (exitMenuItem == null) {
			exitMenuItem = new JMenuItem();
			exitMenuItem.setText("Exit");
			exitMenuItem.setMnemonic(KeyEvent.VK_X);
			exitMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					System.exit(0);
				}
			});
		}
		return exitMenuItem;
	}

	/**
	 * This method initializes jTreeScrollPane
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJTreeScrollPane() {
		if (jTreeScrollPane == null) {
			jTreeScrollPane = new JScrollPane();
			jTreeScrollPane.setViewportView(getJTree());
		}
		return jTreeScrollPane;
	}

	/**
	 * This method initializes jTree
	 * 
	 * @return javax.swing.JTree
	 */
	private JTree getJTree() {
		if (jTree == null) {
			jTree = new JTree();
			jTree.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
				public void valueChanged(javax.swing.event.TreeSelectionEvent e) {
					copyMenuItem.setEnabled(true);
					onTreeSelectionChanged(e.getNewLeadSelectionPath());
				}
			});
			jTree.addFocusListener(new java.awt.event.FocusAdapter() {
				public void focusGained(java.awt.event.FocusEvent e) {
					lastActiveObject = jTree;
				}
			});
			((DefaultTreeModel) jTree.getModel()).setRoot(null);
			jTree.addTreeWillExpandListener(this);
			jTree.setCellRenderer(new ViJavaTreeCellRenderer());
			jTree.setTransferHandler(transferHandler);
			ToolTipManager.sharedInstance().registerComponent(jTree);
		}
		return jTree;
	}

	/**
	 * This method initializes connectMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getConnectMenuItem() {
		if (connectMenuItem == null) {
			connectMenuItem = new JMenuItem();
			connectMenuItem.setText("Connect");
			connectMenuItem.setMnemonic(KeyEvent.VK_N);
			connectMenuItem
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							onConnect();
						}
					});
		}
		return connectMenuItem;
	}

	/**
	 * This method initializes jSplitPane
	 * 
	 * @return javax.swing.JSplitPane
	 */
	private JSplitPane getJSplitPane() {
		if (jSplitPane == null) {
			jSplitPane = new JSplitPane();
			jSplitPane.setDividerLocation(400);
			jSplitPane.setRightComponent(getJTableScrollPane());
			jSplitPane.setLeftComponent(getJTreeScrollPane());
		}
		return jSplitPane;
	}

	/**
	 * This method initializes jTableScrollPane
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJTableScrollPane() {
		if (jTableScrollPane == null) {
			jTableScrollPane = new JScrollPane();
			jTableScrollPane.setViewportView(getJTable());
		}
		return jTableScrollPane;
	}

	/**
	 * This method initializes jTable
	 * 
	 * @return javax.swing.JTable
	 */
	private JTable getJTable() {
		if (jTable == null) {
			jTable = new JTable();
			jTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			jTable.setShowGrid(true);
			jTable.setAutoCreateColumnsFromModel(false);
			jTable.setModel(new DefaultTableModel(new Object[] { "Method Name",
					"Return Type", "Parameters" }, 0));
			TableColumn colName = new TableColumn(0);
			colName.setHeaderValue("Method Name");
			jTable.addColumn(colName);
			TableColumn colRetVal = new TableColumn(1);
			colRetVal.setHeaderValue("Return Type");
			jTable.addColumn(colRetVal);
			TableColumn colParams = new TableColumn(2);
			colParams.setHeaderValue("Parameters");
			jTable.addColumn(colParams);
			jTable.setTransferHandler(transferHandler);
			jTable.addFocusListener(new java.awt.event.FocusAdapter() {
				public void focusGained(java.awt.event.FocusEvent e) {
					lastActiveObject = jTable;
					copyMenuItem.setEnabled(true);
				}
			});
		}
		return jTable;
	}

	@Override
	public void treeWillCollapse(TreeExpansionEvent event)
			throws ExpandVetoException {
	}

	@Override
	public void treeWillExpand(TreeExpansionEvent event)
			throws ExpandVetoException {
		Cursor orgCursor = jFrame.getCursor();
		try {
			jFrame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			Object objNode = event.getPath().getLastPathComponent();
			if (!(objNode instanceof VITreeNode)) {
				return;
			}
			VITreeNode node = (VITreeNode) objNode;
			if (!node.willExpand()) {
				throw new ExpandVetoException(event);
			}
			((DefaultTreeModel) jTree.getModel()).reload(node);
		} finally {
			jFrame.setCursor(orgCursor);
		}
	}

	void onTreeSelectionChanged(TreePath path) {
		searchMenu.setEnabled(false);
		((DefaultTableModel) jTable.getModel()).setRowCount(0);
		if (path == null) {
			return;
		}
		Object node = path.getLastPathComponent();
		if ((node == null) || (!(node instanceof VITreeNode))) {
			return;
		}
		VITreeNode viNode = (VITreeNode) node;
		if (viNode.getUserObject() instanceof ManagedEntity) {
			searchMenu.setEnabled(true);
		}
		viNode.onTreeSelectionChanged(jTable);
	}

	/**
	 * This method initializes searchMenu
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getSearchMenu() {
		if (searchMenu == null) {
			searchMenu = new JMenuItem();
			searchMenu.setMnemonic(KeyEvent.VK_S);
			searchMenu.setEnabled(false);
			searchMenu.setText("Search");
			searchMenu.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					TreePath treePath = jTree.getSelectionPath();
					if (treePath != null) {
						Object node = treePath.getLastPathComponent();
						if ((node != null) && (node instanceof VITreeNode)) {
							VITreeNode viTreeNode = (VITreeNode) node;
							Object userObject = viTreeNode.getUserObject();
							if ((userObject != null)
									&& (userObject instanceof ManagedEntity)) {
								jTree.expandPath(treePath);
								ManagedEntity baseEntity = (ManagedEntity) userObject;
								InventoryNavigatorDialog dialog = new InventoryNavigatorDialog(
										getJFrame(), baseEntity);
								dialog.setVisible(true);
								ManagedEntity[] result = dialog
										.getSearchResult();
								if (result != null) {
									VITreeNodeSearch newNode = new VITreeNodeSearch(
											dialog.getSearchMethod(), dialog
											.getSearchMethod(), result);
									DefaultTreeModel model = (DefaultTreeModel) jTree
											.getModel();
									model.insertNodeInto(newNode, viTreeNode,
											viTreeNode.getChildCount());
									TreePath newPath = new TreePath(newNode
											.getPath());
									jTree.scrollPathToVisible(newPath);
									jTree.setSelectionPath(newPath);
								}
							}
						}
					}
				}
			});
		}
		return searchMenu;
	}

	/**
	 * This method initializes editMenu
	 * 
	 * @return javax.swing.JMenu
	 */
	private JMenu getEditMenu() {
		if (editMenu == null) {
			editMenu = new JMenu();
			editMenu.setMnemonic(KeyEvent.VK_E);
			editMenu.setText("Edit");
			editMenu.add(getCopyMenuItem());
			editMenu.add(getSearchMenu());
		}
		return editMenu;
	}

	/**
	 * This method initializes copyMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getCopyMenuItem() {
		if (copyMenuItem == null) {
			copyMenuItem = new JMenuItem();
			copyMenuItem.setMnemonic(KeyEvent.VK_C);
			copyMenuItem.setEnabled(false);
			copyMenuItem.setText("Copy");
			copyMenuItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					StringSelection string = getToClipboardText();
					if (string != null) {
						Toolkit.getDefaultToolkit().getSystemClipboard()
								.setContents(string, string);
					}
				}
			});
		}
		return copyMenuItem;
	}

	/**
	 * Launches this application
	 */
	public static void main(String[] args) {

		JavaCompiler javac = ToolProvider.getSystemJavaCompiler();
		JavaFileManager jfm = javac.getStandardFileManager(
				new DiagnosticCollector<JavaFileObject>(), null, null);
		HashSet<JavaFileObject.Kind> kind = new HashSet<JavaFileObject.Kind>() {
			private static final long serialVersionUID = 1L;
			{
				add(JavaFileObject.Kind.CLASS);
			}
		};
		try {
			Pattern pattern = Pattern.compile(".*\\((.*)\\)");
			for (JavaFileObject f : jfm.list(StandardLocation.CLASS_PATH,
					"com.vmware.vim25.mo", kind, false)) {
				String className = f.getName();
				if (!className.equals("ManagedEntity.class")) {
					Matcher matcher = pattern.matcher(className);
					if(matcher.matches()) {
						className = matcher.group(1);
						className = className.substring(className.lastIndexOf("/")+1);
					}
					try {
						Class<?> c = Class
								.forName("com.vmware.vim25.mo."
										+ className.substring(0,
												className.length() - 6));
						if (ManagedEntity.class.isAssignableFrom(c)) {
							vimManagedEntityClasses.add(c.getSimpleName());
						}
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				Application application = new Application();
				application.getJFrame().setVisible(true);
			}
		});
	}

	void onWindowClosing() {
		if (jTree != null) {
			DefaultTreeModel model = (DefaultTreeModel) jTree.getModel();
			if (model != null) {
				Object root = model.getRoot();
				if (root != null) {
					if (root instanceof VITreeNode) {
						VITreeNode node = (VITreeNode) root;
						Object userObject = node.getUserObject();
						if (userObject != null) {
							if (userObject instanceof ServiceInstance) {
								ServiceInstance serviceInstance = (ServiceInstance) userObject;
								ServerConnection serverConnection = serviceInstance
										.getServerConnection();
								if (serverConnection != null) {
									serverConnection.logout();
								}
							}
						}
					}
				}
			}
		}
	}

	public StringSelection getToClipboardText() {
		TreePath selectionPath = jTree.getSelectionPath();
		if (selectionPath != null) {
			Object node = selectionPath.getLastPathComponent();
			if (node != null) {
				if (node instanceof VITreeNode) {
					VITreeNode viTreeNode = (VITreeNode) node;
					String string = viTreeNode.toClipboardString();
					if (string != null) {
						if (lastActiveObject instanceof JTable) {
							int nRow = jTable.getSelectedRow();
							if (nRow >= 0) {
								DefaultTableModel model = (DefaultTableModel) jTable
										.getModel();
								String rcType = model.getValueAt(nRow, 1)
										.toString();
								if (!rcType.equals("void")) {
									string = rcType + " rc = " + string;
								}
								string += "." + model.getValueAt(nRow, 0) + "("
										+ model.getValueAt(nRow, 2) + ")";
							}
						}
						return new StringSelection(string + ";");
					}
				}
			}
		}
		return null;
	}

	class ViJavaBrowserTransferHandler extends TransferHandler {
		private static final long serialVersionUID = 1L;

		@Override
		public int getSourceActions(JComponent c) {
			return COPY;
		}

		@Override
		protected Transferable createTransferable(JComponent c) {
			return getToClipboardText();
		}
	}

	static class ViJavaTreeCellRenderer extends DefaultTreeCellRenderer {
		private static final long serialVersionUID = 1L;

		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value,
				boolean sel, boolean expanded, boolean leaf, int row,
				boolean hasFocus) {
			JComponent c = (JComponent) super.getTreeCellRendererComponent(
					tree, value, sel, expanded, leaf, row, hasFocus);
			c.setToolTipText(value == null ? null : value.toString());
			return c;
		}

	}
	
	public void onConnect() {
		LoginDialog loginDialog = new LoginDialog(getJFrame());
		loginDialog.setVisible(true);
		ServiceInstance si = loginDialog.getServiceInstance();
		if (si != null) {
			VITreeNodeRoot root = new VITreeNodeRoot(si.getServerConnection()
					.getUrl().toString(), si);
			DefaultTreeModel model = (DefaultTreeModel) jTree.getModel();
			model.setRoot(root);
			model.reload();
			jTree.collapsePath(new TreePath(root));
		}
	}


}
