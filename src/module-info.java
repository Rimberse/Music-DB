/**
 * 
 */
/**
 * @author Rimberse
 *
 */
module music {
	exports application;
	exports application.model;

	requires javafx.base;
	requires javafx.fxml;
	requires transitive javafx.graphics;
	requires javafx.controls;
	requires java.sql;
	
	opens application;
	opens application.model;
}