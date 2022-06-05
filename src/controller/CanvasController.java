package controller;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.Renderer;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Separator;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.Model;
import model.User;

public class CanvasController {
    @FXML
    private MenuItem about;
    @FXML
    private Button addCanvas;
    @FXML
    private Button addCircle;
    @FXML
    private Button addImage;
    @FXML
    private Button addRectangle;
    @FXML
    private Button addText;
    @FXML
    private Pane canvas;
    @FXML
    private MenuItem clearCanvas;
    @FXML
    private MenuItem saveAs;
    @FXML
    private Label coordinates;
    @FXML
    private MenuItem deleteElement;
    @FXML
    private Menu edit;
    @FXML
    private Menu file;
    @FXML
    private Menu help;
    @FXML
    private Button logOut;
    @FXML
    private MenuBar menuBar;
    @FXML
    private MenuItem newCanvas;
    @FXML
    private Button profile;
    @FXML
    private Label profileName;
    @FXML
    private ImageView profilePicture;
    @FXML
    private Slider zoom;
    @FXML
    private Label zoomLabel;
    @FXML
    private VBox createCanvas;
    @FXML
    private TextField width;
    @FXML
    private TextField height;
    @FXML
    private Button save;
    @FXML
    private Button cancel;
    @FXML
    private VBox infoBar;
    @FXML
    private AnchorPane window;
    @FXML
    private Label elementProperty;

    private Stage stage;
    private Stage parentStage;
    private double startX;
    private double startY;
    private ArrayList<Label> texts = new ArrayList<Label>();
    private ArrayList<ImageView> images = new ArrayList<ImageView>();
    private ArrayList<Rectangle> rectangles = new ArrayList<Rectangle>();
    private ArrayList<Circle> circles = new ArrayList<Circle>();
    private ArrayList<Pane> canvasAll = new ArrayList<Pane>();
    private Model model;
    private Node selectedElement;
    private Rectangle frame;
    private Circle topLeft;
    private Circle topMid;
    private Circle topRight;
    private Circle midLeft;
    private Circle midRight;
    private Circle bottomLeft;
    private Circle bottomMid;
    private Circle bottomRight;
    private User user;

    public CanvasController(Stage parentStage, Model model) {
        this.stage = new Stage();
        this.parentStage = parentStage;
        this.model = model;
    }

