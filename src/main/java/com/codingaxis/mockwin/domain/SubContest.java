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
 * A SubContest.
 */
@Entity
@Table(name = "sub_contest")
@Cacheable
@RegisterForReflection
public class SubContest extends PanacheEntityBase implements Serializable {

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
    if (!(o instanceof SubContest)) {
      return false;
    }
    return this.id != null && this.id.equals(((SubContest) o).id);
  }

  @Override
  public int hashCode() {

    return 31;
  }

  @Override
  public String toString() {

    return "SubContest{" + "id=" + this.id + ", description='" + this.description + "'" + "}";
  }

  public SubContest update() {

    return update(this);
  }

  public SubContest persistOrUpdate() {

    return persistOrUpdate(this);
  }

  public static SubContest update(SubContest subContest) {

    if (subContest == null) {
      throw new IllegalArgumentException("subContest can't be null");
    }
    var entity = SubContest.<SubContest> findById(subContest.id);
    if (entity != null) {
      entity.description = subContest.description;
    }
    return entity;
  }

  public static SubContest persistOrUpdate(SubContest subContest) {

    if (subContest == null) {
      throw new IllegalArgumentException("subContest can't be null");
    }
    if (subContest.id == null) {
      persist(subContest);
      return subContest;
    } else {
      return update(subContest);
    }
  }

}
