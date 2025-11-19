package bg.sofia.uni.fmi.mjt.burnout.semester;

import bg.sofia.uni.fmi.mjt.burnout.exception.CryToStudentsDepartmentException;
import bg.sofia.uni.fmi.mjt.burnout.exception.InvalidSubjectRequirementsException;
import bg.sofia.uni.fmi.mjt.burnout.plan.SemesterPlan;
import bg.sofia.uni.fmi.mjt.burnout.subject.UniversitySubject;
import bg.sofia.uni.fmi.mjt.burnout.subject.SubjectRequirement;

import java.util.Objects;

/*
SoftwareEngineeringSemesterPlanner където стратегията при записването
на предметите ще е да се минимизират броя предмети, с които да се покрият изискванията за семестъра.
*/

public final class SoftwareEngineeringSemesterPlanner extends AbstractSemesterPlanner {

    private UniversitySubject[] copyUniversitySubjects(boolean[] selectedSubjects,
                                                       UniversitySubject[] universitySubjects) {
        UniversitySubject[] softwareEngineeringSemesterSubjects =
            new UniversitySubject[getSoftwareEngineeringSemesterSubjectsCount(selectedSubjects)];
        for (int i = 0; i < selectedSubjects.length; i++) {
            if (selectedSubjects[i]) {
                softwareEngineeringSemesterSubjects[i] = universitySubjects[i];
            }
        }
        return softwareEngineeringSemesterSubjects;
    }

    private int getSoftwareEngineeringSemesterSubjectsCount(boolean[] selectedSubjects) {
        int finalSubjectCount = 0;
        for (int i = 0; i < selectedSubjects.length; i++) {
            if (selectedSubjects[i]) {
                finalSubjectCount++;
            }
        }
        return finalSubjectCount;
    }

    private boolean checkMinimalSubjectsCreditsMet(int minimalAmountOfCredits, boolean[] selectedSubjects,
                                                   UniversitySubject[] universitySubjects) {
        int index = 0;
        while (minimalAmountOfCredits > 0 && index != universitySubjects.length) {
            if (!selectedSubjects[index]) {
                selectedSubjects[index] = true;
                minimalAmountOfCredits -= universitySubjects[index].credits();
            }
            index++;
        }

        if (minimalAmountOfCredits > 0) {
            return false;
        }
        return true;
    }

    private void sortUniversitySubjectsOnCredits(UniversitySubject[] universitySubjects) {
        if (Objects.isNull(universitySubjects) || universitySubjects.length == 0) {
            throw new IllegalArgumentException("University subjects array is empty!");
        }

        //sorting based on credits
        for (int i = 1; i < universitySubjects.length; ++i) {
            UniversitySubject key = universitySubjects[i];
            int j = i - 1;

            while (j >= 0 && universitySubjects[j].credits() < key.credits()) {
                universitySubjects[j + 1] = universitySubjects[j];
                j = j - 1;
            }
            universitySubjects[j + 1] = key;
        }

    }

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

    @Override
    public UniversitySubject[] calculateSubjectList(SemesterPlan semesterPlan)
        throws InvalidSubjectRequirementsException {

        validateSemesterPlan(semesterPlan);
        sortUniversitySubjectsOnCredits(semesterPlan.subjects());

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
        if (!checkMinimalSubjectsCreditsMet(minCreditsRequired, visitedSubjects, semesterPlan.subjects())) {
            throw new CryToStudentsDepartmentException("Cannot cover minimal credits for semester!");
        }

        return copyUniversitySubjects(visitedSubjects, semesterPlan.subjects());
    }
}



