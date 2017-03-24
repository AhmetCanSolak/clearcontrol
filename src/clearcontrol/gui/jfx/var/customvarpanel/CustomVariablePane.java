package clearcontrol.gui.jfx.var.customvarpanel;

import clearcontrol.core.math.functions.UnivariateAffineFunction;
import clearcontrol.core.math.functions.gui.jfx.UnivariateAffineFunctionPane;
import clearcontrol.core.variable.Variable;
import clearcontrol.core.variable.bounded.BoundedVariable;
import clearcontrol.gui.jfx.custom.gridpane.CustomGridPane;
import clearcontrol.gui.jfx.var.onoffarray.OnOffArrayPane;
import clearcontrol.gui.jfx.var.slider.VariableSlider;
import clearcontrol.gui.jfx.var.togglebutton.CustomToggleButton;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;

public class CustomVariablePane extends TabPane
{

  private static final double cDefaultWidth = 300;

  private int mCursor = 0;
  private double mSliderWidth;

  private CustomGridPane mCurrentTabGridPane;

  @SuppressWarnings("unchecked")
  public CustomVariablePane()
  {
    this(cDefaultWidth);

    getTabs().addListener((ListChangeListener) change -> {
      final StackPane header =
                             (StackPane) this.lookup(".tab-header-area");

      if (header != null)
      {
        if (this.getTabs().size() == 1)
          header.setPrefHeight(0);
        else
          header.setPrefHeight(-1);
      }
    });

    setStyle("-fx-tab-max-height: 0 ;");
    // final StackPane header = (StackPane) lookup(".tab-header-area");
    // header.setStyle("visibility: hidden ;");

  }

  public CustomVariablePane(double pSliderWidth)
  {
    super();
    mSliderWidth = pSliderWidth;
  }

  public void addTab(String pName)
  {
    Tab lTab = new Tab(pName);
    lTab.setClosable(false);
    getTabs().add(lTab);

    CustomGridPane lGridPane = new CustomGridPane();
    lGridPane.setAlignment(Pos.CENTER);
    lGridPane.setHgap(10);
    lGridPane.setVgap(10);
    lGridPane.setPadding(new Insets(25, 25, 25, 25));

    lTab.setContent(lGridPane);

    mCurrentTabGridPane = lGridPane;

    if (getTabs().size() > 1)
    {
      setStyle("-fx-tab-max-height: 20 ;");
      // final StackPane header = (StackPane) lookup(".tab-header-area");
      // header.setStyle("visibility: showing ;");
    }

    mCursor = 0;
  }

  public CustomToggleButton addToggleButton(String pSelectedText,
                                            String pDeselectedText,
                                            Variable<Boolean> pBooleanVariable)
  {
    final CustomToggleButton lToggleButton =
                                           new CustomToggleButton(pSelectedText,
                                                                  pDeselectedText,
                                                                  pBooleanVariable);

    int lCursor = mCursor++;
    mCurrentTabGridPane.add(lToggleButton, 0, lCursor);
    GridPane.setHgrow(lToggleButton, Priority.ALWAYS);
    GridPane.setColumnSpan(lToggleButton, 3);

    return lToggleButton;
  }

  public <T extends Number> VariableSlider<T> addSliderForVariable(Variable<T> pVariable,
                                                                   T pMin,
                                                                   T pMax,
                                                                   T pGranularity,
                                                                   T pTicks)
  {
    return addSliderForVariable(pVariable.getName(),
                                pVariable,
                                pMin,
                                pMax,
                                pGranularity,
                                pTicks);
  }

  public <T extends Number> VariableSlider<T> addSliderForVariable(String pSliderName,
                                                                   Variable<T> pVariable,
                                                                   T pMin,
                                                                   T pMax,
                                                                   T pGranularity,
                                                                   T pTicks)
  {
    return addSliderForVariable(pSliderName,
                                pVariable,
                                new Variable<T>("Min", pMin),
                                new Variable<T>("Max", pMax),
                                new Variable<T>("Granularity",
                                                pGranularity),
                                pTicks);
  }

