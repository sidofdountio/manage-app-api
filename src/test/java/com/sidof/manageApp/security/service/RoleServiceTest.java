package com.sidof.manageApp.security.service;

import com.sidof.manageApp.security.model.Role;
import com.sidof.manageApp.security.repo.RoleRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleService underTest;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }


    @Test
    void shouldReturnRoleWhenExists() {
        // Given
        Role role = new Role();
        role.setName("ADMIN");

        when(roleRepository.findByName("ADMIN")).thenReturn(Optional.of(role));

        // When
        Role found = underTest.getRoleByName("ADMIN");

        // Then
        assertNotNull(found);
        assertEquals("ADMIN", found.getName());
        verify(roleRepository, times(1)).findByName("ADMIN");
    }

    @Test
    void shouldAddNewRole() {
        // Given
        String roleName = "ADMIN";
        Role role = new Role();
        role.setName("ADMIN");
        when(roleRepository.save(any(Role.class))).thenReturn(role);

        // When
        Role savedNewRole = underTest.saveNewRole(roleName);
        // Then
        assertNotNull(savedNewRole);
//        assertEquals(roleName, saveNewRole.getName());
    }

    @Test
    void shouldThrownAnExceptionIfRoleExist() {
        // Given
        String roleName = "ADMIN";
        Role role = new Role();
        role.setName("ADMIN");

        BDDMockito.given(roleRepository.findByName(roleName)).willReturn(Optional.of(role));

//        when
        assertThatThrownBy(() -> underTest.saveNewRole(roleName))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Role exist with that name :" + roleName);

        verify(roleRepository,never()).save(any());
    }


}