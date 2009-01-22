/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: Dec 8, 2008
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.business.util.tree;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.kesdip.common.util.StringUtils;


/**
 * An object in a tree-like hierarchy.
 * <p>
 * It is used to represent hierarchies of different types into a single tree
 * structure. Two identical objects cannot exist as children of the same parent.
 * </p>
 * 
 * @author gerogias
 */
public class TreeNode implements Serializable {

	/**
	 * Serialization version.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The primary key of the underlying object.
	 */
	private Serializable primaryKey = null;

	/**
	 * The name of the object, used as label.
	 */
	private String name = null;

	/**
	 * The type of the represented object. This can be a constant, a class
	 * name...
	 */
	private String type = null;

	/**
	 * If the node is selected.
	 */
	private boolean selected = false;

	/**
	 * If the node is disabled.
	 */
	private boolean disabled = false;

	/**
	 * A type-specific status. It can be anything.
	 */
	private String status = null;

	/**
	 * The node's parent.
	 */
	private TreeNode parentNode = null;

	/**
	 * The node's children.
	 */
	private List<TreeNode> children = null;

	/**
	 * Default constructor.
	 * 
	 * @param key
	 *            the primary key of the underlying object
	 * @param type
	 *            the object's type
	 * @param name
	 *            the name of the object
	 * @throws IllegalArgumentException
	 *             if the arguments are <code>null</code>/empty
	 */
	public TreeNode(Serializable key, String type, String name) {
		if (key == null || StringUtils.isEmpty(type)
				|| StringUtils.isEmpty(name)) {
			throw new IllegalArgumentException("Arguments cannot be null/empty");
		}
		this.primaryKey = key;
		this.type = type;
		this.name = name;
		this.children = new ArrayList<TreeNode>();
	}

	/**
	 * @return the parentNode
	 */
	public TreeNode getParentNode() {
		return parentNode;
	}

	/**
	 * @param parentNode
	 *            the parentNode to set
	 */
	public void setParentNode(TreeNode parentNode) {
		this.parentNode = parentNode;
	}

	/**
	 * @return the selected
	 */
	public boolean isSelected() {
		return selected;
	}

	/**
	 * @param selected
	 *            the selected to set
	 */
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	/**
	 * @return List a read-only copy of the children
	 */
	public List<TreeNode> getChildren() {
		return Collections.unmodifiableList(children);
	}

	/**
	 * Clears all children and unsets their parent.
	 */
	public void clearChildren() {
		for (TreeNode node : this.children) {
			node.setParentNode(null);
		}
		this.children.clear();
	}

	/**
	 * Adds a child to the end of the list, if it does not already exist.
	 * 
	 * @param node
	 *            the new child
	 * @return boolean <code>true</code> if the child was added,
	 *         <code>false</code> if it was existing or <code>null</code>
	 */
	public boolean addChild(TreeNode node) {
		if (node == null) {
			return false;
		}
		if (this.children.contains(node)) {
			return false;
		}
		this.children.add(node);
		node.setParentNode(this);
		return true;
	}

	/**
	 * Adds a child to the specified index of the list, if it does not already
	 * exist.
	 * 
	 * @param index
	 *            the index
	 * @param node
	 *            the new child
	 * @return boolean <code>true</code> if the child was added,
	 *         <code>false</code> if it was existing or <code>null</code>
	 * @throws ArrayIndexOutOfBoundsException
	 *             if the index is not valid
	 */
	public boolean addChild(int index, TreeNode node) {
		if (node == null) {
			return false;
		}
		if (this.children.contains(node)) {
			return false;
		}
		this.children.add(index, node);
		node.setParentNode(this);
		return true;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the primaryKey
	 */
	public Serializable getPrimaryKey() {
		return primaryKey;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * Equality implementation. 2 nodes are equal if their id and type are the
	 * same.
	 * 
	 * @param obj
	 *            the object to compare with
	 */
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof TreeNode)) {
			return false;
		}
		TreeNode otherNode = (TreeNode) obj;
		if (!this.primaryKey.equals(otherNode.primaryKey)) {
			return false;
		}
		if (!this.type.equals(otherNode.type)) {
			return false;
		}
		return true;
	}

	/**
	 * Generates a hashcode from the primary key and the type.
	 * 
	 * @return int the hashcode
	 */
	public int hashCode() {
		return primaryKey.hashCode() + type.hashCode();
	}

	/**
	 * @return the disabled
	 */
	public boolean isDisabled() {
		return disabled;
	}

	/**
	 * @param disabled
	 *            the disabled to set
	 */
	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String property) {
		this.status = property;
	}

	public void setName(String name) {
		this.name = name;
	}
}
