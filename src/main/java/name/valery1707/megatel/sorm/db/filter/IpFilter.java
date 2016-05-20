package name.valery1707.megatel.sorm.db.filter;

import name.valery1707.megatel.sorm.db.SingularAttributeGetter;
import org.apache.commons.net.util.SubnetUtils;

import javax.annotation.Nonnull;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.math.BigInteger;
import java.util.function.Function;

import static name.valery1707.megatel.sorm.IpUtils.buildSubnet;
import static name.valery1707.megatel.sorm.IpUtils.ipToNumber;

public class IpFilter<D, F> extends BaseFilter<D, F, BigInteger, String> {
	public IpFilter(SingularAttributeGetter<D, BigInteger> field, Function<F, String> getter) {
		super(field, getter);
	}

	@Override
	protected Predicate toPredicateImpl(Root<D> root, CriteriaQuery<?> query, CriteriaBuilder cb, @Nonnull String filter) {
		SubnetUtils subnet = buildSubnet(filter);
		if (subnet != null) {
			return cb.between(
					field(root),
					ipToNumber(subnet.getInfo().getLowAddress()),
					ipToNumber(subnet.getInfo().getHighAddress())
			);
		}
		return null;
	}
}
