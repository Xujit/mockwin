package com.codingaxis.mockwin.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.codingaxis.mockwin.TestUtil;
import org.junit.jupiter.api.Test;


public class CurriculumTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Curriculum.class);
        Curriculum curriculum1 = new Curriculum();
        curriculum1.id = 1L;
        Curriculum curriculum2 = new Curriculum();
        curriculum2.id = curriculum1.id;
        assertThat(curriculum1).isEqualTo(curriculum2);
        curriculum2.id = 2L;
        assertThat(curriculum1).isNotEqualTo(curriculum2);
        curriculum1.id = null;
        assertThat(curriculum1).isNotEqualTo(curriculum2);
    }
}
