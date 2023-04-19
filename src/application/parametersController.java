package application;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.function.UnaryOperator;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class parametersController {
	@FXML
	private Text msg;

	@FXML
	private Button next;

	@FXML
	private TextField txt1;

	@FXML
	private TextField txt2;
	@FXML
	private Button ch;
	@FXML
	private TextField txt3;
	private Stage primaryStage;
	private Scene scene;
	public static int q1;
	public static int q2;
	public static double alpha;
	private boolean status=false;
	public void check() throws IOException{
		if (txt1.getText().length() == 0 || txt2.getText().length() == 0 || txt3.getText().length() == 0) {
			msg.setText("Missing Data!");
		} else {
			try {
				q1 = Integer.parseInt(txt1.getText());
				q2 = Integer.parseInt(txt2.getText());
				alpha = Double.parseDouble(txt3.getText());
				status=true;
			} catch (Exception e) {
				msg.setText("Invalid input.\n Enter a valid number.");
			}
			
		}
	}

	public void next(ActionEvent event) {
		if (txt1.getText().length() == 0 || txt2.getText().length() == 0 || txt3.getText().length() == 0) {
			msg.setText("Missing Data!");
		} else {
			if(status==true) {
				try {
					Parent root = FXMLLoader.load(getClass().getResource("analysis.fxml"));
					primaryStage = (Stage) (((Node) event.getSource())).getScene().getWindow();
					scene = new Scene(root);
					scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
					primaryStage.setScene(scene);
					primaryStage.show();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}else {
				msg.setText("You must check first!");
			}
		}

	}
	
}
