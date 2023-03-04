package org.examemulator.domain;

import java.util.Optional;
import java.util.stream.Stream;

import jakarta.data.repository.CrudRepository;
import jakarta.data.repository.Repository;

// https://vladmihalcea.com/best-spring-data-jparepository/
@Repository
public class ExamGroupRepository implements CrudRepository<ExamGroup, Long> {

    @Override
    public <S extends ExamGroup> S save(S entity) {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public <S extends ExamGroup> Iterable<S> saveAll(Iterable<S> entities) {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public Optional<ExamGroup> findById(Long id) {
	// TODO Auto-generated method stub
	return Optional.empty();
    }

    @Override
    public boolean existsById(Long id) {
	// TODO Auto-generated method stub
	return false;
    }

    @Override
    public Stream<ExamGroup> findAll() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public Stream<ExamGroup> findAllById(Iterable<Long> ids) {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public long count() {
	// TODO Auto-generated method stub
	return 0;
    }

    @Override
    public void deleteById(Long id) {
	// TODO Auto-generated method stub
	
    }

    @Override
    public void delete(ExamGroup entity) {
	// TODO Auto-generated method stub
	
    }

    @Override
    public void deleteAllById(Iterable<Long> ids) {
	// TODO Auto-generated method stub
	
    }

    @Override
    public void deleteAll(Iterable<? extends ExamGroup> entities) {
	// TODO Auto-generated method stub
	
    }

    @Override
    public void deleteAll() {
	// TODO Auto-generated method stub
	
    }
}
