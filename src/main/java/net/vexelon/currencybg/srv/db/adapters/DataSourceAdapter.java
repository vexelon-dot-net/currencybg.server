package net.vexelon.currencybg.srv.db.adapters;

import javax.annotation.Nonnull;

public interface DataSourceAdapter<T, R> {

	@Nonnull
	R fromEntity(T entity);
}
