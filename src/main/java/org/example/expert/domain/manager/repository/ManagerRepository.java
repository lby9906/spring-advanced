package org.example.expert.domain.manager.repository;

import org.example.expert.domain.manager.entity.Manager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ManagerRepository extends JpaRepository<Manager, Long> {
    @Query("SELECT m FROM Manager m JOIN FETCH m.user WHERE m.todo.id = :todoId")
    List<Manager> findByTodoIdWithUser(@Param("todoId") Long todoId);

    @Query("SELECT m FROM Manager m JOIN FETCH m.todo WHERE m.id = :managerId AND  m.todo.id = :todoId")
    Optional<Manager> findByIdAndTodoId(@Param("managerId") Long managerId, @Param("todoId") Long todoId);
}
