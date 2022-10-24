package com.codingaxis.mockwin.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.codingaxis.mockwin.TestUtil;
import org.junit.jupiter.api.Test;


public class ContestStatusReponseTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ContestStatusReponse.class);
        ContestStatusReponse contestStatusReponse1 = new ContestStatusReponse();
        contestStatusReponse1.id = 1L;
        ContestStatusReponse contestStatusReponse2 = new ContestStatusReponse();
        contestStatusReponse2.id = contestStatusReponse1.id;
        assertThat(contestStatusReponse1).isEqualTo(contestStatusReponse2);
        contestStatusReponse2.id = 2L;
        assertThat(contestStatusReponse1).isNotEqualTo(contestStatusReponse2);
        contestStatusReponse1.id = null;
        assertThat(contestStatusReponse1).isNotEqualTo(contestStatusReponse2);
    }
}
