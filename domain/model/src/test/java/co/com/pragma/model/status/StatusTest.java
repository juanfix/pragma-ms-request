package co.com.pragma.model.status;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StatusTest {
    @Test
    void shouldCreateALoanTypeWithArgs() {
        Long id = 1L;
        String name = "Aprobada";
        String description = "Solicitud aprobada";

        Status status = new Status(id, name,description);

        assertNotNull(status);
        assertEquals(id, status.getId());
        assertEquals(name, status.getName());
        assertEquals(description, status.getDescription());
    }

    @Test
    void shouldCreateALoanTypeWithBuilder() {
        Long id = 1L;
        String name = "Aprobada";
        String description = "Solicitud aprobada";

        Status status = Status.builder()
                .id(id)
                .name(name)
                .description(description)
                .build();

        assertNotNull(status);
        assertEquals(id, status.getId());
        assertEquals(name, status.getName());
        assertEquals(description, status.getDescription());
    }

    @Test
    void shouldCreateALoanTypeWithSetters() {        Long id = 1L;
        String name = "Aprobada";
        String description = "Solicitud aprobada";

        Status status = new Status();
        status.setId(id);
        status.setName(name);
        status.setDescription(description);

        assertNotNull(status);
        assertEquals(id, status.getId());
        assertEquals(name, status.getName());
        assertEquals(description, status.getDescription());
    }
}
