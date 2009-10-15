/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: Dec 8, 2008
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.business.validation;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateTemplate;

import com.kesdip.business.exception.InvalidEntityIdentifierException;
import com.kesdip.business.util.Errors;
import com.kesdip.common.util.BeanUtils;
import com.kesdip.common.util.StringUtils;

/**
 * Base class for all validators. Provides utility methods for descendants.
 * 
 * @author gerogias
 */
public abstract class BaseValidator implements Validator {

	/**
	 * The logger.
	 */
	private final static Logger logger = Logger.getLogger(BaseValidator.class);

	/**
	 * The Hibernate template to use for queries.
	 */
	protected HibernateTemplate hibernateTemplate = null;

	/**
	 * Checks if the field is null or empty.
	 * 
	 * @param value
	 *            the value
	 * @param fieldName
	 *            the name of the field
	 * @param errors
	 *            to populate with the message
	 * @return boolean <code>true</code> if the field is empty/<code>null</code>
	 */
	protected final boolean checkNullOrEmpty(String value, String fieldName,
			Errors errors) {
		if (StringUtils.isEmpty(value)) {
			if (logger.isDebugEnabled()) {
				logger.debug(fieldName + " is empty");
			}
			errors.addError(fieldName, "error.field.empty");
			return true;
		}
		return false;
	}

	/**
	 * Checks if the field is null.
	 * 
	 * @param value
	 *            the value
	 * @param fieldName
	 *            the name of the field
	 * @param errors
	 *            to populate with the message
	 * @return boolean <code>true</code> if the field is <code>null</code>
	 */
	protected final boolean checkNull(Object value, String fieldName,
			Errors errors) {
		if (value == null) {
			if (logger.isDebugEnabled()) {
				logger.debug(fieldName + " is null");
			}
			errors.addError(fieldName, "error.field.empty");
			return true;
		}
		return false;
	}

	/**
	 * Checks if the first date field is not equal or greater than the second.
	 * 
	 * @param startDate
	 *            the first field
	 * @param endDate
	 *            the second field
	 * @param fieldName
	 *            the name of the field to receive the error message
	 * @param errors
	 *            to populate with the message
	 * @return boolean <code>true</code> if the first date if greater/equal to
	 *         the second
	 */
	protected final boolean checkDateNotGreater(Date startDate, Date endDate,
			String fieldName, Errors errors) {
		if (startDate.after(endDate) || startDate.equals(endDate)) {
			if (logger.isDebugEnabled()) {
				logger.debug(fieldName + " has " + startDate + " > " + endDate);
			}
			errors.addError(fieldName, "error.invalid.dateRange");
			return true;
		}
		return false;
	}

	/**
	 * Checks if the given date field is in the future.
	 * 
	 * @param date
	 *            the date field
	 * @param fieldName
	 *            the name of the field to receive the error message
	 * @param errors
	 *            to populate with the message
	 * @return boolean <code>true</code> if the date is in the future
	 */
	protected final boolean checkDateInTheFuture(Date date, String fieldName,
			Errors errors) {
		Date now = new Date();
		if (date.after(now)) {
			if (logger.isDebugEnabled()) {
				logger.debug(fieldName + " has future date " + date);
			}
			errors.addError(fieldName, "error.date.future");
			return true;
		}
		return false;
	}

	/**
	 * Checks that the field size is not greater than the given one,
	 * non-inclusive.
	 * 
	 * @param value
	 *            the value to check
	 * @param fieldName
	 *            the name of the field
	 * @param length
	 *            the max length
	 * @param errors
	 *            to populate with the message
	 * @return <code>true</code> if the length is greater than specified
	 */
	protected final boolean checkLengthNotGreaterThan(String value,
			String fieldName, int length, Errors errors) {
		if (value != null && value.length() > length) {
			if (logger.isDebugEnabled()) {
				logger.debug(fieldName + " is longer than " + length);
			}
			errors.addError(fieldName, "error.field.tooLong");
			return true;
		}
		return false;
	}

	/**
	 * Checks that the number is greater than zero.
	 * 
	 * @param number
	 *            the number to check
	 * @param fieldName
	 *            the name of the field
	 * @param errors
	 *            to populate with the message
	 * @return boolean <code>true</code> if the number is 0/negative
	 */
	protected final boolean checkPositive(Number number, String fieldName,
			Errors errors) {
		if (number.doubleValue() <= 0) {
			if (logger.isDebugEnabled()) {
				logger.debug(fieldName + " is negative " + number);
			}
			errors.addError(fieldName, "error.field.negative");
			return true;
		}
		return false;
	}

