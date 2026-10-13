package com.university;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RegistrationServiceTest {

    @Test
    void shouldRegisterStudentForValidCourse() {
        Student student = new Student("S101", "Anita");
        Course course = new Course(
                "CS301", "Data Structures", 4, 30, true, null);

        RegistrationService service = new RegistrationService();
        boolean result = service.register(student, course);

        assertTrue(result);
        assertTrue(student.isRegistered("CS301"));
        assertEquals(4, student.getTotalCredits());
        assertEquals(1, course.getCurrentEnrollment());
    }

    @Test
    void shouldRejectInactiveCourse() {
        Student student = new Student("S101", "Anita");
        Course course = new Course(
                "CS301", "Data Structures", 4, 30, false, null);

        RegistrationService service = new RegistrationService();
        boolean result = service.register(student, course);

        assertFalse(result);
        assertFalse(student.isRegistered("CS301"));
        assertEquals(0, student.getTotalCredits());
        assertEquals(0, course.getCurrentEnrollment());
    }

    @Test
    void shouldRejectMissingPrerequisite() {
        Student student = new Student("S101", "Anita");
        Course course = new Course(
                "CS401", "Advanced Algorithms", 4, 30, true, "CS301");

        RegistrationService service = new RegistrationService();
        boolean result = service.register(student, course);

        assertFalse(result);
        assertFalse(student.isRegistered("CS401"));
        assertEquals(0, student.getTotalCredits());
        assertEquals(0, course.getCurrentEnrollment());
    }

    @Test
    void shouldAllowRegistrationWhenPrerequisiteIsCompleted() {
        Student student = new Student("S101", "Anita");
        student.addCompletedPrerequisite("CS301");

        Course course = new Course(
                "CS401", "Advanced Algorithms", 4, 30, true, "CS301");

        RegistrationService service = new RegistrationService();
        boolean result = service.register(student, course);

        assertTrue(result);
        assertTrue(student.isRegistered("CS401"));
        assertEquals(4, student.getTotalCredits());
        assertEquals(1, course.getCurrentEnrollment());
    }

    @Test
    void shouldRejectDuplicateRegistration() {
        Student student = new Student("S101", "Anita");
        Course course = new Course(
                "CS301", "Data Structures", 4, 30, true, null);

        RegistrationService service = new RegistrationService();

        assertTrue(service.register(student, course));
        boolean secondAttempt = service.register(student, course);

        assertFalse(secondAttempt);
        assertEquals(1, student.getRegisteredCourses().size());
        assertEquals(4, student.getTotalCredits());
        assertEquals(1, course.getCurrentEnrollment());
    }

    @Test
    void shouldRejectRegistrationWhenCreditLimitIsExceeded() {
        Student student = new Student("S101", "Anita");

        // Preserve the original test's 20-credit setup.
        student.addRegistration("CS101", 4);
        student.addRegistration("CS102", 4);
        student.addRegistration("CS201", 4);
        student.addRegistration("CS202", 4);
        student.addRegistration("CS203", 4);

        Course course = new Course(
                "CS301", "Data Structures", 4, 30, true, null);

        RegistrationService service = new RegistrationService();
        boolean result = service.register(student, course);

        assertFalse(result);
        assertFalse(student.isRegistered("CS301"));
        assertEquals(20, student.getTotalCredits());
        assertEquals(0, course.getCurrentEnrollment());
    }

    @Test
    void shouldRejectRegistrationWhenCourseIsAtCapacity() {
        Student student = new Student("S201", "Ravi");
        Course course = new Course(
                "CS501", "Networks", 3, 2, true, null);

        course.incrementEnrollment();
        course.incrementEnrollment();

        RegistrationService service = new RegistrationService();

        assertFalse(service.register(student, course));
        assertFalse(student.isRegistered("CS501"));
        assertEquals(0, student.getTotalCredits());
        assertEquals(2, course.getCurrentEnrollment());
    }

    @Test
    void shouldAllowRegistrationForLastAvailableSeat() {
        Student student = new Student("S202", "Priya");
        Course course = new Course(
                "CS502", "Databases", 3, 2, true, null);

        course.incrementEnrollment();

        RegistrationService service = new RegistrationService();

        assertTrue(service.register(student, course));
        assertTrue(student.isRegistered("CS502"));
        assertEquals(3, student.getTotalCredits());
        assertEquals(2, course.getCurrentEnrollment());
    }

    @Test
    void shouldAllowFifthCourseWhenOtherRulesPass() {
        Student student = new Student("S203", "Arun");
        RegistrationService service = new RegistrationService();

        for (int i = 1; i <= 4; i++) {
            Course existing = new Course(
                    "OLD" + i, "Existing " + i, 3, 10, true, null);

            assertTrue(service.register(student, existing));
        }

        Course fifth = new Course(
                "CS505", "Fifth Course", 3, 10, true, null);

        assertTrue(service.register(student, fifth));
        assertEquals(5, student.getRegisteredCourses().size());
        assertEquals(15, student.getTotalCredits());
        assertEquals(1, fifth.getCurrentEnrollment());
    }

    @Test
    void shouldRejectSixthCourseWithoutChangingState() {
        Student student = new Student("S204", "Meena");
        RegistrationService service = new RegistrationService();

        for (int i = 1; i <= 5; i++) {
            Course existing = new Course(
                    "EX" + i, "Existing " + i, 3, 10, true, null);

            assertTrue(service.register(student, existing));
        }

        Course sixth = new Course(
                "CS506", "Sixth Course", 3, 10, true, null);

        assertFalse(service.register(student, sixth));
        assertEquals(5, student.getRegisteredCourses().size());
        assertEquals(15, student.getTotalCredits());
        assertFalse(student.isRegistered("CS506"));
        assertEquals(0, sixth.getCurrentEnrollment());
    }
}