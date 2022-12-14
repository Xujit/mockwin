package com.codingaxis.mockwin.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * A Mcq.
 */
@Entity
@Table(name = "mcq")
@Cacheable
@RegisterForReflection
public class Mcq extends PanacheEntityBase implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
  @SequenceGenerator(name = "sequenceGenerator")
  public Long id;

  @Column(name = "modification_counter")
  public Integer modificationCounter;

  @Column(name = "question")
  public String question;

  @Column(name = "chapter")
  public String chapter;

  @Column(name = "deleted")
  public Boolean deleted;

  @Column(name = "created_by")
  public Long createdBy;

  @OneToOne
  @JoinColumn(unique = true)
  public Exam exam;

  @OneToOne
  @JoinColumn(unique = true)
  public Subject subject;

  @OneToOne
  public Category category;

  @JsonbTransient
  @OneToMany
  @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
  public Set<Options> options = new HashSet<>();

  @ManyToOne
  @JoinColumn(name = "contest_response_id")
  @JsonbTransient
  public ContestResponse contestResponse;

  // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove

  @Override
  public boolean equals(Object o) {

    if (this == o) {
      return true;
    }
    if (!(o instanceof Mcq)) {
      return false;
    }
    return this.id != null && this.id.equals(((Mcq) o).id);
  }

  @Override
  public int hashCode() {

    return 31;
  }

  @Override
  public String toString() {

    return "Mcq{" + "id=" + this.id + ", modificationCounter=" + this.modificationCounter + ", question='"
        + this.question + "'" + ", chapter='" + this.chapter + "'" + ", deleted='" + this.deleted + "'" + ", createdBy="
        + this.createdBy + "}";
  }

  public Mcq update() {

    return update(this);
  }

  public Mcq persistOrUpdate() {

    return persistOrUpdate(this);
  }

  public static Mcq update(Mcq mcq) {

    if (mcq == null) {
      throw new IllegalArgumentException("mcq can't be null");
    }
    var entity = Mcq.<Mcq> findById(mcq.id);
    if (entity != null) {
      entity.modificationCounter = mcq.modificationCounter;
      entity.question = mcq.question;
      entity.chapter = mcq.chapter;
      entity.deleted = mcq.deleted;
      entity.createdBy = mcq.createdBy;
      entity.exam = mcq.exam;
      entity.subject = mcq.subject;
      entity.category = mcq.category;
      entity.options = mcq.options;
      entity.contestResponse = mcq.contestResponse;
    }
    return entity;
  }

  public static Mcq persistOrUpdate(Mcq mcq) {

    if (mcq == null) {
      throw new IllegalArgumentException("mcq can't be null");
    }
    if (mcq.id == null) {
      persist(mcq);
      return mcq;
    } else {
      return update(mcq);
    }
  }

  /**
   * @param userId
   * @param exam2
   * @param subject2
   * @param chapter2
   * @return
   */
  public static List<Mcq> findByCreatedByAndExamAndSubjectAndChapter(Long userId, String exam, String subject,
      String chapter) {

    if (userId != null && exam != null && subject != null && chapter != null) {
      // make a service with all fields matching
      // mcqs = mcqRepository.findByCreatedByAndExamAndSubjectAndChapter(userId,exam,subject,chapter);
    }

    // TODO Auto-generated method stub
    return null;
  }

}
