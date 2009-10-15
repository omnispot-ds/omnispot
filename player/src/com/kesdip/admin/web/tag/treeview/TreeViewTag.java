/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: Dec 1, 2008
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */
package com.kesdip.admin.web.tag.treeview;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspTagException;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.tags.RequestContextAwareTag;

import com.kesdip.admin.web.constenum.IObjectKeysEnum;
import com.kesdip.business.util.tree.TreeNode;
import com.kesdip.business.util.tree.TreeViewGenerator;
import com.kesdip.common.util.BeanUtils;
import com.kesdip.common.util.StreamUtils;

/**
 * Renders a <code>Yahoo UI</code> tree on the page.
 * <p>
 * Only works in a Spring application and requires that
 * {@link TreeViewGenerationBean} is in the context named
 * <code>treeViewGenerator</code>. Multiple instances may co-exist on
 * the same page; only the CSS and JS of the first occurence will be included in
 * the page.
 * </p>
 * 
 * @author gerogias
 */
public class TreeViewTag extends RequestContextAwareTag {

	/**
	 * Script template.
	 */
	private final String SCRIPT_TEMPLATE = "com/kesdip/admin/web/tag/treeview/script-template.txt";

	/**
	 * Template for the tree and the tree view.
	 */
	private final String TREE_TEMPLATE = "com/kesdip/admin/web/tag/treeview/tree-template.txt";

	/**
	 * Template for the nodes.
	 */
	private final String NODE_TEMPLATE = "com/kesdip/admin/web/tag/treeview/node-template.txt";

	/**
	 * Serialization version.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The source bean property.
	 */
	private String beanProperty = null;

	/**
	 * The node types as a big string.
	 */
	private String nodeTypes = null;

	/**
	 * If disabled nodes are rendered.
	 */
	private String renderDisabled = "false";

	/**
	 * The control's id.
	 */
	private String id = null;

	/**
	 * The node style map.
	 */
	private Map<String, CssInfo> nodeStyles = null;

	/**
	 * Messages to use when replacing messages in the tree.
	 */
	private ResourceBundle resourceBundle = null;

	/**
	 * The current request object.
	 */
	private Object currentObject = null;

	/**
	 * Default constructor. Creates the wrapped resource bundle.
	 */
	public TreeViewTag() {
		super();
		resourceBundle = ResourceBundle.getBundle("messages");
	}

	/**
	 * Renders the tag.
	 * 
	 * @see org.springframework.web.servlet.tags.RequestContextAwareTag#doStartTagInternal()
	 */
	@Override
	@SuppressWarnings("unchecked")
	protected int doStartTagInternal() throws Exception {
		// get tree view generation bean
		WebApplicationContext ctx = getRequestContext()
				.getWebApplicationContext();
		Object sourceBean = ctx.getBean(IObjectKeysEnum.TREE_VIEW_GEN_KEY);
		if (!ctx.containsBean(IObjectKeysEnum.TREE_VIEW_GEN_KEY)
				|| !(sourceBean instanceof TreeViewGenerator)) {
			throw new JspTagException(IObjectKeysEnum.TREE_VIEW_GEN_KEY
					+ " does not exist in the Spring context");
		}
		currentObject = pageContext.getRequest().getAttribute(
				IObjectKeysEnum.CURRENT_REQUEST_OBJECT);
		// parse node style info
		nodeStyles = getNodeStyles();
		// check duplicate script inclusion
		if (pageContext.getRequest().getAttribute(
				IObjectKeysEnum.REQUEST_TAG_KEY) == null) {
			pageContext.getRequest().setAttribute(
					IObjectKeysEnum.REQUEST_TAG_KEY, "foo");
			String template = StreamUtils.readResource(getClass()
					.getClassLoader().getResource(SCRIPT_TEMPLATE));
			pageContext.getOut().print(
					MessageFormat.format(template,
							new Object[] { ((HttpServletRequest) pageContext
									.getRequest()).getContextPath() }));
		}
		// invoke the method identified in the tag
		List<TreeNode> nodes = (List<TreeNode>) BeanUtils.invokeGetter(
				sourceBean, getBeanProperty());
		// create the tree, using the css classes
		processNodes(nodes);
		return EVAL_PAGE;
	}

	/**
	 * Parses the node style string.
	 * 
	 * @return Map the map of style info
	 * @throws JspTagException
	 *             if the string is malformed
	 */
	private Map<String, CssInfo> getNodeStyles() throws JspTagException {
		Map<String, CssInfo> styles = new HashMap<String, CssInfo>();
		String[] nodeTypeParts = nodeTypes.split("\\|");
		for (String typePart : nodeTypeParts) {
			String[] nodeStyleParts = typePart.split("\\,");
			// no ":" char is not valid
			if (nodeStyleParts.length < 2) {
				throw new JspTagException("Invalid 'nodesTypes' value: ..."
						+ typePart + "...");
			}
			styles.put(nodeStyleParts[0].trim(), new CssInfo(nodeStyleParts));
		}
		return styles;
	}

	/**
	 * Process the nodes and write directly to the output.
	 */
	private final void processNodes(List<TreeNode> nodes) throws Exception {
		String treeTemplate = StreamUtils.readResource(getClass()
				.getClassLoader().getResource(TREE_TEMPLATE));
		String nodeTemplate = StreamUtils.readResource(getClass()
				.getClassLoader().getResource(NODE_TEMPLATE));

		StringBuilder allNodeString = new StringBuilder();
		int index = 0;
		for (TreeNode node : nodes) {
			processNode(node, index++, this.id + "_root", nodeTemplate,
					allNodeString);
		}

		String treeString = MessageFormat.format(treeTemplate, new Object[] {
				this.id, allNodeString.toString() });
		pageContext.getOut().write(treeString);
	}

