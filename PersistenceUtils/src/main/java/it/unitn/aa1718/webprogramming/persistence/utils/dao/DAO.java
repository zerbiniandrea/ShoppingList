/*
 * AA 2017-2018
 * Introduction to Web Programming
 * Commons - DAO exceptions
 * UniTN
 */
package it.unitn.aa1718.webprogramming.persistence.utils.dao;

import it.unitn.aa1718.webprogramming.persistence.utils.dao.exceptions.DAOException;
import it.unitn.aa1718.webprogramming.persistence.utils.dao.exceptions.DAOFactoryException;
import java.util.List;

/**
 * The basic DAO interface that all DAOs must implement.
 *
 * @param <ENTITY_CLASS> the class of the entity to handle.
 * @param <PRIMARY_KEY_CLASS> the class of the primary key of the entity the DAO
 * handle.
 * 
 * @author Stefano Chirico &lt;stefano dot chirico at unitn dot it&gt;
 * @since 2018.04.21
 */
public interface DAO<ENTITY_CLASS, PRIMARY_KEY_CLASS> {

    /**
     * Returns the number of records of {@code ENTITY_CLASS} stored on the
     * persistence system of the application.
     *
     * @return the number of records present into the storage system.
     * @throws DAOException if an error occurred during the information
     * retrieving.
     *
     * @author Stefano Chirico
     * @since 1.0.180421
     */
    public Long getCount() throws DAOException;

    /**
     * Returns the {@code ENTITY_CLASS} instance of the storage system record
     * with the primary key equals to the one passed as parameter.
     *
     * @param primaryKey the primary key used to obtain the entity instance.
     * @return the {@code ENTITY_CLASS} instance of the storage system record
     * with the primary key equals to the one passed as parameter or
     * {@code null} if no entities with that primary key is present into the
     * storage system.
     * @throws DAOException if an error occurred during the information
     * retrieving.
     *
     * @author Stefano Chirico
     * @since 1.0.180421
     */
    public ENTITY_CLASS getByPrimaryKey(PRIMARY_KEY_CLASS primaryKey) throws DAOException;

    /**
     * Returns the list of all the valid entities of type {@code ENTITY_CLASS}
     * stored by the storage system.
     *
     * @return the list of all the valid entities of type {@code ENTITY_CLASS}.
     * @throws DAOException if an error occurred during the information
     * retrieving.
     *
     * @author Stefano Chirico
     * @since 1.0.180421
     */
    public List<ENTITY_CLASS> getAll() throws DAOException;
    
    /**
     * If this DAO can interact with it, then returns the DAO of class passed as
     * parameter.
     *
     * @param <DAO_CLASS> the class name of the DAO that can interact with this
     * DAO.
     * @param daoClass the class of the DAO that can interact with this DAO.
     * @return the instance of the DAO or null if no DAO of the type passed as
     * parameter can interact with this DAO.
     * @throws DAOFactoryException if an error occurred.
     *
     * @author Stefano Chirico
     * @since 1.0.180421
     */
    public <DAO_CLASS extends DAO> DAO_CLASS getDAO(Class<DAO_CLASS> daoClass) throws DAOFactoryException;
}