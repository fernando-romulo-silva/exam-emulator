package org.examemulator.gui.components;

import javax.swing.tree.DefaultMutableTreeNode;

public class MainJTreeTreeNode extends DefaultMutableTreeNode {

    private static final long serialVersionUID = 1L;

    private final Class<?> type;
    
    public MainJTreeTreeNode(final Object userObject, final Class<?> type) {
        super(userObject, true);
        this.type = type;
    }
    
    public Class<?> getType() {
	return type;
    }
}
