package application;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class UpdateArtistDialogController {
	@FXML
	private TextField nameField;
	
	public String processResults() {
		String name = nameField.getText().trim();
		return name;
	}
}