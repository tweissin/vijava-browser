package com.pitchcat.vijavabrowser;

import com.vmware.vim25.mo.ServerConnection;
import com.vmware.vim25.mo.ServiceInstance;

public class VITreeNodeRoot extends VITreeNode {
	private static final long serialVersionUID = 1L;

	public VITreeNodeRoot(String display, ServiceInstance si) {
		super(null, display, si, null);
	}

	@Override
	public String toClipboardString() {
		ServerConnection connection = ((ServiceInstance) userObject)
				.getServerConnection();
		return "new ServiceInstance(new URL(\"" + connection.getUrl()
				+ "\"),\"" + connection.getUsername() + "\",\"********\",true)";
	}

}
