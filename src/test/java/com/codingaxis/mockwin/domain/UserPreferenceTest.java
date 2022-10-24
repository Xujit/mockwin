package com.codingaxis.mockwin.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.codingaxis.mockwin.TestUtil;
import org.junit.jupiter.api.Test;


public class UserPreferenceTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(UserPreference.class);
        UserPreference userPreference1 = new UserPreference();
        userPreference1.id = 1L;
        UserPreference userPreference2 = new UserPreference();
        userPreference2.id = userPreference1.id;
        assertThat(userPreference1).isEqualTo(userPreference2);
        userPreference2.id = 2L;
        assertThat(userPreference1).isNotEqualTo(userPreference2);
        userPreference1.id = null;
        assertThat(userPreference1).isNotEqualTo(userPreference2);
    }
}
