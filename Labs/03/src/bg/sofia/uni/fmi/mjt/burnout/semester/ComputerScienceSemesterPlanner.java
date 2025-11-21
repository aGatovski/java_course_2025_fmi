package bg.sofia.uni.fmi.mjt.burnout.semester;

import bg.sofia.uni.fmi.mjt.burnout.exception.CryToStudentsDepartmentException;
import bg.sofia.uni.fmi.mjt.burnout.exception.InvalidSubjectRequirementsException;
import bg.sofia.uni.fmi.mjt.burnout.plan.SemesterPlan;
import bg.sofia.uni.fmi.mjt.burnout.subject.SubjectRequirement;
import bg.sofia.uni.fmi.mjt.burnout.subject.UniversitySubject;

import java.util.Objects;

public final class ComputerScienceSemesterPlanner extends AbstractSemesterPlanner {

    private void sortUniversitySubjectsOnRatings(UniversitySubject[] universitySubjects) {
        if (Objects.isNull(universitySubjects) || universitySubjects.length == 0) {
            throw new IllegalArgumentException("University subjects array is empty!");
        }

        for (int i = 1; i < universitySubjects.length; ++i) {
            UniversitySubject key = universitySubjects[i];
            int j = i - 1;

            while (j >= 0 && universitySubjects[j].rating() < key.rating()) {
                universitySubjects[j + 1] = universitySubjects[j];
                j = j - 1;
            }

            universitySubjects[j + 1] = key;
        }
    }

    private boolean[] selectRequiredSubjects(SubjectRequirement[] subjectRequirements,
                                             UniversitySubject[] subjects) {
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
        sortUniversitySubjectsOnRatings(semesterPlan.subjects());

        boolean[] selectedSubjects =
            selectRequiredSubjects(semesterPlan.subjectRequirements(), semesterPlan.subjects());
        //vs koito im trqbvat dori da ne pokrivat credite si
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
