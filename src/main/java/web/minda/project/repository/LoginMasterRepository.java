package web.minda.project.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PathVariable;
import jakarta.transaction.Transactional;
import web.minda.project.entity.LoginMaster;

public interface LoginMasterRepository extends JpaRepository<LoginMaster, Long> {

	Optional<LoginMaster> findByEmail(String email);
	
	
}
