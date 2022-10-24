package com.codingaxis.mockwin.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.codingaxis.mockwin.TestUtil;
import org.junit.jupiter.api.Test;


public class SubjectTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Subject.class);
        Subject subject1 = new Subject();
        subject1.id = 1L;
        Subject subject2 = new Subject();
        subject2.id = subject1.id;
        assertThat(subject1).isEqualTo(subject2);
        subject2.id = 2L;
        assertThat(subject1).isNotEqualTo(subject2);
        subject1.id = null;
        assertThat(subject1).isNotEqualTo(subject2);
    }
}
