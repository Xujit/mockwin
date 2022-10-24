package com.codingaxis.mockwin.domain;

import java.io.Serializable;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * A Exam.
 */
@Entity
@Table(name = "exam")
@Cacheable
@RegisterForReflection
public class Exam extends PanacheEntityBase implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
  @SequenceGenerator(name = "sequenceGenerator")
  public Long id;

  @Column(name = "modification_counter")
  public Integer modificationCounter;

  @NotNull
  @Column(name = "exam_name", nullable = false)
  public String examName;

  @Column(name = "description")
  public String description;

  // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove

  @Override
  public boolean equals(Object o) {

    if (this == o) {
      return true;
    }
    if (!(o instanceof Exam)) {
      return false;
    }
    return this.id != null && this.id.equals(((Exam) o).id);
  }

  @Override
  public int hashCode() {

    return 31;
  }

  @Override
  public String toString() {

    return "Exam{" + "id=" + this.id + ", modificationCounter=" + this.modificationCounter + ", examName='"
        + this.examName + "'" + ", description='" + this.description + "'" + "}";
  }

  public Exam update() {

    return update(this);
  }

  public Exam persistOrUpdate() {

    return persistOrUpdate(this);
  }

  public static Exam update(Exam exam) {

    if (exam == null) {
      throw new IllegalArgumentException("exam can't be null");
    }
    var entity = Exam.<Exam> findById(exam.id);
    if (entity != null) {
      entity.modificationCounter = exam.modificationCounter;
      entity.examName = exam.examName;
      entity.description = exam.description;
    }
    return entity;
  }

  public static Exam persistOrUpdate(Exam exam) {

    if (exam == null) {
      throw new IllegalArgumentException("exam can't be null");
    }
    if (exam.id == null) {
      persist(exam);
      return exam;
    } else {
      return update(exam);
    }
  }

}
