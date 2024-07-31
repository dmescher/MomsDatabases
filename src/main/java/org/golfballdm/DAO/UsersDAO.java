package org.golfballdm.DAO;

public class UsersDAO extends GenericDAO {
    private static UsersDAO instance = new UsersDAO();
    private static final Object lock = new Object();

    private UsersDAO() {
        super("users");
    }

    public static UsersDAO getInstance() {
        synchronized (lock) {
            if (null == instance) {
                instance = new UsersDAO();
            }
        }

        if (!instance.isConfigured()) {
            instance.configure();
        }

        return instance;
    }
}
