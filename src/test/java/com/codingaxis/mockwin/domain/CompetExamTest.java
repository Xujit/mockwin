package com.codingaxis.mockwin.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.codingaxis.mockwin.TestUtil;
import org.junit.jupiter.api.Test;


public class CompetExamTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CompetExam.class);
        CompetExam competExam1 = new CompetExam();
        competExam1.id = 1L;
        CompetExam competExam2 = new CompetExam();
        competExam2.id = competExam1.id;
        assertThat(competExam1).isEqualTo(competExam2);
        competExam2.id = 2L;
        assertThat(competExam1).isNotEqualTo(competExam2);
        competExam1.id = null;
        assertThat(competExam1).isNotEqualTo(competExam2);
    }
}
