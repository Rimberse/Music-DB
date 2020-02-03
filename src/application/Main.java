package application;
	
import application.model.Datasource;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;


public class Main extends Application {
	
	@Override
	public void start(Stage primaryStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("main.fxml"));
			Parent root = loader.load();
			Controller controller = loader.getController();
			controller.listArtists();
			
			Scene scene = new Scene(root,800,600);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
			primaryStage.setTitle("Music Database");
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void init() throws Exception {
		if(!Datasource.getInstance().open()) {
		
			Platform.runLater(() -> {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Error Dialog");
				alert.setHeaderText("FATAL ERROR:");
				alert.setContentText("Couldn't connect to database");
				alert.showAndWait();

				if (alert.getResult() == ButtonType.OK) {
					Platform.exit();
				}
			});
		}
	}

	@Override
	public void stop() throws Exception {
		Datasource.getInstance().close();
	}
}