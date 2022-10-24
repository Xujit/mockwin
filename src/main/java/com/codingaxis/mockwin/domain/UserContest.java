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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * A UserContest.
 */
@Entity
@Table(name = "user_contest")
@Cacheable
@RegisterForReflection
public class UserContest extends PanacheEntityBase implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
  @SequenceGenerator(name = "sequenceGenerator")
  public Long id;

  @Column(name = "modification_counter")
  public Integer modificationCounter;

  @Column(name = "user_id")
  public Long userId;

  @Column(name = "contest_id")
  public Long contestId;

  @Column(name = "rank")
  public Long rank;

  @Column(name = "score")
  public Long score;

  @JsonbDateFormat(value = "yyyy-MM-dd")
  @Column(name = "last_updated")
  public LocalDate lastUpdated;

  @JsonbDateFormat(value = "yyyy-MM-dd")
  @Column(name = "created")
  public LocalDate created;

  @Column(name = "completed")
  public Boolean completed;

  @Column(name = "deleted")
  public Boolean deleted;

  // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove

  @Override
  public boolean equals(Object o) {

    if (this == o) {
      return true;
    }
    if (!(o instanceof UserContest)) {
      return false;
    }
    return this.id != null && this.id.equals(((UserContest) o).id);
  }

  @Override
  public int hashCode() {

    return 31;
  }

  @Override
  public String toString() {

    return "UserContest{" + "id=" + this.id + ", modificationCounter=" + this.modificationCounter + ", userId="
        + this.userId + ", contestId=" + this.contestId + ", rank=" + this.rank + ", score=" + this.score
        + ", lastUpdated='" + this.lastUpdated + "'" + ", created='" + this.created + "'" + ", completed='"
        + this.completed + "'" + ", deleted='" + this.deleted + "'" + "}";
  }

  public UserContest update() {

    return update(this);
  }

  public UserContest persistOrUpdate() {

    return persistOrUpdate(this);
  }

  public static UserContest update(UserContest userContest) {

    if (userContest == null) {
      throw new IllegalArgumentException("userContest can't be null");
    }
    var entity = UserContest.<UserContest> findById(userContest.id);
    if (entity != null) {
      entity.modificationCounter = userContest.modificationCounter;
      entity.userId = userContest.userId;
      entity.contestId = userContest.contestId;
      entity.rank = userContest.rank;
      entity.score = userContest.score;
      entity.lastUpdated = userContest.lastUpdated;
      entity.created = userContest.created;
      entity.completed = userContest.completed;
      entity.deleted = userContest.deleted;
    }
    return entity;
  }

  public static UserContest persistOrUpdate(UserContest userContest) {

    if (userContest == null) {
      throw new IllegalArgumentException("userContest can't be null");
    }
    if (userContest.id == null) {
      persist(userContest);
      return userContest;
    } else {
      return update(userContest);
    }
  }

}
