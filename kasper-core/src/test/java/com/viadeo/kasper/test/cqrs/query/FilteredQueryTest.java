package com.viadeo.kasper.test.cqrs.query;

import com.viadeo.kasper.cqrs.query.QueryDTO;
import com.viadeo.kasper.cqrs.query.filter.QueryDQO;
import com.viadeo.kasper.cqrs.query.filter.QueryFilter;
import com.viadeo.kasper.cqrs.query.filter.impl.AbstractQueryDQO;
import com.viadeo.kasper.cqrs.query.filter.impl.FilteredQuery;
import com.viadeo.kasper.cqrs.query.filter.impl.base.BaseQueryField;
import junit.framework.Assert;
import junit.framework.TestCase;
import org.junit.Test;

public class FilteredQueryTest extends TestCase {

	// ------------------------------------------------------------------------

	private static final class DTOTest implements QueryDTO {
		private static final long serialVersionUID = -7571158053188747427L;
		@SuppressWarnings("unused")
		private String name;

		public DTOTest setName(final String name) {
			this.name = name;
			return this;
		}

	}

	// "*Field" classes are public (so there is no gain in testing accessibility here)
	public static final class FieldTest<DQO extends QueryDQO<DQO>> extends BaseQueryField<String, DQO> {

	}

	private static final class DQOTest extends AbstractQueryDQO<DQOTest> {
		private static final long serialVersionUID = 5709183469621265842L;
		public FieldTest<DQOTest> name;
	}

	private static final class QueryTest extends FilteredQuery<DQOTest> {
		private static final long serialVersionUID = -7163435283528783159L;
	}

	// ------------------------------------------------------------------------

	@Test
	public void test() {

		final QueryTest query = new QueryTest();
		final DQOTest dqo = query.dqo();

		final QueryFilter<DQOTest> filter =
				dqo.name.filter().endsWith("test").or(
						dqo.name.filter().startsWith("test").and(
								dqo.name.filter().endsWith("two")
						)
				);

		final DTOTest dto = new DTOTest(); // Normally done internally (protected) by the service with this.dto()

		Assert.assertTrue(filter.isSatisfiedBy(dto.setName("the test")));
		Assert.assertTrue(filter.isSatisfiedBy(dto.setName("test two")));

		// then query.setFilter(filter);
		// Here call the service with query, using gateway
	}

}