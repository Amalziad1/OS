package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class mainController {
	@FXML
    private Button ch;

    @FXML
    private Button g;
    private Stage primaryStage;
    private Scene scene;
    
    public void chooseFileButton(ActionEvent event) {
    	try {
			Parent root=FXMLLoader.load(getClass().getResource("inputFile.fxml"));
			primaryStage=(Stage) (((Node) event.getSource())).getScene().getWindow();
			scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
    }
    public void generatorFile(ActionEvent event) {
    	try {
    		Parent root=FXMLLoader.load(getClass().getResource("surface.fxml"));
			primaryStage=(Stage) (((Node) event.getSource())).getScene().getWindow();
			scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
    }
}
