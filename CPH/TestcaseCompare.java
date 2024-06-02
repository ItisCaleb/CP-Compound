<?xml version="1.0" encoding="UTF-8"?>

        <?import javafx.geometry.Insets?>
        <?import javafx.scene.control.*?>
        <?import javafx.scene.layout.*?>

<VBox id="checker" prefHeight="400.0" prefWidth="600.0" xmlns="http://javafx.com/javafx/21" xmlns:fx="http://javafx.com/fxml/1" fx:controller="com.itiscaleb.cpcompound.controller.CheckerController">
    <children>
        <BorderPane>
            <top>
                <VBox spacing="10" BorderPane.alignment="CENTER">
                    <padding>
                        <Insets top="10" right="10" bottom="10" left="10"/>
                    </padding>
                    <HBox spacing="10" alignment="CENTER">
                        <CheckBox text="是否严格比对" fx:id="strictMatchCheckBox"/>
                        <Label text="文字{TestCase}" fx:id="testCaseLabel"/>
                        <Button text="重新比对此测试用例" onAction="#recompareTestCase"/>
                        <Button text="删除此测试用例" onAction="#deleteTestCase"/>
                    </HBox>
                    <Button text="Add Test Case" onAction="#addTestCase"/>
                    <Button text="Run Comparisons" onAction="#runComparisons"/>
                </VBox>
            </top>
            <center>
                <ScrollPane fitToWidth="true">
                    <VBox fx:id="testCaseBox" spacing="10">
                        <padding>
                            <Insets top="10" right="10" bottom="10" left="10"/>
                        </padding>
                    </VBox>
                </ScrollPane>
            </center>
        </BorderPane>
    </children>
</VBox>
