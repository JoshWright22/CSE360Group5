package application;

/**
 * The UserRole enumerator is used to represent the different types of roles users can have.
 */
public enum UserRole {

	ADMIN, STUDENT, REVIEWER, INSTRUCTOR, STAFF;

	// Override the toString() method to return what gets stored in the database
	@Override
	public String toString() {
		return this.name().toLowerCase();
	}
}
