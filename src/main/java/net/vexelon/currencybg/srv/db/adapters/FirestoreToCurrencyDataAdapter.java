package net.vexelon.currencybg.srv.db.adapters;

import com.google.cloud.firestore.QueryDocumentSnapshot;
import net.vexelon.currencybg.srv.db.models.CurrencyData;

import javax.annotation.Nonnull;

public class FirestoreToCurrencyDataAdapter implements DataSourceAdapter<QueryDocumentSnapshot, CurrencyData> {

	@Nonnull
	@Override
	public CurrencyData fromEntity(QueryDocumentSnapshot entity) {
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
