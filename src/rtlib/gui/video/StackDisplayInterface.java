package rtlib.gui.video;

import rtlib.core.variable.types.objectv.ObjectVariable;
import rtlib.stack.StackInterface;

public interface StackDisplayInterface
{

	ObjectVariable<StackInterface> getOutputStackVariable();

	void setOutputStackVariable(ObjectVariable<StackInterface> pOutputStackVariable);

}