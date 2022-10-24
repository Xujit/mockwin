package com.codingaxis.mockwin.domain;

import java.io.Serializable;
import java.time.LocalDate;
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
 * A CompetExam.
 */
@Entity
@Table(name = "compet_exam")
@Cacheable
@RegisterForReflection
public class CompetExam extends PanacheEntityBase implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
  @SequenceGenerator(name = "sequenceGenerator")
  public Long id;

  @Column(name = "modification_counter")
  public Integer modificationCounter;

  @Column(name = "compet_exams_id")
  public Long competExamsId;

  @NotNull
  @Column(name = "name", nullable = false)
  public String name;

  @Column(name = "description")
  public String description;

  @Column(name = "status")
  public Integer status;

  @Column(name = "created_date")
  public LocalDate createdDate;

  @Column(name = "last_updated")
  public LocalDate lastUpdated;

  @OneToMany
  @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
  public Set<Course> courses = new HashSet<>();

  // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove

  @Override
  public boolean equals(Object o) {

    if (this == o) {
      return true;
    }
    if (!(o instanceof CompetExam)) {
      return false;
    }
    return this.id != null && this.id.equals(((CompetExam) o).id);
  }

  @Override
  public int hashCode() {

    return 31;
  }

  @Override
  public String toString() {

    return "CompetExam{" + "id=" + this.id + ", modificationCounter=" + this.modificationCounter + ", competExamsId="
        + this.competExamsId + ", name='" + this.name + "'" + ", description='" + this.description + "'" + ", status="
        + this.status + ", createdDate='" + this.createdDate + "'" + ", lastUpdated='" + this.lastUpdated + "'" + "}";
  }

  public CompetExam update() {

    return update(this);
  }

  public CompetExam persistOrUpdate() {

    return persistOrUpdate(this);
  }

  public static CompetExam update(CompetExam competExam) {

    if (competExam == null) {
      throw new IllegalArgumentException("competExam can't be null");
    }
    var entity = CompetExam.<CompetExam> findById(competExam.id);
    if (entity != null) {
      entity.modificationCounter = competExam.modificationCounter;
      entity.competExamsId = competExam.competExamsId;
      entity.name = competExam.name;
      entity.description = competExam.description;
      entity.status = competExam.status;
      entity.createdDate = competExam.createdDate;
      entity.lastUpdated = competExam.lastUpdated;
      entity.courses = competExam.courses;
    }
    return entity;
  }

  public static CompetExam persistOrUpdate(CompetExam competExam) {

    if (competExam == null) {
      throw new IllegalArgumentException("competExam can't be null");
    }
    if (competExam.id == null) {
      persist(competExam);
      return competExam;
    } else {
      return update(competExam);
    }
  }

}
