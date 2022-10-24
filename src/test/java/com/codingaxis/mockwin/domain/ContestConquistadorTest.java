package com.codingaxis.mockwin.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.codingaxis.mockwin.TestUtil;
import org.junit.jupiter.api.Test;


public class ContestConquistadorTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ContestConquistador.class);
        ContestConquistador contestConquistador1 = new ContestConquistador();
        contestConquistador1.id = 1L;
        ContestConquistador contestConquistador2 = new ContestConquistador();
        contestConquistador2.id = contestConquistador1.id;
        assertThat(contestConquistador1).isEqualTo(contestConquistador2);
        contestConquistador2.id = 2L;
        assertThat(contestConquistador1).isNotEqualTo(contestConquistador2);
        contestConquistador1.id = null;
        assertThat(contestConquistador1).isNotEqualTo(contestConquistador2);
    }
}
