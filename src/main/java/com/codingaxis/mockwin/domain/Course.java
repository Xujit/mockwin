package com.codingaxis.mockwin.domain;

import java.io.Serializable;
import java.time.LocalDate;

import javax.json.bind.annotation.JsonbDateFormat;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * A Course.
 */
@Entity
@Table(name = "course")
@Cacheable
@RegisterForReflection
public class Course extends PanacheEntityBase implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
  @SequenceGenerator(name = "sequenceGenerator")
  public Long id;

  @Column(name = "modification_counter")
  public Integer modificationCounter;

  @Column(name = "name")
  public String name;

  @JsonbDateFormat(value = "yyyy-MM-dd")
  @Column(name = "created")
  public LocalDate created;

  @JsonbDateFormat(value = "yyyy-MM-dd")
  @Column(name = "last_updated")
  public LocalDate lastUpdated;

  @Column(name = "status")
  public String status;

  @Column(name = "compet_exam_id")
  public Long competExamId;

  @Column(name = "curriculum")
  public String curriculum;

  @Column(name = "prize_mech")
  public String prizeMech;

  @OneToOne
  @JoinColumn(unique = true)
  public ExamType examType;

  // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove

  @Override
  public boolean equals(Object o) {

    if (this == o) {
      return true;
    }
    if (!(o instanceof Course)) {
      return false;
    }
    return this.id != null && this.id.equals(((Course) o).id);
  }

  @Override
  public int hashCode() {

    return 31;
  }

  @Override
  public String toString() {

    return "Course{" + "id=" + this.id + ", modificationCounter=" + this.modificationCounter + ", name='" + this.name
        + "'" + ", created='" + this.created + "'" + ", lastUpdated='" + this.lastUpdated + "'" + ", status='"
        + this.status + "'" + ", competExamId=" + this.competExamId + ", curriculum='" + this.curriculum + "'"
        + ", prizeMech='" + this.prizeMech + "'" + "}";
  }

  public Course update() {

    return update(this);
  }

  public Course persistOrUpdate() {

    return persistOrUpdate(this);
  }

  public static Course update(Course course) {

    if (course == null) {
      throw new IllegalArgumentException("course can't be null");
    }
    var entity = Course.<Course> findById(course.id);
    if (entity != null) {
      entity.modificationCounter = course.modificationCounter;
      entity.name = course.name;
      entity.created = course.created;
      entity.lastUpdated = course.lastUpdated;
      entity.status = course.status;
      entity.competExamId = course.competExamId;
      entity.curriculum = course.curriculum;
      entity.prizeMech = course.prizeMech;
      entity.examType = course.examType;
    }
    return entity;
  }

  public static Course persistOrUpdate(Course course) {

    if (course == null) {
      throw new IllegalArgumentException("course can't be null");
    }
    if (course.id == null) {
      persist(course);
      return course;
    } else {
      return update(course);
    }
  }

}
