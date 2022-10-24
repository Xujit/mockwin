package com.codingaxis.mockwin.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.codingaxis.mockwin.TestUtil;
import org.junit.jupiter.api.Test;


public class UserTypeTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(UserType.class);
        UserType userType1 = new UserType();
        userType1.id = 1L;
        UserType userType2 = new UserType();
        userType2.id = userType1.id;
        assertThat(userType1).isEqualTo(userType2);
        userType2.id = 2L;
        assertThat(userType1).isNotEqualTo(userType2);
        userType1.id = null;
        assertThat(userType1).isNotEqualTo(userType2);
    }
}
