package net.soomsam.zirmegghuette.zars.persistence.utils;

import java.sql.SQLException;

import org.hibernate.dialect.MySQL5InnoDBDialect;
import org.hibernate.exception.JDBCExceptionHelper;
import org.hibernate.exception.TemplatedViolatedConstraintNameExtracter;
import org.hibernate.exception.ViolatedConstraintNameExtracter;

public class HibernateMySqlDialect extends MySQL5InnoDBDialect {
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
		@Override
		public String extractConstraintName(final SQLException sqle) {
			String constraintName = null;

			final int errorCode = JDBCExceptionHelper.extractErrorCode(sqle);
			final String sqlState = JDBCExceptionHelper.extractSqlState(sqle);

			if (1062 == errorCode && "23000".equals(sqlState)) {
				constraintName = extractUsingTemplate("' for key '", "'", sqle.getMessage());
			}

			return constraintName;
		}
	};
}
