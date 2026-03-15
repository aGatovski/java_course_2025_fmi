package bg.sofia.uni.fmi.mjt.burnout.semester;

import bg.sofia.uni.fmi.mjt.burnout.exception.DisappointmentException;
import bg.sofia.uni.fmi.mjt.burnout.exception.InvalidSubjectRequirementsException;
import bg.sofia.uni.fmi.mjt.burnout.plan.SemesterPlan;
import bg.sofia.uni.fmi.mjt.burnout.subject.SubjectRequirement;
import bg.sofia.uni.fmi.mjt.burnout.subject.UniversitySubject;

import java.util.Objects;

public abstract sealed class AbstractSemesterPlanner implements SemesterPlannerAPI
    permits SoftwareEngineeringSemesterPlanner, ComputerScienceSemesterPlanner {

    private static final int GRANDMA_MULTIPLIER = 2;
    private static final int JAR_PER_DAY = 5;

    protected void validateSemesterPlan(SemesterPlan semesterPlan) throws InvalidSubjectRequirementsException {
        if (Objects.isNull(semesterPlan)) {
            throw new IllegalArgumentException("Semester plan is missing or null!");
        }

        for (int i = 0; i < semesterPlan.subjectRequirements().length - 1; i++) {
            for (int j = i + 1; j < semesterPlan.subjectRequirements().length; j++) {
                if (semesterPlan.subjectRequirements()[i].equals(semesterPlan.subjectRequirements()[j])) {
                    throw new InvalidSubjectRequirementsException("There is a duplicate subject requirement!");
                }
            }
        }
    }

    protected int getSelectedSubjectsCount(boolean[] selectedSubjects) {
        int finalSubjectCount = 0;

        for (boolean selectedSubject : selectedSubjects) {
            if (selectedSubject) {
                finalSubjectCount++;
            }
        }
        return finalSubjectCount;
    }

    protected UniversitySubject[] getSelectedSemesterPlan(boolean[] selectedSubjects,
                                                          UniversitySubject[] universitySubjects) {
        UniversitySubject[] selectedSemesterPlan =
            new UniversitySubject[getSelectedSubjectsCount(selectedSubjects)];

        int semesterPlanIter = 0;

        for (int i = 0; i < selectedSubjects.length; i++) {
            if (selectedSubjects[i]) {
                selectedSemesterPlan[semesterPlanIter++] = universitySubjects[i];
            }
        }

        return selectedSemesterPlan;
    }

    protected boolean checkMinimalSubjectsRequiredMet(boolean[] selectedSubjects,
                                                      SubjectRequirement[] subjectRequirements,
                                                      UniversitySubject[] universitySubjects) {

        for (SubjectRequirement subjectRequirement : subjectRequirements) {
            int minAmountEnrolled = subjectRequirement.minAmountEnrolled();

            for (int i = 0; i < selectedSubjects.length; i++) {
                if (selectedSubjects[i] && universitySubjects[i].category().equals(subjectRequirement.category())) {
                    minAmountEnrolled--;
                }
            }

            if (minAmountEnrolled > 0) {
                return false;
            }
        }

        return true;
    }

    protected int getTotalEarnedCredits(boolean[] selectedSubjects, UniversitySubject[] universitySubjects) {
        int creditsEarned = 0;

        for (int i = 0; i < selectedSubjects.length; i++) {
            if (selectedSubjects[i]) {
                creditsEarned += universitySubjects[i].credits();
            }
        }

        return creditsEarned;
    }

    protected boolean resolveRequiredCreditsAmount(boolean[] selectedSubjects, UniversitySubject[] universitySubjects,
                                                 int earnedCredits, int minimalAmountOfCredits) {

        for (int i = 0; i < selectedSubjects.length; i++) {
            if (earnedCredits < minimalAmountOfCredits) {
                if (!selectedSubjects[i]) {
                    selectedSubjects[i] = true;
                    earnedCredits += universitySubjects[i].credits();
                }
            } else {
                break;
            }
        }

        return earnedCredits >= minimalAmountOfCredits;
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
