package iis.project.Studio;

import iis.project.Studio.dto.StudioInfo;
import iis.project.User.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudioRepository extends JpaRepository<Studio,Long> {
    List<Studio> findAllByUsersContaining(User user);
    List<Studio> findAllByTeachersContaining(User user);
    List<Studio> findAllByManagerId(Long managerId);
}
