package org.golfballdm.shared;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import jakarta.ws.rs.core.MultivaluedHashMap;
import org.junit.jupiter.api.Test;
import jakarta.ws.rs.core.MultivaluedMap;

import java.util.List;
import java.util.Map;

public class ParameterValidatorTests {
    private final ParameterValidator validator = new ParameterValidatorImpl();

    // Even though each test uses a parameter map, I'm not defining one for the class as a whole,
    // so tests can be run in parallel with no worries about stomping on each other, regardless
    // of what the test framework might be, as long as it has @Test annotations.

    @Test
    public void DuplicateValueFieldTest() {
        MultivaluedMap<String, String> paramMap = new MultivaluedHashMap<>();
        paramMap.put("field01",List.of("FamilyID","Sex"));
        paramMap.put("value01",List.of("205"));
        Exception e = assertThrows(IllegalArgumentException.class,() -> validator.validateParameters(paramMap));
        assertEquals(e.getMessage(),"Duplicate values for parameter field01");
    }

    @Test
    public void DuplicateValueValueTest() {
        MultivaluedMap<String, String> paramMap = new MultivaluedHashMap<>();
        paramMap.put("field01",List.of("FamilyID"));
        paramMap.put("value01",List.of("205","206"));
        Exception e = assertThrows(IllegalArgumentException.class,() -> validator.validateParameters(paramMap));
        assertEquals(e.getMessage(),"Duplicate values for parameter value01");
    }

    @Test
    public void MissingValueTest() {
        MultivaluedMap<String, String> paramMap = new MultivaluedHashMap<>();
        paramMap.put("field01",List.of("FamilyID"));
        Exception e = assertThrows(IllegalArgumentException.class,() -> validator.validateParameters(paramMap));
        assertEquals(e.getMessage(),"Missing value for key field01");
    }

    @Test
    public void MissingFieldTest() {
        MultivaluedMap<String, String> paramMap = new MultivaluedHashMap<>();
        paramMap.put("value01",List.of("205"));
        Exception e = assertThrows(IllegalArgumentException.class,() -> validator.validateParameters(paramMap));
        assertEquals(e.getMessage(),"Unpaired value value01");
    }

    @Test
    public void BadFieldNameTest() {
        // Bad field names aren't processed at all, so they'll lead to a count mismatch.
        MultivaluedMap<String, String> paramMap = new MultivaluedHashMap<>();
        paramMap.put("field1",List.of("FamilyID"));
        Exception e = assertThrows(IllegalArgumentException.class,() -> validator.validateParameters(paramMap));
        assertEquals(e.getMessage(),"Invalid field count");
    }

    @Test
    public void SortDirectionMissingSortByColumnTest() {
        MultivaluedMap<String, String> paramMap = new MultivaluedHashMap<>();
        paramMap.put(Query.distinctFieldParameterName,List.of("FamilyID"));
        paramMap.put(Query.sortDirection,List.of("asc"));
        Exception e = assertThrows(IllegalArgumentException.class,() -> validator.validateParameters(paramMap));
        assertEquals(e.getMessage(),"Sort direction is missing column name");
    }

    @Test
    public void BadSortDirectionTest() {
        MultivaluedMap<String, String> paramMap = new MultivaluedHashMap<>();
        paramMap.put(Query.sortFieldParameterName,List.of("FamilyID"));
        paramMap.put(Query.sortDirection,List.of("ascending"));
        Exception e = assertThrows(IllegalArgumentException.class,() -> validator.validateParameters(paramMap));
        assertEquals(e.getMessage(),"Sort direction must be asc/desc");
    }

    @Test
    public void HappyPathOneParameterTest() {
        MultivaluedMap<String, String> paramMap = new MultivaluedHashMap<>();
        paramMap.put("field01",List.of("FamilyID"));
        paramMap.put("value01",List.of("205"));
        Map<String, String> mappedParameters = validator.validateParameters(paramMap);
        assertEquals(mappedParameters.get("FamilyID"),"205");
    }

    @Test
    public void HappyPathTwoParametersTest() {
        MultivaluedMap<String, String> paramMap = new MultivaluedHashMap<>();
        paramMap.put("field01",List.of("FamilyID"));
        paramMap.put("value01",List.of("205"));
        paramMap.put("field02",List.of("Sex"));
        paramMap.put("value02",List.of("f"));
        Map<String, String> mappedParameters = validator.validateParameters(paramMap);
        assertEquals(mappedParameters.get("FamilyID"),"205");
        assertEquals(mappedParameters.get("Sex"),"f");
    }

    @Test
    public void HappyPathDistinctValueWithDataTest() {
        MultivaluedMap<String, String> paramMap = new MultivaluedHashMap<>();
        paramMap.put(Query.distinctFieldParameterName, List.of("Color"));
        paramMap.put("field01",List.of("FamilyID"));
        paramMap.put("value01",List.of("205"));
        Map<String, String> mappedParameters = validator.validateParameters(paramMap);
        assertEquals(mappedParameters.get("FamilyID"),"205");
        assertEquals(mappedParameters.get(Query.distinctFieldParameterName),"Color");
    }

    @Test
    public void HappyPathDistinctValueWithNoDataTest() {
        MultivaluedMap<String, String> paramMap = new MultivaluedHashMap<>();
        paramMap.put(Query.distinctFieldParameterName, List.of("Age"));
        Map<String, String> mappedParameters = validator.validateParameters(paramMap);
        assertEquals(mappedParameters.get(Query.distinctFieldParameterName),"Age");
    }

    @Test
    public void HappyPathTenParameterTest() {
        MultivaluedMap<String, String> paramMap = new MultivaluedHashMap<>();
        paramMap.put("field01",List.of("Field1"));
        paramMap.put("value01",List.of("Value1"));
        paramMap.put("field02",List.of("Field2"));
        paramMap.put("value02",List.of("value2"));
        paramMap.put("field03",List.of("field3"));
        paramMap.put("value03",List.of("value3"));
        paramMap.put("field04",List.of("field4"));
        paramMap.put("value04",List.of("value4"));
        paramMap.put("value05",List.of("value5"));
        paramMap.put("field05",List.of("field5"));
        paramMap.put("field06",List.of("field6"));
        paramMap.put("value06",List.of("value6"));
        paramMap.put("field07",List.of("field7"));
        paramMap.put("field08",List.of("field8"));
        paramMap.put("value07",List.of("value7"));
        paramMap.put("value08",List.of("value8"));
        paramMap.put("field09",List.of("field9"));
        paramMap.put("value09",List.of("value9"));
        paramMap.put("field10",List.of("field10"));
        paramMap.put("value10",List.of("value10"));
        Map<String, String> mappedParameters = validator.validateParameters(paramMap);
        assertEquals(mappedParameters.get("field10"),"value10");
        assertEquals(mappedParameters.get("field7"),"value7");
    }
}