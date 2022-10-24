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

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * A ExamType.
 */
@Entity
@Table(name = "exam_type")
@Cacheable
@RegisterForReflection
public class ExamType extends PanacheEntityBase implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
  @SequenceGenerator(name = "sequenceGenerator")
  public Long id;

  @Column(name = "exam_type")
  public String examType;

  // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove

  @Override
  public boolean equals(Object o) {

    if (this == o) {
      return true;
    }
    if (!(o instanceof ExamType)) {
      return false;
    }
    return this.id != null && this.id.equals(((ExamType) o).id);
  }

  @Override
  public int hashCode() {

    return 31;
  }

  @Override
  public String toString() {

    return "ExamType{" + "id=" + this.id + ", examType='" + this.examType + "'" + "}";
  }

  public ExamType update() {

    return update(this);
  }

  public ExamType persistOrUpdate() {

    return persistOrUpdate(this);
  }

  public static ExamType update(ExamType examType) {

    if (examType == null) {
      throw new IllegalArgumentException("examType can't be null");
    }
    var entity = ExamType.<ExamType> findById(examType.id);
    if (entity != null) {
      entity.examType = examType.examType;
    }
    return entity;
  }

  public static ExamType persistOrUpdate(ExamType examType) {

    if (examType == null) {
      throw new IllegalArgumentException("examType can't be null");
    }
    if (examType.id == null) {
      persist(examType);
      return examType;
    } else {
      return update(examType);
    }
  }

}
