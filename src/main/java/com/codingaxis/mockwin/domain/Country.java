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
 * A Country.
 */
@Entity
@Table(name = "country")
@Cacheable
@RegisterForReflection
public class Country extends PanacheEntityBase implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
  @SequenceGenerator(name = "sequenceGenerator")
  public Long id;

  @Column(name = "modification_counter")
  public Integer modificationCounter;

  @NotNull
  @Column(name = "country_name", nullable = false, unique = true)
  public String countryName;

  @Column(name = "description")
  public String description;

  // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove

  @Override
  public boolean equals(Object o) {

    if (this == o) {
      return true;
    }
    if (!(o instanceof Country)) {
      return false;
    }
    return this.id != null && this.id.equals(((Country) o).id);
  }

  @Override
  public int hashCode() {

    return 31;
  }

  @Override
  public String toString() {

    return "Country{" + "id=" + this.id + ", modificationCounter=" + this.modificationCounter + ", countryName='"
        + this.countryName + "'" + ", description='" + this.description + "'" + "}";
  }

  public Country update() {

    return update(this);
  }

  public Country persistOrUpdate() {

    return persistOrUpdate(this);
  }

  public static Country update(Country country) {

    if (country == null) {
      throw new IllegalArgumentException("country can't be null");
    }
    var entity = Country.<Country> findById(country.id);
    if (entity != null) {
      entity.modificationCounter = country.modificationCounter;
      entity.countryName = country.countryName;
      entity.description = country.description;

    }
    return entity;
  }

  public static Country persistOrUpdate(Country country) {

    if (country == null) {
      throw new IllegalArgumentException("country can't be null");
    }
    if (country.id == null) {
      persist(country);
      return country;
    } else {
      return update(country);
    }
  }

}
