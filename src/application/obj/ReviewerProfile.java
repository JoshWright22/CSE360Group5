package application.obj;

public class ReviewerProfile {
    private String userName;
    private String bio;
    private String expertise;
    private int yearsExperience;
    private int totalReviews;
    private double averageRating;

    public ReviewerProfile(String userName) {
        this.userName = userName;
        this.bio = "";
        this.expertise = "";
        this.yearsExperience = 0;
        this.totalReviews = 0;
        this.averageRating = 0.0;
    }

    public String getUserName() {
        return userName;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getExpertise() {
        return expertise;
    }

    public void setExpertise(String expertise) {
        this.expertise = expertise;
    }

    public int getYearsExperience() {
        return yearsExperience;
    }

    public void setYearsExperience(int yearsExperience) {
        this.yearsExperience = yearsExperience;
    }

    public int getTotalReviews() {
        return totalReviews;
    }

    public void setTotalReviews(int totalReviews) {
        this.totalReviews = totalReviews;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }
}
