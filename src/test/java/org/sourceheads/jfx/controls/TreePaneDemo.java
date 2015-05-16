package org.sourceheads.jfx.controls;

import java.util.Random;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * (...)
 *
 * @author Stefan Fiedler
 */
public class TreePaneDemo extends Application {

    @Override
    public void start(final Stage primaryStage) throws Exception {

        final TreeNode root = buildNode(
                buildNode(),
                buildNode(buildNode(),buildNode()),
                buildNode(buildNode(),buildNode()),
                buildNode()
        );
        // root.setExpanded(true);

        final TreePane treePane = new TreePane(root);
        treePane.setPadding(new Insets(10));

        final DefaultTreeLayout treeLayout = new DefaultTreeLayout(treePane);
        treePane.setTreeLayout(treeLayout);
        treeLayout.setVerticalSpacing(10);
        treeLayout.setHorizontalSpacing(10);

        final DefaultConnectorLayout connectorLayout = new DefaultConnectorLayout(treePane);
        treePane.setConnectorLayout(connectorLayout);

        final ScrollPane scrollPane = new ScrollPane(treePane);

        final CheckBox chkShowRoot = new CheckBox("Show root");
        chkShowRoot.setMinWidth(Region.USE_PREF_SIZE);
        chkShowRoot.setSelected(true);
        treePane.showRootProperty().bind(chkShowRoot.selectedProperty());

        //
        // tree layout

        final Spinner<Double> horizontalSpinner = new Spinner<>(
                new SpinnerValueFactory.DoubleSpinnerValueFactory(0, 100, 32, 1));
        horizontalSpinner.setPrefWidth(70);
        horizontalSpinner.setMinWidth(Region.USE_PREF_SIZE);
        horizontalSpinner.setEditable(true);
        treeLayout.horizontalSpacingProperty().bind(horizontalSpinner.valueProperty());
        final Spinner<Double> verticalSpinner = new Spinner<>(
                new SpinnerValueFactory.DoubleSpinnerValueFactory(0, 100, 18, 1));
        verticalSpinner.setPrefWidth(70);
        verticalSpinner.setMinWidth(Region.USE_PREF_SIZE);
        verticalSpinner.setEditable(true);
        treeLayout.verticalSpacingProperty().bind(verticalSpinner.valueProperty());

        final CheckBox chkAlignCousins = new CheckBox("Align cousins");
        chkAlignCousins.setMinWidth(Region.USE_PREF_SIZE);
        treeLayout.alignCousinsProperty().bind(chkAlignCousins.selectedProperty());

        //
        // connector layout

        final Slider vConnPosSlider = new Slider(0, 1, .5);

        final Spinner<Double> vConnPosSpinner = new Spinner<>(
                new SpinnerValueFactory.DoubleSpinnerValueFactory(0.5, 100.5, 20.5, 1));
        vConnPosSpinner.setPrefWidth(70);
        vConnPosSpinner.setMinWidth(Region.USE_PREF_SIZE);
        vConnPosSpinner.setEditable(true);

        final ToggleGroup vConnPosTglGrp = new ToggleGroup();
        final RadioButton vConnPosRelRadio = new RadioButton("Relative");
        vConnPosRelRadio.setToggleGroup(vConnPosTglGrp);
        vConnPosRelRadio.setUserData(DefaultConnectorLayout.VerticalPositioning.RELATIVE);
        final RadioButton vConnPosAbsRadio = new RadioButton("Absolute");
        vConnPosAbsRadio.setToggleGroup(vConnPosTglGrp);
        vConnPosAbsRadio.setUserData(DefaultConnectorLayout.VerticalPositioning.ABSOLUTE);

        vConnPosTglGrp.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            final DefaultConnectorLayout.VerticalPositioning vp =
                    (DefaultConnectorLayout.VerticalPositioning) newValue.getUserData();
            connectorLayout.setVerticalPositioning(vp);
            connectorLayout.verticalPositionProperty().unbind();
            switch (vp) {
                case RELATIVE:
                    connectorLayout.verticalPositionProperty().bind(vConnPosSlider.valueProperty());
                    break;
                case ABSOLUTE:
                    connectorLayout.verticalPositionProperty().bind(vConnPosSpinner.valueProperty());
                    break;
            }
        });
        vConnPosAbsRadio.setSelected(true);

        final VBox vConnPosBox = new VBox(10,
                new Label("Vertical line positioning"),
                new HBox(10, vConnPosRelRadio, vConnPosSlider),
                new HBox(10, vConnPosAbsRadio, vConnPosSpinner));

        //

        final Slider hConnPosSlider = new Slider(0, 1, .5);

        final Spinner<Double> hConnPosSpinner = new Spinner<>(
                new SpinnerValueFactory.DoubleSpinnerValueFactory(0.5, 100.5, 12.5, 1));
        hConnPosSpinner.setPrefWidth(70);
        hConnPosSpinner.setMinWidth(Region.USE_PREF_SIZE);
        hConnPosSpinner.setEditable(true);

        final ToggleGroup hConnPosTglGrp = new ToggleGroup();
        final RadioButton hConnPosRelRadio = new RadioButton("Relative");
        hConnPosRelRadio.setToggleGroup(hConnPosTglGrp);
        hConnPosRelRadio.setUserData(DefaultConnectorLayout.HorizontalPositioning.RELATIVE);
        final RadioButton hConnPosAbsLeftRadio = new RadioButton("Absolute left");
        hConnPosAbsLeftRadio.setToggleGroup(hConnPosTglGrp);
        hConnPosAbsLeftRadio.setUserData(DefaultConnectorLayout.HorizontalPositioning.ABSOLUTE_LEFT);
        final RadioButton hConnPosAbsRightRadio = new RadioButton("Absolute right");
        hConnPosAbsRightRadio.setToggleGroup(hConnPosTglGrp);
        hConnPosAbsRightRadio.setUserData(DefaultConnectorLayout.HorizontalPositioning.ABSOLUTE_RIGHT);

        hConnPosTglGrp.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            final DefaultConnectorLayout.HorizontalPositioning hp =
                    (DefaultConnectorLayout.HorizontalPositioning) newValue.getUserData();
            connectorLayout.setHorizontalPositioning(hp);
            connectorLayout.verticalPositionProperty().unbind();
            switch (hp) {
                case RELATIVE:
                    connectorLayout.horizontalPositionProperty().bind(hConnPosSlider.valueProperty());
                    break;
                case ABSOLUTE_LEFT:
                    connectorLayout.horizontalPositionProperty().bind(hConnPosSpinner.valueProperty());
                    break;
                case ABSOLUTE_RIGHT:
                    connectorLayout.horizontalPositionProperty().bind(hConnPosSpinner.valueProperty());
                    break;
            }
        });
        hConnPosRelRadio.setSelected(true);

        final VBox hConnPosBox = new VBox(10,
                new Label("Horizontal line positioning"),
                new HBox(10, hConnPosRelRadio, hConnPosSlider),
                new HBox(10, new VBox(10, hConnPosAbsLeftRadio, hConnPosAbsRightRadio), hConnPosSpinner));

        //

        final HBox hBox = new HBox(10,
                chkShowRoot,
                new TitledPane("Tree layout", new HBox(10,
                        new VBox(new Label("H spacing"), horizontalSpinner),
                        new VBox(new Label("V spacing"), verticalSpinner),
                        chkAlignCousins
                )),
                new TitledPane("Connector layout", new HBox(10, hConnPosBox, vConnPosBox))
        );
        // hBox.setAlignment(Pos.CENTER_LEFT);

        final VBox vBox = new VBox(hBox, scrollPane);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        vBox.setPrefWidth(1024);
        vBox.setPrefHeight(768);
        vBox.setPadding(new Insets(10));
        vBox.setSpacing(10);

        final Scene scene = new Scene(vBox);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Tree test");
        primaryStage.show();
    }

    private TreeNode buildNode(final TreeNode... children) {
        final TreeNode treeNode = new TreeNode();
        final ObservableList<TreeNode> nodeChildren = treeNode.getChildren();
        nodeChildren.addAll(children);

        final Button btnAdd = new Button("Add");
        btnAdd.setPadding(new Insets(1, 4, 1, 4));
        btnAdd.setOnAction(event -> nodeChildren.add(buildNode()));

        final  Button btnRemove = new Button("Remove");
        btnRemove.setPadding(new Insets(1, 4, 1, 4));
        btnRemove.setOnAction(event -> treeNode.getParent().getChildren().remove(treeNode));

        final CheckBox checkBox = new CheckBox();
        checkBox.setSelected(true);
        checkBox.disableProperty().bind(Bindings.isEmpty(nodeChildren));
        // checkBox.visibleProperty().bind(Bindings.isNotEmpty(nodeChildren));
        treeNode.expandedProperty().bind(checkBox.selectedProperty());

        final Label label = new Label(lorem());
        label.setOnMouseClicked(event -> label.setText(lorem()));

        final HBox hBox = new HBox(checkBox, label, btnAdd, btnRemove);
        HBox.setMargin(btnAdd, new Insets(0, 0, 0, 8));
        HBox.setMargin(btnRemove, new Insets(0, 0, 0, 8));
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.setSpacing(0);

        final TitledPane titledPane = new TitledPane(null, hBox);
        titledPane.setExpanded(children.length > 0);

        final VBox paneTitle = new VBox(new Label(lorem()), new Label(lorem()));
        titledPane.setGraphic(paneTitle);

        treeNode.setNode(titledPane);

        return treeNode;
    }

    public static void main(final String[] args) {
        launch();
    }

    private static final String[] LOREM_IPSUM = {
            "Lorem ipsum", "Dolor sit amet", "Consectetur adipiscing", "Elit sed do eiusmod", "Tempor incididunt",
            "Labore et dolore", "Magna aliqua", "Ut enim ad minim veniam", "Quis nostrud", "Exercitation",
            "Ullamco laboris", "Nisi ut aliquip", "Ex ea commodo consequat", "Duis aute irure", "Dolor in reprehenderit",
            "Voluptate velit esse", "Cillum dolore", "Fugiat nulla pariatur", "Excepteur sint occaecat",
            "Cupidatat non proident", "Sunt in culpa qui", "Officia deserunt", "Mollit anim id", "Est laborum"};

    private static final Random RANDOM = new Random(System.currentTimeMillis());

    private static String lorem() {
        return LOREM_IPSUM[RANDOM.nextInt(LOREM_IPSUM.length)];
    }
}
