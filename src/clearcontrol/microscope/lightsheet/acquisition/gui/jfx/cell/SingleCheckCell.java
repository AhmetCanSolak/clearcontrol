package clearcontrol.microscope.lightsheet.acquisition.gui.jfx.cell;

import clearcontrol.device.name.NameableInterface;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;

public class SingleCheckCell<T extends NameableInterface> extends
																													ListCell<T>
{

	private SingleCheckListView<T> mSingleCheckListView;
	private SingleCheckCellManager<T> mStateCellManager;

	private HBox mHbox;
	private TextField mNameTextField;
	private Pane mPane = new Pane();
	private CheckBox mCheckBox;
	private T mCorrespondingItem;

	public SingleCheckCell(	SingleCheckListView<T> pSingleCheckListView,
													SingleCheckCellManager<T> pStateCellManager)
	{
		super();
		mSingleCheckListView = pSingleCheckListView;
		mStateCellManager = pStateCellManager;

		SingleCheckCell<T> lThisStateCell = this;

		mHbox = new HBox();
		mHbox.setAlignment(Pos.CENTER_LEFT);
		mHbox.setPadding(new Insets(0, 0, 0, 0));

		mCheckBox = new CheckBox();
		mCheckBox.setPadding(new Insets(0, 0, 0, 0));
		HBox.setMargin(mCheckBox, new Insets(0, 0, 0, 0));

		mNameTextField = new TextField("--");
		mNameTextField.setPadding(new Insets(0, 0, 0, 0));
		HBox.setMargin(mCheckBox, new Insets(0, 0, 0, 0));
		mNameTextField.setStyle("-fx-focus-color: transparent;");
		mNameTextField.setStyle("-fx-background-color: #fff; -fx-border-color: #fff; -fx-border-width: 0; -fx-border-image-width: 0; -fx-background-image: null; -fx-region-background: null;-fx-border-insets: 0; -fx-background-size:0; -fx-border-image-insets:0;");

		mNameTextField.focusedProperty().addListener((obs, o, n) -> {
			mSingleCheckListView.getSelectionModel()
													.select(lThisStateCell.getItem());

			if (!n && mCorrespondingItem != null)
				mCorrespondingItem.setName(mNameTextField.getText());
		});

		mNameTextField.setOnKeyPressed((e) -> {
			if (e.getCode().equals(KeyCode.ENTER))
				mCorrespondingItem.setName(mNameTextField.getText());
		});

		mHbox.getChildren().addAll(mCheckBox, mNameTextField, mPane);
		HBox.setHgrow(mPane, Priority.ALWAYS);
		mCheckBox.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent event)
			{
				if (mCorrespondingItem != null)
				{
					mStateCellManager.checkOnly(lThisStateCell);
				}
			}
		});
	}

	public void setChecked(boolean pSelected)
	{
		mCheckBox.selectedProperty().set(pSelected);
	}

	@Override
	protected void updateItem(T pItem, boolean pEmpty)
	{

		super.updateItem(pItem, pEmpty);
		setText(null); // No text in label of super class
		if (pEmpty)
		{
			mCorrespondingItem = null;
			setGraphic(null);
		}
		else
		{
			mCorrespondingItem = pItem;
			mNameTextField.setText(mCorrespondingItem.getName());
			setGraphic(mHbox);
		}

		mStateCellManager.updateChecked();
	}

}
