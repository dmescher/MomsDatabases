package org.golfballdm.DAO;

public class CensusDAO extends GenericDAO {
    private static CensusDAO instance = new CensusDAO();
    private static final Object lock = new Object();

    private CensusDAO() {
        super("census");
    }

    public static CensusDAO getInstance() {
        synchronized (lock) {
            if (null == instance) {
                instance = new CensusDAO();
            }
        }

        if (!instance.isConfigured()) {
            instance.configure();
        }

        return instance;
    }
}
