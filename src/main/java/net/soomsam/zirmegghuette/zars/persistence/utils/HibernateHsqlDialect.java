package net.soomsam.zirmegghuette.zars.persistence.utils;

import java.sql.SQLException;

import org.hibernate.dialect.HSQLDialect;
import org.hibernate.exception.JDBCExceptionHelper;
import org.hibernate.exception.TemplatedViolatedConstraintNameExtracter;
import org.hibernate.exception.ViolatedConstraintNameExtracter;

public class HibernateHsqlDialect extends HSQLDialect {
	@Override
	public ViolatedConstraintNameExtracter getViolatedConstraintNameExtracter() {
		return EXTRACTER;
	}

	private static ViolatedConstraintNameExtracter EXTRACTER = new TemplatedViolatedConstraintNameExtracter() {
		/**
		 * Extract the name of the violated constraint from the given SQLException.
		 * 
		 * @param sqle
		 *            The exception that was the result of the constraint violation.
		 * @return The extracted constraint name.
		 */
		public String extractConstraintName(SQLException sqle) {
			String constraintName = null;

			int errorCode = JDBCExceptionHelper.extractErrorCode(sqle);
			String sqlState = JDBCExceptionHelper.extractSqlState(sqle);

			if (errorCode == -8) {
				constraintName = extractUsingTemplate("Integrity constraint violation ", " table:", sqle.getMessage());
			} else if (errorCode == -9) {
				constraintName = extractUsingTemplate("Violation of unique index: ", " in statement [", sqle.getMessage());
			} else if (errorCode == -104) {
				if ("23000".equals(sqlState)) {
					constraintName = extractUsingTemplate("duplicate value(s) for column(s) ", " in statement [", sqle.getMessage());
				} else {
					constraintName = extractUsingTemplate("Unique constraint violation: ", " in statement [", sqle.getMessage());
				}
			} else if (errorCode == -177) {
				constraintName = extractUsingTemplate("Integrity constraint violation - no parent ", " table:", sqle.getMessage());
			}

			return constraintName;
		}
	};
}
