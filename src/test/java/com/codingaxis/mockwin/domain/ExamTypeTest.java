package com.codingaxis.mockwin.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.codingaxis.mockwin.TestUtil;
import org.junit.jupiter.api.Test;


public class ExamTypeTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ExamType.class);
        ExamType examType1 = new ExamType();
        examType1.id = 1L;
        ExamType examType2 = new ExamType();
        examType2.id = examType1.id;
        assertThat(examType1).isEqualTo(examType2);
        examType2.id = 2L;
        assertThat(examType1).isNotEqualTo(examType2);
        examType1.id = null;
        assertThat(examType1).isNotEqualTo(examType2);
    }
}
