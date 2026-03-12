package com.tejas.metlife.actifytask.repository;

import com.tejas.metlife.actifytask.model.UserTask;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserTaskRepository extends JpaRepository<UserTask, Long> {
    List<UserTask> findByAssignedToEmail(String email);
}
