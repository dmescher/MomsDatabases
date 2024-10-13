package org.golfballdm.orm;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public interface GenericMapper {
    List<Object> map(ResultSet rs) throws SQLException;
}
