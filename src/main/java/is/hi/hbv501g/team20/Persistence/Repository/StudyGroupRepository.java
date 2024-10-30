package is.hi.hbv501g.team20.Persistence.Repository;

import is.hi.hbv501g.team20.Persistence.Entities.StudyGroup;
import is.hi.hbv501g.team20.Persistence.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StudyGroupRepository extends JpaRepository<StudyGroup, Long> {
    StudyGroup save(StudyGroup studyGroup);
    void delete(StudyGroup studyGroup);
    StudyGroup findById(long id);
    List<StudyGroup> findAll();

    @Query("SELECT sg FROM StudyGroup sg INNER JOIN sg.members m WHERE m.id = :id")
    List<StudyGroup> findByUserId(long id);

//    @Query("SELECT sg FROM StudyGroup sg WHERE sg.id NOT IN " +
//            "(SELECT sgm.id FROM sg.members sgm WHERE sgm.id = :id)")

    @Query("SELECT sg FROM StudyGroup sg where sg.id NOT IN " +
            "(SELECT sg.id from sg.members sgm where sgm.id = :id)")
    List<StudyGroup> findAllExceptUser(long id);
    //select * from studygroup where id not in (select studygroup_id from studygroup_members where user_id = 2);
}
