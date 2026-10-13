package com.university;

public class RegistrationService {

    private static final int MAX_CREDITS = 20;
    private static final int MAX_COURSES = 5;

    public boolean register(Student student, Course course) {

        if (!course.isActive()) {
            return false;
        }

        // Reject registration when the course is already full.
        if (course.getCurrentEnrollment() >= course.getCapacity()) {
            return false;
        }

        if (student.isRegistered(course.getCourseCode())) {
            return false;
        }

        // Allow at most five registered courses per student.
        if (student.getRegisteredCourses().size() >= MAX_COURSES) {
            return false;
        }

        String prerequisite = course.getPrerequisite();

        if (prerequisite != null
                && !prerequisite.isBlank()
                && !student.hasCompletedPrerequisite(prerequisite)) {
            return false;
        }

        if (student.getTotalCredits() + course.getCredits() > MAX_CREDITS) {
            return false;
        }

        student.addRegistration(
                course.getCourseCode(),
                course.getCredits());

        course.incrementEnrollment();

        return true;
    }
}