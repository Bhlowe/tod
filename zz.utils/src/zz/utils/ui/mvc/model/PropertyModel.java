package zz.utils.ui.mvc.model;

import zz.utils.properties.ConstantProperty;
import zz.utils.properties.IProperty;
import zz.utils.properties.IRWProperty;

public class PropertyModel<T>
{
	public static final IProperty<String> DEFAULT_LABEL = new ConstantProperty<String>(""); 
	public static final IRWProperty DEFAULT_VALUE = new ConstantProperty(null);
	public static final IProperty<Boolean> DEFAULT_ENABLED = new ConstantProperty<Boolean>(true);
	public static final IProperty<Boolean> DEFAULT_VALID = new ConstantProperty<Boolean>(true);
	
	public IProperty<String> pLabel = DEFAULT_LABEL;
	public IRWProperty<T> pValue = DEFAULT_VALUE;
	public IProperty<Boolean> pEnabled = DEFAULT_ENABLED;
	public IProperty<Boolean> pValid = DEFAULT_VALID;
	
	public PropertyModel() {
	}
	
	public PropertyModel(IRWProperty<T> pValue) {
		this.pValue = pValue;
	}

	public PropertyModel(String label, IRWProperty<T> pValue) {
		this.pLabel = new ConstantProperty<String>(label);
		this.pValue = pValue;
	}
	
	public PropertyModel(String label, IRWProperty<T> pValue, IProperty<Boolean> pEnabled) {
		this.pLabel = new ConstantProperty<String>(label);
		this.pValue = pValue;
		this.pEnabled = pEnabled;
	}
	
	public PropertyModel(String label, IRWProperty<T> pValue, IProperty<Boolean> pEnabled, IProperty<Boolean> pValid) {
		this.pLabel = new ConstantProperty<String>(label);
		this.pValue = pValue;
		this.pEnabled = pEnabled;
		this.pValid = pValid;
	}
	
	public PropertyModel(IProperty<String> pLabel, IRWProperty<T> pValue, IProperty<Boolean> pEnabled) {
		this.pLabel = pLabel;
		this.pValue = pValue; 
		this.pEnabled = pEnabled;
	}
}
