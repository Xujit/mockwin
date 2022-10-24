package com.codingaxis.mockwin.domain;

import java.io.Serializable;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnore;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * A Subject.
 */
@Entity
@Table(name = "subject")
@Cacheable
@RegisterForReflection
public class Subject extends PanacheEntityBase implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
  @SequenceGenerator(name = "sequenceGenerator")
  public Long id;

  @Column(name = "modification_counter")
  public Integer modificationCounter;

  @Column(name = "subject_name")
  public String subjectName;

  @OneToOne(mappedBy = "subject")
  @JsonIgnore
  public Mcq mcq;

  // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove

  @Override
  public boolean equals(Object o) {

    if (this == o) {
      return true;
    }
    if (!(o instanceof Subject)) {
      return false;
    }
    return this.id != null && this.id.equals(((Subject) o).id);
  }

  @Override
  public int hashCode() {

    return 31;
  }

  @Override
  public String toString() {

    return "Subject{" + "id=" + this.id + ", modificationCounter=" + this.modificationCounter + ", subjectName='"
        + this.subjectName + "'" + "}";
  }

  public Subject update() {

    return update(this);
  }

  public Subject persistOrUpdate() {

    return persistOrUpdate(this);
  }

  public static Subject update(Subject subject) {

    if (subject == null) {
      throw new IllegalArgumentException("subject can't be null");
    }
    var entity = Subject.<Subject> findById(subject.id);
    if (entity != null) {
      entity.modificationCounter = subject.modificationCounter;
      entity.subjectName = subject.subjectName;
      entity.mcq = subject.mcq;
    }
    return entity;
  }

  public static Subject persistOrUpdate(Subject subject) {

    if (subject == null) {
      throw new IllegalArgumentException("subject can't be null");
    }
    if (subject.id == null) {
      persist(subject);
      return subject;
    } else {
      return update(subject);
    }
  }

}
