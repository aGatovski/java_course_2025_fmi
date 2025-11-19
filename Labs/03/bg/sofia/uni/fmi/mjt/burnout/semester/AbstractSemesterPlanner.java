package bg.sofia.uni.fmi.mjt.burnout.semester;

import bg.sofia.uni.fmi.mjt.burnout.exception.DisappointmentException;
import bg.sofia.uni.fmi.mjt.burnout.subject.SubjectRequirement;
import bg.sofia.uni.fmi.mjt.burnout.subject.UniversitySubject;

public abstract sealed class AbstractSemesterPlanner implements SemesterPlannerAPI
    permits SoftwareEngineeringSemesterPlanner, ComputerScienceSemesterPlanner {

    private static final int GRANDMA_MULTIPLIER = 2;
    private static final int JAR_PER_DAY = 5;

    protected boolean checkMinimalSubjectsRequiredMet(int minSubjectRequirement, int minimalAmountOfCredits,
                                                    boolean[] selectedSubjects, SubjectRequirement subjectRequirement,
                                                    UniversitySubject[] universitySubjects) {

        int minAmountEnrolled = subjectRequirement.minAmountEnrolled();
        int index = 0;
        while (minAmountEnrolled > 0 && index != universitySubjects.length) {
            if (universitySubjects[index].category() == subjectRequirement.category()) {
                minAmountEnrolled--;
                selectedSubjects[index] = true; //
                minimalAmountOfCredits -= universitySubjects[index].credits(); //credits gained
            } else {
                index++;
            }
        }
        //Check if minimal required subjects are met if not throw crytostudentsdep exception
        if (minAmountEnrolled > 0) {
            return false;
        }
        return true;
    }

    public int calculateJarCount(UniversitySubject[] subjects, int maximumSlackTime, int semesterDuration) {
        if (subjects == null || subjects.length == 0) {
            throw new IllegalArgumentException("University subjects are null or missing!");
        }
        if (maximumSlackTime <= 0) {
            throw new IllegalArgumentException("Maximum slack time is not positive!");
        }
        if (semesterDuration <= 0) {
            throw new IllegalArgumentException("Semester duration is not positive!");
        }

        int jarCount;
        double neededRestTime = 0;
        int neededStudyTimeForSemester = 0;
        for (UniversitySubject subject : subjects) {
            neededRestTime += Math.ceil(subject.category().getCoef() * subject.neededStudyTime());
            neededStudyTimeForSemester += subject.neededStudyTime();
        }

        if (neededRestTime > maximumSlackTime) {
            throw new DisappointmentException("Grandma is disappointed! Too much rest time.");
        }

        jarCount = neededStudyTimeForSemester / JAR_PER_DAY;
        if (semesterDuration < neededStudyTimeForSemester) {
            jarCount *= GRANDMA_MULTIPLIER;
        }

        return jarCount;
    }
}
