package clearcontrol.gui.jfx.gridpane;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;

public class StandardGridPane extends GridPane
{

	public static final int cStandardGap = 10;
	public static final int cStandardPadding = 25;

	public StandardGridPane()
	{
		this(cStandardPadding, cStandardGap);
	}

	public StandardGridPane(int pAddPading, int pGaps)
	{
		super();
		setAlignment(Pos.CENTER);
		setHgap(pGaps);
		setVgap(pGaps);
		setPadding(new Insets(pAddPading,
													pAddPading,
													pAddPading,
													pAddPading));
	}

}