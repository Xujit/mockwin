package com.codingaxis.mockwin.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.codingaxis.mockwin.TestUtil;
import org.junit.jupiter.api.Test;


public class CompetExamResponseTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CompetExamResponse.class);
        CompetExamResponse competExamResponse1 = new CompetExamResponse();
        competExamResponse1.id = 1L;
        CompetExamResponse competExamResponse2 = new CompetExamResponse();
        competExamResponse2.id = competExamResponse1.id;
        assertThat(competExamResponse1).isEqualTo(competExamResponse2);
        competExamResponse2.id = 2L;
        assertThat(competExamResponse1).isNotEqualTo(competExamResponse2);
        competExamResponse1.id = null;
        assertThat(competExamResponse1).isNotEqualTo(competExamResponse2);
    }
}
