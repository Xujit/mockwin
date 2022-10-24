package com.codingaxis.mockwin.domain;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import javax.json.bind.annotation.JsonbTransient;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.runtime.annotations.RegisterForReflection;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * A Assign.
 */
@Entity
@Table(name = "assign")
@Cacheable
@RegisterForReflection
public class Assign extends PanacheEntityBase implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    public Long id;

    @Column(name = "modification_counter")
    public Integer modificationCounter;

    @NotNull
    @Column(name = "fullname", nullable = false)
    public String fullname;

    @NotNull
    @Column(name = "status", nullable = false)
    public String status;

    @Column(name = "user_type_id")
    public Integer userTypeId;

    @Column(name = "comments")
    public String comments;

    @OneToMany(mappedBy = "assign")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    public Set<File> files = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Assign)) {
            return false;
        }
        return id != null && id.equals(((Assign) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Assign{" +
            "id=" + id +
            ", modificationCounter=" + modificationCounter +
            ", fullname='" + fullname + "'" +
            ", status='" + status + "'" +
            ", userTypeId=" + userTypeId +
            ", comments='" + comments + "'" +
            "}";
    }

    public Assign update() {
        return update(this);
    }

    public Assign persistOrUpdate() {
        return persistOrUpdate(this);
    }

    public static Assign update(Assign assign) {
        if (assign == null) {
            throw new IllegalArgumentException("assign can't be null");
        }
        var entity = Assign.<Assign>findById(assign.id);
        if (entity != null) {
            entity.modificationCounter = assign.modificationCounter;
            entity.fullname = assign.fullname;
            entity.status = assign.status;
            entity.userTypeId = assign.userTypeId;
            entity.comments = assign.comments;
            entity.files = assign.files;
        }
        return entity;
    }

    public static Assign persistOrUpdate(Assign assign) {
        if (assign == null) {
            throw new IllegalArgumentException("assign can't be null");
        }
        if (assign.id == null) {
            persist(assign);
            return assign;
        } else {
            return update(assign);
        }
    }


}
