package bg.sofia.uni.fmi.mjt.burnout.semester;

import bg.sofia.uni.fmi.mjt.burnout.exception.CryToStudentsDepartmentException;
import bg.sofia.uni.fmi.mjt.burnout.exception.InvalidSubjectRequirementsException;
import bg.sofia.uni.fmi.mjt.burnout.plan.SemesterPlan;
import bg.sofia.uni.fmi.mjt.burnout.subject.SubjectRequirement;
import bg.sofia.uni.fmi.mjt.burnout.subject.UniversitySubject;

import java.util.Objects;

public final class ComputerScienceSemesterPlanner extends AbstractSemesterPlanner {

    private void validateSemesterPlan(SemesterPlan semesterPlan) throws InvalidSubjectRequirementsException {
        if (Objects.isNull(semesterPlan)) {
            throw new IllegalArgumentException("Semester plan is missing or null!");
        }
        // to be fixed and moved out of this method since its validation
        for (int i = 0; i < semesterPlan.subjectRequirements().length - 1; i++) {
            for (int j = i + 1; j < semesterPlan.subjectRequirements().length; j++) {
                if (semesterPlan.subjectRequirements()[i].equals(semesterPlan.subjectRequirements()[j])) {
                    throw new InvalidSubjectRequirementsException("There is a duplicate subject requirement!");
                }
            }
        }
    }

    private void sortUniversitySubjectsOnRatings(UniversitySubject[] universitySubjects) {
        if (Objects.isNull(universitySubjects) || universitySubjects.length == 0) {
            throw new IllegalArgumentException("University subjects array is empty!");
        }

        //sorting based on ratings
        for (int i = 1; i < universitySubjects.length; ++i) {
            UniversitySubject key = universitySubjects[i];
            int j = i - 1;

            while (j >= 0 && universitySubjects[j].rating() > key.rating()) {
                universitySubjects[j + 1] = universitySubjects[j];
                j = j - 1;
            }
            universitySubjects[j + 1] = key;
        }

    }

    private int getComputerScienceSemesterSubjectsCount(boolean[] selectedSubjects) {
        int finalSubjectCount = 0;
        for (int i = 0; i < selectedSubjects.length; i++) {
            if (selectedSubjects[i]) {
                finalSubjectCount++;
            }
        }
        return finalSubjectCount;
    }

    private UniversitySubject[] copyUniversitySubjects(boolean[] selectedSubjects,
                                                       UniversitySubject[] universitySubjects) {
        UniversitySubject[] computerScienceSemesterSubjects =
            new UniversitySubject[getComputerScienceSemesterSubjectsCount(selectedSubjects)];
        for (int i = 0; i < selectedSubjects.length; i++) {
            if (selectedSubjects[i]) {
                computerScienceSemesterSubjects[i] = universitySubjects[i];
            }
        }
        return computerScienceSemesterSubjects;
    }

    @Override
    public UniversitySubject[] calculateSubjectList(SemesterPlan semesterPlan)
        throws InvalidSubjectRequirementsException {

        validateSemesterPlan(semesterPlan);
        sortUniversitySubjectsOnRatings(semesterPlan.subjects());

        int minCreditsRequired = semesterPlan.minimalAmountOfCredits();
        boolean[] visitedSubjects = new boolean[semesterPlan.subjects().length];

        for (SubjectRequirement subjectRequirement : semesterPlan.subjectRequirements()) {
            int minAmountEnrolled = subjectRequirement.minAmountEnrolled();

            //Check if minimal required subjects are met if not throw crytostudentsdep exception
            if (!checkMinimalSubjectsRequiredMet(minAmountEnrolled, minCreditsRequired, visitedSubjects,
                subjectRequirement, semesterPlan.subjects())) {
                throw new CryToStudentsDepartmentException("Cannot cover minimal amount of subjects");
            }

        }

        return copyUniversitySubjects(visitedSubjects, semesterPlan.subjects());
    }
}