	/**
	 * Checks that the date is in the past.
	 * 
	 * @param date
	 *            the date
	 * @param fieldName
	 *            the name of the field being checked
	 * @param errors
	 *            the errors object
	 * @return boolean <code>true</code> if the date is not in the past
	 */
	protected final boolean checkDateInThePast(Date date, String fieldName,
			Errors errors) {
		Date now = new Date();
		if (now.compareTo(date) < 0) {
			if (logger.isDebugEnabled()) {
				logger.debug(fieldName + " has past date " + date);
			}
			errors.addError(fieldName, "error.date.future");
			return true;
		}
		return false;
	}

	/**
	 * Checks if the given table.column has the given value (case-ignore). The
	 * query ignores the entities which have the given primary key.
	 * 
	 * @param table
	 * @param field
	 * @param value
	 *            may be <code>null</code>
	 * @param primaryKey
	 *            (optional) if included, filters out entities wit the given
	 *            primary key (ID)
	 * @return boolean <code>false</code> if it does not exist or the value is
	 *         <code>null</code>
	 */
	protected final boolean fieldExistsCaseIgnore(String table, String column,
			String value, Long primaryKey) {
		if (value == null) {
			logger.debug("Value is null");
			return false;
		}
		Session session = hibernateTemplate.getSessionFactory()
				.getCurrentSession();
		StringBuffer qString = new StringBuffer();
		qString.append("SELECT * FROM ").append(table).append(" WHERE UPPER(")
				.append(table).append('.').append(column).append(") = ?");
		if (primaryKey != null) {
			qString.append(" AND ").append(table).append('.').append("ID <> ?");
		}
		SQLQuery query = session.createSQLQuery(qString.toString());
		query.setString(0, value.toUpperCase().trim());
		if (primaryKey != null) {
			query.setLong(1, primaryKey);
		}
		if (logger.isDebugEnabled() && query.list().size() != 0) {
			logger.debug(table + "." + column + " has value " + value
					+ ". Primary key " + primaryKey);
		}
		return query.list().size() != 0;
	}

	/**
	 * Checks if the given table.column has the given value (case-ignore) having
	 * the same table.foreignKey (= parent). The query ignores the entities
	 * which have the given primary key.
	 * 
	 * @param table
	 * @param field
	 * @param value
	 *            may be <code>null</code>
	 * @param primaryKey
	 *            filters out entities with the given primary key (ID)
	 * @param foreignKeyColumn
	 *            only takes into account entities having the same foreignKey
	 *            column as the one with the primary key
	 * @return boolean <code>false</code> if it does not exist or the value is
	 *         <code>null</code>
	 */
	protected final boolean fieldExistsCaseIgnore(String table, String column,
			String value, Long primaryKey, String foreignKeyColumn) {
		if (value == null) {
			logger.debug("Value is null");
			return false;
		}
		Session session = hibernateTemplate.getSessionFactory()
				.getCurrentSession();
		StringBuffer qString = new StringBuffer();

		// select foreign key
		qString.append("SELECT ").append(table).append('.').append(
				foreignKeyColumn).append(" FROM ").append(table).append(
				" WHERE ID = ?");
		SQLQuery query = session.createSQLQuery(qString.toString());
		query.setLong(0, primaryKey);
		Long foreignKey = new Long(((Number) query.uniqueResult()).longValue());

		// search for similar values
		qString.delete(0, qString.length());
		qString.append("SELECT * FROM ").append(table).append(" WHERE UPPER(")
				.append(table).append('.').append(column).append(") = ?")
				.append(" AND ").append(table).append('.').append("ID <> ?")
				.append(" AND ").append(table).append('.').append(
						foreignKeyColumn).append(" = ?");
		query = session.createSQLQuery(qString.toString());
		query.setString(0, value.toUpperCase().trim());
		query.setLong(1, primaryKey);
		query.setLong(2, foreignKey);

		if (logger.isDebugEnabled() && query.list().size() != 0) {
			logger.debug(table + "." + column + " has value " + value
					+ ". Primary key " + primaryKey);
		}
		return query.list().size() != 0;
	}
	
