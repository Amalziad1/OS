package application;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class pausingController implements Initializable{
	private Stage primaryStage;
	private Scene scene;
	@FXML
    private Button back;

    @FXML
    private Label cp;

    @FXML
    private Label iq;

    @FXML
    private Text Q1;

    @FXML
    private Text Q2;

    @FXML
    private Text q3;

    @FXML
    private Text q4;

    @FXML
    private Text q5;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		ArrayList<String> args=Driver.stopMethod();
		Q1.setText(args.get(0));
		Q2.setText(args.get(1));
		q3.setText(args.get(2));
		q4.setText(args.get(3));
		q5.setText(args.get(4));
		cp.setText(args.get(5));
		iq.setText(args.get(6));
	}
	public void back(ActionEvent event) {
		try {
			Driver.stop=false;
			System.out.println("==================================");
			Parent root = FXMLLoader.load(getClass().getResource("analysis.fxml"));
			primaryStage = (Stage) (((Node) event.getSource())).getScene().getWindow();
			scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

}
