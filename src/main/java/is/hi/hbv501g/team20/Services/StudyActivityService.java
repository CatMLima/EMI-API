package is.hi.hbv501g.team20.Services;

import is.hi.hbv501g.team20.Persistence.Entities.StudyActivity;

import java.util.List;

public interface StudyActivityService {
    StudyActivity save (StudyActivity studyActivity);
    void delete (StudyActivity studyActivity);
    void deleteAll();
    boolean existsById(Long id);
    StudyActivity findById(long id);
    List<StudyActivity> findAll();
}
