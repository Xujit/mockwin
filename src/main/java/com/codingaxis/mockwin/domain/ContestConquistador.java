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

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * A ContestConquistador.
 */
@Entity
@Table(name = "contest_conquistador")
@Cacheable
@RegisterForReflection
public class ContestConquistador extends PanacheEntityBase implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
  @SequenceGenerator(name = "sequenceGenerator")
  public Long id;

  @Column(name = "contest_id")
  public Long contestId;

  @Column(name = "status")
  public String status;

  @OneToMany
  @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
  public Set<PrizeWinner> prizeWinners = new HashSet<>();

  // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove

  @Override
  public boolean equals(Object o) {

    if (this == o) {
      return true;
    }
    if (!(o instanceof ContestConquistador)) {
      return false;
    }
    return this.id != null && this.id.equals(((ContestConquistador) o).id);
  }

  @Override
  public int hashCode() {

    return 31;
  }

  @Override
  public String toString() {

    return "ContestConquistador{" + "id=" + this.id + ", contestId=" + this.contestId + ", status='" + this.status + "'"
        + "}";
  }

  public ContestConquistador update() {

    return update(this);
  }

  public ContestConquistador persistOrUpdate() {

    return persistOrUpdate(this);
  }

  public static ContestConquistador update(ContestConquistador contestConquistador) {

    if (contestConquistador == null) {
      throw new IllegalArgumentException("contestConquistador can't be null");
    }
    var entity = ContestConquistador.<ContestConquistador> findById(contestConquistador.id);
    if (entity != null) {
      entity.contestId = contestConquistador.contestId;
      entity.status = contestConquistador.status;
      entity.prizeWinners = contestConquistador.prizeWinners;
    }
    return entity;
  }

  public static ContestConquistador persistOrUpdate(ContestConquistador contestConquistador) {

    if (contestConquistador == null) {
      throw new IllegalArgumentException("contestConquistador can't be null");
    }
    if (contestConquistador.id == null) {
      persist(contestConquistador);
      return contestConquistador;
    } else {
      return update(contestConquistador);
    }
  }

}
