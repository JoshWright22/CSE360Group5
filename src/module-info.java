module GroupProject {
	requires javafx.controls;
	requires java.sql;
	requires javafx.graphics;
	requires org.junit.platform.commons;
	requires org.junit.jupiter.api;
	requires org.opentest4j;
	requires junit;

	opens application to javafx.graphics, javafx.fxml;
}
