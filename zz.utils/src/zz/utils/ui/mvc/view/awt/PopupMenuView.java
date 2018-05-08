package zz.utils.ui.mvc.view.awt;

import java.awt.PopupMenu;

import zz.utils.ui.mvc.model.ActionModel;
import zz.utils.ui.mvc.model.PropertyModel;

@SuppressWarnings("serial")
public class PopupMenuView extends PopupMenu {
	public void add(ActionModel item) {
		add(new MenuItemView(item));
	}
	
	public void add(PropertyModel<Boolean> item) {
		add(new CheckboxMenuItemView(item));
	}
}