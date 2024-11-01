package is.hi.hbv501g.team20.Persistence.Repository;

import is.hi.hbv501g.team20.Persistence.Entities.Post;
import is.hi.hbv501g.team20.Persistence.Entities.StudyGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    void delete(Post post);
    Post save(Post post);
    List<Post> findAll();

    @Query("SELECT p FROM Post p WHERE p.studygroup = :studygroup")
    List<Post> findByStudyGroup(@Param("studygroup") StudyGroup studyGroup);
}
