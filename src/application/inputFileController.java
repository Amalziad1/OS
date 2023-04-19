package application;

import java.io.File;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class inputFileController {
	@FXML
	private Button back;

	@FXML
	private TextField name;

	@FXML
	private Button next;
	@FXML
	private Text msg;
	private Stage primaryStage;
	private Scene scene;
	public static String fileName;
	public static boolean status=false;
	

	public void back(ActionEvent event) {
		try {
			Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));
			primaryStage = (Stage) (((Node) event.getSource())).getScene().getWindow();
			scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void next(ActionEvent event) {
		String path=name.getText();
		if(path==null) {
			msg.setText("Please enter a name");
		}else {
			status=true;
			path=path+".txt";
			File file=new File(path);
			if(file.exists()) {
				try {
					fileName=path;
					Parent root = FXMLLoader.load(getClass().getResource("parameters.fxml"));
					primaryStage = (Stage) (((Node) event.getSource())).getScene().getWindow();
					scene = new Scene(root);
					scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
					primaryStage.setScene(scene);
					primaryStage.show();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}else {
				msg.setText("File Not Found!");
			}
		}
		
	}

}
