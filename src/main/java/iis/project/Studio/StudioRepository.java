package iis.project.Studio;

import iis.project.User.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudioRepository extends JpaRepository<Studio,Long> {
    @Query("SELECT s.users FROM Studio s WHERE s.id = :studioId")
    List<User> findUsersByStudioId(@Param("studioId") Long studioId);
}
