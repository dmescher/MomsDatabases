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
        Connection conn = Mockito.mock(Connection.class);
        PreparedStatement ps = Mockito.mock(PreparedStatement.class);
        Query query = new Query(paramMap,"dbo.FreeResidents", new FreeResident());
        try  {
            when(conn.prepareStatement(anyString())).thenReturn(ps);
            query.generatePreparedStatement(conn);
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
        Connection conn = Mockito.mock(Connection.class);
        PreparedStatement ps = Mockito.mock(PreparedStatement.class);
        Query query = new Query(paramMap,"dbo.Freeresidents", new FreeResident());
        try {
            when(conn.prepareStatement(anyString())).thenReturn(ps);
            query.generatePreparedStatement(conn);
            assertTrue(
                    StringUtils.equalsIgnoreCase(query.getPreparedStatementString(),
                            "SELECT * FROM dbo.FreeResidents  WHERE FamilyID=? AND Sex=?;")
            );
        } catch (SQLException e) {
            fail("Threw a SQLException");
        }
    }
}
