package net.vexelon.currencybg.srv.db.adapters;

import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.vexelon.currencybg.srv.db.models.CurrencySource;
import net.vexelon.currencybg.srv.db.models.SourceUpdateRestrictions;

import javax.annotation.Nonnull;

public class FirestoreCurrencySourceAdapter implements DataSourceAdapter<QueryDocumentSnapshot, CurrencySource> {

	private final Gson gson = new Gson();

	@Nonnull
	@Override
	public CurrencySource fromSourceEntity(QueryDocumentSnapshot entity) {
		var source = new CurrencySource();
		source.setSourceId(entity.getLong("source_id").intValue());
		source.setStatus(entity.getLong("status").intValue());
		source.setSourceName(entity.getString("name"));
		source.setUpdatePeriod(entity.getLong("update_period").intValue());
		source.setLastUpdate(entity.getTimestamp("last_update").toDate());

		if (entity.contains("update_restrictions")) {
			SourceUpdateRestrictions updateInfo = gson.fromJson(entity.getString("update_restrictions"),
					new TypeToken<SourceUpdateRestrictions>() {}.getType());
			source.setUpdateRestrictions(updateInfo);
		} else {
			source.setUpdateRestrictions(SourceUpdateRestrictions.empty());
		}

		return source;
	}
}
