package zz.utils.ui.propertyeditors;

import java.awt.BorderLayout;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Hashtable;

import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import zz.utils.properties.IRWProperty;
import zz.utils.ui.StackLayout;
import zz.utils.undo2.UndoStack;

public abstract class FloatPropertyEditor {
	@SuppressWarnings("serial")
	public static abstract class AbstractSlider extends SimplePropertyEditor<Float>
	implements ChangeListener, FocusListener
	{
		private boolean itsChanging = false;
		private boolean itsOperationStarted = false;
		
		private final JSlider itsSlider;
		private final JLabel itsValueLabel;
		
		public AbstractSlider(UndoStack aUndoStack, IRWProperty<Float> aProperty)
		{
			super(aUndoStack, aProperty);
			itsSlider = new JSlider();
			itsSlider.setOpaque(false);
			itsValueLabel = new JLabel();
			itsValueLabel.setOpaque(false);
			
			itsSlider.setMinimum(getSliderMin());
			itsSlider.setMaximum(getSliderMax());
			itsSlider.addChangeListener(this);
			itsSlider.addFocusListener(this);
			
			Hashtable<Integer, JLabel> theLabels = createLabels();
			if (theLabels != null) itsSlider.setLabelTable(theLabels);
			
			setLayout(new BorderLayout());
			add(itsSlider, BorderLayout.CENTER);
			add(itsValueLabel, BorderLayout.SOUTH);
		}
		
		protected abstract int getSliderMin();
		protected abstract int getSliderMax();
		
		protected Hashtable<Integer, JLabel> createLabels()
		{
			return null;
		}
		
		protected abstract Float sliderToValue(int aPos);
		protected abstract int valueToSlider(Float aValue);
		
		@Override
		protected void valueToUi(Float aValue)
		{
			itsChanging = true;
			itsSlider.setValue(valueToSlider(aValue));
			itsValueLabel.setText(""+aValue);
			itsChanging = false;
		}
		
		public void focusGained(FocusEvent aE)
		{
		}
		
		public void focusLost(FocusEvent aE)
		{
			uiToProperty();
		}
		
		public void stateChanged(ChangeEvent aE)
		{
			if (! itsChanging) 
			{
				if (! itsOperationStarted)
				{
					startOperation();
					itsOperationStarted = true;
				}
				getProperty().set(uiToValue());
				if (! itsSlider.getValueIsAdjusting())
				{
					commitOperation();
					itsOperationStarted = false;
				}
			}
		}
		
		@Override
		protected Float uiToValue()
		{
			int p = itsSlider.getValue();
			return sliderToValue(p);
		}
	}
	
	@SuppressWarnings("serial")
	public static class LogSlider extends AbstractSlider
	{
		protected static final int R = 1000; 
		private final int itsRange;
		private final double itsK;
		private final double itsLnK;
		
		public LogSlider(UndoStack aUndoStack, IRWProperty<Float> aProperty, int aRange)
		{
			super(aUndoStack, aProperty);
			itsRange = aRange;
			itsK = itsRange*itsRange;
			itsLnK = Math.log(itsK);
		}
		
		@Override
		protected Hashtable<Integer, JLabel> createLabels()
		{
			Hashtable<Integer, JLabel> theLabels = new Hashtable<Integer, JLabel>();
			for(int p=getSliderMin();p<getSliderMax();p+=R/5)
			{
				theLabels.put(p, new JLabel(""+sliderToValue(p)));
			}
			return theLabels;
		}
		
		@Override
		protected int getSliderMin()
		{
			return 0;
		}

		@Override
		protected int getSliderMax()
		{
			return R;
		}

		@Override
		protected Float sliderToValue(int p)
		{
			if (p == 0) return 0f;
			double v = Math.pow(itsK, (1.0*p/R)-0.5);
			return (float) v;
		}

		@Override
		protected int valueToSlider(Float aValue)
		{
			float v = aValue != null ? aValue : 1f;
			double p0 = (Math.log(v)/itsLnK) + 0.5;
			return (int) (p0*R);
		}
	}
	
	@SuppressWarnings("serial")
	public static class LogSlider10 extends LogSlider
	{
		public LogSlider10(UndoStack aUndoStack, IRWProperty<Float> aProperty)
		{
			super(aUndoStack, aProperty, 10);
		}
	}
	
	@SuppressWarnings("serial")
	public static class LogSlider100 extends LogSlider
	{
		public LogSlider100(UndoStack aUndoStack, IRWProperty<Float> aProperty)
		{
			super(aUndoStack, aProperty, 100);
		}
	}
	
	@SuppressWarnings("serial")
	public static class LogSlider1000 extends LogSlider
	{
		public LogSlider1000(UndoStack aUndoStack, IRWProperty<Float> aProperty)
		{
			super(aUndoStack, aProperty, 1000);
		}
	}
	
	@SuppressWarnings("serial")
	public static class NegPosLogSlider extends LogSlider
	{
		public NegPosLogSlider(UndoStack aUndoStack, IRWProperty<Float> aProperty, int aRange)
		{
			super(aUndoStack, aProperty, aRange);
		}
		
		@Override
		protected int getSliderMin()
		{
			return -R;
		}
		
		@Override
		protected int getSliderMax()
		{
			return R;
		}
		
		@Override
		protected Float sliderToValue(int p)
		{
			return p >= 0 ? super.sliderToValue(p) : -super.sliderToValue(-p);
		}
		
		@Override
		protected int valueToSlider(Float aValue)
		{
			if (aValue == null) return 0;
			float v = aValue;
			return v >= 0 ? super.valueToSlider(v) : -super.valueToSlider(-v);
		}
	}
	
	@SuppressWarnings("serial")
	public static class NegPosLogSlider10 extends NegPosLogSlider
	{
		public NegPosLogSlider10(UndoStack aUndoStack, IRWProperty<Float> aProperty)
		{
			super(aUndoStack, aProperty, 10);
		}
		
	}
	
	@SuppressWarnings("serial")
	public static class NegPosLogSlider100 extends NegPosLogSlider
	{
		public NegPosLogSlider100(UndoStack aUndoStack, IRWProperty<Float> aProperty)
		{
			super(aUndoStack, aProperty, 100);
		}
		
	}
	
	@SuppressWarnings("serial")
	public static class NegPosLogSlider1000 extends NegPosLogSlider
	{
		public NegPosLogSlider1000(UndoStack aUndoStack, IRWProperty<Float> aProperty)
		{
			super(aUndoStack, aProperty, 1000);
		}
		
	}
	
	@SuppressWarnings("serial")
	public static class Spinner extends SimplePropertyEditor<Float>
	implements ChangeListener, FocusListener
	{
		private final JSpinner itsSpinner = new JSpinner(new SpinnerNumberModel(Float.valueOf(0), null, null, Float.valueOf(1)));
		
		public Spinner(UndoStack aUndoStack, IRWProperty<Float> aProperty)
		{
			super(aUndoStack, aProperty);
			setLayout(new StackLayout());
			add(itsSpinner);
			itsSpinner.addChangeListener(this);
			itsSpinner.addFocusListener(this);
		}
		
		public void focusGained(FocusEvent aE)
		{
		}

		public void focusLost(FocusEvent aE)
		{
			uiToProperty();
		}

		public void stateChanged(ChangeEvent aE)
		{
			uiToProperty();
		}
		
		@Override
		protected void valueToUi(Float aValue)
		{
			itsSpinner.setValue(aValue != null ? aValue : 0);
		}
		
		@Override
		protected Float uiToValue()
		{
			return (Float) itsSpinner.getValue();
		}

	}
	
}
