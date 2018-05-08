package zz.utils.ui.mvc.model;

import zz.utils.properties.ConstantProperty;
import zz.utils.properties.IProperty;

public class ActionModel {
	private static final IProperty<String> DEFAULT_LABEL = new ConstantProperty<String>(""); 
	private static final IProperty<Boolean> DEFAULT_ENABLED = new ConstantProperty<Boolean>(true);
	private static final Runnable DEFAULT_ACTION = new Runnable() {
		public void run() {};
	};
	
	public IProperty<String> pLabel = DEFAULT_LABEL;
	public Runnable action = DEFAULT_ACTION;
	public IProperty<Boolean> pEnabled = DEFAULT_ENABLED;
	
	public ActionModel() {
	}
	
	public ActionModel(String label, Runnable action) {
		this.pLabel = new ConstantProperty<String>(label);
		this.action = action;
	}

	public ActionModel(String label, Runnable action, IProperty<Boolean> pEnabled) {
		this.pLabel = new ConstantProperty<String>(label);
		this.action = action;
		this.pEnabled = pEnabled;
	}

	public ActionModel(IProperty<String> pLabel, Runnable action, IProperty<Boolean> pEnabled) {
		this.pLabel = pLabel;
		this.action = action;
		this.pEnabled = pEnabled;
	}
}