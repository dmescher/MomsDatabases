package org.golfballdm.orm;

import org.golfballdm.models.FreeResident;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FreeResMapper implements GenericMapper {
    @Override
    public List<Object> map(ResultSet rs) throws SQLException {
        ArrayList<Object> rtnVal = new ArrayList<>();
        while (rs.next()) {
            FreeResident row = new FreeResident();
            row.setPersonID(rs.getInt("PersonID"));
            if (null != rs.getString("Name")) {
                row.setName(rs.getString("Name"));
            }
            row.setFamilyID(rs.getInt("FamilyID"));
            row.setPage(rs.getInt("Page"));
            if (null != rs.getString("Sex")) {
                row.setSex(rs.getString("Sex").charAt(0));
            }
            if (null != rs.getString("Color")) {
                row.setColor(rs.getString("Color").charAt(0));
            }
            if (null != rs.getString("Profession")) {
                row.setProfession(rs.getString("Profession"));
            }
            if (null != rs.getString("Married")) {
                row.setMarried(rs.getString("Married").charAt(0));
            }
            if (null != rs.getString("Schooling")) {
                row.setSchooling(rs.getString("Schooling").charAt(0));
            }
            if (null != rs.getString("IlliterateOver20")) {
                row.setIlliterateOver20(rs.getString("IlliterateOver20").charAt(0));
            }
            rtnVal.add(row);
        }
        return rtnVal;
    }
}
