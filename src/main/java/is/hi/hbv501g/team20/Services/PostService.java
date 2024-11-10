package is.hi.hbv501g.team20.Services;

import is.hi.hbv501g.team20.Persistence.Entities.Post;
import is.hi.hbv501g.team20.Persistence.Entities.StudyGroup;
import is.hi.hbv501g.team20.Persistence.Entities.User;

import java.util.List;

public interface PostService {

    Post save(Post post);
    void delete(Post post);
    List<Post> findAll();
    List<Post> findByStudyGroup(StudyGroup studyGroup);
}
