package ru.dozen.mephi.meta.util.filter;

import static java.util.Objects.nonNull;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import java.io.Serializable;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class Filter<T extends Serializable> implements Serializable {

    private T eq;
    private T neq;
    @Schema(description = "Если null, то фильтр не применяется. Если false, то производится поиск не null полей, если true, то выполняется поиск null полей")
    private Boolean isNull = null;

    private static <T, A> Specification<A> eq(Function<Root<A>, Path<T>> pathFunction, T value) {
        return (nonNull(value)) ? (root, query, cb) -> cb.equal(pathFunction.apply(root), value) : null;
    }

    private static <T, A> Specification<A> neq(Function<Root<A>, Path<T>> pathFunction, T value) {
        return (nonNull(value)) ? (root, query, cb) -> cb.notEqual(pathFunction.apply(root), value) : null;
    }

    private static <T, A> Specification<A> isNull(Function<Root<A>, Path<T>> pathFunction) {
        return (root, query, cb) -> cb.isNull(pathFunction.apply(root));
    }

    private static <T, A> Specification<A> isNotNull(Function<Root<A>, Path<T>> pathFunction) {
        return (root, query, cb) -> cb.isNotNull(pathFunction.apply(root));
    }

    protected <A, V> Function<Root<A>, Path<V>> pathFunction(final String attributePath) {
        return root -> {
            var attributeNames = StringUtils.split(attributePath, ".");
            Path<V> res = root.get(attributeNames[0]);
            for (int i = 1; i < attributeNames.length; ++i) {
                res = res.get(attributeNames[i]);
            }
            return res;
        };
    }

    public <A> Specification<A> toSpecification(final String attributeName) {
        var pf = this.<A, T>pathFunction(attributeName);
        if (isNull != null) {
            return isNull.equals(Boolean.TRUE) ?
                    isNull(pf) :
                    isNotNull(pf);
        }
        if (eq != null) {
            return eq(pf, eq);
        }
        if (neq != null) {
            return neq(pf, neq);
        }
        return null;
    }
}