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
 * A Category.
 */
@Entity
@Table(name = "category")
@Cacheable
@RegisterForReflection
public class Category extends PanacheEntityBase implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
  @SequenceGenerator(name = "sequenceGenerator")
  public Long id;

  @Column(name = "modification_counter")
  public Integer modificationCounter;

  @NotNull
  @Column(name = "category_name", nullable = false)
  public String categoryName;

  @Column(name = "description")
  public String description;

  @Column(name = "country_id")
  public Long countryId;

  // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove

  @Override
  public boolean equals(Object o) {

    if (this == o) {
      return true;
    }
    if (!(o instanceof Category)) {
      return false;
    }
    return this.id != null && this.id.equals(((Category) o).id);
  }

  @Override
  public int hashCode() {

    return 31;
  }

  @Override
  public String toString() {

    return "Category{" + "id=" + this.id + ", modificationCounter=" + this.modificationCounter + ", categoryName='"
        + this.categoryName + "'" + ", description='" + this.description + "'" + ", countryId=" + this.countryId + "}";
  }

  public Category update() {

    return update(this);
  }

  public Category persistOrUpdate() {

    return persistOrUpdate(this);
  }

  public static Category update(Category category) {

    if (category == null) {
      throw new IllegalArgumentException("category can't be null");
    }
    var entity = Category.<Category> findById(category.id);
    if (entity != null) {
      entity.modificationCounter = category.modificationCounter;
      entity.categoryName = category.categoryName;
      entity.description = category.description;
      entity.countryId = category.countryId;
    }
    return entity;
  }

  public static Category persistOrUpdate(Category category) {

    if (category == null) {
      throw new IllegalArgumentException("category can't be null");
    }
    if (category.id == null) {
      persist(category);
      return category;
    } else {
      return update(category);
    }
  }

}
