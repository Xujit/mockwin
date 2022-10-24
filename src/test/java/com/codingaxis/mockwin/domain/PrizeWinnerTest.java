package com.codingaxis.mockwin.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.codingaxis.mockwin.TestUtil;
import org.junit.jupiter.api.Test;


public class PrizeWinnerTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(PrizeWinner.class);
        PrizeWinner prizeWinner1 = new PrizeWinner();
        prizeWinner1.id = 1L;
        PrizeWinner prizeWinner2 = new PrizeWinner();
        prizeWinner2.id = prizeWinner1.id;
        assertThat(prizeWinner1).isEqualTo(prizeWinner2);
        prizeWinner2.id = 2L;
        assertThat(prizeWinner1).isNotEqualTo(prizeWinner2);
        prizeWinner1.id = null;
        assertThat(prizeWinner1).isNotEqualTo(prizeWinner2);
    }
}
