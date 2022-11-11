package org.eihq.quiltshow.model;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import static org.hamcrest.Matchers.*;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class PersonTest {

    
    @Test
    void getFullName_shouldReturnFullName_fromBothNames() {
        String firstName = "Mary";
        String lastName = "Antoinette";

        Person p = new Person();
        p.setFirstName(firstName);
        p.setLastName(lastName);

        String actual = p.getFullName();
        assertNotNull(actual);
        assertTrue(actual.contains(firstName));
        assertTrue(actual.contains(lastName));
    }
}
