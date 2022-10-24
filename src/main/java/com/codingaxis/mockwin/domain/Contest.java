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
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * A Contest.
 */
@Entity
@Table(name = "contest")
@Cacheable
@RegisterForReflection
public class Contest extends PanacheEntityBase implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
  @SequenceGenerator(name = "sequenceGenerator")
  public Long id;

  @Column(name = "modification_counter")
  public Integer modificationCounter;

  @Column(name = "name")
  public String name;

  @Column(name = "description")
  public String description;

  @Column(name = "type")
  public String type;

  @Column(name = "start_date_time")
  public LocalDate startDateTime;

  @Column(name = "end_date_time")
  public LocalDate endDateTime;

  @Column(name = "commence_time")
  public LocalDate commenceTime;

  @Column(name = "finish_time")
  public LocalDate finishTime;

  @Column(name = "duration")
  public Long duration;

  @Column(name = "prizes")
  public String prizes;

  @Column(name = "state")
  public String state;

  @Column(name = "region")
  public String region;

  @Column(name = "noofmcqs")
  public Long noofmcqs;

  @Column(name = "eligibility_criteria")
  public String eligibilityCriteria;

  @Column(name = "winner_selection")
  public String winnerSelection;

  @Column(name = "recurring")
  public Boolean recurring;

  @Column(name = "status")
  public String status;

  @Column(name = "created")
  public LocalDate created;

  @Column(name = "last_updated")
  public LocalDate lastUpdated;

  @Column(name = "completed")
  public Boolean completed;

  @Column(name = "created_by")
  public Long createdBy;

  @Column(name = "visibility")
  public String visibility;

  @Column(name = "sponsored_by")
  public String sponsoredBy;

  @Column(name = "file_id")
  public Long fileId;

  @OneToOne
  @JoinColumn(unique = true)
  public Exam exam;

  @OneToOne
  @JoinColumn(unique = true)
  public Country country;

  @OneToMany(mappedBy = "contest")
  @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
  public Set<Curriculum> curricula = new HashSet<>();

  @OneToMany(mappedBy = "contest")
  @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
  public Set<SubContest> subContests = new HashSet<>();

  @OneToOne(mappedBy = "contest")
  @JsonIgnore
  public ContestResponse contestResponse;

  // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove

  @Override
  public boolean equals(Object o) {

    if (this == o) {
      return true;
    }
    if (!(o instanceof Contest)) {
      return false;
    }
    return this.id != null && this.id.equals(((Contest) o).id);
  }

  @Override
  public int hashCode() {

    return 31;
  }

  @Override
  public String toString() {

    return "Contest{" + "id=" + this.id + ", modificationCounter=" + this.modificationCounter + ", name='" + this.name
        + "'" + ", description='" + this.description + "'" + ", type='" + this.type + "'" + ", startDateTime='"
        + this.startDateTime + "'" + ", endDateTime='" + this.endDateTime + "'" + ", commenceTime='" + this.commenceTime
        + "'" + ", finishTime='" + this.finishTime + "'" + ", duration=" + this.duration + ", prizes='" + this.prizes
        + "'" + ", state='" + this.state + "'" + ", region='" + this.region + "'" + ", noofmcqs=" + this.noofmcqs
        + ", eligibilityCriteria='" + this.eligibilityCriteria + "'" + ", winnerSelection='" + this.winnerSelection
        + "'" + ", recurring='" + this.recurring + "'" + ", status='" + this.status + "'" + ", created='" + this.created
        + "'" + ", lastUpdated='" + this.lastUpdated + "'" + ", completed='" + this.completed + "'" + ", createdBy="
        + this.createdBy + ", visibility='" + this.visibility + "'" + ", sponsoredBy='" + this.sponsoredBy + "'"
        + ", fileId=" + this.fileId + "}";
  }

  public Contest update() {

    return update(this);
  }

  public Contest persistOrUpdate() {

    return persistOrUpdate(this);
  }

  public static Contest update(Contest contest) {

    if (contest == null) {
      throw new IllegalArgumentException("contest can't be null");
    }
    var entity = Contest.<Contest> findById(contest.id);
    if (entity != null) {
      entity.modificationCounter = contest.modificationCounter;
      entity.name = contest.name;
      entity.description = contest.description;
      entity.type = contest.type;
      entity.startDateTime = contest.startDateTime;
      entity.endDateTime = contest.endDateTime;
      entity.commenceTime = contest.commenceTime;
      entity.finishTime = contest.finishTime;
      entity.duration = contest.duration;
      entity.prizes = contest.prizes;
      entity.state = contest.state;
      entity.region = contest.region;
      entity.noofmcqs = contest.noofmcqs;
      entity.eligibilityCriteria = contest.eligibilityCriteria;
      entity.winnerSelection = contest.winnerSelection;
      entity.recurring = contest.recurring;
      entity.status = contest.status;
      entity.created = contest.created;
      entity.lastUpdated = contest.lastUpdated;
      entity.completed = contest.completed;
      entity.createdBy = contest.createdBy;
      entity.visibility = contest.visibility;
      entity.sponsoredBy = contest.sponsoredBy;
      entity.fileId = contest.fileId;
      entity.exam = contest.exam;
      entity.country = contest.country;
      entity.curricula = contest.curricula;
      entity.subContests = contest.subContests;
      entity.contestResponse = contest.contestResponse;
    }
    return entity;
  }

  public static Contest persistOrUpdate(Contest contest) {

    if (contest == null) {
      throw new IllegalArgumentException("contest can't be null");
    }
    if (contest.id == null) {
      persist(contest);
      return contest;
    } else {
      return update(contest);
    }
  }

}
