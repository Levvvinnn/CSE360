module PasswordEvaluationTestbed {
	requires javafx.controls;
	
	opens passwordEvaluationTestbedMain to javafx.graphics, javafx.fxml;
}
