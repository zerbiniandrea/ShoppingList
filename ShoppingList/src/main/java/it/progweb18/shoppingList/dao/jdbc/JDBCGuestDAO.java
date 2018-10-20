package it.progweb18.shoppingList.dao.jdbc;

import it.progweb18.shoppingList.dao.GuestDAO;
import it.progweb18.shoppingList.dao.entities.Guest;
import it.unitn.aa1718.webprogramming.persistence.utils.dao.exceptions.DAOException;
import it.unitn.aa1718.webprogramming.persistence.utils.dao.jdbc.JDBCDAO;
import static java.lang.Integer.parseInt;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.servlet.http.Cookie;

/**
 * The JDBC implementation of the {@link GuestDAO} interface.
 */
public class JDBCGuestDAO extends JDBCDAO<Guest, Integer> implements GuestDAO{
    private final static String COOKIE_NAME="ProgWeb18.ShoppingList.GuestID";

    /**
     * The default constructor of the class.
     * @param con the connection to the persistence system.
     */
    public JDBCGuestDAO(Connection con) {
        super(con);
    }
    
    /**
     * Returns the number of {@link Guest guests} stored on the persistence system
     * of the application.
     *
     * @return the number of records present into the storage system.
     * @throws DAOException if an error occurred during the information
     * retrieving.
     */
    @Override
    public Long getCount() throws DAOException {
        try (Statement stmt = CON.createStatement()) {
            ResultSet counter = stmt.executeQuery("SELECT COUNT(*) FROM guests");
            if (counter.next()) {
                return counter.getLong(1);
            }

        } catch (SQLException ex) {
            throw new DAOException("Impossible to count guests", ex);
        }

        return 0L;
    }

    /**
     * Returns the {@link Guest guest} with the primary key equals to the one
     * passed as parameter.
     *
     * @param primaryKey the {@code id} of the {@code guest} to get.
     * @return the {@code guest} with the id equals to the one passed as
     * parameter or {@code null} if no entities with that id are present into
     * the storage system.
     * @throws DAOException if an error occurred during the information
     * retrieving.
     */
    @Override
    public Guest getByPrimaryKey(Integer primaryKey) throws DAOException {
        if (primaryKey == null) {
            throw new DAOException("primaryKey is null");
        }
        try (PreparedStatement stm = CON.prepareStatement("SELECT * FROM guests WHERE id = ?")) {
            stm.setInt(1, primaryKey);
            try (ResultSet rs = stm.executeQuery()) {
                rs.next();
                Guest guest = new Guest();
                guest.setId(rs.getInt("id"));

                try (PreparedStatement todoStatement = CON.prepareStatement("SELECT count(*) FROM guests_shopping_lists WHERE id_guest = ?")) {
                    todoStatement.setInt(1, guest.getId());

                    ResultSet counter = todoStatement.executeQuery();
                    counter.next();
                    guest.setShoppingListsCount(counter.getInt(1));
                }

                return guest;
            }
        } catch (SQLException ex) {
            throw new DAOException("Impossible to get the user for the passed primary key", ex);
        }
    }

    /**
     * Returns the list of all the valid {@link Category categories} stored by the
     * storage system.
     *
     * @return the list of all the valid {@code categories}.
     * @throws DAOException if an error occurred during the information
     * retrieving.
     */
    @Override
    public List<Guest> getAll() throws DAOException {
        List<Guest> guests = new ArrayList<>();

        try (Statement stm = CON.createStatement()) {
            try (ResultSet rs = stm.executeQuery("SELECT * FROM guests")) {

                PreparedStatement shoppingListsStatement = CON.prepareStatement("SELECT count(*) FROM guests_shopping_lists WHERE id_guest = ?");

                while (rs.next()) {
                    Guest guest = new Guest();
                    guest.setId(rs.getInt("id"));

                    shoppingListsStatement.setInt(1, guest.getId());

                    ResultSet counter = shoppingListsStatement.executeQuery();
                    counter.next();
                    
                    guest.setShoppingListsCount(counter.getInt(1));

                    guests.add(guest);
                }
            }
        } catch (SQLException ex) {
            throw new DAOException("Impossible to get the list of users", ex);
        }

        return guests;
    }

    @Override
    public Guest create() throws DAOException {
        int id;
        
        try (Statement stmt = CON.createStatement()) {
            ResultSet max = stmt.executeQuery("SELECT MAX(id) FROM guests");
            if (max.next()) {
                id=max.getInt(1);
            }else{
                id=0;
            }
            
            id++;
            
            Guest guest=new Guest();
            guest.setId(id);
            

            String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            final String query="INSERT INTO guests (last_login) VALUES (\'"+timeStamp+"\')";
            PreparedStatement ps = CON.prepareStatement(query);

            if(ps.executeUpdate()==1){
                return guest;
            }else{ 
                return null;
           }
        } catch (SQLException ex) {
            throw new DAOException("Impossible to create the guest", ex);
        }
    }
    
    @Override
    public Guest getGuest(Cookie[] cookies) throws DAOException {
        Guest guest=new Guest();

        if( cookies != null ) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(COOKIE_NAME)){
                    try{
                        guest.setId(parseInt(cookie.getValue()));
                    }catch(RuntimeException ex){
                        //TODO: log the exception
                    }
                }
            }
        }

        if(guest.getId()<0)
            guest=create();

        setLastLogin(guest);
        
        return guest;
    }
    
    @Override
    public boolean setLastLogin(Guest guest) throws DAOException {
        if (guest == null) {
            throw new DAOException("parameter not valid", new IllegalArgumentException("The passed guest is null"));
        }

        String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        final String query="UPDATE guests SET last_login=\'"+timeStamp+"\' WHERE id=?";
        
        try (PreparedStatement ps = CON.prepareStatement(query)) {
            ps.setInt(1, guest.getId());
            
            return ps.executeUpdate()==1;
        } catch (SQLException ex) {
            throw new DAOException("Impossible to update the guest", ex);
        }
    }

    @Override
    public Integer countShoppingLists(Guest guest) throws DAOException {
        if (guest == null) {
            throw new DAOException("parameter not valid", new IllegalArgumentException("The passed guest is null"));
        }
        
        try (PreparedStatement stm = CON.prepareStatement("SELECT count(*) FROM guests_shopping_lists WHERE id_guest = ?")) {
            stm.setInt(1, guest.getId());

            ResultSet counter = stm.executeQuery();
            counter.next();
            return counter.getInt(1);
        } catch (SQLException ex) {
            throw new DAOException("Impossible to count the number of shopping lists created by this guest", ex);
        }
    }
}
