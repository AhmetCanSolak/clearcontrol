package clearcontrol.microscope.sim.gui;

import clearcontrol.gui.jfx.var.customvarpanel.CustomVariablePane;
import clearcontrol.gui.jfx.var.togglebutton.CustomToggleButton;
import clearcontrol.microscope.sim.SimulationManager;

/**
 * Simulation manager panel
 *
 * @author royer
 */
public class SimulationManagerPanel extends CustomVariablePane
{

  /**
   * Instanciates a simulation manager panel.
   * 
   * @param pSimulationManager
   *          simulation manager
   */
  public SimulationManagerPanel(SimulationManager pSimulationManager)
  {
    super();

    addTab("Logging");

    CustomToggleButton lToggleButton =
                                     addToggleButton("Logging On",
                                                     "Logging Off",
                                                     pSimulationManager.getLoggingOnVariable());

    lToggleButton.setMinWidth(250);

  }

}
