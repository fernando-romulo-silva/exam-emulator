package org.examemulator.infra.util.domain;

import java.lang.reflect.ParameterizedType;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import jakarta.data.repository.CrudRepository;
import jakarta.data.repository.Repository;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

// https://vladmihalcea.com/best-spring-data-jparepository/
@Repository
public abstract class GenericRepository<T, K> implements CrudRepository<T, K> {

    @Inject
    protected EntityManager entityManager;

    @SuppressWarnings("unchecked")
    private Class<T> getEntityClazz() {
	return ((Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0]);
    }

    @Override
    public <S extends T> S save(final S entity) {

	if (entityManager.contains(entity)) {
	    return entityManager.merge(entity);    
	}
	
	entityManager.persist(entity);

	return entity;
    }
    
    public <S extends T> S update(final S entity) {

	return entityManager.merge(entity);
    }
    
    @Override
    public <S extends T> Iterable<S> saveAll(final Iterable<S> entities) {

	for (final var s : entities) {
	    entityManager.merge(s);
	}

	return entities;
    }

    @Override
    public Optional<T> findById(final K id) {

	final var entityClazz = getEntityClazz();

	try {
	    return Optional.of(entityManager.find(entityClazz, id));
	} catch (final IllegalArgumentException ex) {
	    return Optional.empty();
	}
    }

    @Override
    public boolean existsById(final K id) {

	final var entityClazz = getEntityClazz();

	return Objects.nonNull(entityManager.getReference(entityClazz, id));
    }

    @Override
    public Stream<T> findAll() {

	final var entityClazz = getEntityClazz();

	final var query = entityManager.createQuery("select o from ".concat(entityClazz.getSimpleName()).concat(" o"), entityClazz);

	return query.getResultStream();
    }

    @Override
    public Stream<T> findAllById(final Iterable<K> ids) {
	final var entityClazz = getEntityClazz();

	final var sqlString = "select o from ".concat(entityClazz.getSimpleName()).concat(" o where id in (:ids)");

	final var query = entityManager.createQuery(sqlString, entityClazz).setParameter("ids", ids);

	return query.getResultStream();
    }

    @Override
    public long count() {

	final var entityClazz = getEntityClazz();

	final var query = entityManager.createQuery("select count(*) from ".concat(entityClazz.getSimpleName()).concat(" o"), Long.class);

	return query.getSingleResult();
    }

    @Override
    public void deleteById(final K id) {

	final var entityClazz = getEntityClazz();

	final var entity = entityManager.find(entityClazz, id);

	entityManager.remove(entity);
    }

    @Override
    public void delete(final T entity) {
	entityManager.remove(entity);
    }

    @Override
    public void deleteAllById(final Iterable<K> ids) {
	final var entityClazz = getEntityClazz();

	final var queryString = "delete from ".concat(entityClazz.getSimpleName()).concat(" o WHERE o.ids in (:ids)");

	final var query = entityManager.createQuery(queryString);
	query.setParameter("ids", ids);

	query.executeUpdate();
    }

    @Override
    public void deleteAll(final Iterable<? extends T> entities) {
	for (final var entity : entities) {
	    entityManager.remove(entity);
	}
    }

    @Override
    public void deleteAll() {
	final var entityClazz = getEntityClazz();

	final var queryString = "delete from ".concat(entityClazz.getSimpleName()).concat(" o ");
	
	final var query = entityManager.createQuery(queryString);

	query.executeUpdate();
    }

}
