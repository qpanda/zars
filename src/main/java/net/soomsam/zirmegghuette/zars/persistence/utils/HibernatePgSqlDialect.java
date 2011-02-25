package net.soomsam.zirmegghuette.zars.persistence.utils;

import java.sql.SQLException;

import org.hibernate.dialect.PostgreSQLDialect;
import org.hibernate.exception.JDBCExceptionHelper;
import org.hibernate.exception.TemplatedViolatedConstraintNameExtracter;
import org.hibernate.exception.ViolatedConstraintNameExtracter;

public class HibernatePgSqlDialect extends PostgreSQLDialect {
	@Override
	public ViolatedConstraintNameExtracter getViolatedConstraintNameExtracter() {
		return EXTRACTER;
	}

	/**
	 * Constraint-name extractor for Postgres contraint violation exceptions. Orginally contributed by Denny Bartelt.
	 */
	private static ViolatedConstraintNameExtracter EXTRACTER = new TemplatedViolatedConstraintNameExtracter() {
		@Override
		public String extractConstraintName(final SQLException sqle) {
			final String sqlState = JDBCExceptionHelper.extractSqlState(sqle);
			final String constaintName = extractConstraintName(sqlState, sqle.getMessage());
			if (null != constaintName) {
				return constaintName;
			}

			if (null != sqle.getNextException()) {
				return extractConstraintName(sqlState, sqle.getNextException().getMessage());
			}

			return null;
		}

		private String extractConstraintName(final String sqlState, final String exceptionMessage) {
			if ("23514".equals(sqlState)) {
				// CHECK VIOLATION
				return extractUsingTemplate("violates check constraint \"", "\"", exceptionMessage);
			} else if ("23505".equals(sqlState)) {
				// UNIQUE VIOLATION
				return extractUsingTemplate("violates unique constraint \"", "\"", exceptionMessage);
			} else if ("23503".equals(sqlState)) {
				// FOREIGN KEY VIOLATION
				return extractUsingTemplate("violates foreign key constraint \"", "\"", exceptionMessage);
			} else if ("23502".equals(sqlState)) {
				// NOT NULL VIOLATION
				return extractUsingTemplate("null value in column \"", "\" violates not-null constraint", exceptionMessage);
			}

			return null;
		}
	};
}
