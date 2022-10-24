package com.codingaxis.mockwin.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.codingaxis.mockwin.TestUtil;
import org.junit.jupiter.api.Test;


public class CountryTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Country.class);
        Country country1 = new Country();
        country1.id = 1L;
        Country country2 = new Country();
        country2.id = country1.id;
        assertThat(country1).isEqualTo(country2);
        country2.id = 2L;
        assertThat(country1).isNotEqualTo(country2);
        country1.id = null;
        assertThat(country1).isNotEqualTo(country2);
    }
}
