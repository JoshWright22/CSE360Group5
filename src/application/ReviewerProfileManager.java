package application;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import databasePart1.DatabaseHelper;
import application.obj.ReviewerProfile;

/**
 * Acts as a manager class for dealing with ReviewerProfile objects. This use of
 * data hiding and encapsulation helps ensure parity between data stored in the
 * local cache and data stored on the MySQL database.
 */
public class ReviewerProfileManager {

	private final DatabaseHelper database;
	private final Set<ReviewerProfile> profileSet = new HashSet<>();

	/**
	 * Constructs a new ReviewerProfileManager. The given {@link DatabaseHelper}
	 * will be used to interact with the program's database.
	 */
	public ReviewerProfileManager(DatabaseHelper database) {
		this.database = database;
		createTableIfNotExists();
		createPendingReviewersTableIfNotExists();
		fetchProfiles();
	}

	private void createTableIfNotExists() {
		if (database.getConnection() == null) {
			System.err.println("Database connection is null. Cannot create table.");
			return;
		}

		String sql = """
				    CREATE TABLE IF NOT EXISTS ReviewerProfiles (
				        userName VARCHAR(255) PRIMARY KEY,
				        bio VARCHAR(1000),
				        expertise VARCHAR(255),
				        yearsExperience INT,
				        totalReviews INT,
				        averageRating DOUBLE
				    )
				""";

		try (Statement stmt = database.getConnection().createStatement()) {
			stmt.execute(sql);
			System.out.println("ReviewerProfiles table is ready.");
		} catch (SQLException e) {
			System.err.println("Failed to create ReviewerProfiles table.");
			e.printStackTrace();
		}
	}

	private void createPendingReviewersTableIfNotExists() {
		if (database.getConnection() == null) {
			System.err.println("Database connection is null. Cannot create pending reviewers table.");
			return;
		}

		String sql = """
				    CREATE TABLE IF NOT EXISTS PendingReviewers (
				        userName VARCHAR(255) PRIMARY KEY,
				        requestDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP
				    )
				""";

		try (Statement stmt = database.getConnection().createStatement()) {
			stmt.execute(sql);
			System.out.println("PendingReviewers table is ready.");
		} catch (SQLException e) {
			System.err.println("Failed to create PendingReviewers table.");
			e.printStackTrace();
		}
	}

	/**
	 * Fetches data used to construct {@link ReviewerProfile} objects from the
	 * database, constructs those objects, and stores them in a local cache.
	 */
	public void fetchProfiles() {
		if (database.getConnection() == null) {
			System.err.println("Database connection is null. Cannot fetch profiles.");
			return;
		}

		String query = "SELECT * FROM ReviewerProfiles";
		try (Statement stmt = database.getConnection().createStatement(); ResultSet rs = stmt.executeQuery(query)) {

			profileSet.clear();

			while (rs.next()) {
				String userName = rs.getString("userName");
				ReviewerProfile profile = new ReviewerProfile(userName);
				profile.setBio(rs.getString("bio"));
				profile.setExpertise(rs.getString("expertise"));
				profile.setYearsExperience(rs.getInt("yearsExperience"));
				profile.setTotalReviews(rs.getInt("totalReviews"));
				profile.setAverageRating(rs.getDouble("averageRating"));

				profileSet.add(profile);
			}
		} catch (SQLException e) {
			System.err.println("Failed to fetch reviewer profiles from database.");
			e.printStackTrace();
		}
	}

	/**
	 * Retrieves a {@link ReviewerProfile}. First, the method checks the local cache
	 * for the object. Then, if that fails, it checks the database for the a
	 * username match to construct the appropriate ReviewerProfile object. If that
	 * fails, then the function will create a new ReviewerProfile and add it into
	 * the database.
	 * 
	 * <p>
	 * <b>This method will never return null.</b>
	 * 
	 * @param userName - Username of ReviewerProfile to look for
	 * @return pre-existing or new ReviewerProfile object
	 */
	public ReviewerProfile getOrCreateProfile(String userName) {
		// Check in-memory first
		for (ReviewerProfile profile : profileSet) {
			if (profile.getUserName().equals(userName)) {
				return profile;
			}
		}

		// Check database for existing profile
		if (database.getConnection() != null) {
			String query = "SELECT * FROM ReviewerProfiles WHERE userName = ?";
			try (PreparedStatement stmt = database.getConnection().prepareStatement(query)) {
				stmt.setString(1, userName);
				ResultSet rs = stmt.executeQuery();
				if (rs.next()) {
					ReviewerProfile profile = new ReviewerProfile(userName);
					profile.setBio(rs.getString("bio"));
					profile.setExpertise(rs.getString("expertise"));
					profile.setYearsExperience(rs.getInt("yearsExperience"));
					profile.setTotalReviews(rs.getInt("totalReviews"));
					profile.setAverageRating(rs.getDouble("averageRating"));
					profileSet.add(profile);
					return profile;
				}
			} catch (SQLException e) {
				System.err.println("Failed to check database for profile: " + userName);
				e.printStackTrace();
			}

			// Insert new profile if it does not exist
			ReviewerProfile newProfile = new ReviewerProfile(userName);
			String insert = "INSERT INTO ReviewerProfiles (userName, bio, expertise, yearsExperience, totalReviews, averageRating) VALUES (?, ?, ?, ?, ?, ?)";
			try (PreparedStatement stmt = database.getConnection().prepareStatement(insert)) {
				stmt.setString(1, userName);
				stmt.setString(2, "");
				stmt.setString(3, "");
				stmt.setInt(4, 0);
				stmt.setInt(5, 0);
				stmt.setDouble(6, 0.0);
				stmt.executeUpdate();
			} catch (SQLException e) {
				System.err.println("Failed to create new reviewer profile for " + userName);
				e.printStackTrace();
			}
			profileSet.add(newProfile);
			return newProfile;
		}

		// Fallback if database is null
		ReviewerProfile fallbackProfile = new ReviewerProfile(userName);
		profileSet.add(fallbackProfile);
		return fallbackProfile;
	}

	/**
	 * Pushes the data contained in a local {@link ReviewerProfile} to its
	 * counterpart stored in the database.
	 * 
	 * @param profile - ReviewerProfile to update
	 */
	public void updateProfile(ReviewerProfile profile) {
		if (database.getConnection() == null) {
			System.err.println("Database connection is null. Cannot update profile.");
			return;
		}

		String query = "UPDATE ReviewerProfiles SET bio = ?, expertise = ?, yearsExperience = ?, totalReviews = ?, averageRating = ? WHERE userName = ?";
		try (PreparedStatement stmt = database.getConnection().prepareStatement(query)) {
			stmt.setString(1, profile.getBio());
			stmt.setString(2, profile.getExpertise());
			stmt.setInt(3, profile.getYearsExperience());
			stmt.setInt(4, profile.getTotalReviews());
			stmt.setDouble(5, profile.getAverageRating());
			stmt.setString(6, profile.getUserName());
			stmt.executeUpdate();
		} catch (SQLException e) {
			System.err.println("Failed to update reviewer profile for " + profile.getUserName());
			e.printStackTrace();
		}
	}

	/**
	 * Returns an unmodifiable {@link Set} containing the current local cache of
	 * {@link ReviewerProfile} objects.
	 * 
	 * @return
	 */
	public Set<ReviewerProfile> getProfiles() {
		return Collections.unmodifiableSet(profileSet);
	}
}
