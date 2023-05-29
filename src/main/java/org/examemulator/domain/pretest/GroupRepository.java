package org.examemulator.domain.pretest;

import java.util.Optional;
import java.util.stream.Stream;

import jakarta.data.repository.CrudRepository;
import jakarta.data.repository.Repository;

// https://vladmihalcea.com/best-spring-data-jparepository/
@Repository
public class GroupRepository implements CrudRepository<PreGroup, Long> {

    @Override
    public <S extends PreGroup> S save(S entity) {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public <S extends PreGroup> Iterable<S> saveAll(Iterable<S> entities) {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public Optional<PreGroup> findById(Long id) {
	// TODO Auto-generated method stub
	return Optional.empty();
    }

    @Override
    public boolean existsById(Long id) {
	// TODO Auto-generated method stub
	return false;
    }

    @Override
    public Stream<PreGroup> findAll() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public Stream<PreGroup> findAllById(Iterable<Long> ids) {
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
    public void delete(PreGroup entity) {
	// TODO Auto-generated method stub
	
    }

    @Override
    public void deleteAllById(Iterable<Long> ids) {
	// TODO Auto-generated method stub
	
    }

    @Override
    public void deleteAll(Iterable<? extends PreGroup> entities) {
	// TODO Auto-generated method stub
	
    }

    @Override
    public void deleteAll() {
	// TODO Auto-generated method stub
	
    }
}
