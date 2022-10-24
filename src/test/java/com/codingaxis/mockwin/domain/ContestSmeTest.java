package com.codingaxis.mockwin.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.codingaxis.mockwin.TestUtil;
import org.junit.jupiter.api.Test;


public class ContestSmeTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ContestSme.class);
        ContestSme contestSme1 = new ContestSme();
        contestSme1.id = 1L;
        ContestSme contestSme2 = new ContestSme();
        contestSme2.id = contestSme1.id;
        assertThat(contestSme1).isEqualTo(contestSme2);
        contestSme2.id = 2L;
        assertThat(contestSme1).isNotEqualTo(contestSme2);
        contestSme1.id = null;
        assertThat(contestSme1).isNotEqualTo(contestSme2);
    }
}
