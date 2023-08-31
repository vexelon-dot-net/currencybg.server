package net.vexelon.currencybg.srv.db.adapters;

import com.google.gson.Gson;
import net.vexelon.currencybg.srv.db.models.CurrencySource;

import javax.annotation.Nonnull;
import java.util.Map;

public class CurrencySourceToFirestoreAdapter implements DataSourceAdapter<CurrencySource, Map<String, Object>> {

	private final Gson gson = new Gson();

	@Nonnull
	@Override
	public Map<String, Object> fromEntity(CurrencySource entity) {
		return Map.of("source_id", entity.getSourceId(), "status", entity.getStatus(), "name", entity.getSourceName(),
				"update_period", entity.getUpdatePeriod(), "last_update", entity.getLastUpdate(), "update_restrictions",
				gson.toJson(entity.getUpdateRestrictions()));
	}
}
