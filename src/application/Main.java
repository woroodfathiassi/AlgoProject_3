package application;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.PriorityQueue;
import java.util.Scanner;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.scene.transform.Scale;

public class Main extends Application {

	static int numOfPointChoice = 0;
	File f;
	Stage stage = new Stage();
	Pane leftPane = new Pane();
	Pane LeftPane2 = new Pane();

	CityNode_vertex[] allCities;
	Button[] pinCityOnMap;

	ComboBox<String> targets = new ComboBox<>();
	ComboBox<String> sources = new ComboBox<>();

	double bestDistance = 0;

	ImageView iimage;

	boolean isClick = false;
	Text noPath;
	TableView<info> pathTV = new TableView<>();
	Label dist = new Label();

	@Override
	public void start(Stage primaryStage) throws FileNotFoundException {
		noPath = new Text("");
		HBox myScreen = new HBox();

		// Load the image (the map)
		Image backgroundImage = new Image("g1.png");

		iimage = new ImageView(backgroundImage);
		iimage.setFitWidth(750);
		// iimage.setFitHeight(740);
		leftPane.getChildren().addAll(iimage, LeftPane2);
		leftPane.setPrefSize(750, 750);

		// ________________________________________________________________________________

		Pane rightPane = new Pane();

		Text title = new Text("GAZA MAP");
		title.setLayoutX(150);
		title.setLayoutY(50);
		title.getStyleClass().add("tit");
		rightPane.getChildren().add(title);

		// the file choser:
		FileChooser fileChooser = new FileChooser();
		f = fileChooser.showOpenDialog(stage);
		readFile(f);

		// sources title and combo-box
		Text src = new Text("Source:");
		src.getStyleClass().add("tex");
		src.setLayoutX(60);
		src.setLayoutY(150);
		rightPane.getChildren().add(src);

		sources.getStyleClass().add("com");
		sources.setLayoutX(200);
		sources.setLayoutY(120);
		rightPane.getChildren().add(sources);
		sources.setPromptText("Source");

		// targets title and combo-box
		Text tar = new Text("Target:");
		tar.getStyleClass().add("tex");
		tar.setLayoutX(60);
		tar.setLayoutY(230);
		rightPane.getChildren().add(tar);

		targets.getStyleClass().add("com");
		targets.setLayoutX(200);
		targets.setLayoutY(200);
		rightPane.getChildren().add(targets);
		targets.setPromptText("Target");

		// fill the two combo-boxes
		for (int i = 0; i < allCities.length; i++) {
			if (!allCities[i].getName().substring(1).matches("\\d+")) {
				sources.getItems().add(allCities[i].getName());
				targets.getItems().add(allCities[i].getName());
			}
		}

		Button runButton = new Button("Run");
		runButton.getStyleClass().add("butt");
		runButton.setLayoutX(110);
		runButton.setLayoutY(290);
		rightPane.getChildren().add(runButton);
		runButton.setPrefSize(250, 20);

		rightPane.getChildren().add(pathTV);
		pathTable("");

		Text distTit = new Text("Distance:");
		distTit.getStyleClass().add("tex2");
		distTit.setLayoutX(80);
		distTit.setLayoutY(660);
		rightPane.getChildren().add(distTit);

		dist.getStyleClass().add("lab");
		dist.setLayoutX(210);
		dist.setLayoutY(635);
		dist.setPrefSize(170, 37);
		rightPane.getChildren().add(dist);

		noPath.setLayoutY(720);
		noPath.setLayoutX(50);
		rightPane.getChildren().add(noPath);

		runButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				if (sources.getValue() != null && targets.getValue() != null) {
					LeftPane2.getChildren().clear();
					bestDistance = 0;

					int ind = sources.getItems().indexOf(sources.getValue());
					try {
						readFile(f);
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					MyDijkstra(ind);
					String path = findShortestPath(allCities[targets.getItems().indexOf(targets.getValue())]);
					pathTable(path);

					// Create a DecimalFormat object with the desired format
					DecimalFormat decimalFormat = new DecimalFormat("#.###");

					// Format the original number using the DecimalFormat
					String roundedNumberString = decimalFormat.format(bestDistance);

					// Parse the formatted string back to a double
					double roundedNumber = Double.parseDouble(roundedNumberString);

					dist.setText(roundedNumber + " KM");
					if (roundedNumber == 0) {

						noPath.setText("NOO Path");
					} else {
						noPath.setText(" ");
					}
				}
			}
		});

		myScreen.getChildren().addAll(leftPane, rightPane);
		Scene scene = new Scene(myScreen, 1200, 743);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		stage.setScene(scene);
		stage.show();

	}

	public static void main(String[] args) {

		launch(args);
	}

	private void readFile(File file) throws FileNotFoundException {

		Scanner scan = new Scanner(file);

		String[] line1 = scan.nextLine().split(",");
		int numOfCity = Integer.parseInt(line1[0]);
		int numOfAdjacents = Integer.parseInt(line1[1]);

		allCities = new CityNode_vertex[numOfCity];
		pinCityOnMap = new Button[numOfCity];

		// read all cities and create object for each one, then put on the map
		for (int i = 0; i < numOfCity; i++) {
			String[] cityLine = scan.nextLine().split(",");

			String name = cityLine[0];
			double ylat = Double.parseDouble(cityLine[1]); // read latitude
			double xlog = Double.parseDouble(cityLine[2]); // read longitude

			// create Node Object for each city in the file
			CityNode_vertex newNode = new CityNode_vertex(name, false, xlog, ylat, Integer.MAX_VALUE,
					new CityNode_vertex());
			allCities[i] = newNode;

			// put the city on the map as button(pin image)
			ImageView blackPin = new ImageView(new Image("pin.png"));
			blackPin.setFitWidth(20);
			blackPin.setFitHeight(20);

			pinCityOnMap[i] = new Button(name);

			// display the city name when I hovered it
			Tooltip tooltip1 = new Tooltip(name);
			Tooltip.install(pinCityOnMap[i], tooltip1);

			// if the node is not city => don't display it on the map
			if (!allCities[i].getName().substring(1).matches("\\d+")) {
				pinCityOnMap[i].setStyle("-fx-background-color: transparent;/*without background*/");
				pinCityOnMap[i].setGraphic(blackPin);
				pinCityOnMap[i].setLayoutX(newNode.getX() - 20);
				pinCityOnMap[i].setLayoutY(newNode.getY() + 30);
			}

			//
			int tempI = i;
			pinCityOnMap[i].setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent arg0) {
					try {
						// change pis's color
						buttonAction(tempI);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
				}

			});

			if (name != "s50")
				leftPane.getChildren().add(pinCityOnMap[i]);

		}

		for (int i = 0; i < numOfAdjacents; i++) {
			// read line by line
			String[] cityAdjacentLine = scan.nextLine().split(",");
			String nameCity = cityAdjacentLine[0];
			String nameAd = cityAdjacentLine[1];

			CityNode_vertex tempCity = null;
			CityNode_vertex tempAd = null;

			// .................
			for (int j = 0; j < numOfCity; j++) {
				if (allCities[j].getName().equalsIgnoreCase(nameCity)) {
					tempCity = allCities[j];

				}
				if (allCities[j].getName().equalsIgnoreCase(nameAd)) {
					tempAd = allCities[j];
				}
			}
			// .............

			// add the adjacent => directed =>same the file
			tempCity.getListOFAdj().add(new Adjacent(tempAd, calculateDistance(tempCity.getLatitude(),
					tempCity.getLongitude(), tempAd.getLatitude(), tempAd.getLongitude())));

//			// add the adjacent => directed =>opposite the file
			tempAd.getListOFAdj().add(new Adjacent(tempCity, calculateDistance(tempAd.getLatitude(),
					tempAd.getLongitude(), tempCity.getLatitude(), tempCity.getLongitude())));
		}
	}

	private void buttonAction(int i) throws FileNotFoundException {
		LeftPane2.getChildren().clear();
		System.out.println("i =  " + i);
		// to change the color of pin on the map
		if (isClick == false) {
			readFile(f);
			MyDijkstra(i);

			ImageView ab = new ImageView(new Image("pin2.png"));
			ab.setFitWidth(20);
			ab.setFitHeight(20);

			pinCityOnMap[i].setGraphic(ab);

			// set the city name on the combo-box
			sources.setValue(allCities[i].getName());

			isClick = true;
		} else {

			String str = findShortestPath(allCities[i]);
			pathTable(str);
			// Create a DecimalFormat object with the desired format
			DecimalFormat decimalFormat = new DecimalFormat("#.###");

			// Format the original number using the DecimalFormat
			String roundedNumberString = decimalFormat.format(bestDistance);

			// Parse the formatted string back to a double
			double roundedNumber = Double.parseDouble(roundedNumberString);

			dist.setText(roundedNumber + " KM");
			if (roundedNumber == 0) {

				noPath.setText("NOO Path");
			} else {
				noPath.setText(" ");
			}

			ImageView ab = new ImageView(new Image("pin1.png"));

			ab.setFitWidth(20);
			ab.setFitHeight(20);

			pinCityOnMap[i].setGraphic(ab);

			targets.setValue(allCities[i].getName());

			isClick = false;
		}

	}

	private void MyDijkstra(int index) {
		System.out.println(allCities[index].getName() + " the city in dijkstra");
		allCities[index].setDistancee(0);

		PriorityQueue<CityNode_vertex> priQueue_H = new PriorityQueue<>();
		priQueue_H.add(allCities[index]);

		// allCities[index] is the city which i will start from it
		while (!priQueue_H.isEmpty()) {
			CityNode_vertex cityNode = priQueue_H.poll();
			cityNode.setKnown(true);

			for (int i = 0; i < cityNode.getListOFAdj().size(); i++) {
				// check if the city knows or not
				if (cityNode.getListOFAdj().get(i).getNode().isKnown() == false) {
					// find the shortest cost in the city-Adjacents
					if (cityNode.getListOFAdj().get(i).getWeight() + cityNode.getDistancee() < cityNode.getListOFAdj()
							.get(i).getNode().getDistancee()) {

						cityNode.getListOFAdj().get(i).getNode()
								.setDistancee(cityNode.getListOFAdj().get(i).getWeight() + cityNode.getDistancee());

						cityNode.getListOFAdj().get(i).getNode().setPath(cityNode);

						priQueue_H.add(cityNode.getListOFAdj().get(i).getNode());
					}

				}

			}
		}
	}

	private String findShortestPath(CityNode_vertex node) {
		System.out.println("worood => " + node.getName());
		String ppAATH = " ";
		bestDistance = 0;

		while (node != null) {

			if (node.getName() != null) {

				if (!node.getName().substring(1).matches("\\d+")) {
					ppAATH = "," + node.getName() + ppAATH;
				}

				if (node.getPath().getPath() != null) {

					bestDistance += calculateDistance(node.getLatitude(), node.getLongitude(),
							node.getPath().getLatitude(), node.getPath().getLongitude());

					Line line = new Line(node.getX() - 2, node.getY() + 50, node.getPath().getX() - 2,
							node.getPath().getY() + 50);
					line.setStrokeWidth(1);
					LeftPane2.getChildren().add(line);
					line.setStyle("-fx-stroke: orange;");
				}
			}
			node = node.getPath();
		}
		return ppAATH;
	}

	// calculate distance
	private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
		final double R = 6371.0; // Radius of the Earth in kilometers

		// latitude and longitude from degrees to radians
		double dLat = Math.toRadians(lat2 - lat1);
		double dLon = Math.toRadians(lon2 - lon1);

		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(lat1))
				* Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2) * Math.sin(dLon / 2);

		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

		// Calculate the distance
		double distance = R * c; // Distance in kilometers
		return distance;
	}

	private void pathTable(String str) {
		pathTV.setLayoutX(100);
		pathTV.setLayoutY(420);
		pathTV.getColumns().clear();
		pathTV.getItems().clear();
		pathTV.refresh();
		pathTV.setPrefSize(270, 170);
		pathTV.getStyleClass().add("table-view");

		TableColumn<info, String> source = new TableColumn<>("Source");
		source.setPrefWidth(133); // Adjust the width as needed
		source.setCellValueFactory(new PropertyValueFactory<info, String>("s"));

		TableColumn<info, String> target = new TableColumn<info, String>("Target");
		target.setPrefWidth(133); // Adjust the width as needed
		target.setCellValueFactory(new PropertyValueFactory<>("t"));

		pathTV.getColumns().addAll(source, target);

		String[] temp = str.split(",");
		for (int i = 0; i < temp.length - 2; i++) {
			pathTV.getItems().add((new info(temp[i + 1], temp[i + 2])));
		}
	}
}
