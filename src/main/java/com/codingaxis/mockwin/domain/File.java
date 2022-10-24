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
 * A File.
 */
@Entity
@Table(name = "file")
@Cacheable
@RegisterForReflection
public class File extends PanacheEntityBase implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
  @SequenceGenerator(name = "sequenceGenerator")
  public Long id;

  @Column(name = "modification_counter")
  public Integer modificationCounter;

  @Column(name = "filename")
  public String filename;

  @Column(name = "assign_id")
  public Long assignId;

  // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove

  @Override
  public boolean equals(Object o) {

    if (this == o) {
      return true;
    }
    if (!(o instanceof File)) {
      return false;
    }
    return this.id != null && this.id.equals(((File) o).id);
  }

  @Override
  public int hashCode() {

    return 31;
  }

  @Override
  public String toString() {

    return "File{" + "id=" + this.id + ", modificationCounter=" + this.modificationCounter + ", filename='"
        + this.filename + "'" + ", assignId=" + this.assignId + "}";
  }

  public File update() {

    return update(this);
  }

  public File persistOrUpdate() {

    return persistOrUpdate(this);
  }

  public static File update(File file) {

    if (file == null) {
      throw new IllegalArgumentException("file can't be null");
    }
    var entity = File.<File> findById(file.id);
    if (entity != null) {
      entity.modificationCounter = file.modificationCounter;
      entity.filename = file.filename;
      entity.assignId = file.assignId;
    }
    return entity;
  }

  public static File persistOrUpdate(File file) {

    if (file == null) {
      throw new IllegalArgumentException("file can't be null");
    }
    if (file.id == null) {
      persist(file);
      return file;
    } else {
      return update(file);
    }
  }

}