    @FXML
    public void initialize() {

        user = model.getCurrentUser();
        try {
            profileName.setText(user.getFirstName() + " " + user.getLastName());
            profilePicture.setImage(model.getUserDao().retrieveImage(user.getUsername()));
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }

        canvas.setVisible(false);
        createCanvas.setVisible(false);

        addCanvas.setOnAction(e -> {
            if (canvas.isVisible()) {
                canvas.setVisible(false);
            }
            createCanvas.setVisible(true);
        });

        newCanvas.setOnAction(e -> {
            if (canvas.isVisible()) {
                canvas.setVisible(false);
            }
            createCanvas.setVisible(true);
        });

        cancel.setOnAction(e -> {
            createCanvas.setVisible(false);
            canvas.setVisible(true);
        });

        save.setOnAction(e -> {
            try {
                if (Double.parseDouble(width.getText()) > 0 && Double.parseDouble(height.getText()) > 0) {
                    canvas.getChildren().clear();
                    createCanvas.setVisible(false);
                    canvas.setPrefWidth(Double.parseDouble(width.getText()));
                    canvas.setPrefHeight(Double.parseDouble(height.getText()));
                    canvas.setTranslateX(390 / 2 - canvas.getPrefWidth() / 2);
                    canvas.setTranslateY(467 / 2 - canvas.getPrefHeight() / 2);
                    canvas.setVisible(true);
                } else {
                    width.setText("invalid inputs!");
                    height.setText("invalid inputs!");
                }
            } catch (Exception err) {
                width.setText("invalid inputs!");
                height.setText("invalid inputs!");
            }
        });

        addRectangle.setOnAction(e -> {
            if (canvas.isVisible()) {
                rectangles.add(new Rectangle(50, 30, Color.GREY));
                makeDraggable(rectangles.get(rectangles.size() - 1));
                makeDeleteable(rectangles.get(rectangles.size() - 1));
                rectangles.get(rectangles.size() - 1).setTranslateX(canvas.getWidth() / 2);
                rectangles.get(rectangles.size() - 1).setTranslateY(canvas.getHeight() / 2);
                canvas.getChildren().add(rectangles.get(rectangles.size() - 1));
            } else {
                coordinates.setText("No canvas has been created!");
                coordinates.setTextFill(Color.RED);
            }
        });

        addCircle.setOnAction(e -> {
            if (canvas.isVisible()) {
                circles.add(new Circle(20, Color.GREY));
                makeCircleDraggable(circles.get(circles.size() - 1));
                makeDeleteable(circles.get(circles.size() - 1));
                circles.get(circles.size() - 1).setTranslateX(canvas.getWidth() / 2);
                circles.get(circles.size() - 1).setTranslateY(canvas.getHeight() / 2);
                canvas.getChildren().add(circles.get(circles.size() - 1));
            } else {
                coordinates.setText("No canvas has been created!");
                coordinates.setTextFill(Color.RED);
            }
        });

        addText.setOnAction(e -> {
            if (canvas.isVisible()) {
                texts.add(new Label("Text"));
                makeDraggable(texts.get(texts.size() - 1));
                makeDeleteable(texts.get(texts.size() - 1));
                texts.get(texts.size() - 1).setStyle("-fx-font: 20 Arial;");
                texts.get(texts.size() - 1).setTextFill(Color.BLACK);
                texts.get(texts.size() - 1).setTranslateX(canvas.getWidth() / 2);
                texts.get(texts.size() - 1).setTranslateY(canvas.getHeight() / 2);
                canvas.getChildren().add(texts.get(texts.size() - 1));
            } else {
                coordinates.setText("No canvas has been created!");
                coordinates.setTextFill(Color.RED);
            }
        });

        addImage.setOnAction(e -> {
            if (canvas.isVisible()) {
                FileChooser fileChooser = new FileChooser();
                ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("*.png", "*.jpg", "*.jpeg");
                fileChooser.getExtensionFilters().add(extensionFilter);
                File selectedFile = fileChooser.showOpenDialog(stage);
                InputStream fileInputStream;

                try {
                    if (selectedFile != null) {
                        fileInputStream = new FileInputStream(selectedFile);
                        images.add(new ImageView());
                        images.get(images.size() - 1).setImage(new Image(fileInputStream));
                        makeDraggable(images.get(images.size() - 1));
                        makeDeleteable(images.get(images.size() - 1));
                        images.get(images.size() - 1).setFitWidth(100);
                        images.get(images.size() - 1).setFitHeight(100);
                        canvas.getChildren().add(images.get(images.size() - 1));
                    }
                } catch (IOException err) {
                    err.printStackTrace();
                }
            } else {
                coordinates.setText("No canvas has been created!");
                coordinates.setTextFill(Color.RED);
            }
        });

        logOut.setOnAction(e -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/LogOutConfirmationView.fxml"));

                Callback<Class<?>, Object> controllerFactory = param -> {
                    return new LogOutConfirmationController(stage, model);
                };

                loader.setControllerFactory(controllerFactory);
                AnchorPane root = loader.load();

                LogOutConfirmationController logOutConfirmationController = loader.getController();
                logOutConfirmationController.showStage(root);

                stage.close();
            } catch (IOException err) {
                err.printStackTrace();
            }
        });

        clearCanvas.setOnAction(e -> {
            canvas.getChildren().clear();
        });

        window.setOnMouseMoved(e -> {
            double x = Math.round(e.getSceneX());
            double y = Math.round(e.getSceneY());
            coordinates.setText("x: " + x + " y: " + y + " ");
            coordinates.setTextFill(Color.BLACK);
        });

        profile.setOnAction(e -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/ProfileView.fxml"));

                Callback<Class<?>, Object> controllerFactory = param -> {
                    return new ProfileController(stage, model);
                };

                loader.setControllerFactory(controllerFactory);
                HBox root = loader.load();

                ProfileController profileController = loader.getController();
                profileController.showStage(root);

                stage.close();
            } catch (IOException err) {
                err.printStackTrace();
            }
        });

        stage.setOnShowing(e -> {
            stage.centerOnScreen();
            user = model.getCurrentUser();
            try {
                profileName.setText(user.getFirstName() + " " + user.getLastName());
                profilePicture.setImage(model.getUserDao().retrieveImage(user.getUsername()));
            } catch (SQLException | IOException err) {
                err.printStackTrace();
            }
        });

        zoom.setOnMouseDragged(e -> {
            zoomLabel.setText("Zoom: " + Math.round(zoom.getValue()) + "%");
            canvas.setScaleX(zoom.getValue() / 100);
            canvas.setScaleY(zoom.getValue() / 100);
        });

        zoom.setOnMouseClicked(e -> {
            zoomLabel.setText("Zoom: " + Math.round(zoom.getValue()) + "%");
            canvas.setScaleX(zoom.getValue() / 100);
            canvas.setScaleY(zoom.getValue() / 100);
        });

        saveAs.setOnAction(e -> {
            if (canvas.isVisible()) {
                unselect();
                captureAndSaveDisplay();
            } else {
            }
        });
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void showStage(Pane root) {
        Scene scene = new Scene(root, 683, 562);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("Canvas");
        stage.show();
    }

    public void makeDraggable(Node node) { // Does not apply to circles
        node.setOnMousePressed(e -> {
            unselect();
            select(node);
            startX = e.getSceneX() - node.getTranslateX() * canvas.getScaleX();
            startY = e.getSceneY() - node.getTranslateY() * canvas.getScaleY();
        });

        node.setOnMouseDragged(e -> {
            node.setTranslateX((e.getSceneX() - startX) / canvas.getScaleX());
            node.setTranslateY((e.getSceneY() - startY) / canvas.getScaleY());

            if (node.getTranslateX() <= canvas.getScaleX()) {
                node.setTranslateX(canvas.getScaleX());
            }

            if (node.getTranslateY() <= canvas.getScaleY()) {
                node.setTranslateY(canvas.getScaleY());
            }

            if (node.getTranslateX() + node.getBoundsInParent().getWidth() >= canvas.getScaleX() + canvas.getWidth()) {
                node.setTranslateX(canvas.getScaleX() + canvas.getWidth() - node.getBoundsInParent().getWidth());
            }

            if (node.getTranslateY() + node.getBoundsInParent().getHeight() >= canvas.getScaleY()
                    + canvas.getHeight()) {
                node.setTranslateY(canvas.getScaleX() + canvas.getHeight() - node.getBoundsInParent().getHeight());
            }
        });
    }

    public void makeCircleDraggable(Node node) {
        node.setOnMousePressed(e -> {
            unselect();
            select(node);
            startX = e.getSceneX() - node.getTranslateX() * canvas.getScaleX();
            startY = e.getSceneY() - node.getTranslateY() * canvas.getScaleY();
        });

        node.setOnMouseDragged(e -> {
            node.setTranslateX((e.getSceneX() - startX) / canvas.getScaleX());
            node.setTranslateY((e.getSceneY() - startY) / canvas.getScaleY());

            if (node.getTranslateX() - node.getBoundsInParent().getWidth() / 2 <= canvas.getScaleX()) {
                node.setTranslateX(canvas.getScaleX() + node.getBoundsInParent().getWidth() / 2);
            }

            if (node.getTranslateY() - node.getBoundsInParent().getHeight() / 2 <= canvas.getScaleY()) {
                node.setTranslateY(canvas.getScaleY() + node.getBoundsInParent().getHeight() / 2);
            }

            if (node.getTranslateX() + node.getBoundsInParent().getWidth() / 2 >= canvas.getScaleX()
                    + canvas.getWidth()) {
                node.setTranslateX(canvas.getScaleX() + canvas.getWidth() - node.getBoundsInParent().getWidth() / 2);
            }

            if (node.getTranslateY() + node.getBoundsInParent().getHeight() / 2 >= canvas.getScaleY()
                    + canvas.getHeight()) {
                node.setTranslateY(canvas.getScaleX() + canvas.getHeight() - node.getBoundsInParent().getHeight() / 2);
            }
        });
    }

    public void select(Node node) {
        unselect();
        selectedElement = node;
        frame = new Rectangle(node.getBoundsInParent().getWidth(),
                node.getBoundsInParent().getHeight());
        frame.widthProperty().bind(new ReadOnlyObjectWrapper<>(node.getBoundsInParent().getWidth()));
        frame.heightProperty().bind(new ReadOnlyObjectWrapper<>(node.getBoundsInParent().getHeight()));
        frame.rotateProperty().bind(node.rotateProperty());
        frame.translateXProperty().bind(node.translateXProperty());
        frame.translateYProperty().bind(node.translateYProperty());
        if (node instanceof Circle) {
            frame.layoutXProperty().bind(node.layoutXProperty().subtract(node.getBoundsInParent().getWidth() / 2));
            frame.layoutYProperty().bind(node.layoutYProperty().subtract(node.getBoundsInParent().getHeight() / 2));
        } else {
            frame.layoutXProperty().bind(node.layoutXProperty());
            frame.layoutYProperty().bind(node.layoutYProperty());
        }
        frame.setStroke(Color.BLACK);
        frame.setFill(Color.TRANSPARENT);
        frame.getStrokeDashArray().addAll(2d);
        canvas.getChildren().add(frame);

        node.toFront();

        topLeft = new Circle(4, Color.LIGHTBLUE);
        topLeft.setStrokeWidth(1);
        topLeft.setStroke(Color.BLACK);
        topLeft.layoutXProperty().bind(frame.layoutXProperty());
        topLeft.layoutYProperty().bind(frame.layoutYProperty());
        topLeft.translateXProperty().bind(frame.translateXProperty());
        topLeft.translateYProperty().bind(frame.translateYProperty());
        topMid = new Circle(4, Color.LIGHTBLUE);
        topMid.setStrokeWidth(1);
        topMid.setStroke(Color.BLACK);
        topMid.layoutXProperty().bind(frame.layoutXProperty().add(frame.getWidth() / 2));
        topMid.layoutYProperty().bind(frame.layoutYProperty());
        topMid.translateXProperty().bind(frame.translateXProperty());
        topMid.translateYProperty().bind(frame.translateYProperty());
        topRight = new Circle(4, Color.LIGHTBLUE);
        topRight.setStrokeWidth(1);
        topRight.setStroke(Color.BLACK);
        topRight.layoutXProperty().bind(frame.layoutXProperty().add(frame.getWidth()));
        topRight.layoutYProperty().bind(frame.layoutYProperty());
        topRight.translateXProperty().bind(frame.translateXProperty());
        topRight.translateYProperty().bind(frame.translateYProperty());
        midLeft = new Circle(4, Color.LIGHTBLUE);
        midLeft.setStrokeWidth(1);
        midLeft.setStroke(Color.BLACK);
        midLeft.layoutXProperty().bind(frame.layoutXProperty());
        midLeft.layoutYProperty().bind(frame.layoutYProperty().add(frame.getHeight() / 2));
        midLeft.translateXProperty().bind(frame.translateXProperty());
        midLeft.translateYProperty().bind(frame.translateYProperty());
        midRight = new Circle(4, Color.LIGHTBLUE);
        midRight.setStrokeWidth(1);
        midRight.setStroke(Color.BLACK);
        midRight.layoutXProperty().bind(frame.layoutXProperty().add(frame.getWidth()));
        midRight.layoutYProperty().bind(frame.layoutYProperty().add(frame.getHeight() / 2));
        midRight.translateXProperty().bind(frame.translateXProperty());
        midRight.translateYProperty().bind(frame.translateYProperty());
        bottomLeft = new Circle(4, Color.LIGHTBLUE);
        bottomLeft.setStrokeWidth(1);
        bottomLeft.setStroke(Color.BLACK);
        bottomLeft.layoutXProperty().bind(frame.layoutXProperty());
        bottomLeft.layoutYProperty().bind(frame.layoutYProperty().add(frame.getHeight()));
        bottomLeft.translateXProperty().bind(frame.translateXProperty());
        bottomLeft.translateYProperty().bind(frame.translateYProperty());
        bottomMid = new Circle(4, Color.LIGHTBLUE);
        bottomMid.setStrokeWidth(1);
        bottomMid.setStroke(Color.BLACK);
        bottomMid.layoutXProperty().bind(frame.layoutXProperty().add(frame.getWidth() / 2));
        bottomMid.layoutYProperty().bind(frame.layoutYProperty().add(frame.getHeight()));
        bottomMid.translateXProperty().bind(frame.translateXProperty());
        bottomMid.translateYProperty().bind(frame.translateYProperty());
        bottomRight = new Circle(4, Color.LIGHTBLUE);
        bottomRight.setStrokeWidth(1);
        bottomRight.setStroke(Color.BLACK);
        bottomRight.layoutXProperty().bind(frame.layoutXProperty().add(frame.getWidth()));
        bottomRight.layoutYProperty().bind(frame.layoutYProperty().add(frame.getHeight()));
        bottomRight.translateXProperty().bind(frame.translateXProperty());
        bottomRight.translateYProperty().bind(frame.translateYProperty());
        canvas.getChildren().addAll(topLeft, topMid, topRight, midLeft, midRight, bottomLeft, bottomMid, bottomRight);

        Node[] nodes = { topLeft, topMid, topRight, midLeft, midRight, bottomLeft, bottomMid, bottomRight };
        makeResizable(nodes);

        window.setOnMouseClicked(e -> {
            if (e.getSceneX() < 75 || e.getSceneX() > 485 || e.getSceneY() < 48 || e.getSceneY() > 530) {
                unselect();
            }
        });

        elementProperty.setText("w: " + Math.round(selectedElement.getBoundsInParent().getWidth()) + " h: "
                + Math.round(selectedElement.getBoundsInParent().getHeight()) + " angle: "
                + Math.round(selectedElement.getRotate()) + " deg");

        if (selectedElement != null) {
            if (selectedElement instanceof Circle || selectedElement instanceof Rectangle) {
                infoBar.getChildren().clear();

                HBox shapeInfoBorderColourRow = new HBox();
                MenuItem borderColourWhite = new MenuItem("White");
                MenuItem borderColourYellow = new MenuItem("Yellow");
                MenuItem borderColourGreen = new MenuItem("Green");
                MenuButton shapeInfoBorderColour = new MenuButton(
                        ((Shape) selectedElement).getStroke() != null ? ((Shape) selectedElement).getStroke().toString()
                                : "None",
                        null, borderColourWhite,
                        borderColourYellow,
                        borderColourGreen);
                shapeInfoBorderColourRow.setSpacing(3);
                shapeInfoBorderColourRow.getChildren().addAll(new Label("Border Colour"), shapeInfoBorderColour);

                borderColourWhite.setOnAction(e -> {
                    ((Shape) selectedElement).setStroke(Color.WHITE);
                    shapeInfoBorderColour.setText("White");
                });
                borderColourYellow.setOnAction(e -> {
                    ((Shape) selectedElement).setStroke(Color.YELLOW);
                    shapeInfoBorderColour.setText("Yellow");
                });
                borderColourGreen.setOnAction(e -> {
                    ((Shape) selectedElement).setStroke(Color.GREEN);
                    shapeInfoBorderColour.setText("Green");
                });

                HBox shapeInfoBorderWidth = new HBox();
                TextField borderWidth = new TextField(String.valueOf(((Shape) selectedElement).getStrokeWidth()));
                borderWidth.setMaxWidth(80);
                shapeInfoBorderWidth.setSpacing(3);
                shapeInfoBorderWidth.getChildren().addAll(new Label("Border width"), borderWidth);

                borderWidth.setOnKeyTyped(e -> {
                    try {
                        ((Shape) selectedElement).setStrokeWidth(Double.parseDouble(borderWidth.getText()));
                    } catch (Exception err) {
                        borderWidth.setText("invalid");
                        err.printStackTrace();
                    }
                });

                HBox rotationRow = new HBox();
                Slider rotation = new Slider(-360, 360, selectedElement.getRotate());
                rotation.setMaxWidth(80);
                rotationRow.setSpacing(3);
                rotationRow.getChildren().addAll(new Label("Rotation"), rotation);

                rotation.setOnMouseDragged(e -> {
                    selectedElement.setRotate(rotation.getValue());
                });

                HBox shapeInfoBackgroundRow = new HBox();
                MenuItem backgroundColourWhite = new MenuItem("White");
                MenuItem backgroundColourPink = new MenuItem("Pink");
                MenuItem backgroundColourLightBlue = new MenuItem("Light Blue");
                MenuButton shapeInfobackgroundColour = new MenuButton(
                        ((Shape) selectedElement).getFill().toString(),
                        null, backgroundColourWhite,
                        backgroundColourPink, backgroundColourLightBlue);
                shapeInfoBackgroundRow.setSpacing(3);
                shapeInfoBackgroundRow.getChildren().addAll(new Label("Background"), shapeInfobackgroundColour);

                infoBar.setSpacing(5);
                infoBar.setPadding(new Insets(20, 3, 0, 5));

                backgroundColourWhite.setOnAction(e -> {
                    ((Shape) selectedElement).setFill(Color.WHITE);
                    shapeInfobackgroundColour.setText("White");
                });
                backgroundColourPink.setOnAction(e -> {
                    ((Shape) selectedElement).setFill(Color.PINK);
                    shapeInfobackgroundColour.setText("Pink");
                });
                backgroundColourLightBlue.setOnAction(e -> {
                    ((Shape) selectedElement).setFill(Color.LIGHTBLUE);
                    shapeInfobackgroundColour.setText("Light Blue");
                });

                infoBar.getChildren().addAll(shapeInfoBorderColourRow, shapeInfoBorderWidth, rotationRow,
                        new Separator(),
                        shapeInfoBackgroundRow);
            } else if (selectedElement instanceof Label) {
                infoBar.getChildren().clear();

                HBox textInfoTextRow = new HBox();
                TextField textInfoText = new TextField(((Labeled) selectedElement).getText());
                textInfoText.setMaxWidth(80);
                textInfoTextRow.setSpacing(3);
                textInfoTextRow.getChildren().addAll(new Label("Text"), textInfoText);

                textInfoText.setOnKeyTyped(e -> {
                    ((Labeled) selectedElement).setText(textInfoText.getText());
                });

                HBox textInfoFontRow = new HBox();
                MenuItem font0 = new MenuItem("Arial");
                MenuItem font1 = new MenuItem("charm");
                MenuButton textInfoFont = new MenuButton(((Labeled) selectedElement).getFont().getName(), null,
                        font0, font1);
                textInfoFontRow.setSpacing(3);
                textInfoFontRow.getChildren().addAll(new Label("Font"), textInfoFont);

                font0.setOnAction(e -> {
                    ((Labeled) selectedElement).setStyle("-fx-font-family: Arial;" + "-fx-font-size: "
                            + ((Labeled) selectedElement).getFont().getSize() + ";");
                });
                font1.setOnAction(e -> {
                    ((Labeled) selectedElement).setStyle("-fx-font-family: charm;" + "-fx-font-size: "
                            + ((Labeled) selectedElement).getFont().getSize() + ";");
                });

                HBox textInfoFontSizeRow = new HBox();
                TextField textInfoFontSize = new TextField(
                        String.valueOf(((Labeled) selectedElement).getFont().getSize()));
                textInfoFontSize.setMaxWidth(80);
                textInfoFontSizeRow.setSpacing(3);
                textInfoFontSizeRow.getChildren().addAll(new Label("Font size"), textInfoFontSize);

                textInfoFontSize.setOnKeyTyped(e -> {
                    try {
                        ((Labeled) selectedElement)
                                .setStyle("-fx-font-size: " + Double.parseDouble(textInfoFontSize.getText()) + ";"
                                        + "-fx-font-family: " + ((Labeled) selectedElement).getFont().getName() + ";");
                    } catch (Exception err) {
                        textInfoFontSize.setText("Invalid inputs!");
                        err.printStackTrace();
                    }
                });

                HBox textInfoAttributesRow = new HBox();
                HBox textInfoAttributesButtons = new HBox();
                Button textInfoAttributesBold = new Button("Bold");
                Button textInfoAttributesItalic = new Button("Italic");
                textInfoAttributesButtons.getChildren().addAll(textInfoAttributesBold, textInfoAttributesItalic);
                textInfoAttributesRow.setSpacing(3);
                textInfoAttributesRow.getChildren().addAll(new Label("Attributes"), textInfoAttributesButtons);

                textInfoAttributesBold.setOnAction(e -> {
                    ((Labeled) selectedElement).setFont(Font.font(((Labeled) selectedElement).getFont().getName(),
                            FontWeight.BOLD, ((Labeled) selectedElement).getFont().getSize()));

                    textInfoAttributesBold.setOnAction(e1 -> {
                        ((Labeled) selectedElement).setFont(Font.font(((Labeled) selectedElement).getFont().getName(),
                                FontWeight.NORMAL, ((Labeled) selectedElement).getFont().getSize()));
                    });
                });

                textInfoAttributesBold.setOnAction(e -> {
                    ((Labeled) selectedElement).setFont(Font.font(((Labeled) selectedElement).getFont().getName(),
                            FontPosture.ITALIC, ((Labeled) selectedElement).getFont().getSize()));

                    textInfoAttributesBold.setOnAction(e1 -> {
                        ((Labeled) selectedElement).setFont(Font.font(((Labeled) selectedElement).getFont().getName(),
                                FontPosture.REGULAR, ((Labeled) selectedElement).getFont().getSize()));
                    });
                });

                HBox textInfoTextColourRow = new HBox();
                MenuItem textColourBlack = new MenuItem("Black");
                MenuItem textColourRed = new MenuItem("Red");
                MenuItem textColourBlue = new MenuItem("Blue");
                MenuButton textInfoTextColour = new MenuButton(((Labeled) selectedElement).getTextFill().toString(),
                        null, textColourBlack, textColourRed,
                        textColourBlue);
                textInfoTextColourRow.setSpacing(3);
                textInfoTextColourRow.getChildren().addAll(new Label("Text Colour"), textInfoTextColour);

                textColourBlack.setOnAction(e -> {
                    ((Labeled) selectedElement).setTextFill(Color.BLACK);
                    textInfoTextColour.setText("Black");
                });
                textColourRed.setOnAction(e -> {
                    ((Labeled) selectedElement).setTextFill(Color.RED);
                    textInfoTextColour.setText("Red");
                });
                textColourBlue.setOnAction(e -> {
                    ((Labeled) selectedElement).setTextFill(Color.BLUE);
                    textInfoTextColour.setText("Blue");
                });

                HBox textInfoAlignmentRow = new HBox();
                HBox textInfoAlignmentButtons = new HBox();
                Button textInfoAlignmentLeft = new Button("Left");
                Button textInfoAlignmentMid = new Button("Mid");
                Button textInfoAlignmentRight = new Button("Right");
                textInfoAlignmentButtons.getChildren().addAll(textInfoAlignmentLeft, textInfoAlignmentMid,
                        textInfoAlignmentRight);
                textInfoAlignmentRow.setSpacing(3);
                textInfoAlignmentRow.getChildren().addAll(new Label("Alignment"), textInfoAlignmentButtons);

                textInfoAlignmentLeft.setOnAction(e -> {
                    ((Labeled) selectedElement).setTextAlignment(TextAlignment.LEFT);
                });
                textInfoAlignmentMid.setOnAction(e -> {
                    ((Labeled) selectedElement).setTextAlignment(TextAlignment.CENTER);
                });
                textInfoAlignmentRight.setOnAction(e -> {
                    ((Labeled) selectedElement).setTextAlignment(TextAlignment.RIGHT);
                });

                HBox textInfoBorderColourRow = new HBox();
                MenuItem borderColourWhite = new MenuItem("White");
                MenuItem borderColourYellow = new MenuItem("Yellow");
                MenuItem borderColourGreen = new MenuItem("Green");
                MenuButton textInfoBorderColour = new MenuButton(
                        ((Labeled) selectedElement).getBorder() != null
                                ? ((Labeled) selectedElement).getBorder().getStrokes().get(0).getTopStroke().toString()
                                : "None",
                        null,
                        borderColourWhite, borderColourYellow, borderColourGreen);
                textInfoBorderColourRow.setSpacing(3);
                textInfoBorderColourRow.getChildren().addAll(new Label("Border Colour"), textInfoBorderColour);

                borderColourWhite.setOnAction(e -> {
                    ((Labeled) selectedElement).setBorder(Border.stroke(Color.WHITE));
                    textInfoBorderColour.setText("White");
                });
                borderColourYellow.setOnAction(e -> {
                    ((Labeled) selectedElement).setBorder(Border.stroke(Color.YELLOW));
                    textInfoBorderColour.setText("Yellow");
                });
                borderColourGreen.setOnAction(e -> {
                    ((Labeled) selectedElement).setBorder(Border.stroke(Color.GREEN));
                    textInfoBorderColour.setText("Green");
                });

                HBox textInfoBorderWidth = new HBox();
                TextField borderWidth = new TextField(((Labeled) selectedElement).getBorder() != null
                        ? String.valueOf(
                                ((Labeled) selectedElement).getBorder().getStrokes().get(0).getWidths().getBottom())
                        : "0");
                borderWidth.setMaxWidth(80);
                textInfoBorderWidth.setSpacing(3);
                textInfoBorderWidth.getChildren().addAll(new Label("Border width"), borderWidth);

                borderWidth.setOnKeyTyped(e -> {
                    try {
                        ((Labeled) selectedElement).setBorder(new Border(new BorderStroke(
                                ((Labeled) selectedElement).getBorder().getStrokes().get(0).getBottomStroke(),
                                BorderStrokeStyle.SOLID, CornerRadii.EMPTY,
                                new BorderWidths(Double.parseDouble(borderWidth.getText())))));
                    } catch (Exception err) {
                        borderWidth.setText("invalid");
                        err.printStackTrace();
                    }
                });

                HBox rotationRow = new HBox();
                Slider rotation = new Slider(-360, 360, selectedElement.getRotate());
                rotation.setMaxWidth(80);
                rotationRow.setSpacing(3);
                rotationRow.getChildren().addAll(new Label("Rotation"), rotation);

                rotation.setOnMouseDragged(e -> {
                    selectedElement.setRotate(rotation.getValue());
                });

                HBox textInfoBackgroundRow = new HBox();
                MenuItem backgroundColourWhite = new MenuItem("White");
                MenuItem backgroundColourPink = new MenuItem("Pink");
                MenuItem backgroundColourLightBlue = new MenuItem("Light Blue");
                MenuButton textInfobackgroundColour = new MenuButton(
                        ((Labeled) selectedElement).getBackground() != null
                                ? ((Labeled) selectedElement).getBackground().getFills().get(0).toString()
                                : "White",
                        null,
                        backgroundColourWhite,
                        backgroundColourPink, backgroundColourLightBlue);
                textInfoBackgroundRow.setSpacing(3);
                textInfoBackgroundRow.getChildren().addAll(new Label("Background"), textInfobackgroundColour);

                infoBar.setSpacing(5);
                infoBar.setPadding(new Insets(20, 3, 0, 5));

                backgroundColourWhite.setOnAction(e -> {
                    ((Labeled) selectedElement).setBackground(
                            new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
                    textInfobackgroundColour.setText("White");
                });
                backgroundColourPink.setOnAction(e -> {
                    ((Labeled) selectedElement).setBackground(
                            new Background(new BackgroundFill(Color.PINK, CornerRadii.EMPTY, Insets.EMPTY)));
                    textInfobackgroundColour.setText("Pink");
                });
                backgroundColourLightBlue.setOnAction(e -> {
                    ((Labeled) selectedElement).setBackground(
                            new Background(new BackgroundFill(Color.LIGHTBLUE, CornerRadii.EMPTY, Insets.EMPTY)));
                    textInfobackgroundColour.setText("Light Blue");
                });

                infoBar.getChildren().addAll(textInfoTextRow, textInfoFontRow, textInfoFontSizeRow,
                        textInfoAttributesRow, textInfoTextColourRow, textInfoAlignmentRow, new Separator(),
                        textInfoBorderColourRow, textInfoBorderWidth, rotationRow, new Separator(),
                        textInfoBackgroundRow);
            } else if (selectedElement instanceof ImageView) {
                infoBar.getChildren().clear();
            }
        } else {
            infoBar.getChildren().clear();

            HBox DefaultInfoBackgroundRow = new HBox();
            MenuItem backgroundColourWhite = new MenuItem("White");
            MenuItem backgroundColourPink = new MenuItem("Pink");
            MenuItem backgroundColourLightBlue = new MenuItem("Light Blue");
            MenuButton DefaultInfobackgroundColour = new MenuButton(
                    canvas.getBackground().getFills().get(0).getFill().toString(), null,
                    backgroundColourWhite,
                    backgroundColourPink, backgroundColourLightBlue);
            DefaultInfoBackgroundRow.setSpacing(3);
            DefaultInfoBackgroundRow.getChildren().addAll(new Label("Background"), DefaultInfobackgroundColour);

            backgroundColourWhite.setOnAction(e -> {
                canvas.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
            });
            backgroundColourPink.setOnAction(e -> {
                canvas.setBackground(new Background(new BackgroundFill(Color.PINK, CornerRadii.EMPTY, Insets.EMPTY)));
            });
            backgroundColourLightBlue.setOnAction(e -> {
                canvas.setBackground(
                        new Background(new BackgroundFill(Color.LIGHTBLUE, CornerRadii.EMPTY, Insets.EMPTY)));
            });

            infoBar.getChildren().add(DefaultInfoBackgroundRow);
        }
    }

    public void unselect() {
        this.selectedElement = null;
        canvas.getChildren().removeAll(frame, topLeft, topMid, topRight, midLeft, midRight, bottomLeft, bottomMid,
                bottomRight);

        infoBar.getChildren().clear();

        HBox DefaultInfoBackgroundRow = new HBox();
        MenuItem backgroundColourWhite = new MenuItem("White");
        MenuItem backgroundColourPink = new MenuItem("Pink");
        MenuItem backgroundColourLightBlue = new MenuItem("Light Blue");
        MenuButton DefaultInfobackgroundColour = new MenuButton(
                canvas.getBackground().getFills().get(0).getFill().toString(), null,
                backgroundColourWhite,
                backgroundColourPink, backgroundColourLightBlue);
        DefaultInfoBackgroundRow.setSpacing(3);
        DefaultInfoBackgroundRow.getChildren().addAll(new Label("Background"), DefaultInfobackgroundColour);

        backgroundColourWhite.setOnAction(e -> {
            canvas.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        });
        backgroundColourPink.setOnAction(e -> {
            canvas.setBackground(new Background(new BackgroundFill(Color.PINK, CornerRadii.EMPTY, Insets.EMPTY)));
        });
        backgroundColourLightBlue.setOnAction(e -> {
            canvas.setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE, CornerRadii.EMPTY, Insets.EMPTY)));
        });

        infoBar.getChildren().add(DefaultInfoBackgroundRow);
    }

    public void makeDeleteable(Node node) {
        if (selectedElement != null) {
            deleteElement.setOnAction(e -> {
                canvas.getChildren().remove(selectedElement);
                unselect();
            });
        }
    }

    public void makeResizable(Node[] nodes) {

        for (Node node : nodes) {
            node.setOnMouseEntered(e -> {
                canvas.setCursor(Cursor.HAND);
            });

            node.setOnMouseExited(e1 -> {
                canvas.setCursor(Cursor.DEFAULT);
            });

            node.setOnMousePressed(e -> {
                startX = e.getSceneX() - node.getTranslateX() * canvas.getScaleX();
                startY = e.getSceneY() - node.getTranslateY() * canvas.getScaleY();
            });
        }

        if (selectedElement instanceof Rectangle) {
            nodes[0].setOnMouseDragged(e -> {
                ((Rectangle) selectedElement).setWidth(
                        frame.getWidth()
                                - ((e.getSceneX() - startX) / canvas.getScaleX() - nodes[0].getTranslateX()));
                ((Rectangle) selectedElement).setHeight(
                        frame.getHeight()
                                - ((e.getSceneY() - startY) / canvas.getScaleY() - nodes[0].getTranslateY()));
            });

            nodes[1].setOnMouseDragged(e -> {
                ((Rectangle) selectedElement).setHeight(
                        frame.getHeight()
                                - ((e.getSceneY() - startY) / canvas.getScaleY() - nodes[1].getTranslateY()));
            });

            nodes[2].setOnMouseDragged(e -> {
                ((Rectangle) selectedElement).setWidth(
                        frame.getWidth() + (e.getSceneX() - startX) / canvas.getScaleX() - nodes[2].getTranslateX());
                ((Rectangle) selectedElement).setHeight(
                        frame.getHeight()
                                - ((e.getSceneY() - startY) / canvas.getScaleY() - nodes[2].getTranslateY()));
            });

            nodes[3].setOnMouseDragged(e -> {
                ((Rectangle) selectedElement).setWidth(
                        frame.getWidth()
                                - ((e.getSceneX() - startX) / canvas.getScaleX() - nodes[3].getTranslateX()));
            });

            nodes[4].setOnMouseDragged(e -> {
                ((Rectangle) selectedElement).setWidth(
                        frame.getWidth() + (e.getSceneX() - startX) / canvas.getScaleX() - nodes[4].getTranslateX());
            });

            nodes[5].setOnMouseDragged(e -> {
                ((Rectangle) selectedElement).setWidth(
                        frame.getWidth()
                                - ((e.getSceneX() - startX) / canvas.getScaleX() - nodes[5].getTranslateX()));
                ((Rectangle) selectedElement).setHeight(
                        frame.getHeight() + (e.getSceneY() - startY) / canvas.getScaleY() - nodes[5].getTranslateY());
            });

            nodes[6].setOnMouseDragged(e -> {
                ((Rectangle) selectedElement).setHeight(
                        frame.getHeight() + (e.getSceneY() - startY) / canvas.getScaleY() - nodes[6].getTranslateY());
            });

            nodes[7].setOnMouseDragged(e -> {
                ((Rectangle) selectedElement).setWidth(
                        frame.getWidth() + (e.getSceneX() - startX) / canvas.getScaleX() - nodes[7].getTranslateX());
                ((Rectangle) selectedElement).setHeight(
                        frame.getHeight() + (e.getSceneY() - startY) / canvas.getScaleY()
                                - nodes[7].getTranslateY());
            });
        } else if (selectedElement instanceof Circle) {
            nodes[0].setOnMouseDragged(e -> {
                ((Circle) selectedElement).setRadius(frame.getWidth() / 2
                        - ((e.getSceneX() - startX) / canvas.getScaleX() - nodes[7].getTranslateX()));
            });

            nodes[1].setVisible(false);

            nodes[2].setOnMouseDragged(e -> {
                ((Circle) selectedElement).setRadius(frame.getWidth() / 2
                        + (e.getSceneX() - startX) / canvas.getScaleX() - nodes[7].getTranslateX());
            });

            nodes[3].setVisible(false);

            nodes[4].setVisible(false);

            nodes[5].setOnMouseDragged(e -> {
                ((Circle) selectedElement).setRadius(frame.getWidth() / 2
                        - ((e.getSceneX() - startX) / canvas.getScaleX() - nodes[7].getTranslateX()));
            });

            nodes[6].setVisible(false);

            nodes[7].setOnMouseDragged(e -> {
                ((Circle) selectedElement).setRadius(frame.getWidth() / 2
                        + (e.getSceneX() - startX) / canvas.getScaleX() - nodes[7].getTranslateX());
            });
        } else if (selectedElement instanceof Label) {
            nodes[0].setOnMouseDragged(e -> {
                ((Labeled) selectedElement).setPrefWidth(
                        frame.getWidth()
                                - ((e.getSceneX() - startX) / canvas.getScaleX() - nodes[0].getTranslateX()));
                ((Labeled) selectedElement).setPrefHeight(
                        frame.getHeight()
                                - ((e.getSceneY() - startY) / canvas.getScaleY() - nodes[0].getTranslateY()));
            });

            nodes[1].setOnMouseDragged(e -> {
                ((Labeled) selectedElement).setPrefHeight(
                        frame.getHeight()
                                - ((e.getSceneY() - startY) / canvas.getScaleY() - nodes[1].getTranslateY()));
            });

            nodes[2].setOnMouseDragged(e -> {
                ((Labeled) selectedElement).setPrefWidth(
                        frame.getWidth() + (e.getSceneX() - startX) / canvas.getScaleX() - nodes[2].getTranslateX());
                ((Labeled) selectedElement).setPrefHeight(
                        frame.getHeight()
                                - ((e.getSceneY() - startY) / canvas.getScaleY() - nodes[2].getTranslateY()));
            });

            nodes[3].setOnMouseDragged(e -> {
                ((Labeled) selectedElement).setPrefWidth(
                        frame.getWidth()
                                - ((e.getSceneX() - startX) / canvas.getScaleX() - nodes[3].getTranslateX()));
            });

            nodes[4].setOnMouseDragged(e -> {
                ((Labeled) selectedElement).setPrefWidth(
                        frame.getWidth() + (e.getSceneX() - startX) / canvas.getScaleX() - nodes[4].getTranslateX());
            });

            nodes[5].setOnMouseDragged(e -> {
                ((Labeled) selectedElement).setPrefWidth(
                        frame.getWidth()
                                - ((e.getSceneX() - startX) / canvas.getScaleX() - nodes[5].getTranslateX()));
                ((Labeled) selectedElement).setPrefHeight(
                        frame.getHeight() + (e.getSceneY() - startY) / canvas.getScaleY() - nodes[5].getTranslateY());
            });

            nodes[6].setOnMouseDragged(e -> {
                ((Labeled) selectedElement).setPrefHeight(
                        frame.getHeight() + (e.getSceneY() - startY) / canvas.getScaleY() - nodes[6].getTranslateY());
            });

            nodes[7].setOnMouseDragged(e -> {
                ((Labeled) selectedElement).setPrefWidth(
                        frame.getWidth() + (e.getSceneX() - startX) / canvas.getScaleX() - nodes[7].getTranslateX());
                ((Labeled) selectedElement).setPrefHeight(
                        frame.getHeight() + (e.getSceneY() - startY) / canvas.getScaleY()
                                - nodes[7].getTranslateY());
            });
        }

    }

    public void captureAndSaveDisplay() {
        FileChooser fileChooser = new FileChooser();

        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("png files (*.png)", "*.png"));

        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            try {
                WritableImage writableImage = new WritableImage((int) canvas.getWidth(),
                        (int) canvas.getHeight());
                canvas.snapshot(null, writableImage);
                BufferedImage renderedImage = SwingFXUtils.fromFXImage(writableImage, null);
                ImageIO.write(renderedImage, "png", file);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

}
