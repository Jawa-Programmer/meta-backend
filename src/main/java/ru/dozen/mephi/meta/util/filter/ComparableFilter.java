package ru.dozen.mephi.meta.util.filter;

import static java.util.Objects.nonNull;

import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.jpa.domain.Specification;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class ComparableFilter<T extends Comparable<T>> extends Filter<T> {

    private T lt;
    private T gt;
    private T lte;
    private T gte;

    private static <A, T extends Comparable<T>> Specification<A> lt(Function<Root<A>, Path<T>> pathFunction, T value) {
        return (nonNull(value)) ? (root, query, cb) -> cb.lessThan(pathFunction.apply(root), value) : null;
    }

    private static <A, T extends Comparable<T>> Specification<A> gt(Function<Root<A>, Path<T>> pathFunction, T value) {
        return (nonNull(value)) ? (root, query, cb) -> cb.greaterThan(pathFunction.apply(root), value) : null;
    }

    private static <A, T extends Comparable<T>> Specification<A> lte(Function<Root<A>, Path<T>> pathFunction, T value) {
        return (nonNull(value)) ? (root, query, cb) -> cb.lessThanOrEqualTo(pathFunction.apply(root), value) : null;
    }

    private static <A, T extends Comparable<T>> Specification<A> gte(Function<Root<A>, Path<T>> pathFunction, T value) {
        return (nonNull(value)) ? (root, query, cb) -> cb.greaterThanOrEqualTo(pathFunction.apply(root), value) : null;
    }


    @Override
    public <A> Specification<A> toSpecification(final String attributeName) {
        Specification<A> s = super.toSpecification(attributeName);
        if (s != null) {
            return s;
        }
        Specification<A> less = null;
        if (lt != null) {
            less = lt(root -> root.get(attributeName), lt);
        } else if (lte != null) {
            less = lte(root -> root.get(attributeName), lte);
        }

        Specification<A> greater = null;
        if (gt != null) {
            greater = gt(root -> root.get(attributeName), gt);
        } else if (gte != null) {
            greater = gte(root -> root.get(attributeName), gte);
        }

        if (less != null && greater != null) {
            return less.and(greater);
        }
        return less != null ? less : greater;

    }
}
