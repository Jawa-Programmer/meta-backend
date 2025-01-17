package ru.dozen.mephi.meta.repository;

import ru.dozen.mephi.meta.domain.RoleRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRecordsRepository extends JpaRepository<RoleRecord, Long> {

}
