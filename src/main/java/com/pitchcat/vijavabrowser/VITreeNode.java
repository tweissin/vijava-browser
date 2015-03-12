package com.pitchcat.vijavabrowser;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Comparator;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;

public class VITreeNode extends DefaultMutableTreeNode {
	private static final long serialVersionUID = 1L;

	protected String method;
	protected String display;
	protected String cast;

	public VITreeNode(String method, String display, Object viObject,
			String cast) {
		super(viObject);
		this.method = method;
		this.display = display;
		this.cast = cast;
		if (viObject != null) {
			add(new DefaultMutableTreeNode());
		}
	}

	@Override
	public String toString() {
		return display;
	}

	protected static Method[] getSortedMethods(Class<? extends Object> cls) {
		Method[] methods = cls.getMethods();
		Arrays.sort(methods, new Comparator<Method>() {
			@Override
			public int compare(Method o1, Method o2) {
				if (o1 == null && o2 == null)
					return 0;
				if (o1 == null && o2 != null)
					return 1;
				if (o1 != null && o2 == null)
					return -1;
				String o1Name = o1.getName();
				String o2Name = o2.getName();
				if (o1Name == null && o2Name == null)
					return 0;
				if (o1Name == null && o2Name != null)
					return 1;
				if (o1Name != null && o2Name == null)
					return -1;
				return o1Name.compareToIgnoreCase(o2Name);
			}
		});
		return methods;
	}

	public static String getValueText(Object obj) {
		if (obj == null) {
			return "(null)";
		} else if (obj.getClass().isArray()) {
			return obj.getClass().getComponentType().getSimpleName() + "[]";
		} else {
			try {
				Method method = obj.getClass().getMethod("getName");
				Object name = method.invoke(obj);
				if (name != null) {
					String strName = name.toString();
					if (strName.length() > 0) {
						return strName;
					}
				}
			} catch (Throwable e) {
			}
			return obj.toString();
		}
	}

	public boolean willExpand() {
		removeAllChildren();
		if (userObject == null)
			return false;
		Class<? extends Object> cls = userObject.getClass();
		if (cls.isArray()) {
			try {
				int length = Array.getLength(userObject);
				for (int i = 0; i < length; ++i) {
					String display = cls.getSimpleName();
					if (display.endsWith("[]")) {
						display = display.substring(0, display.length() - 1)
								+ i + "]";
					} else {
						display += ":" + i + "";
					}
					Object obj = Array.get(userObject, i);
					display += " = " + getValueText(obj);
					String cast = null;
					if ((obj != null)
							&& (!cls.getComponentType().getCanonicalName()
									.equals(obj.getClass().getCanonicalName()))) {
						cast = obj.getClass().getCanonicalName();
					}
					add(new VITreeNode("[" + i + "]", display, obj, cast));
				}
			} catch (Exception e) {
				e.printStackTrace();
				add(new VITreeNode("[?]", e.toString(), e, null));
			}
		} else {
			for (Method method : getSortedMethods(cls)) {
				if (((method.getModifiers() & Modifier.PUBLIC) != Modifier.PUBLIC)) {
					continue;
				}
				String methodName = method.getName();
				String displayMethodName = null;
				if (methodName.startsWith("get")) {
					displayMethodName = methodName.substring(3);
				} else if (methodName.equals("toString")
						|| methodName.startsWith("is")
						|| methodName.startsWith("query")
						// || methodName.startsWith("retrieve")
						|| methodName.startsWith("current")) {
					displayMethodName = methodName;
				} else {
					continue;
				}

				Class<? extends Object> returnType = method.getReturnType();
				if (returnType.equals(Void.class))
					continue;
				if (method.getParameterTypes().length != 0)
					continue;
				String display = displayMethodName + "("
						+ returnType.getSimpleName() + ")";
				Object result = null;
				try {
					result = method.invoke(userObject);
					display += " = " + getValueText(result);
				} catch (Exception e) {
					e.printStackTrace();
					display += " -> " + e.toString();
					result = null;
				}
				String cast = null;
				if ((result != null)
						&& (!returnType.getCanonicalName().equals(
								result.getClass().getCanonicalName()))) {
					cast = result.getClass().getCanonicalName();
				}
				add(new VITreeNode("." + methodName + "()", display, result,
						cast));
			}
		}

		return true;
	}

	public String toClipboardString() {
		if ((method != null) && (parent != null)) {
			String result = ((VITreeNode) parent).toClipboardString() + method;
			if (cast != null) {
				result = "(" + cast + ")(" + result + ")";
			}
			return result;
		}
		return toString();
	}

	public void onTreeSelectionChanged(JTable table) {
		if (userObject == null)
			return;
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		Class<? extends Object> cls = userObject.getClass();
		for (Method method : getSortedMethods(cls)) {
			if ((method.getModifiers() & Modifier.PUBLIC) != Modifier.PUBLIC)
				continue;
			String methodName = method.getName();
			Class<? extends Object> returnType = method.getReturnType();
			String returnTypeName = returnType.getSimpleName();
			StringBuilder parameters = new StringBuilder();
			for (Class<? extends Object> paramType : method.getParameterTypes()) {
				if (parameters.length() != 0)
					parameters.append(", ");
				parameters.append(paramType.getSimpleName());
			}
			model.addRow(new Object[] { methodName, returnTypeName,
					parameters.toString() });
		}
	}

}
