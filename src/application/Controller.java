package application;

import java.io.IOException;
import java.util.Optional;

import application.model.Album;
import application.model.Artist;
import application.model.Datasource;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;

public class Controller {
	@FXML
	private BorderPane mainBorderPane;
	@FXML
	private TableView artistTable;
	@FXML
	private Button showAlbums;
	@FXML
	private Button listArtists;
	@FXML
	private ProgressBar progressBar;
	
//	@FXML
//	public void initialize() {
//		name = nameField.getText().trim();
//	}
	
	@FXML
	public void listArtists() {
		Task<ObservableList<Artist>> task = new GetAllArtistsTask();
		artistTable.itemsProperty().bind(task.valueProperty());
		progressBar.progressProperty().bind(task.progressProperty());
		progressBar.setVisible(true);
		
		task.setOnSucceeded(e -> progressBar.setVisible(false));
		task.setOnFailed(e -> progressBar.setVisible(false));
		
		new Thread(task).start();
		
		listArtists.setDisable(true);
		if (showAlbums.isDisable()) {
			showAlbums.setDisable(false);
		}
	}
	
	@FXML
	public void listAlbumsForArtist() {
		final Artist artist = (Artist) artistTable.getSelectionModel().getSelectedItem();
		if (artist == null) {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Warning");
			alert.setHeaderText("No artist selected!");
			alert.showAndWait();
			
			if (alert.getResult() == ButtonType.OK) {
				return;
			}
		}
		
		Task<ObservableList<Album>> task = new Task<>() {
			
			@Override
			protected ObservableList<Album> call() throws Exception {
				return FXCollections.observableArrayList(Datasource.getInstance().queryAlbumsForArtistId(artist.getId()));
			}
		};
		
		artistTable.itemsProperty().bind(task.valueProperty());
		
		new Thread(task).start();
		
		showAlbums.setDisable(true);
		if (listArtists.isDisabled()) {
			listArtists.setDisable(false);
		}
	}
	
	
	public void updateArtist(String newName) {
		final Artist artist = (Artist) artistTable.getSelectionModel().getSelectedItem();		// artistTable.getItems().get(2);
		Task<Boolean> task = new Task<Boolean>() {

			@Override
			protected Boolean call() throws Exception {
				return Datasource.getInstance().updateArtistName(artist.getId(), newName);
			}
		};
		
		task.setOnSucceeded(e -> {
			if (task.valueProperty().get()) {
				artist.setName(newName);
				artistTable.refresh();
			}
		});
		
		new Thread(task).start();
	}
	
	@FXML
	public void showUpdateArtistDialog() {
		Dialog<ButtonType> dialog = new Dialog<>();
		dialog.initOwner(mainBorderPane.getScene().getWindow());
		dialog.setTitle("Update Artist Name");
		dialog.setHeaderText("Use this dialog to update artist name");
		FXMLLoader fxmlLoader = new FXMLLoader();
		fxmlLoader.setLocation(getClass().getClassLoader().getResource("UpdateArtistDialog.fxml"));
		
		try {
			dialog.getDialogPane().setContent(fxmlLoader.load());
		} catch(IOException exception) {
			System.out.println("Couldn't load the dialog");
			exception.printStackTrace();
			return;
		}
		dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
		dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
		
		Optional<ButtonType> result = dialog.showAndWait();
		if (result.isPresent() && result.get() == ButtonType.OK) {
			UpdateArtistDialogController controller = fxmlLoader.getController();
			String newName = controller.processResults();
			updateArtist(newName);
		}
	}
}

class GetAllArtistsTask extends Task<ObservableList<Artist>> {

	@Override
	public ObservableList<Artist> call() {
		return FXCollections.observableArrayList(Datasource.getInstance().queryArtists(Datasource.ORDER_BY_ASC));
	}
}