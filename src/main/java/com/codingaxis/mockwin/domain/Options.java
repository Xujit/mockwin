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
 * A Options.
 */
@Entity
@Table(name = "options")
@Cacheable
@RegisterForReflection
public class Options extends PanacheEntityBase implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
  @SequenceGenerator(name = "sequenceGenerator")
  public Long id;

  @Column(name = "modification_counter")
  public Integer modificationCounter;

  @NotNull
  @Column(name = "answer", nullable = false)
  public String answer;

  @Column(name = "correct")
  public Boolean correct;

  // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove

  @Override
  public boolean equals(Object o) {

    if (this == o) {
      return true;
    }
    if (!(o instanceof Options)) {
      return false;
    }
    return this.id != null && this.id.equals(((Options) o).id);
  }

  @Override
  public int hashCode() {

    return 31;
  }

  @Override
  public String toString() {

    return "Options{" + "id=" + this.id + ", modificationCounter=" + this.modificationCounter + ", answer='"
        + this.answer + "'" + ", correct='" + this.correct + "'" + "}";
  }

  public Options update() {

    return update(this);
  }

  public Options persistOrUpdate() {

    return persistOrUpdate(this);
  }

  public static Options update(Options options) {

    if (options == null) {
      throw new IllegalArgumentException("options can't be null");
    }
    var entity = Options.<Options> findById(options.id);
    if (entity != null) {
      entity.modificationCounter = options.modificationCounter;
      entity.answer = options.answer;
      entity.correct = options.correct;
    }
    return entity;
  }

  public static Options persistOrUpdate(Options options) {

    if (options == null) {
      throw new IllegalArgumentException("options can't be null");
    }
    if (options.id == null) {
      persist(options);
      return options;
    } else {
      return update(options);
    }
  }

}
