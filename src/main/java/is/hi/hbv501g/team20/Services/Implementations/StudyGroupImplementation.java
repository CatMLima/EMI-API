package is.hi.hbv501g.team20.Services.Implementations;

import is.hi.hbv501g.team20.Persistence.Entities.StudyGroup;
import is.hi.hbv501g.team20.Persistence.Entities.User;
import is.hi.hbv501g.team20.Persistence.Repository.StudyGroupRepository;
import is.hi.hbv501g.team20.Persistence.Repository.UserRepository;
import is.hi.hbv501g.team20.Services.StudyGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudyGroupImplementation implements StudyGroupService {

    @Autowired
    StudyGroupRepository studyGroupRepo;

    @Autowired
    UserRepository userRepo;
    @Autowired
    private StudyGroupRepository studyGroupRepository;

    @Override
    public StudyGroup save(StudyGroup studyGroup) {return studyGroupRepo.save(studyGroup);}

    @Override
    public void delete(StudyGroup studyGroup) {
        studyGroupRepo.delete(studyGroup);
    }

    @Override
    public StudyGroup findById(long id) {
        return studyGroupRepo.findById(id);
    }

    @Override
    public List<StudyGroup> findByUserId(long id) { return studyGroupRepo.findByUserId(id); }

    @Override
    public List<StudyGroup> findAllExceptUser(long id) { return studyGroupRepo.findAllExceptUser(id); }

    @Override
    public List<StudyGroup> findAll() {
        return studyGroupRepo.findAll();
    }

    @Override
    public void removeUserFromStudyGroups(User user) {
        List<StudyGroup> studyGroups = user.getStudyGroupsMember();
        for (StudyGroup studyGroup : studyGroups) {
            studyGroup.removeMember(user);
        }
    }


}
