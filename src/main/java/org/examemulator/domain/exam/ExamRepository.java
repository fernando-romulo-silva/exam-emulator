package org.examemulator.domain.exam;

import org.examemulator.util.domain.GenericRepository;

import jakarta.data.repository.Repository;
import jakarta.enterprise.context.ApplicationScoped;

@Repository
@ApplicationScoped
public class ExamRepository extends GenericRepository<Exam, Long> {


}
