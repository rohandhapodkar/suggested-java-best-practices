package test.java.trywithreousrce;

import java.io.IOException;
import java.io.Writer;
import java.sql.Connection;
import java.sql.SQLException;

public abstract class ResultSetHelper {

	public void copyResultSetToFile(String query, String fileName) throws SQLException, IOException, ValidationException {

		try (Connection con = this.getConnection(); 
				Writer writer = this.getFileWriter(fileName)) {
			this.copyResultSetToStream(con, query, writer);
		}
	}

	public abstract void copyResultSetToStream(Connection con, String query, Writer writer) 
			throws SQLException, IOException, ValidationException ;

	public abstract Connection getConnection() throws SQLException ;

	public abstract Writer getFileWriter(String fileName) throws IOException ;

}

class ValidationException extends Exception {

	public ValidationException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}
	
}
