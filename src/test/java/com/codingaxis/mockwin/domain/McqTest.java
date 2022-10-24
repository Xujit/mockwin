package com.codingaxis.mockwin.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.codingaxis.mockwin.TestUtil;
import org.junit.jupiter.api.Test;


public class McqTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Mcq.class);
        Mcq mcq1 = new Mcq();
        mcq1.id = 1L;
        Mcq mcq2 = new Mcq();
        mcq2.id = mcq1.id;
        assertThat(mcq1).isEqualTo(mcq2);
        mcq2.id = 2L;
        assertThat(mcq1).isNotEqualTo(mcq2);
        mcq1.id = null;
        assertThat(mcq1).isNotEqualTo(mcq2);
    }
}
