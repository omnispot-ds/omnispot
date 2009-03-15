package com.kesdip.designer.handler;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.internal.dialogs.ShowViewDialog;
import org.eclipse.ui.internal.registry.ViewRegistry;
import org.eclipse.ui.views.IViewCategory;
import org.eclipse.ui.views.IViewDescriptor;

@SuppressWarnings("restriction")
public class ShowViewHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		
		openOther(window);
		
		return null;
	}

	/**
	 * Opens a view selection dialog, allowing the user to chose a view.
	 */
	private final void openOther(final IWorkbenchWindow window) {
		final IWorkbenchPage page = window.getActivePage();
		if (page == null) {
			return;
		}
		
		final ShowViewDialog dialog = new ShowViewDialog(window,
				new FilteringViewRegistry());
		dialog.open();
		
		if (dialog.getReturnCode() == Window.CANCEL) {
			return;
		}
		
		final IViewDescriptor[] descriptors = dialog.getSelection();
		for (int i = 0; i < descriptors.length; ++i) {
			try {
                openView(descriptors[i].getId(), window);
			} catch (PartInitException e) {
			}
		}
	}

	/**
	 * Opens the view with the given identifier.
	 * 
	 * @param viewId
	 *            The view to open; must not be <code>null</code>
	 * @throws PartInitException
	 *             If the part could not be initialized.
	 */
	private final void openView(final String viewId,
			final IWorkbenchWindow activeWorkbenchWindow)
			throws PartInitException {

		final IWorkbenchPage activePage = activeWorkbenchWindow.getActivePage();
		if (activePage == null) {
			return;
		}

        activePage.showView(viewId);
	}

	private static class FilteringViewRegistry extends ViewRegistry {
		
		@Override
		public IViewCategory[] getCategories() {
			IViewCategory[] categories = super.getCategories();
			IViewCategory[] retVal = new IViewCategory[categories.length];
			for (int i = 0; i < categories.length; i++) {
				retVal[i] = new FilteringViewCategory(categories[i]);
			}
			return retVal;
		}

		@Override
		public IViewDescriptor[] getViews() {
			IViewDescriptor[] views = super.getViews();
			List<IViewDescriptor> retVal = new ArrayList<IViewDescriptor>();
			for (IViewDescriptor view : views) {
				if ("org.eclipse.ui.views.ContentOutline".equals(view.getId()) ||
						"org.eclipse.ui.views.TaskList".equals(view.getId()) ||
						"org.eclipse.ui.views.PropertySheet".equals(view.getId()) ||
						"org.eclipse.gef.ui.palette_view".equals(view.getId()) ||
						"org.eclipse.pde.runtime.LogView".equals(view.getId())) {
					retVal.add(view);
				}
			}
			return retVal.toArray(new IViewDescriptor[0]);
		}
		
	}
	
	private static class FilteringViewCategory implements IViewCategory {
		private IViewCategory delegate;
		
		public FilteringViewCategory(IViewCategory delegate) {
			this.delegate = delegate;
		}

		@Override
		public String getId() {
			return delegate.getId();
		}

		@Override
		public String getLabel() {
			return delegate.getLabel();
		}

		@Override
		public IPath getPath() {
			return delegate.getPath();
		}

		@Override
		public IViewDescriptor[] getViews() {
			IViewDescriptor[] views = delegate.getViews();
			List<IViewDescriptor> retVal = new ArrayList<IViewDescriptor>();
			for (IViewDescriptor view : views) {
				if ("org.eclipse.ui.views.ContentOutline".equals(view.getId()) ||
						"org.eclipse.ui.views.TaskList".equals(view.getId()) ||
						"org.eclipse.ui.views.PropertySheet".equals(view.getId()) ||
						"org.eclipse.gef.ui.palette_view".equals(view.getId()) ||
						"org.eclipse.pde.runtime.LogView".equals(view.getId())) {
					retVal.add(view);
				}
			}
			return retVal.toArray(new IViewDescriptor[0]);
		}
		
	}
}
