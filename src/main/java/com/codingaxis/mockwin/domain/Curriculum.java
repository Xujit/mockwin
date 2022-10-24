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
 * A Curriculum.
 */
@Entity
@Table(name = "curriculum")
@Cacheable
@RegisterForReflection
public class Curriculum extends PanacheEntityBase implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
  @SequenceGenerator(name = "sequenceGenerator")
  public Long id;

  @Column(name = "description")
  public String description;

  // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove

  @Override
  public boolean equals(Object o) {

    if (this == o) {
      return true;
    }
    if (!(o instanceof Curriculum)) {
      return false;
    }
    return this.id != null && this.id.equals(((Curriculum) o).id);
  }

  @Override
  public int hashCode() {

    return 31;
  }

  @Override
  public String toString() {

    return "Curriculum{" + "id=" + this.id + ", description='" + this.description + "'" + "}";
  }

  public Curriculum update() {

    return update(this);
  }

  public Curriculum persistOrUpdate() {

    return persistOrUpdate(this);
  }

  public static Curriculum update(Curriculum curriculum) {

    if (curriculum == null) {
      throw new IllegalArgumentException("curriculum can't be null");
    }
    var entity = Curriculum.<Curriculum> findById(curriculum.id);
    if (entity != null) {
      entity.description = curriculum.description;

    }
    return entity;
  }

  public static Curriculum persistOrUpdate(Curriculum curriculum) {

    if (curriculum == null) {
      throw new IllegalArgumentException("curriculum can't be null");
    }
    if (curriculum.id == null) {
      persist(curriculum);
      return curriculum;
    } else {
      return update(curriculum);
    }
  }

}
