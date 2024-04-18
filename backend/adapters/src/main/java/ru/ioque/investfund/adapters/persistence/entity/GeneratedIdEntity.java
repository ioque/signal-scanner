package ru.ioque.investfund.adapters.persistence.entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.hibernate.proxy.HibernateProxy;

import java.util.Objects;

@ToString
@MappedSuperclass
@NoArgsConstructor
@AllArgsConstructor
@Getter(AccessLevel.PUBLIC)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GeneratedIdEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Override
    public final boolean equals(Object object) {
        if (this == object) return true;
        if (object == null) return false;
        Class<?> oEffectiveClass =
            object instanceof HibernateProxy ? ((HibernateProxy) object)
                .getHibernateLazyInitializer()
                .getPersistentClass() : object.getClass();
        Class<?> thisEffectiveClass =
            this instanceof HibernateProxy ? ((HibernateProxy) this)
                .getHibernateLazyInitializer()
                .getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        GeneratedIdEntity that = (GeneratedIdEntity) object;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this)
            .getHibernateLazyInitializer()
            .getPersistentClass()
            .hashCode() : getClass().hashCode();
    }
}
