package net.vexelon.currencybg.srv.db.adapters;

import net.vexelon.currencybg.srv.db.models.CurrencyData;

import javax.annotation.Nonnull;
import java.util.Map;

public class CurrencyDataToFirestoreAdapter implements DataSourceAdapter<CurrencyData, Map<String, Object>> {

	@Nonnull
	@Override
	public Map<String, Object> fromEntity(@Nonnull CurrencyData entity) {
		return Map.of("code", entity.getCode(), "ratio", entity.getRatio(), "buy", entity.getBuy(), "sell",
				entity.getSell(), "date", entity.getDate(), "source_id", entity.getSource());
	}
}
