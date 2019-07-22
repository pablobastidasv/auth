package co.pablobastidasv.user.boundary;

import co.pablobastidasv.user.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.Optional;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserManagerTest {

    @Mock
    private EntityManager em;

    @InjectMocks
    private UserManager userManager;

    @Mock
    private TypedQuery<User> query;

    private String username = "username";
    private String tenant = "avalane";

    @Test
    @SuppressWarnings("unchecked")
    void findByUserTenant_NoResultException() {
        when(em.createNamedQuery(anyString(), any(Class.class))).thenReturn(query);
        when(query.setParameter(anyString(), anyString())).thenReturn(query);

        when(query.getSingleResult()).thenThrow(NoResultException.class);

        Optional<User> byUser = userManager.findByUserAndTenant(username, tenant);

        verify(em).createNamedQuery("User.findByUsernameAndTenant", User.class);
        assertFalse(byUser.isPresent());
    }

    @Test
    @SuppressWarnings("unchecked")
    void findByUserTenant() {
        User user = new User();
        when(em.createNamedQuery(anyString(), any(Class.class))).thenReturn(query);
        when(query.setParameter(anyString(), anyString())).thenReturn(query);

        when(query.getSingleResult()).thenReturn(user);

        Optional<User> byUser = userManager.findByUserAndTenant(username, tenant);

        verify(em).createNamedQuery("User.findByUsernameAndTenant", User.class);
        assertTrue(byUser.isPresent());
        assertEquals(user, byUser.get());
    }
}