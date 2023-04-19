package application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.function.UnaryOperator;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.stage.Stage;

public class surfaceController {
	@FXML
	private TextField MaxNoCPU;

	@FXML
	private TextField maxArrival;

	@FXML
	private TextField maxCPU;

	@FXML
	private TextField maxIO;

	@FXML
	private TextField minCPU;

	@FXML
	private TextField minIO;

	@FXML
	private Button next;

	@FXML
	private TextField pnum;

	@FXML
	private Button refill;
	@FXML
	private Label msg;

	public static boolean status=false;
	public void initialSet() {
		UnaryOperator<TextFormatter.Change> filter = change -> {
			String newText = change.getControlNewText();
			if (newText.matches("-?[0-9]*")) {
				return change;
			}
			return null;
		};
		MaxNoCPU.setTextFormatter(new TextFormatter<>(filter));
		maxArrival.setTextFormatter(new TextFormatter<>(filter));
		maxCPU.setTextFormatter(new TextFormatter<>(filter));
		maxIO.setTextFormatter(new TextFormatter<>(filter));
		minCPU.setTextFormatter(new TextFormatter<>(filter));
		minIO.setTextFormatter(new TextFormatter<>(filter));
		pnum.setTextFormatter(new TextFormatter<>(filter));
	}

	public void check() {
		initialSet();
		if (MaxNoCPU.getText().length() == 0 || maxArrival.getText().length() == 0 || maxCPU.getText().length() == 0
				|| maxIO.getText().length() == 0 || minCPU.getText().length() == 0 || minIO.getText().length() == 0
				|| pnum.getText().length() == 0) {
			msg.setText("Error: missing data");
		} else {
			status=true;
			int num = Integer.parseInt(pnum.getText());
			int arrival = Integer.parseInt(maxArrival.getText());
			int cpu = Integer.parseInt(maxCPU.getText());
			int minC = Integer.parseInt(minCPU.getText());
			int maxC = Integer.parseInt(maxCPU.getText());
			int minI = Integer.parseInt(minIO.getText());
			int maxI = Integer.parseInt(maxIO.getText());

			File file = new File("data.txt");
			try {
				FileWriter writer = new FileWriter(file);
				if (minC < maxC || minI < maxI) {
					for (int i = 0; i < num; i++) {// i is process id

						Random rand = new Random();
						int randomArrivalTime = rand.nextInt(arrival + 1);// from 0 to arrival
						int randomNoOfCPU = 2 * rand.nextInt(cpu / 2) + 1; // on interval [1,maxNoOfCPU] (odd number
																			// since
																			// it
																			// begin and end in cpuBurst)
						int[] burst = new int[randomNoOfCPU];// both cpu burst and i/o burst

						writer.write(i + "\t" + randomArrivalTime + "\t");
						for (int j = 0; j < randomNoOfCPU; j++) {
							if (j % 2 == 0) {// cpu burst space
								burst[j] = rand.nextInt((maxC - minC) + 1) + minC;
								writer.write(burst[j] + "\t");
							} else {// i/o burst space
								burst[j] = rand.nextInt((maxI - minI) + 1) + minI;
								writer.append(burst[j] + "\t");
							}
						}
						writer.append("\n");
					}
					msg.setText("Operation is true \nClick Next");
					writer.close();
				} else {
					msg.setText("Error:you must \npick valid min max");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
	private Stage primaryStage;
    private Scene scene;
	public void next(ActionEvent event) {
		System.out.println(status);
		if(status==true) {
			try {
				Parent root=FXMLLoader.load(getClass().getResource("parameters.fxml"));
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

}
