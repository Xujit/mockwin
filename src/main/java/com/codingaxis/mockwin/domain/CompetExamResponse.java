package com.codingaxis.mockwin.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * A CompetExamResponse.
 */
@Entity
@Table(name = "compet_exam_response")
@Cacheable
@RegisterForReflection
public class CompetExamResponse extends PanacheEntityBase implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
  @SequenceGenerator(name = "sequenceGenerator")
  public Long id;

  @Column(name = "modification_counter")
  public Integer modificationCounter;

  @NotNull
  @Column(name = "name", nullable = false)
  public String name;

  @Column(name = "description")
  public String description;

  @OneToMany
  @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
  public Set<Course> courses = new HashSet<>();

  // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove

  @Override
  public boolean equals(Object o) {

    if (this == o) {
      return true;
    }
    if (!(o instanceof CompetExamResponse)) {
      return false;
    }
    return this.id != null && this.id.equals(((CompetExamResponse) o).id);
  }

  @Override
  public int hashCode() {

    return 31;
  }

  @Override
  public String toString() {

    return "CompetExamResponse{" + "id=" + this.id + ", modificationCounter=" + this.modificationCounter + ", name='"
        + this.name + "'" + ", description='" + this.description + "'" + "}";
  }

  public CompetExamResponse update() {

    return update(this);
  }

  public CompetExamResponse persistOrUpdate() {

    return persistOrUpdate(this);
  }

  public static CompetExamResponse update(CompetExamResponse competExamResponse) {

    if (competExamResponse == null) {
      throw new IllegalArgumentException("competExamResponse can't be null");
    }
    var entity = CompetExamResponse.<CompetExamResponse> findById(competExamResponse.id);
    if (entity != null) {
      entity.modificationCounter = competExamResponse.modificationCounter;
      entity.name = competExamResponse.name;
      entity.description = competExamResponse.description;
      entity.courses = competExamResponse.courses;
    }
    return entity;
  }

  public static CompetExamResponse persistOrUpdate(CompetExamResponse competExamResponse) {

    if (competExamResponse == null) {
      throw new IllegalArgumentException("competExamResponse can't be null");
    }
    if (competExamResponse.id == null) {
      persist(competExamResponse);
      return competExamResponse;
    } else {
      return update(competExamResponse);
    }
  }

}
