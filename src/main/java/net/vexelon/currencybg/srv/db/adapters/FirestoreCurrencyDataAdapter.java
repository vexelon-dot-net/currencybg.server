package net.vexelon.currencybg.srv.db.adapters;

import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.gson.Gson;
import net.vexelon.currencybg.srv.db.models.CurrencyData;

import javax.annotation.Nonnull;

public class FirestoreCurrencyDataAdapter implements DataSourceAdapter<QueryDocumentSnapshot, CurrencyData> {

	private final Gson gson = new Gson();

	@Nonnull
	@Override
	public CurrencyData fromSourceEntity(QueryDocumentSnapshot entity) {
		var currency = new CurrencyData();
		currency.setCode(entity.getString("code"));
		currency.setRatio(entity.getLong("ratio").intValue());
		currency.setBuy(entity.getString("buy"));
		currency.setSell(entity.getString("sell"));
		currency.setDate(entity.getTimestamp("date").toDate());
		currency.setSource(entity.getLong("source_id").intValue());
		return currency;
	}
}
