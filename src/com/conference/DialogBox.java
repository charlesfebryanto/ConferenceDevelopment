package com.conference;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.util.Optional;

public class DialogBox {
    public static void alertBox(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static boolean confirmationBox(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        Optional<ButtonType> result = alert.showAndWait();
        if(result.get() == ButtonType.OK) {
            return true;
        } else {
            return false;
        }
    }

    private static void stringOnly(TextField fieldName) {
        try {
            if(fieldName.getText().length() > 0) {
                if (Integer.parseInt(fieldName.getText().charAt(fieldName.getText().length() - 1) + "") > 0
                        || Integer.parseInt(fieldName.getText().charAt(fieldName.getText().length() - 1) + "") < 0) {
                    DialogBox.alertBox("Error", "No Number allowed.");
                    Platform.runLater(() -> fieldName.clear());
                }
            }
        } catch (NumberFormatException nfe) {
//                Do nothing, user enter a letter
        }
    }

    // boolean return type
    public static boolean numberOnly(TextField fieldName) {
        try {
            if(fieldName.getText().length() > 0) {
                if (Integer.parseInt(fieldName.getText().charAt(fieldName.getText().length() - 1) + "") > 0
                        || Integer.parseInt(fieldName.getText().charAt(fieldName.getText().length() - 1) + "") < 0) {
                    return true;
                }
            }
            return false;
        } catch (NumberFormatException nfe) {
            DialogBox.alertBox("Error", "No Letter allowed.");
            Platform.runLater(() -> fieldName.clear());
            return false;
        }
    }

    public static void stringOnly(TextField fieldName, int maxLength, String alertTitle, String alertMessage) {
        if(fieldName.getText().length() > maxLength) {
            DialogBox.alertBox(alertTitle, alertMessage);
            Platform.runLater(() -> fieldName.clear());
        }
        DialogBox.stringOnly(fieldName);
    }


    public static void numberOnly(TextField fieldName, int maxLength, String alertTitle, String alertMessage) {
        if(fieldName.getText().length() > maxLength) {
            DialogBox.alertBox(alertTitle, alertMessage);
//            Platform.runLater(() -> fieldName.clear());
        }
        DialogBox.numberOnly(fieldName);
    }

    public static void lengthCheck(TextArea textAreaName, int maxLength, String alertTitle, String alertMessage) {
        if(textAreaName.getText().length() > maxLength) {
            DialogBox.alertBox(alertTitle, alertMessage);
            Platform.runLater(() -> textAreaName.clear());
        }
    }

    public static void lengthCheck(TextField textField, int maxLength, String alertTitle, String alertMessage) {
        if(textField.getText().length() > maxLength) {
            DialogBox.alertBox(alertTitle, alertMessage);
            Platform.runLater(() -> textField.clear());
        }
    }

}
