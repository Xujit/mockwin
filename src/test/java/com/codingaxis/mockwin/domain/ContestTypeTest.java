package com.codingaxis.mockwin.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.codingaxis.mockwin.TestUtil;
import org.junit.jupiter.api.Test;


public class ContestTypeTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ContestType.class);
        ContestType contestType1 = new ContestType();
        contestType1.id = 1L;
        ContestType contestType2 = new ContestType();
        contestType2.id = contestType1.id;
        assertThat(contestType1).isEqualTo(contestType2);
        contestType2.id = 2L;
        assertThat(contestType1).isNotEqualTo(contestType2);
        contestType1.id = null;
        assertThat(contestType1).isNotEqualTo(contestType2);
    }
}