	/**
	 * Checks if the given table.column has the given value (case-ignore) having
	 * the same table.foreignKey (= parent). The method intent is to be used when 
	 * creating an entity and checks need to be performed against existing entities
	 * with the same parent.
	 * 
	 * @param table
	 * @param field
	 * @param value
	 *            may be <code>null</code>
	 * @param foreignKeyColumn
	 *            the foreign key column
	 * @param foreignKeyValue
	 * 			  only takes into account entities having the same foreignKeyValue 
	 * @return boolean <code>false</code> if it does not exist or the value is
	 *         <code>null</code>
	 */
//	protected final boolean fieldExistsCaseIgnore(String table, String column,
//			String value, String foreignKeyColumn, Long foreignKeyValue) {
//		if (value == null) {
//			logger.debug("Value is null");
//			return false;
//		}
//		Session session = hibernateTemplate.getSessionFactory()
//				.getCurrentSession();
//		StringBuffer qString = new StringBuffer();
//
//		// search for similar values
//		qString.delete(0, qString.length());
//		qString.append("SELECT * FROM ").append(table).append(" WHERE UPPER(")
//				.append(table).append('.').append(column).append(") = ?")
//				.append(" AND ").append(table).append('.').append(
//						foreignKeyColumn).append(" = ?");
//		SQLQuery query = session.createSQLQuery(qString.toString());
//		query = session.createSQLQuery(qString.toString());
//		query.setString(0, value.toUpperCase().trim());
//		query.setLong(1, foreignKeyValue);
//
//		if (logger.isDebugEnabled() && query.list().size() != 0) {
//			logger.debug(table + "." + column + " has value " + value
//					+ ". Foreign key " + foreignKeyValue);
//		}
//		return query.list().size() != 0;
//	}

	/**
	 * Checks if the given table.column has the given value (case-ignore) having
	 * the same table.foreignKey (= parent).
	 * 
	 * @param table
	 * @param field
	 * @param value
	 *            may be <code>null</code>
	 * @param foreignKeyColumn
	 *            the foreignKey column
	 * @param foreignKey
	 *            the foreign key value
	 * @return boolean <code>false</code> if it does not exist or the value is
	 *         <code>null</code>
	 */
	protected final boolean fieldExistsCaseIgnore(String table, String column,
			String value, String foreignKeyColumn, Long foreignKey) {
		if (value == null) {
			logger.debug("Value is null");
			return false;
		}
		Session session = hibernateTemplate.getSessionFactory()
				.getCurrentSession();
		StringBuffer qString = new StringBuffer();

		// search for similar values
		qString.delete(0, qString.length());
		qString.append("SELECT * FROM ").append(table).append(" WHERE UPPER(")
				.append(table).append('.').append(column).append(") = ?")
				.append(" AND ").append(table).append('.').append(
						foreignKeyColumn).append(" = ?");
		SQLQuery query = session.createSQLQuery(qString.toString());
		query.setString(0, value.toUpperCase().trim());
		query.setLong(1, foreignKey);

		if (logger.isDebugEnabled() && query.list().size() != 0) {
			logger.debug(table + "." + column + " has value " + value
					+ ". Foreign key " + foreignKey);
		}
		return query.list().size() != 0;
	}

	/**
	 * Checks if the object with the given primary key has children. It checks
	 * the getter of the given field. Delegates to
	 * {@link #hasChildrenObj(Object, String)}.
	 * 
	 * @param clazz
	 *            the class of the target object
	 * @param key
	 *            the primary key of the class
	 * @param field
	 *            the field name
	 * @return boolean <code>false</code> if it does not have children or the
	 *         key does not exist
	 */
	protected final boolean hasChildrenKey(Class clazz, Serializable key,
			String field) {
		Object entity = retrieveEntity(clazz, key);

		if (entity == null) {
			logger.debug("Entity is null");
			return false;
		}

		return hasChildrenObj(entity, field);
	}

	/**
	 * Checks the <code>public Collection get[fieldName]()</code> method of
	 * the object. If it returns a non-empty collection, the object is assumed
	 * to have children.
	 * 
	 * @param obj
	 *            the object to check
	 * @param fieldName
	 *            name of the field
	 * @return boolean <code>true</code> if the object has children
	 */
	protected final boolean hasChildrenObj(Object obj, String fieldName) {

		try {
			Object result = BeanUtils.invokeGetter(obj, fieldName);
			if (!(result instanceof Collection)) {
				return false;
			}
			Collection children = (Collection) result;
			if (children.size() != 0) {
				return true;
			}
		} catch (Exception e) {
			return false;
		}
		return false;
	}

	/**
	 * Return the object having the given type and primary key.
	 * 
	 * @param clazz
	 * @param id
	 * @return Object the object or <code>null</code>
	 * @throws InvalidEntityIdentifierException
	 */
	protected final Object retrieveEntity(Class clazz, Serializable id) {
		Object obj = hibernateTemplate.get(clazz, id);
		return obj;
	}

	/**
	 * @return the hibernateTemplate
	 */
	public HibernateTemplate getHibernateTemplate() {
		return hibernateTemplate;
	}

	/**
	 * @param hibernateTemplate
	 *            the hibernateTemplate to set
	 */
	public void setHibernateTemplate(HibernateTemplate hibernateTemplate) {
		this.hibernateTemplate = hibernateTemplate;
	}

}
