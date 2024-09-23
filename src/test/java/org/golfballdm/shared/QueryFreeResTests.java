package org.golfballdm.shared;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.golfballdm.models.FreeResident;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;


public class QueryFreeResTests {
    @Test
    public void testQueryCreationSingleParameter() {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("FamilyID","205");
        Query query = new Query(paramMap,new String[]{"dbo.FreeResidents"}, new FreeResident());
        try  {
            query.generatePreparedStatementString();
            assertTrue(
                    StringUtils.equalsIgnoreCase(query.getPreparedStatementString(),
                            "SELECT * FROM dbo.FreeResidents  WHERE FamilyID=?;")
            );
        } catch (SQLException e) {
            fail("Threw a SQLException "+e.getMessage());
        }
    }

    @Test
    public void testQueryCreationTwoParameter() {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("FamilyID","205");
        paramMap.put("Sex","f");
        Query query = new Query(paramMap,new String[]{"dbo.Freeresidents"}, new FreeResident());
        try {
            query.generatePreparedStatementString();
            assertTrue(
                    StringUtils.equalsIgnoreCase(query.getPreparedStatementString(),
                            "SELECT * FROM dbo.FreeResidents  WHERE FamilyID=? AND Sex=?;")
            );
        } catch (SQLException e) {
            fail("Threw a SQLException");
        }
    }

    @Test
    public void testQueryCreationInEqOperator() {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("Age.GT","5");
        Query query = new Query(paramMap, new String[]{"dbo.Freeresidents"}, new FreeResident());
        try {
            query.generatePreparedStatementString();
            assertTrue(
                    StringUtils.equalsIgnoreCase(query.getPreparedStatementString(),
                            "SELECT * FROM dbo.FreeResidents  WHERE Age>?;")
            );
        } catch (SQLException e) {
            fail("Threw a SQLException");
        }
    }
}