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
 * Manages reviewer profiles with database persistence.
 * Fetches existing profiles, creates new ones if needed, and allows updates.
 */
public class ReviewerProfileManager {

    private final DatabaseHelper database;
    private final Set<ReviewerProfile> profileSet = new HashSet<>();

    public ReviewerProfileManager(DatabaseHelper database) {
        this.database = database;
        fetchProfiles();
    }

    /**
     * Fetches all reviewer profiles from the database.
     */
    public void fetchProfiles() {
        if (database.getConnection() == null) {
            System.err.println("Database connection is null. Cannot fetch profiles.");
            return;
        }

        String query = "SELECT * FROM ReviewerProfiles";
        try (Statement stmt = database.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            profileSet.clear();

            while (rs.next()) {
                String userName = rs.getString("userName");
                String bio = rs.getString("bio");
                String expertise = rs.getString("expertise");
                int yearsExperience = rs.getInt("yearsExperience");
                int totalReviews = rs.getInt("totalReviews");
                double averageRating = rs.getDouble("averageRating");

                ReviewerProfile profile = new ReviewerProfile(userName);
                profile.setBio(bio);
                profile.setExpertise(expertise);
                profile.setYearsExperience(yearsExperience);
                profile.setTotalReviews(totalReviews);
                profile.setAverageRating(averageRating);

                profileSet.add(profile);
            }
        } catch (SQLException e) {
            System.err.println("Failed to fetch reviewer profiles from database.");
            e.printStackTrace();
        }
    }

    /**
     * Retrieves an existing profile or creates a new one if not found.
     */
    public ReviewerProfile getOrCreateProfile(String userName) {
        for (ReviewerProfile profile : profileSet) {
            if (profile.getUserName().equals(userName)) {
                return profile;
            }
        }

        ReviewerProfile newProfile = new ReviewerProfile(userName);

        if (database.getConnection() != null) {
            String query = "INSERT INTO ReviewerProfiles (userName, bio, expertise, yearsExperience, totalReviews, averageRating) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = database.getConnection().prepareStatement(query)) {
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
        }

        profileSet.add(newProfile);
        return newProfile;
    }

    /**
     * Updates an existing profile in the database.
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
     * Returns all profiles as an unmodifiable set.
     */
    public Set<ReviewerProfile> getProfiles() {
        return Collections.unmodifiableSet(profileSet);
    }
}
