package zz.utils.ui.mvc.model;

import zz.utils.properties.ConstantProperty;
import zz.utils.properties.IListProperty;
import zz.utils.properties.IProperty;
import zz.utils.properties.IRWProperty;

public class ChoicePropertyModel<T> extends PropertyModel<T>
{
	public static final IListProperty DEFAULT_CHOICES = null;
	
	public IListProperty<T> pChoices = DEFAULT_CHOICES;
	
	public ChoicePropertyModel() {
	}
	
	public ChoicePropertyModel(IRWProperty<T> pValue, IListProperty<T> pChoices) {
		this.pValue = pValue;
		this.pChoices = pChoices;
	}

	public ChoicePropertyModel(String label, IRWProperty<T> pValue, IListProperty<T> pChoices) {
		this.pLabel = new ConstantProperty<String>(label);
		this.pValue = pValue;
		this.pChoices = pChoices;
	}
	
	public ChoicePropertyModel(String label, IRWProperty<T> pValue, IListProperty<T> pChoices, IProperty<Boolean> pEnabled) {
		this.pLabel = new ConstantProperty<String>(label);
		this.pValue = pValue;
		this.pChoices = pChoices;
		this.pEnabled = pEnabled;
	}
	
	public ChoicePropertyModel(String label, IRWProperty<T> pValue, IListProperty<T> pChoices, IProperty<Boolean> pEnabled, IProperty<Boolean> pValid) {
		this.pLabel = new ConstantProperty<String>(label);
		this.pValue = pValue;
		this.pChoices = pChoices;
		this.pEnabled = pEnabled;
		this.pValid = pValid;
	}
	
	public ChoicePropertyModel(IProperty<String> pLabel, IRWProperty<T> pValue, IListProperty<T> pChoices, IProperty<Boolean> pEnabled) {
		this.pLabel = pLabel;
		this.pValue = pValue; 
		this.pChoices = pChoices;
		this.pEnabled = pEnabled;
	}
	
}