	/**
	 * Recursive method to process a node and its children and generate their
	 * tree view string.
	 * 
	 * @param node
	 *            the node to process
	 * @param index
	 *            the index of this child in its parent's collection
	 * @param parentId
	 *            the constructed Javascript var name of the parent
	 * @param nodeTemplate
	 *            the template
	 * @param allNodeString
	 *            the builder to append to
	 * @throws JspTagException
	 *             if the node type does not have a style defined
	 */
	private void processNode(TreeNode node, int index, String parentVarName,
			String nodeTemplate, StringBuilder allNodeString)
			throws JspTagException {
		// check the node's style and ignore if unknown type
		CssInfo cssInfo = nodeStyles.get(node.getType());
		if (cssInfo == null) {
			return;
		}
		// skip disabled nodes if necessary
		if (node.isDisabled() && !"true".equalsIgnoreCase(this.renderDisabled)) {
			return;
		}
		String nodeVarName = parentVarName + "_" + node.getPrimaryKey() + "_"
				+ index;
		String nodeLabel = getNodeLabel(node);
		String parentNodeName = node.getParentNode() == null ? this.id
				+ "_root" : parentVarName;
		boolean expanded = isNodeExpanded(node);
		String nodeString = MessageFormat.format(nodeTemplate, new Object[] {
				parentNodeName, nodeVarName, nodeLabel, expanded,
				cssInfo.getStyle(node.getStatus()), getHref(node) });
		if (expanded) {
			allNodeString.append("expandNode(" + parentNodeName + ");\n");
		}
		allNodeString.append(nodeString);
		int childIndex = 0;
		for (TreeNode childNode : node.getChildren()) {
			processNode(childNode, childIndex++, nodeVarName, nodeTemplate,
					allNodeString);
		}
	}

	/**
	 * Get the HREF from this node.
	 * 
	 * @param node
	 *            the node
	 * @return String the HREF
	 */
	private String getHref(TreeNode node) {
		String pathName = Character.toLowerCase(node.getType().charAt(0))
				+ node.getType().substring(1);
		return ((HttpServletRequest) pageContext.getRequest()).getContextPath()
				+ "/secure/" + pathName + "/view.do?id=" + node.getPrimaryKey();

	}

	/**
	 * @return the beanProperty
	 */
	public String getBeanProperty() {
		return beanProperty;
	}

	/**
	 * @param beanProperty
	 *            the beanProperty to set
	 */
	public void setBeanProperty(String beanProperty) {
		this.beanProperty = beanProperty;
	}

	/**
	 * @return the nodeTypes
	 */
	public String getNodeTypes() {
		return nodeTypes;
	}

	/**
	 * @param nodeTypes
	 *            the nodeTypes to set
	 */
	public void setNodeTypes(String nodeTypes) {
		this.nodeTypes = nodeTypes;
	}

	/**
	 * @return the renderDisabled
	 */
	public String getRenderDisabled() {
		return renderDisabled;
	}

	/**
	 * @param renderDisabled
	 *            the renderDisabled to set
	 */
	public void setRenderDisabled(String renderDisabled) {
		this.renderDisabled = renderDisabled;
	}

	/**
	 * Contains the CSS classes for a particular node.
	 * 
	 * @author gerogias
	 */
	private final class CssInfo {
		/**
		 * Map of statuses and styles for them.
		 */
		private Map<String, String> styles = null;

		/**
		 * Default constructor. Parses the list of styles, ignoring the first
		 * item.
		 * 
		 * @param styles
		 *            the styles string
		 */
		private CssInfo(String[] stylesArray) throws JspTagException {
			styles = new HashMap<String, String>();
			for (int i = 1; i < stylesArray.length; i++) {
				String[] styleParts = stylesArray[i].split("\\:");
				if (styleParts.length != 2) {
					throw new JspTagException("Invalid style info ..."
							+ stylesArray[i] + "...");
				}
				styles.put(styleParts[0].trim(), styleParts[1].trim());
			}
		}

		/**
		 * Returns the style string for this status. If it is not found, a
		 * default value is returned.
		 * 
		 * @param status
		 *            the status
		 * @return String the style
		 */
		private String getStyle(String status) {
			return styles.containsKey(status) ? styles.get(status) : styles
					.values().iterator().next();
		}

	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Returns the label for the node. If the node's name is contained in curly
	 * braces, it is replaced with a message from the resource bundle.
	 * 
	 * @param node
	 *            the node
	 * @return String the label for the node
	 */
	private final String getNodeLabel(TreeNode node) {
		String label = node.getName();
		if (label.startsWith("{") && label.endsWith("}")) {
			label = resourceBundle.getString(label.substring(1,
					label.length() - 1));
		}
		return label;
	}

	/**
	 * Checks the current node against the current object. Checks the node's
	 * type against the object's class and the primary key against the object's
	 * id.
	 * 
	 * @param node
	 *            the node to check
	 * @return boolean <code>true</code> if the node should be expanded
	 */
	private final boolean isNodeExpanded(TreeNode node) {
		if (currentObject == null) {
			return false;
		}
		if (!node.getType().equals(
				BeanUtils.getClassName(currentObject.getClass()))) {
			return false;
		}

		// try with long "id"
		try {
			Long id = (Long) BeanUtils.invokeGetter(currentObject, "id");
			if (id.equals(node.getPrimaryKey())) {
				return true;
			}
		} catch (Exception e) {
			// do nothing for now, one more case to check
		}

		// try with "username"
		try {
			String username = (String) BeanUtils.invokeGetter(currentObject,
					"username");
			if (username.equals(node.getPrimaryKey())) {
				return true;
			}
		} catch (Exception e) {
			// ok, now we can fail
			return false;
		}

		return false;
	}
}