  public <T extends Number> VariableSlider<T> addSliderForVariable(String pSliderName,
                                                                   Variable<T> pVariable,
                                                                   Variable<T> pMin,
                                                                   Variable<T> pMax,
                                                                   Variable<T> pGranularity,
                                                                   T pTicks)
  {
    final VariableSlider<T> lSlider =
                                    new VariableSlider<T>(pSliderName,
                                                          pVariable,
                                                          pMin,
                                                          pMax,
                                                          pGranularity,
                                                          pTicks);
    lSlider.getSlider().setPrefWidth(mSliderWidth);
    lSlider.getSlider().setMinWidth(mSliderWidth / 4);
    lSlider.getSlider().setMaxWidth(Double.MAX_VALUE);

    GridPane.setHgrow(lSlider.getSlider(), Priority.ALWAYS);

    int lCursor = mCursor++;
    mCurrentTabGridPane.add(lSlider.getLabel(), 0, lCursor);
    mCurrentTabGridPane.add(lSlider.getSlider(), 1, lCursor);
    mCurrentTabGridPane.add(lSlider.getTextField(), 2, lCursor);

    return lSlider;
  }

  public <T extends Number> VariableSlider<T> addSliderForVariable(BoundedVariable<T> pVariable,
                                                                   T pTicks)
  {
    return addSliderForVariable(pVariable.getName(),
                                pVariable,
                                pTicks);
  }

  public <T extends Number> VariableSlider<T> addSliderForVariable(String pSliderName,
                                                                   BoundedVariable<T> pVariable,
                                                                   T pTicks)
  {
    final VariableSlider<T> lSlider =
                                    new VariableSlider<T>(pSliderName,
                                                          pVariable,
                                                          pTicks);
    lSlider.setPrefWidth(mSliderWidth);
    lSlider.setMinWidth(mSliderWidth / 4);
    lSlider.setMaxWidth(Double.MAX_VALUE);

    GridPane.setHgrow(lSlider.getSlider(), Priority.ALWAYS);

    int lCursor = mCursor++;
    mCurrentTabGridPane.add(lSlider.getLabel(), 0, lCursor);
    mCurrentTabGridPane.add(lSlider.getSlider(), 1, lCursor);
    mCurrentTabGridPane.add(lSlider.getTextField(), 2, lCursor);

    return lSlider;
  }

  public <T extends Number> OnOffArrayPane addOnOffArray(String pOnOffArrayPaneName)
  {

    final OnOffArrayPane lOnOffArrayPane = new OnOffArrayPane();
    lOnOffArrayPane.setVertical(false);

    lOnOffArrayPane.setPrefWidth(mSliderWidth);
    lOnOffArrayPane.setMinWidth(mSliderWidth / 4);
    lOnOffArrayPane.setMaxWidth(Double.MAX_VALUE);

    GridPane.setHgrow(lOnOffArrayPane, Priority.ALWAYS);

    Label lLabel = new Label(pOnOffArrayPaneName);

    int lCursor = mCursor++;
    mCurrentTabGridPane.add(lLabel, 0, lCursor);
    mCurrentTabGridPane.add(lOnOffArrayPane, 1, lCursor);
    GridPane.setColumnSpan(lOnOffArrayPane, 2);

    return lOnOffArrayPane;
  }

  public void addFunctionPane(String pName,
                              Variable<UnivariateAffineFunction> pFunction)
  {
    Label lLabel = new Label(pName + " ");
    UnivariateAffineFunctionPane lFunctionPane =
                                               new UnivariateAffineFunctionPane(null,
                                                                                pFunction);
    int lCursor = mCursor++;
    mCurrentTabGridPane.add(lLabel, 0, lCursor);
    mCurrentTabGridPane.add(lFunctionPane, 1, lCursor);
    GridPane.setColumnSpan(lFunctionPane, 2);
  }

}
