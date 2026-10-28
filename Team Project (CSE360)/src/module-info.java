/**
 * The FoundationsF26 module: requires JavaFX controls and java.sql for the database, and
 * opens applicationMain to JavaFX so its FXML/reflection-based startup can run.
 */
module FoundationsF26 {
	requires javafx.controls;
	requires java.sql;
	
	opens applicationMain to javafx.graphics, javafx.fxml;
}
