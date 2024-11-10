package is.hi.hbv501g.team20.Services;

import is.hi.hbv501g.team20.Persistence.Entities.StudyGroup;
import is.hi.hbv501g.team20.Persistence.Entities.User;

import java.util.List;

public interface StudyGroupService {

    StudyGroup save(StudyGroup studyGroup);
    void delete(StudyGroup studyGroup);
    StudyGroup findById(long id);
    List<StudyGroup> findByUserId(long id);
    List<StudyGroup> findAllExceptUser(long id);
    List<StudyGroup> findAll();
    void removeUserFromStudyGroups(User user);

}
