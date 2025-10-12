package application;

public enum UserRole {

	ADMIN, STUDENT, REVIEWER, INSTRUCTOR, STAFF;

	// Override the toString() method to return what gets stored in the database
	@Override
	public String toString() {
		return this.name().toLowerCase();
	}
}
