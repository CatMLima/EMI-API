package is.hi.hbv501g.team20.Services.Implementations;

import is.hi.hbv501g.team20.Persistence.Entities.Post;
import is.hi.hbv501g.team20.Persistence.Entities.StudyGroup;
import is.hi.hbv501g.team20.Persistence.Repository.PostRepository;
import is.hi.hbv501g.team20.Services.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostServiceImplementation implements PostService {

    @Autowired
    PostRepository postRepo;

    @Override
    public Post save(Post post) { return postRepo.save(post); }

    @Override
    public void delete(Post post) {
        postRepo.delete(post);
    }

    @Override
    public List<Post> findAll() {
        return postRepo.findAll();
    }

    @Override
    public List<Post> findByStudyGroup(StudyGroup studyGroup) {
        return postRepo.findByStudyGroup(studyGroup);
    }
}
