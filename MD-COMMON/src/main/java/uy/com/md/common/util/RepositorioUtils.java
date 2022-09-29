package uy.com.md.common.util;

import java.util.Date;

import org.springframework.data.jpa.domain.Specification;

public class RepositorioUtils{
	public static <E> Specification<E> startPeriod(Class<E> type, String nameAttribute, Date valueAttribute){
		return (root, query, cb) -> { return cb.lessThanOrEqualTo(root.get(nameAttribute), valueAttribute); };
	}

	public static <E> Specification<E> endPeriod(Class<E> type, String nameAttribute, Date valueAttribute){
		return (root, query, cb) -> { return cb.or(cb.greaterThanOrEqualTo(root.get(nameAttribute), valueAttribute), cb.isNull(root.get(nameAttribute))); };
	}

	public static <E> Specification<E> excludeDeleted(Class<E> type){
		return (root, query, cb) -> { return cb.isNull(root.get("fbaja")); };
	}

	public static <E> Specification<E> attributeEqual(Class<E> type, String attribute, Object valueAttribute){
		return (root, query, cb) -> { return cb.equal(root.get(attribute), valueAttribute); };
	}
	
	public static <E> Specification<E> attributeIsNull(Class<E> type, String attribute){
		return (root, query, cb) -> { return cb.isNull(root.get(attribute)); };
	}
}
