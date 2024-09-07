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


public class QueryFreeResTests {
    @Test
    public void testQueryCreationSingleParameter() {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("FamilyID","205");
        Query<FreeResident> query = new Query<>(paramMap,"dbo.Freeresidents", FreeResident.class);
        try (Connection conn = Mockito.mock(Connection.class);
             PreparedStatement ps = query.generatePreparedStatement(conn)) {
            assertTrue(
                    StringUtils.equalsIgnoreCase(query.getPreparedStatementString(),
                            "SELECT * FROM dbo.FreeResidents WHERE FamilyID=205")
            );
            System.out.println(query.getPreparedStatementString());
            assertEquals(1, ps.getParameterMetaData().getParameterCount());
            assertTrue(
                    StringUtils.equalsIgnoreCase(ps.getParameterMetaData().getParameterTypeName(1),
                            "INTEGER")
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
        Query<FreeResident> query = new Query<>(paramMap,"dbo.Freeresidents", FreeResident.class);
        try (Connection conn = Mockito.mock(Connection.class);
             PreparedStatement ps = query.generatePreparedStatement(conn)) {
            assertTrue(
                    StringUtils.equalsIgnoreCase(query.getPreparedStatementString(),
                            "SELECT * FROM dbo.FreeResidents WHERE FamilyID=205 AND Sex='f'")
            );
            assertEquals(1, ps.getParameterMetaData().getParameterCount());
            assertTrue(
                    StringUtils.equalsIgnoreCase(ps.getParameterMetaData().getParameterTypeName(1),
                            "INTEGER")
            );
        } catch (SQLException e) {
            fail("Threw a SQLException");
        }
    }
}
