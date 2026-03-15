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

    private void sortUniversitySubjectsOnCredits(UniversitySubject[] universitySubjects) {
        if (Objects.isNull(universitySubjects) || universitySubjects.length == 0) {
            throw new IllegalArgumentException("University subjects array is empty!");
        }

        //sorting based on credits descending
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

    private boolean[] selectRequiredSubjects(SubjectRequirement[] subjectRequirements, UniversitySubject[] subjects) {
        boolean[] selectedSubjects = new boolean[subjects.length];

        for (SubjectRequirement subjectRequirement : subjectRequirements) {
            int minAmountEnrolled = subjectRequirement.minAmountEnrolled();

            for (int i = 0; i < subjects.length; i++) {
                if (subjects[i].category() == subjectRequirement.category() && minAmountEnrolled > 0) {
                    selectedSubjects[i] = true;
                    minAmountEnrolled--;
                }
            }
        }

        return selectedSubjects;
    }

    @Override
    public UniversitySubject[] calculateSubjectList(SemesterPlan semesterPlan)
        throws InvalidSubjectRequirementsException {

        validateSemesterPlan(semesterPlan);
        sortUniversitySubjectsOnCredits(semesterPlan.subjects());

        boolean[] selectedSubjects =
            selectRequiredSubjects(semesterPlan.subjectRequirements(), semesterPlan.subjects());

        //Check if selected subjects meet minimal subject requirement
        if (!checkMinimalSubjectsRequiredMet(selectedSubjects, semesterPlan.subjectRequirements(),
            semesterPlan.subjects())) {
            throw new CryToStudentsDepartmentException("Cannot cover minimal amount of subjects");
        }

        int earnedCredits = getTotalEarnedCredits(selectedSubjects, semesterPlan.subjects());

        //Check if minimal subject earned credits meet required amount
        if (earnedCredits < semesterPlan.minimalAmountOfCredits()) {
            //Check if you can meet required credits
            if (!resolveRequiredCreditsAmount(selectedSubjects, semesterPlan.subjects(), earnedCredits,
                semesterPlan.minimalAmountOfCredits())) {
                throw new CryToStudentsDepartmentException("Cannot meet required amount of credits for semester!");
            }
        }

        return getSelectedSemesterPlan(selectedSubjects, semesterPlan.subjects());
    }
}



