/*
 * AA 2017-2018
 * Introduction to Web Programming
 * Commons - DAO patterns
 * UniTN
 */
package it.unitn.aa1718.webprogramming.persistence.utils.dao.factories;

import it.unitn.aa1718.webprogramming.persistence.utils.dao.DAO;
import it.unitn.aa1718.webprogramming.persistence.utils.dao.exceptions.DAOFactoryException;

/**
 * This interface must be implemented by all the concrete {@code DAOFactor(y)}
 * ies.
 * 
 * @author Stefano Chirico &lt;stefano dot chirico at unitn dot it&gt;
 * @since 2018.04.21
 */
public interface DAOFactory {

    /**
     * Shutdowns the connection to the storage system.
     * 
     * @author Stefano Chirico
     * @since 1.0.180421
     */
    public void shutdown();
    
    /**
     * Returns the concrete {@link DAO dao} which type is the class passed as
     * parameter.
     * 
     * @param <DAO_CLASS> the class name of the {@code dao} to get.
     * @param daoInterface the class instance of the {@code dao} to get.
     * @return the concrete {@code dao} which type is the class passed as
     * parameter.
     * @throws DAOFactoryException if an error occurred during the operation.
     * 
     * @author Stefano Chirico
     * @since 1.0.180421
     */
    public <DAO_CLASS extends DAO> DAO_CLASS getDAO(Class<DAO_CLASS> daoInterface) throws DAOFactoryException;
}
