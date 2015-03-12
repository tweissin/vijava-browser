package com.pitchcat.vijavabrowser;

import com.vmware.vim25.mo.ManagedEntity;

public class VITreeNodeSearch extends VITreeNode {
	private static final long serialVersionUID = 1L;

	public VITreeNodeSearch(String searchMethod, String display,
			ManagedEntity[] searchResult) {
		super(searchMethod, display, searchResult, null);
	}

	@Override
	public String toClipboardString() {
		return "(new InventoryNavigator("
				+ ((VITreeNode) parent).toClipboardString() + "))." + method;
	}
}
