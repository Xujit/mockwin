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
 * A BannerContest.
 */
@Entity
@Table(name = "banner_contest")
@Cacheable
@RegisterForReflection
public class BannerContest extends PanacheEntityBase implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
  @SequenceGenerator(name = "sequenceGenerator")
  public Long id;

  @Column(name = "modification_counter")
  public Integer modificationCounter;

  @NotNull
  @Column(name = "approved_file", nullable = false)
  public String approvedFile;

  @Column(name = "contest_id")
  public Long contestId;

  @NotNull
  @Column(name = "assigned_to", nullable = false)
  public Long assignedTo;

  @Column(name = "user_type_id")
  public Long userTypeId;

  @NotNull
  @Column(name = "status", nullable = false)
  public String status;

  @NotNull
  @Column(name = "fullname", nullable = false)
  public String fullname;

  @NotNull
  @Column(name = "comments", nullable = false)
  public String comments;

  @Column(name = "reason")
  public String reason;

  @OneToMany
  @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
  public Set<File> files = new HashSet<>();

  // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove

  @Override
  public boolean equals(Object o) {

    if (this == o) {
      return true;
    }
    if (!(o instanceof BannerContest)) {
      return false;
    }
    return this.id != null && this.id.equals(((BannerContest) o).id);
  }

  @Override
  public int hashCode() {

    return 31;
  }

  @Override
  public String toString() {

    return "BannerContest{" + "id=" + this.id + ", modificationCounter=" + this.modificationCounter + ", approvedFile='"
        + this.approvedFile + "'" + ", contestId=" + this.contestId + ", assignedTo=" + this.assignedTo
        + ", userTypeId=" + this.userTypeId + ", status='" + this.status + "'" + ", fullname='" + this.fullname + "'"
        + ", comments='" + this.comments + "'" + ", reason='" + this.reason + "'" + "}";
  }

  public BannerContest update() {

    return update(this);
  }

  public BannerContest persistOrUpdate() {

    return persistOrUpdate(this);
  }

  public static BannerContest update(BannerContest bannerContest) {

    if (bannerContest == null) {
      throw new IllegalArgumentException("bannerContest can't be null");
    }
    var entity = BannerContest.<BannerContest> findById(bannerContest.id);
    if (entity != null) {
      entity.modificationCounter = bannerContest.modificationCounter;
      entity.approvedFile = bannerContest.approvedFile;
      entity.contestId = bannerContest.contestId;
      entity.assignedTo = bannerContest.assignedTo;
      entity.userTypeId = bannerContest.userTypeId;
      entity.status = bannerContest.status;
      entity.fullname = bannerContest.fullname;
      entity.comments = bannerContest.comments;
      entity.reason = bannerContest.reason;
      entity.files = bannerContest.files;
    }
    return entity;
  }

  public static BannerContest persistOrUpdate(BannerContest bannerContest) {

    if (bannerContest == null) {
      throw new IllegalArgumentException("bannerContest can't be null");
    }
    if (bannerContest.id == null) {
      persist(bannerContest);
      return bannerContest;
    } else {
      return update(bannerContest);
    }
  }

}
