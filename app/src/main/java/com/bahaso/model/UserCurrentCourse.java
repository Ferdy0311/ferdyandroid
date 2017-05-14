package com.bahaso.model;


public class UserCurrentCourse {
    private String  courseName, courseImg, totalPoint, totalScore;
    private double courseProgress;
    private int  courseID, coursePoint, courseScore;

    public UserCurrentCourse(){}

    public UserCurrentCourse( int courseID, String courseName, String courseImg, String totalScore, String totalPoint,
                             int courseScore, int coursePoint,  double courseProgress ){
        this.courseID = courseID;
        this.courseName = courseName;
        this.coursePoint = coursePoint;
        this.courseScore = courseScore;
        this.courseProgress = courseProgress;
        this.courseImg = courseImg;
        this.totalPoint = totalPoint;
        this.totalScore = totalScore;
    }

    public void setCourseID(int courseID) { this.courseID = courseID; }
    public int getCourseID() { return courseID; }
    public void setCourseName(String courseName) { this.courseName = courseName; }
    public String getCourseName() { return courseName; }
    public void setCoursePoint(int coursePoint) { this.coursePoint = coursePoint; }
    public int getCoursePoint() { return coursePoint; }
    public void setCourseScore(int courseScore) { this.courseScore = courseScore; }
    public int getCourseScore() { return courseScore; }
    public void setCourseProgress(double courseProgress) { this.courseProgress = courseProgress; }
    public double getCourseProgress() { return courseProgress; }
    public void setCourseImg(String courseImg) { this.courseImg = courseImg; }
    public String getCourseImg() { return courseImg; }
    public void setTotalPoint(String totalPoint) { this.totalPoint = totalPoint; }
    public String getTotalPoint() { return totalPoint; }
    public void setTotalScore(String totalScore) { this.totalScore = totalScore; }
    public String getTotalScore() { return  totalScore; }

}
